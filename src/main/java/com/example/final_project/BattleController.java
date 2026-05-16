package com.example.final_project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import com.example.final_project.models.BossEnemy;
import com.example.final_project.models.Enemy;
import com.example.final_project.models.Player;
import javafx.stage.Stage;

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
    private TextArea combatLog;

    // --- 2. The Actual Game Models ---
    private Player player;
    private Enemy currentEnemy; // We use 'Enemy' so it can be Normal or Boss!

    // --- 3. Initialization ---
    @FXML
    public void initialize() {
        // Instantiate Umair's classes!
        player = new Player("Umair");
        currentEnemy = new BossEnemy("Dark Knight", "Hellfire Strike");

        combatLog.setText("A wild " + currentEnemy.getName() + " appears!\nBattle Start!\n\n");
        updateUI();
    }

    // --- 4. Player Actions ---
    @FXML
    public void onAttackClick(ActionEvent event) {
        if (player.isDead() || currentEnemy.isDead()) return; // Stop if game is over

        // 1. Player attacks
        int damage = player.attack();
        currentEnemy.takeDamage(damage);
        combatLog.appendText(player.getName() + " strikes for " + damage + " damage!\n");

        // 2. Check Boss Phase Change
        if (currentEnemy instanceof BossEnemy) {
            BossEnemy boss = (BossEnemy) currentEnemy;
            if (boss.checkPhaseChange()) {
                combatLog.appendText("WARNING: " + boss.getName() + " entered Phase 2! Damage Increased!\n");
            }
        }

        checkGameState();
    }

    @FXML
    public void onDefendClick(ActionEvent event) {
        if (player.isDead() || currentEnemy.isDead()) return;

        player.defend(); // This sets defending to true AND heals for 5!
        combatLog.appendText(player.getName() + " raises their shield and recovers 5 HP.\n");

        enemyTurn(); // Enemy still gets to attack while you defend
    }

    @FXML
    public void onMagicClick(ActionEvent event) {
        if (player.isDead() || currentEnemy.isDead()) return;

        // NOTE: Umair hasn't added a magicAttack() to the Player class yet!
        // We will fake it for now by adding a flat +15 to a normal attack.
        int magicDamage = player.attack() + 15;
        currentEnemy.takeDamage(magicDamage);

        combatLog.appendText(player.getName() + " casts a spell for " + magicDamage + " damage!\n");
        checkGameState();
    }

    // --- 5. Game Loop Logic ---

    private void checkGameState() {
        updateUI();

        if (currentEnemy.isDead()) {
            combatLog.appendText("\nVICTORY! The " + currentEnemy.getName() + " is defeated!\n");
            player.gainExperience(currentEnemy.getReward());
            // TODO: Trigger Game Over Screen teleport here added succesfully
            switchToGameOverScreen(true, "You have successfully slain the " + currentEnemy.getName() + "!");
        } else {
            // If the enemy survived, it's their turn!
            enemyTurn();
        }
    }

    private void enemyTurn() {
        if (currentEnemy.isDead()) return;

        // Enemy calculates their attack
        int enemyRawDamage = currentEnemy.chooseAction();

        // Subtract player's defense (if they clicked defend)
        int actualDamage = Math.max(0, enemyRawDamage - player.getDefenseReduction());

        player.takeDamage(actualDamage);
        player.resetDefense(); // Defense only lasts for one hit!

        combatLog.appendText(currentEnemy.getName() + " hits for " + actualDamage + " damage!\n\n");
        updateUI();

        if (player.isDead()) {
            combatLog.appendText("\nDEFEAT! " + player.getName() + " has fallen...\n");
            // TODO: Trigger Game Over Screen teleport here added successfully
            switchToGameOverScreen(false, "You fell in battle against the " + currentEnemy.getName() + ".");
        }
    }

    // --- 6. UI Updater ---
    private void updateUI() {
        // Cast to double so it does decimal division (e.g., 0.5 for 50%)
        playerHealthBar.setProgress((double) player.getCurrentHealth() / player.getMaxHealth());
        enemyHealthBar.setProgress((double) currentEnemy.getCurrentHealth() / currentEnemy.getMaxHealth());

        playerHealthText.setText(player.getCurrentHealth() + " / " + player.getMaxHealth() + " HP");
        enemyHealthText.setText(currentEnemy.getCurrentHealth() + " / " + currentEnemy.getMaxHealth() + " HP");
    }

    private void switchToGameOverScreen(boolean playerWon, String massage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/final_project/game-over.fxml"));
            Parent root = loader.load();

            // Grab the controller of the game-over screen and pass the results to it!
            GameOverController gameOverController = loader.getController();
            gameOverController.setGameResult(playerWon, massage);

            Scene scene = new Scene(root, 1280, 720);
            scene.getStylesheets().add(getClass().getResource("/com/example/final_project/style.css").toExternalForm());

            // Grab the current window stage from any visible UI element (like the combat log)
            Stage stage = (Stage) combatLog.getScene().getWindow();
            stage.setScene(scene);

        } catch (IOException e) {
            System.out.println("Error loading game-over screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

