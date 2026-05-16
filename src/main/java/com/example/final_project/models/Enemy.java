package com.example.final_project.models;

public abstract class Enemy extends Entity {
    protected String enemyType;
    protected int rewardExperience;

    public Enemy(String name, String enemyType, int maxHealth, int damage, int rewardExp) {
        super(name, maxHealth, damage);
        this.enemyType = enemyType;
        this.rewardExperience = rewardExp;
    }
    public abstract int chooseAction();

    public int getReward() {
        return rewardExperience;
    }
    public String getEnemyType() {
        return enemyType;
    }
}
