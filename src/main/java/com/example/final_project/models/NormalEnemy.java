package com.example.final_project.models;

public class NormalEnemy extends Enemy{
    private int spawnLevel;
    private int attackPower;

    public NormalEnemy(String name,int spawnLevel){
        super(name,"Normal",50+(spawnLevel*20),10+(spawnLevel*4),30+(spawnLevel*15));
        this.spawnLevel = spawnLevel;
        this.attackPower = 6 + (spawnLevel * 2);
    }

    public int basicAttack(){
        int randomBonus = (int)(Math.random() * 6);
        return attackPower + randomBonus;
    }
    @Override
    public int chooseAction(){
        return basicAttack();
    }
    public int getSpawnLevel() {
        return spawnLevel;
    }

}
