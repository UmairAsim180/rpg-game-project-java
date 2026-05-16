package models;

public class Player extends Entity {
    private int level;
    private int experience;
    private int defenseBonus;
    private boolean isDefending;

    public Player(String name){
        super(name,120,18);
        this.level=1;
        this.experience=0;
        this.defenseBonus=10;
        this.isDefending=false;
    }

    public int attack(){
        isDefending=false;
        int randomBonus=(int)(Math.random()*10);
        return damage+randomBonus+(level*2);
    }

    public void defend(){
        isDefending=true;
        this.heal(5);
    }
    public void gainExperience(int exp) {
        experience += exp;
        int expNeeded = getExpNeeded();

        if (experience >= expNeeded) {
            levelUp();
        }
    }

    public void levelUp() {
        level++;
        maxHealth += 25;
        currentHealth = maxHealth;
        damage += 7;
        defenseBonus += 5;
        experience = 0;
    }
    public int getExpNeeded() {
        return level * 60;
    }
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
