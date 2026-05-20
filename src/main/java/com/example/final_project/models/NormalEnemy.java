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

        return 40 * spawnLevel;
    }
    public int getSpawnLevel() {
        return spawnLevel;
    }

}
