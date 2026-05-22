# ⚔️ Path of the Silent Sword
### A Turn-Based RPG — Java OOP Final Project (BSSE Semester 2)

---

## 📖 About the Game

**Path of the Silent Sword** is a JavaFX-based turn-based RPG where you name your hero, fight through escalating enemies across 3 levels, and face a powerful boss in a climactic final battle. The game features sprite animations, sound effects, a leveling system, and a polished dark-themed UI.

---

## 🎮 Gameplay Overview

- Enter your hero's name on the start screen to begin.
- Battle enemies in **turn-based combat** — you act first, then the enemy responds.
- Defeat **2 enemies per level** to progress to the next stage.
- Beat all 3 levels to win the game.

### Combat Actions

| Key | Button | Action | Effect |
|-----|--------|--------|--------|
| `A` / `1` | Attack | Physical Strike | Deals `damage + random(0–9) + (level × 2)` |
| `D` / `2` | Defend | Guard Stance | Blocks enemy damage this turn; heals +5 HP |
| `M` / `3` | Magic | Magic Attack | Deals higher damage; **3-turn cooldown** |

> Actions can be triggered with keyboard shortcuts or on-screen buttons.

---

## 🗺️ Level Structure

| Level | Enemy | Type | HP | Notes |
|-------|-------|------|----|-------|
| 1 | Goblin Scout | Normal | 70 HP | Basic attacker |
| 1 | Goblin Scout | Normal | 70 HP | Second fight |
| 2 | Orc Warrior | Normal | 90 HP | Hits harder |
| 2 | Orc Warrior | Normal | 90 HP | Second fight |
| 3 | Dark Knight | **BOSS** | 450 HP | Two-phase boss |

Between levels, the player is **healed for +40 HP**. The player sprite also visually upgrades each level.

---

## 👾 Enemy AI

### Normal Enemy
- Uses `basicAttack()` every turn: `damage + random(0–5)`.
- Stats scale with spawn level.
- Rewards: `40 × spawnLevel` XP on death.

### Boss Enemy — Dark Knight *(Hellfire Strike)*
- Alternates between **Heavy Attack** and **Special Attack** every 3rd turn.
- **Phase 2** triggers when HP drops below 50% — damage permanently increases by +10.
- A "Enemy Damage Increased!" warning flashes on screen when Phase 2 activates.
- Reward: **500 XP** on defeat.

---

## 🧙 Player Stats & Leveling

**Starting Stats:** 120 HP | 18 Base Damage | 10 Defense Bonus

| On Level Up | Increase |
|-------------|----------|
| Max HP | +25 (fully restored) |
| Base Damage | +7 |
| Defense Bonus | +5 |
| XP needed | `currentLevel × 60` |

Leveling up is triggered automatically when enough XP is earned from defeating enemies.

---

## 🏗️ Project Structure

```
src/
└── main/
    ├── java/
    │   ├── module-info.java
    │   └── com/example/final_project/
    │       ├── HelloApplication.java          # JavaFX entry point
    │       ├── Launcher.java                  # Main launcher
    │       ├── StartScreenController.java     # Name entry & navigation
    │       ├── BattleController.java          # Core game logic & UI
    │       ├── GameOverController.java        # Victory/Defeat screen
    │       ├── HowToPlayController.java       # Rules screen
    │       └── models/
    │           ├── Entity.java                # Abstract base class
    │           ├── Player.java                # Player with leveling & combat
    │           ├── Enemy.java                 # Abstract enemy base
    │           ├── NormalEnemy.java           # Scalable normal enemy
    │           └── BossEnemy.java             # Two-phase boss
    └── resources/
        └── com/example/final_project/
            ├── start_screen.fxml
            ├── main-game.fxml
            ├── game-over.fxml
            ├── rules.fxml
            └── style.css
```

---

## 🧱 OOP Concepts Demonstrated

| Concept | Where Used |
|---------|------------|
| **Abstract Classes** | `Entity` (base for all characters), `Enemy` (base for all enemies) |
| **Inheritance** | `Player → Entity`, `NormalEnemy → Enemy → Entity`, `BossEnemy → Enemy → Entity` |
| **Polymorphism** | `chooseAction()` overridden differently in `NormalEnemy` and `BossEnemy` |
| **Encapsulation** | Private fields with getters/setters across all model classes |
| **Composition** | `BattleController` composes `Player` and `Enemy` objects |
| **Method Overriding** | `chooseAction()`, `getReward()` — both declared abstract in `Enemy`, implemented in subclasses |

---

## 🛠️ Tech Stack

- **Language:** Java 17+
- **UI Framework:** JavaFX (FXML + CSS)
- **Audio:** `javafx.scene.media.AudioClip`
- **Build Tool:** Maven (implied by package structure)
- **IDE:** IntelliJ IDEA (recommended)

---

## ▶️ How to Run

### Prerequisites
- Java 17 or higher
- JavaFX SDK (if not bundled via Maven)
- IntelliJ IDEA (recommended) or any Java IDE

### Steps

1. **Clone or unzip** the project.
2. Open in **IntelliJ IDEA** as a Maven project.
3. Make sure JavaFX is configured in your SDK/module path.
4. Run `Launcher.java` (or `HelloApplication.java`) as the main class.

> The window launches at a fixed resolution of **1280 × 720**.

---

## 📸 Screens

| Screen | Description |
|--------|-------------|
| **Start Screen** | Enter your hero's name; access "How to Play" or Exit |
| **Battle Screen** | Main gameplay — health bars, XP tracker, combat log, sprite animations |
| **How to Play** | Rules and controls reference |
| **Game Over** | Shows **VICTORY** (gold) or **DEFEAT** (red) with a result message |

---

## ✨ Extra Features

- 🎵 **Sound Effects** — hit, shield, magic, and enemy-hit sounds
- 💥 **Damage Vignette** — dark red screen flash when the player takes damage
- 🖼️ **Sprite Animations** — player and enemy sprites swap on attack/magic actions
- 🎨 **Sprite Progression** — player sprite visually changes per level
- ⌨️ **Keyboard Controls** — `A`, `D`, `M` (or `1`, `2`, `3`) for quick input
- ✅ **Input Validation** — can't start without entering a name

---

## 👨‍💻 Author

#### **Muhammad Umair**
BSSE — Semester 2
COMSATS University Islamabad, Lahore Campus

#### **Umair Rasheed**
BSSE — Semester 2
COMSATS University Islamabad, Lahore Campus
