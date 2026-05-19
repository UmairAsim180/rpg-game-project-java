package com.example.final_project.models;

public class BossEnemy extends Enemy{
    private int phase;
    private String specialAbility;
    private int turnCounter;

    public BossEnemy(String name, String specialAbility){
        // Change 200 to 450!
        super(name, "Boss", 450, 13);
        this.phase = 1;
        this.specialAbility = specialAbility;
        this.turnCounter = 0;
    }

    public int heavyAttack() {
        int randomBonus = (int) (Math.random() * 10);
        return damage + randomBonus + (phase * 5);
    }
    public int specialAttack() {
        return (damage * 2) + (int)(Math.random() * 8);
    }
    @Override
    public int chooseAction(){
        turnCounter++;

        if (turnCounter % 3 == 0){
            return specialAttack();
        }
        return heavyAttack();
    }
    public boolean checkPhaseChange(){
        if (phase == 1 && currentHealth <= maxHealth / 2){
            changePhase();
            return true;
        }
        return false;
    }
    public void changePhase(){
        phase = 2;
        damage += 10;
    }
    public boolean isSpecialAttackTurn(){
        return (turnCounter + 1) % 3 == 0;
    }

    public int getPhase() {
        return phase;
    }

    public String getSpecialAbility() {
        return specialAbility;
    }

    public int getTurnCounter() {
        return turnCounter;
    }
    @Override
    public int getReward() {
        return 500; // Massive XP drop for the final boss!
    }
}
