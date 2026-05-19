package com.example.final_project.models;

public class NormalEnemy extends Enemy{
    private int spawnLevel;

    public NormalEnemy(String name,int spawnLevel){
        super(name,"Normal",50+(spawnLevel*20),6 + (spawnLevel * 2));
        this.spawnLevel = spawnLevel;
    }

    public int basicAttack(){
        int randomBonus = (int)(Math.random() * 6);
        return getDamage() + randomBonus;
    }
    @Override
    public int chooseAction(){
        return basicAttack();
    }
    @Override
    public int getReward() {
        // Level 1 enemies give 40 XP. Level 2 enemies give 80 XP.
        return 40 * spawnLevel;
    }
    public int getSpawnLevel() {
        return spawnLevel;
    }

}
