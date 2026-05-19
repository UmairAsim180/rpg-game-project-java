package com.example.final_project;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import com.example.final_project.models.BossEnemy;
import com.example.final_project.models.Enemy;
import com.example.final_project.models.NormalEnemy;
import com.example.final_project.models.Player;
import javafx.util.Duration;

import javafx.scene.media.AudioClip;

import java.io.IOException;

public class BattleController {

    // --- 1. UI Elements ---
    @FXML
    private ProgressBar playerHealthBar;
    @FXML
    private ProgressBar enemyHealthBar;
    @FXML
    private Label playerHealthText;
    @FXML
    private Label enemyHealthText;
    @FXML
    private Label lvlDisplay;
    @FXML
    private Label playerLog;
    @FXML
    private Label enemyLog;
    @FXML
    private Label enemyNameLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private Label playerStatsLabel;
    // --- Image UI Elements ---
    @FXML
    private ImageView playerImageView;
    @FXML
    private ImageView enemyImageView;

    // --- Image Storage ---
    private Image playerRestImg;
    private Image playerAttackImg;
    private Image playerMagicImg;
    private Image enemyRestImg;
    private Image enemyAttackImg;

    // --- 2. Game Models & State Trackers ---
    private Player player;
    private Enemy currentEnemy;
    private String playerName;

    private AudioClip hitSound;
    private AudioClip shieldSound;
    private AudioClip enemyHitSound;
    private AudioClip magicSound;

    private int currentLevel = 1;
    private int enemiesDefeatedThisLevel = 0;


    // NEW: Cooldown timer to prevent magic spamming!
    private int magicCooldown = 0;

    @FXML
    public void initialize() {
        currentLevel = 1;
        enemiesDefeatedThisLevel = 0;
        magicCooldown = 0;
        // LOAD SOUNDS
        try {
            hitSound = new AudioClip(getClass().getResource("/com/example/final_project/hit.mp3").toExternalForm());
            shieldSound = new AudioClip(getClass().getResource("/com/example/final_project/shield.mp3").toExternalForm());
            enemyHitSound = new AudioClip(getClass().getResource("/com/example/final_project/enemy_hit.mp3").toExternalForm());
            magicSound = new AudioClip(getClass().getResource("/com/example/final_project/magic.mp3").toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: Could not load audio files. Check filenames!");
        }

        // LOAD IMAGES
        try {
            enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest.png"));
            enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack.png"));
        } catch (Exception e) {
            System.out.println("Warning: Could not load enemy images. Check filenames!");
        }
        loadPlayerImages(currentLevel);
        Platform.runLater(() -> {
            // Grab the current scene from any UI element
            Scene scene = playerHealthBar.getScene();

            if (scene != null) {
                scene.setOnKeyPressed((KeyEvent event) -> {
                    // Check which key was pressed
                    KeyCode key = event.getCode();

                    // A or 1 = Attack
                    if (key == KeyCode.A || key == KeyCode.DIGIT1 || key == KeyCode.NUMPAD1) {
                        onAttackClick(null);
                    }
                    // D or 2 = Defend
                    else if (key == KeyCode.D || key == KeyCode.DIGIT2 || key == KeyCode.NUMPAD2) {
                        onDefendClick(null);
                    }
                    // M or 3 = Magic
                    else if (key == KeyCode.M || key == KeyCode.DIGIT3 || key == KeyCode.NUMPAD3) {
                        onMagicClick(null);
                    }
                });
            }
        });
    }

    // --- 3. Initialization ---
    public void setPlayerName(String name) {
        this.playerName = name;
        playerNameLabel.setText(name);
        player = new Player(playerName);
        spawnNextEnemy();
    }


    private void spawnNextEnemy() {
        if (currentLevel == 1) {
            currentEnemy = new NormalEnemy("Goblin Scout", 1);
        } else if (currentLevel == 2) {
            currentEnemy = new NormalEnemy("Orc Warrior", 2);
        } else if (currentLevel == 3) {
            currentEnemy = new BossEnemy("Dark Knight", "Hellfire Strike");
        }
        loadEnemyImages(currentLevel);
        showLevelDisplayTemporary("Level " + currentLevel);
        enemyNameLabel.setText(currentEnemy.getName());
        if (playerImageView != null) playerImageView.setImage(playerRestImg);
        if (enemyImageView != null) enemyImageView.setImage(enemyRestImg);
        updateUI();
    }

    // --- 4. Player Actions ---
    @FXML
    public void onAttackClick(ActionEvent event) {
        if (player.isDead() || currentEnemy.isDead()) return;

        int damage = player.attack();
        currentEnemy.takeDamage(damage);
        triggerPlayerAttackAnimation();
        if (hitSound != null) {
            hitSound.play();
        }
        showEnemyLogTemporary("-" + damage, "red");

        if (currentEnemy instanceof BossEnemy) {
            BossEnemy boss = (BossEnemy) currentEnemy;
            if (boss.checkPhaseChange()) {
                showLevelDisplayTemporary("Enemy Damage Increased!");
            }
        }
        checkGameState();
    }

    @FXML
    public void onDefendClick(ActionEvent event) {
        if (player.isDead() || currentEnemy.isDead()) return;

        player.defend();
        if (shieldSound != null) shieldSound.play();
        showPlayerLogTemporary("+5", "green");
        updateUI();

        // Wait 1.2 seconds for the player to see the heal before the enemy attacks
        PauseTransition wait = new PauseTransition(Duration.seconds(1.2));
        wait.setOnFinished(e -> enemyTurn());
        wait.play();
    }

    @FXML
    public void onMagicClick(ActionEvent event) {
        if (player.isDead() || currentEnemy.isDead()) return;

        // Check if magic is on cooldown!
        if (magicCooldown > 0) {
            showPlayerLogTemporary("Cooldown: " + magicCooldown, "gray");
            return; // Stop the attack
        }
        if (magicSound != null) {
            magicSound.play();
        }
        int magicDamage = player.magicAttack();
        currentEnemy.takeDamage(magicDamage);
        triggerPlayerMagicAnimation();
        showEnemyLogTemporary("-" + magicDamage, "purple");

        magicCooldown = 3; // Reset the cooldown to 3 turns
        checkGameState();
    }

    // --- 5. Game Loop Logic ---
    private void checkGameState() {
        updateUI();

        if (currentEnemy.isDead()) {
            enemiesDefeatedThisLevel++;

            // --- NEW: THE LEVEL UP DETECTOR ---
            int oldLevel = player.getLevel(); // Remember our level
            player.gainExperience(currentEnemy.getReward()); // Give the XP

            // Check if our level went up!
            if (player.getLevel() > oldLevel) {
                showLevelDisplayTemporary("LEVEL UP! (Lvl " + player.getLevel() + ")");
                showPlayerLogTemporary("MAX HP & DMG UP!", "gold");

                // Play a cool sound for leveling up if you have one!
                if (magicSound != null) magicSound.play();
            }
            // ----------------------------------

            if (currentLevel == 3) {
                switchToGameOverScreen(true, "You have slain the Boss and conquered the game!");
            } else if (enemiesDefeatedThisLevel == 2) {
                currentLevel++;
                enemiesDefeatedThisLevel = 0;
                loadPlayerImages(currentLevel);
                showLevelDisplayTemporary("Level " + (currentLevel - 1) + " Cleared!");

                // Heal the player between rounds
                player.heal(40);
                showPlayerLogTemporary("+40 HP", "green");

                PauseTransition wait = new PauseTransition(Duration.seconds(2));
                wait.setOnFinished(e -> spawnNextEnemy());
                wait.play();
            } else {
                PauseTransition wait = new PauseTransition(Duration.seconds(1.5));
                wait.setOnFinished(e -> spawnNextEnemy());
                wait.play();
            }
        } else {
            PauseTransition wait = new PauseTransition(Duration.seconds(1.2));
            wait.setOnFinished(e -> enemyTurn());
            wait.play();
        }
    }

    private void enemyTurn() {
        if (currentEnemy.isDead()) return;

        // Decrease the magic cooldown every time the enemy takes a turn
        if (magicCooldown > 0) {
            magicCooldown--;
        }

        int enemyRawDamage = currentEnemy.chooseAction();
        int actualDamage = Math.max(0, enemyRawDamage - player.getDefenseReduction());

        player.takeDamage(actualDamage);
        if (enemyHitSound != null) enemyHitSound.play();
        triggerEnemyAttackAnimation();
        player.resetDefense();
        showPlayerLogTemporary("-" + actualDamage, "red");
        triggerDamageVignette();
        updateUI();

        if (player.isDead()) {
            // Wait 1.5 seconds so the player sees the final damage text before teleporting
            PauseTransition deathWait = new PauseTransition(Duration.seconds(1.5));
            deathWait.setOnFinished(e -> switchToGameOverScreen(false, "You fell in battle against the " + currentEnemy.getName() + "."));
            deathWait.play();
        }
    }

    // --- 6. Teleportation & UI ---
    private void updateUI() {
        playerHealthBar.setProgress((double) player.getCurrentHealth() / player.getMaxHealth());
        enemyHealthBar.setProgress((double) currentEnemy.getCurrentHealth() / currentEnemy.getMaxHealth());

        playerHealthText.setText(player.getCurrentHealth() + " / " + player.getMaxHealth() + " HP");
        enemyHealthText.setText(currentEnemy.getCurrentHealth() + " / " + currentEnemy.getMaxHealth() + " HP");

        // --- NEW: Update the XP/Level Text ---
        if (playerStatsLabel != null) {
            playerStatsLabel.setText("Lvl: " + player.getLevel() + " | XP: " + player.getExperience() + " / " + player.getExpNeeded());
        }
    }

    private void switchToGameOverScreen(boolean playerWon, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/game-over.fxml"));
            Parent root = loader.load();

            GameOverController gameOverController = loader.getController();
            gameOverController.setGameResult(playerWon, message);

            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/com/example/final_project/style.css").toExternalForm());

            // Swapped combatLog for playerHealthBar to get the window stage safely
            Stage stage = (Stage) playerHealthBar.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("Error loading game-over screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- 7. Temporary Text Helpers ---
    private void showLevelDisplayTemporary(String text) {
        lvlDisplay.setText(text);
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> lvlDisplay.setText(""));
        pause.play();
    }

    private void showPlayerLogTemporary(String text, String color) {
        playerLog.setText(text);
        playerLog.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(event -> playerLog.setText(""));
        pause.play();
    }

    private void showEnemyLogTemporary(String text, String color) {
        enemyLog.setText(text);
        enemyLog.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");

        PauseTransition pause = new PauseTransition(Duration.seconds(1.2));
        pause.setOnFinished(event -> enemyLog.setText(""));
        pause.play();
    }

    // --- Image Animation Helpers ---
    private void triggerPlayerAttackAnimation() {
        // Only run if the image box actually exists
        if (playerImageView != null && playerAttackImg != null) {
            playerImageView.setImage(playerAttackImg); // Swap to attack pose

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> playerImageView.setImage(playerRestImg)); // Swap back
            pause.play();
        }
    }

    private void triggerPlayerMagicAnimation() {
        if (playerImageView != null && playerMagicImg != null) {
            playerImageView.setImage(playerMagicImg); // Swap to magic pose

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> playerImageView.setImage(playerRestImg)); // Swap back
            pause.play();
        }
    }

    private void triggerEnemyAttackAnimation() {
        if (enemyImageView != null && enemyAttackImg != null) {
            enemyImageView.setImage(enemyAttackImg); // Swap to attack pose

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> enemyImageView.setImage(enemyRestImg)); // Swap back
            pause.play();
        }
    }

    private void triggerDamageVignette() {
        // 1. Create a deep red, soft inner shadow
        InnerShadow bloodBorder = new InnerShadow();
        bloodBorder.setColor(Color.DARKRED);
        bloodBorder.setRadius(120); // How far the blood spreads into the center
        bloodBorder.setChoke(0.3);  // How thick and intense the edges are

        // 2. Apply it to the absolute root background of your game window
        if (playerHealthBar.getScene() != null) {
            playerHealthBar.getScene().getRoot().setEffect(bloodBorder);

            // 3. Make it disappear after 0.5 seconds so it flashes quickly!
            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> playerHealthBar.getScene().getRoot().setEffect(null));
            pause.play();
        }
    }

    // --- Dynamic Asset Loader ---
    private void loadPlayerImages(int level) {
        try {
            if (level == 1) {
                // Level 1 Gear
                playerRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-rest.png"));
                playerAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-attack.png"));
                // You don't have a level 1 magic image, so we use the attack one as a fallback!
                playerMagicImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-magic.png"));
            } else if (level == 2) {
                // Level 2 Gear
                playerRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-rest-lvl2.png"));
                playerAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-attack-lvl2.png"));
                playerMagicImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-magic-lvl2.png"));
            } else {
                // Level 3 Boss Fight Gear
                playerRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-rest-lvl3.png"));
                playerAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-attack-lvl3.png"));
                playerMagicImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-magic-lvl3.png"));
            }

            // Instantly apply the resting image to the screen
            if (playerImageView != null) {
                playerImageView.setImage(playerRestImg);
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load player images for Level " + level);
        }

    }

    private void loadEnemyImages(int level) {
        try {
            if (level == 1) {
                // Level 1 Goblin/Scout
                enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest-lvl1.png"));
                enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack-lvl1.png"));
            } else if (level == 2) {
                // Level 2 Orc/Warrior
                enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest-lvl2.png"));
                enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack-lvl2.png"));
            } else {
                // Level 3 Boss (The Demon!)
                enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest.png"));
                enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack.png"));
            }

            // Instantly apply the resting image to the screen
            if (enemyImageView != null) {
                enemyImageView.setImage(enemyRestImg);
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load enemy images for Level " + level);
        }
    }
}