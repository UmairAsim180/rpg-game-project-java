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
    @FXML
    private ImageView playerImageView;
    @FXML
    private ImageView enemyImageView;


    private Image playerRestImg;
    private Image playerAttackImg;
    private Image playerMagicImg;
    private Image enemyRestImg;
    private Image enemyAttackImg;


    private Player player;
    private Enemy currentEnemy;
    private String playerName;

    private AudioClip hitSound;
    private AudioClip shieldSound;
    private AudioClip enemyHitSound;
    private AudioClip magicSound;

    private int currentLevel = 1;
    private int enemiesDefeatedThisLevel = 0;



    private int magicCooldown = 0;

    @FXML
    public void initialize() {
        currentLevel = 1;
        enemiesDefeatedThisLevel = 0;
        magicCooldown = 0;

        try {
            hitSound = new AudioClip(getClass().getResource("/com/example/final_project/hit.mp3").toExternalForm());
            shieldSound = new AudioClip(getClass().getResource("/com/example/final_project/shield.mp3").toExternalForm());
            enemyHitSound = new AudioClip(getClass().getResource("/com/example/final_project/enemy_hit.mp3").toExternalForm());
            magicSound = new AudioClip(getClass().getResource("/com/example/final_project/magic.mp3").toExternalForm());
        } catch (Exception e) {
            System.out.println("Warning: Could not load audio files. Check filenames!");
        }


        try {
            enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest.png"));
            enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack.png"));
        } catch (Exception e) {
            System.out.println("Warning: Could not load enemy images. Check filenames!");
        }
        loadPlayerImages(currentLevel);
        Platform.runLater(() -> {

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

        PauseTransition wait = new PauseTransition(Duration.seconds(1.2));
        wait.setOnFinished(e -> enemyTurn());
        wait.play();
    }

    @FXML
    public void onMagicClick(ActionEvent event) {
        if (player.isDead() || currentEnemy.isDead()) return;


        if (magicCooldown > 0) {
            showPlayerLogTemporary("Cooldown: " + magicCooldown, "gray");
            return;
        }
        if (magicSound != null) {
            magicSound.play();
        }
        int magicDamage = player.magicAttack();
        currentEnemy.takeDamage(magicDamage);
        triggerPlayerMagicAnimation();
        showEnemyLogTemporary("-" + magicDamage, "purple");

        magicCooldown = 3;
        checkGameState();
    }


    private void checkGameState() {
        updateUI();

        if (currentEnemy.isDead()) {
            enemiesDefeatedThisLevel++;


            int oldLevel = player.getLevel();
            player.gainExperience(currentEnemy.getReward());


            if (player.getLevel() > oldLevel) {
                showLevelDisplayTemporary("LEVEL UP! (Lvl " + player.getLevel() + ")");
                showPlayerLogTemporary("MAX HP & DMG UP!", "gold");


                if (magicSound != null) magicSound.play();
            }


            if (currentLevel == 3){
                switchToGameOverScreen(true, "You have slain the Boss and conquered the game!");
            } else if (enemiesDefeatedThisLevel == 2) {
                currentLevel++;
                enemiesDefeatedThisLevel = 0;
                loadPlayerImages(currentLevel);
                showLevelDisplayTemporary("Level " + (currentLevel - 1) + " Cleared!");


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

            PauseTransition deathWait = new PauseTransition(Duration.seconds(1.5));
            deathWait.setOnFinished(e -> switchToGameOverScreen(false, "You fell in battle against the " + currentEnemy.getName() + "."));
            deathWait.play();
        }
    }


    private void updateUI() {
        playerHealthBar.setProgress((double) player.getCurrentHealth() / player.getMaxHealth());
        enemyHealthBar.setProgress((double) currentEnemy.getCurrentHealth() / currentEnemy.getMaxHealth());

        playerHealthText.setText(player.getCurrentHealth() + " / " + player.getMaxHealth() + " HP");
        enemyHealthText.setText(currentEnemy.getCurrentHealth() + " / " + currentEnemy.getMaxHealth() + " HP");


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


            Stage stage = (Stage) playerHealthBar.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("Error loading game-over screen: " + e.getMessage());
            e.printStackTrace();
        }
    }


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


    private void triggerPlayerAttackAnimation() {

        if (playerImageView != null && playerAttackImg != null) {
            playerImageView.setImage(playerAttackImg);

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> playerImageView.setImage(playerRestImg));
            pause.play();
        }
    }

    private void triggerPlayerMagicAnimation() {
        if (playerImageView != null && playerMagicImg != null) {
            playerImageView.setImage(playerMagicImg);

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> playerImageView.setImage(playerRestImg));
            pause.play();
        }
    }

    private void triggerEnemyAttackAnimation() {
        if (enemyImageView != null && enemyAttackImg != null) {
            enemyImageView.setImage(enemyAttackImg);

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> enemyImageView.setImage(enemyRestImg));
            pause.play();
        }
    }

    private void triggerDamageVignette() {

        InnerShadow bloodBorder = new InnerShadow();
        bloodBorder.setColor(Color.DARKRED);
        bloodBorder.setRadius(120);
        bloodBorder.setChoke(0.3);


        if (playerHealthBar.getScene() != null) {
            playerHealthBar.getScene().getRoot().setEffect(bloodBorder);


            PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
            pause.setOnFinished(e -> playerHealthBar.getScene().getRoot().setEffect(null));
            pause.play();
        }
    }


    private void loadPlayerImages(int level) {
        try {
            if (level == 1) {

                playerRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-rest.png"));
                playerAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-attack.png"));

                playerMagicImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-magic.png"));
            } else if (level == 2) {

                playerRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-rest-lvl2.png"));
                playerAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-attack-lvl2.png"));
                playerMagicImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-magic-lvl2.png"));
            } else {

                playerRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-rest-lvl3.png"));
                playerAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-attack-lvl3.png"));
                playerMagicImg = new Image(getClass().getResourceAsStream("/com/example/final_project/player-magic-lvl3.png"));
            }


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

                enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest-lvl1.png"));
                enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack-lvl1.png"));
            } else if (level == 2) {

                enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest-lvl2.png"));
                enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack-lvl2.png"));
            } else {

                enemyRestImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-rest.png"));
                enemyAttackImg = new Image(getClass().getResourceAsStream("/com/example/final_project/enemy-attack.png"));
            }


            if (enemyImageView != null) {
                enemyImageView.setImage(enemyRestImg);
            }
        } catch (Exception e) {
            System.out.println("Warning: Could not load enemy images for Level " + level);
        }
    }
}