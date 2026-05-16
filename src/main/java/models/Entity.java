package models;

public abstract  class Entity {
    protected String name;
    protected int maxHealth;
    protected int currentHealth;
    protected int damage;

    public Entity(String name, int maxHealth, int damage) {
        this.name = name;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.damage = damage;
    }

    public void takeDamage(int amount) {
        currentHealth = Math.max(0, currentHealth - amount);
    }

    public void heal(int amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }

    public boolean isDead() {
        return currentHealth <= 0;
    }

    public String getName() {
        return name;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getDamage() {
        return damage;
    }
    public void setCurrentHealth(int h) {
        this.currentHealth = Math.max(0, Math.min(maxHealth, h));
    }
}
