package com.example.final_project.models;

public class Player extends Entity {
    private int level;
    private int experience; // Put XP back!
    private int defenseBonus;
    private boolean isDefending;

    public Player(String name){
        super(name, 120, 18);
        this.level = 1;
        this.experience = 0; // Start at 0 XP
        this.defenseBonus = 10;
        this.isDefending = false;
    }

    public int attack(){
        isDefending = false;
        int randomBonus = (int)(Math.random() * 10);
        return damage + randomBonus + (level * 2);
    }

    public int magicAttack() {
        isDefending = false;
        int baseMagic = damage + 15;
        int levelBonus = level * 5;
        int randomBonus = (int)(Math.random() * 12);
        return baseMagic + levelBonus + randomBonus;
    }

    public void defend(){
        isDefending = true;
        this.heal(5);
    }

    // --- MUST HAVE: The XP System so you can actually beat the boss! ---
    public void gainExperience(int exp) {
        experience += exp;
        if (experience >= getExpNeeded()) {
            levelUp();
        }
    }

    public void levelUp() {
        level++;
        maxHealth += 25;
        currentHealth = maxHealth; // Heal to full on level up
        damage += 7;
        defenseBonus += 5;
        experience = 0; // Reset XP for the next level
    }

    public int getExpNeeded() {
        return level * 60;
    }
    // ------------------------------------------------------------------

    public int getDefenseReduction() {
        return isDefending ? defenseBonus : 0;
    }

    public void resetDefense() {
        isDefending = false;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }

    public boolean isDefending() {
        return isDefending;
    }
}