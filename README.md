# 🛡️ CowaxPack

[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1%20%7C%201.20.1-brightgreen.svg)](https://minecraft.net/)
[![NeoForge / Forge](https://img.shields.io/badge/Mod%20Loader-NeoForge%20%7C%20Forge-orange.svg)](https://neoforged.net/)
[![Java](https://img.shields.io/badge/Java-21%20%7C%2017-blue.svg)](https://adoptium.net/)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)
[![SuperbWarfare](https://img.shields.io/badge/Addon%20for-SuperbWarfare-red.svg)](https://github.com/Mercurows/SuperbWarfare)

**CowaxPack** is a specialized military vehicle addon for the **Superb Warfare** mod in Minecraft (supporting NeoForge 1.21.1 and Forge 1.20.1). The addon introduces authentic heavy armor and air defense vehicles featuring realistic damage mechanics, custom GeckoLib animations, immersive soundscapes, and balanced combat performance.

---

## 🚀 Featured Vehicles & Content

### 🚜 Armored Combat Vehicles

* **💥 FV4005 Stage II (Heavy Tank Destroyer)**:
  * British heavy siege tank destroyer equipped with the colossal **183mm QF L4 Gun**.
  * **Devastating Firepower:** Huge direct impact and high-explosive blast damage covering an **18-block explosion radius**.
  * **Tactical Balance:** Heavy ballistic trajectory, slow turret traverse rate, and a 35-second reload cycle tailored for high-risk, high-reward tactical engagements.
  * **Armor & Protection:** Multi-segment Oriented Bounding Box (OBB) hitboxes with angle-based armor penetration mechanics.

* **🎯 2S6 Tunguska (Self-Propelled Anti-Aircraft System)**:
  * Modern tracked anti-aircraft weapon system engineered for airspace dominance and light armor suppression.
  * **Twin 30mm 2A38 Auto-Cannons:** High-velocity automatic cannon fire (1050 RPM) designed to counter aircraft and penetrate enemy tank side and rear armor plates.
  * **9M336 Anti-Air Missiles:** 8 dedicated surface-to-air guided missiles with integrated radar targeting (engages airborne targets at altitude $\ge$ 16 blocks).
  * **Mobility & Optics:** Agile tracked chassis, dynamic first-person boresight optic zooming, and custom audio effects.

---

### 🪖 Items & Equipment

| Item | ID | Description |
| :--- | :--- | :--- |
| **Tactical Parachute** | `cowaxpack:parachute` | Emergency vehicle bail-out and high-altitude jump equipment providing fall damage immunity (Curios slot / inventory). |

---

## 🛠️ Developer Tools & Documentation

* **[`calc.py`](calc.py)** — Python utility script for converting Blockbench rotation pivots and cube coordinates into Superb Warfare vehicle JSON configurations.
* **[Coordinate Calculation Guide (`docs/COORDINATES_AND_CALC_GUIDE.md`)](docs/COORDINATES_AND_CALC_GUIDE.md)** — Comprehensive guide on calculating turret pivots, barrel boresight positions, particle emitter vectors, and OBB hitboxes.

---

## 📦 Building & Installation

### Requirements:
* **Minecraft:** `1.21.1` (NeoForge) or `1.20.1` (Forge)
* **Java Development Kit (JDK):** `21` (for 1.21.1) or `17` (for 1.20.1)
* **Dependencies:** [Superb Warfare](https://www.curseforge.com/minecraft/mc-mods/superb-warfare), [GeckoLib](https://www.curseforge.com/minecraft/mc-mods/geckolib), [Curios API](https://www.curseforge.com/minecraft/mc-mods/curios)

### Building from Source:

1. Clone the repository:
   ```bash
   git clone https://github.com/covalscy/CowaxPack.git
   cd CowaxPack
   ```
2. Build the project using Gradle:
   ```bash
   # Windows
   .\gradlew.bat build

   # Linux / macOS
   ./gradlew build
   ```
3. The compiled `.jar` file will be generated in `build/libs/`. Place it into your `.minecraft/mods` directory.

---

## 📂 Project Structure

```
CowaxPack/
├── .gitignore
├── README.md                      # Project documentation
├── calc.py                        # Blockbench -> Superb Warfare coordinate converter
├── docs/
│   └── COORDINATES_AND_CALC_GUIDE.md  # Pivot and transformation documentation
├── SuperbWarfare-0.8.9-1.20/     # Superb Warfare 1.20 source reference
├── SuperbWarfare-0.8.9-1.21/     # Superb Warfare 1.21 source reference
└── src/main/
    ├── java/com/cowax/cowaxpack/  # Java mod source code
    │   ├── client/                # HUD overlays, model renderers
    │   ├── entity/                # Vehicle and projectile entity logic
    │   ├── init/                  # Entity, item, sound and tab registries
    │   └── item/                  # Equipment and item implementations
    └── resources/
        ├── assets/cowaxpack/      # 3D models, textures, animations, lang files
        └── data/cowaxpack/        # Vehicle JSON configurations and data
```

---

## 👥 Authors & Credits

* **Covalscy** — Development, 3D modeling, animations, and mod integration.
* Base Framework: **Mercurows & Superb Warfare Team**.

---

## 📜 License

* **Source Code:** Released under the **[GNU General Public License v3.0 (GPL-3.0)](LICENSE)** in accordance with the base [Superb Warfare](https://github.com/Mercurows/SuperbWarfare) licensing terms. You are free to study, modify, and distribute this codebase under GPL-3.0 copyleft conditions with author attribution.
* **Assets (Models, Textures, Sounds):** Custom 3D models, animations, textures, and sound designs (FV4005, 2S6 Tunguska, CowaxPack assets) are the property of **Covalscy**.
* **Base Mod (Superb Warfare):** All rights to the original Superb Warfare mod, its API, and original assets belong to **Atsuishio**, **Mercurows**, and the **Superb Warfare Team**.
