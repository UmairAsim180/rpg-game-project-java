package com.example.final_project;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.example.final_project.models.BossEnemy;
import com.example.final_project.models.Enemy;
import com.example.final_project.models.NormalEnemy;
import com.example.final_project.models.Player;
import javafx.util.Duration;

import java.io.IOException;

public class BattleController {

    // --- 1. UI Elements ---
    @FXML private ProgressBar playerHealthBar;
    @FXML private ProgressBar enemyHealthBar;
    @FXML private Label playerHealthText;
    @FXML private Label enemyHealthText;
    @FXML private Label lvlDisplay;
    @FXML private Label playerLog;
    @FXML private Label enemyLog;
    @FXML private Label enemyNameLabel;
    @FXML private Label playerNameLabel;
    // --- Image UI Elements ---
    @FXML private ImageView playerImageView;
    @FXML private ImageView enemyImageView;

    // --- Image Storage ---
    private Image playerRestImg;
    private Image playerAttackImg;
    private Image enemyRestImg;
    private Image enemyAttackImg;

    // --- 2. Game Models & State Trackers ---
    private Player player;
    private Enemy currentEnemy;
    private String playerName;

    private int currentLevel = 1;
    private int enemiesDefeatedThisLevel = 0;

    // NEW: Cooldown timer to prevent magic spamming!
    private int magicCooldown = 0;

    // --- 3. Initialization ---
    public void setPlayerName(String name) {
        this.playerName = name;
        playerNameLabel.setText(name);
        player = new Player(playerName);
        spawnNextEnemy();
    }

    @FXML
    public void initialize() {
        currentLevel = 1;
        enemiesDefeatedThisLevel = 0;
        magicCooldown = 0;

        // LOAD IMAGES
        try {
            playerRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-rest.png"));
            playerAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-attack.png"));
            enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest.png"));
            enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack.png"));
        } catch (Exception e) {
            System.out.println("Warning: Could not load images. Check filenames!");
        }
    }

    private void spawnNextEnemy() {
        if (currentLevel == 1) {
            currentEnemy = new NormalEnemy("Goblin Scout", 1);
        } else if (currentLevel == 2) {
            currentEnemy = new NormalEnemy("Orc Warrior", 2);
        } else if (currentLevel == 3) {
            currentEnemy = new BossEnemy("Dark Knight", "Hellfire Strike");
        }

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

        int magicDamage = player.magicAttack();
        currentEnemy.takeDamage(magicDamage);
        triggerPlayerAttackAnimation();
        showEnemyLogTemporary("-" + magicDamage, "purple");

        magicCooldown = 3; // Reset the cooldown to 3 turns
        checkGameState();
    }

    // --- 5. Game Loop Logic ---
    private void checkGameState() {
        updateUI();

        if (currentEnemy.isDead()) {
            player.gainExperience(currentEnemy.getReward());
            enemiesDefeatedThisLevel++;

            if (currentLevel == 3) {
                switchToGameOverScreen(true, "You have slain the Boss and conquered the game!");
            }
            else if (enemiesDefeatedThisLevel == 2) {
                currentLevel++;
                enemiesDefeatedThisLevel = 0;

                showLevelDisplayTemporary("Level " + (currentLevel-1) + " Cleared!");

                player.heal(40);
                showPlayerLogTemporary("+40", "green");

                // Wait 2 seconds so the player can read "Level Cleared" before spawning the next enemy
                PauseTransition wait = new PauseTransition(Duration.seconds(2));
                wait.setOnFinished(e -> spawnNextEnemy());
                wait.play();
            }
            else {
                // Wait 1.5 seconds before spawning the next enemy of the current level
                PauseTransition wait = new PauseTransition(Duration.seconds(1.5));
                wait.setOnFinished(e -> spawnNextEnemy());
                wait.play();
            }
        } else {
            // Wait 1.2 seconds before the enemy hits back
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
        triggerEnemyAttackAnimation();
        player.resetDefense();
        showPlayerLogTemporary("-" + actualDamage, "red");

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

    private void triggerEnemyAttackAnimation() {
        if (enemyImageView != null && enemyAttackImg != null) {
            enemyImageView.setImage(enemyAttackImg); // Swap to attack pose

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> enemyImageView.setImage(enemyRestImg)); // Swap back
            pause.play();
        }
    }
}