package com.example.final_project.models;

public abstract class Enemy extends Entity {
    protected String enemyType;


    public Enemy(String name, String enemyType, int maxHealth, int damage) {
        super(name, maxHealth, damage);
        this.enemyType = enemyType;

    }
    public abstract int chooseAction();


    public String getEnemyType() {
        return enemyType;
    }
}
