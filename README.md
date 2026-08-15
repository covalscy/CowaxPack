# 🛡️ CowaxPack

[![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://minecraft.net/)
[![Forge](https://img.shields.io/badge/Forge-47.2.0+-orange.svg)](https://files.minecraftforge.net/)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://adoptium.net/)
[![SuperbWarfare](https://img.shields.io/badge/Addon%20for-SuperbWarfare-red.svg)](https://github.com/Mercurows/SuperbWarfare)

**CowaxPack** — специализированный военный аддон для модификации **SuperbWarfare** на базе Minecraft Forge 1.20.1. Мод добавляет тяжелую технику (ЗПРК 2С6 «Зенитка», бронемашины серии FV), кастомные баллистические снаряды, тактическое снаряжение (IFF, парашюты, аптечки) и продвинутый HUD.

---

## 🚀 Основной контент и возможности

### 🚜 Военная техника (Vehicles)

* **ЗПРК 2С6 «Зенитка» (`zenit_2c6`)**:
  * Зенитный пушечно-ракетный комплекс для эффективной борьбы с воздушными и легкобронированными наземными целями.
  * **Вооружение:** Спаренные скорострельные автопушки с зенитными снарядами (`ZenitCannonShellEntity`) с разрывным уроном и возможностью дистанционного подрыва / осколочного действия.
  * **Системы:** Поворотная башня с независимым наведением блока орудий по вертикали, реалистичная физика, система перегрева/перезарядки и звуковое сопровождение (`CowaxSounds`).
* **Боевая машина FV (`fv`)**:
  * Тяжелая бронемашина с многосегментной системой коллизий (Oriented Bounding Box — OBB) для реалистичного расчета попаданий в броню.

---

### 🪖 Снаряжение и предметы (Items & Equipment)

| Предмет | ID | Описание |
| :--- | :--- | :--- |
| **Система IFF (Свой-Чужой)** | `cowaxpack:iff` | Модуль опознавания целей. Устанавливается в слот Curios или носится в инвентаре, проецирует статус целей прямо на экран / HUD. |
| **Тактический парашют** | `cowaxpack:parachute` | Экипировка для экстренного десантирования из техники и высотных прыжков без получения урона от падения. |
| **Полевая аптечка** | `cowaxpack:medical_kit` | Набор для быстрого восстановления здоровья экипажа в боевых условиях. |
| **Артиллерийский индикатор** | `cowaxpack:artillery_indicator` | Система расчета баллистических траекторий и целеуказания с наложением маркера на HUD. |

---

### 🖥️ Интерфейс и HUD (Combat Overlays)

* **CowaxVehicleHudOverlay / ZenitHudOverlay**: Кастомные прицельные сетки, шкалы боекомплекта, индикаторы наклона корпуса и угла возвышения орудия.
* **IFF Overlay**: Визуальные метки распознавания дружественных и вражеских объектов в реальном времени.

---

## 🛠️ Инструменты разработчика

* **[`calc.py`](calc.py)** — утилита для быстрого пересчёта координат и точек вращения (Pivot Points) из редактора **Blockbench** в формат JSON-конфигураций SuperbWarfare.
* **[Документация по математике и координатам (`docs/COORDINATES_AND_CALC_GUIDE.md`)](docs/COORDINATES_AND_CALC_GUIDE.md)** — подробное руководство по расчёту пивотов башен, орудий, точек вылета снарядов и OBB-хитбоксов.
* **Исходники SuperbWarfare**: В репозиторий включены подмодули с исходным кодом базового мода (`SuperbWarfare-0.8.9-1.20` и `SuperbWarfare-0.8.9-1.21`) для сверки API и синхронизации механик.

---

## 📦 Сборка и установка

### Системные требования:
* **Minecraft:** `1.20.1`
* **Forge:** `47.2.0` или новее
* **Java Development Kit (JDK):** `17`
* **Зависимости:** [SuperbWarfare](https://github.com/Mercurows/SuperbWarfare), [GeckoLib](https://curseforge.com/minecraft/mc-mods/geckolib), [Curios API](https://curseforge.com/minecraft/mc-mods/curios)

### Сборка из исходников:

1. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/CowaxMC/CowaxPack.git
   cd CowaxPack
   ```
2. Скомпилируйте мод через Gradle:
   ```bash
   # Windows
   gradlew.bat build

   # Linux / macOS
   ./gradlew build
   ```
3. Готовый файл `.jar` будет находиться в папке `build/libs/`. Поместите его в папку `.minecraft/mods`.

> [!TIP]
> Для быстрой локальной сборки и автокопирования в тестовый клиент можно использовать скрипт [`build_and_copy.bat`](build_and_copy.bat).

---

## 📂 Структура проекта

```
CowaxPack/
├── .gitignore
├── README.md                      # Главное описание проекта
├── calc.py                        # Калькулятор координат Blockbench -> SuperbWarfare
├── build_and_copy.bat             # Скрипт сборки и деплоя в тестовый каталог
├── docs/
│   └── COORDINATES_AND_CALC_GUIDE.md  # Руководство по пивотам и трансформациям
├── SuperbWarfare-0.8.9-1.20/     # Исходный код SuperbWarfare (1.20)
├── SuperbWarfare-0.8.9-1.21/     # Исходный код SuperbWarfare (1.21)
└── src/main/
    ├── java/com/cowax/cowaxpack/  # Исходный Java-код мода
    │   ├── client/                # HUD оверлеи, рендереры моделей
    │   ├── entity/                # Сущности техники и снарядов
    │   ├── init/                  # Регистрация предметов, сущностей, звуков
    │   ├── item/                  # Логика предметов (IFF, парашют, аптечка)
    │   └── mixin/                 # Миксины в SuperbWarfare
    └── resources/
        ├── assets/cowaxpack/      # Текстуры, модели, анимации, локализации
        └── data/cowaxpack/        # Конфигурации техники (.json) и рецепты
```

---

## 👥 Авторы и контрибьюторы

* **Cowax / Covalscy** — разработка, моделирование и интеграция.
* Базовый фреймворк: **Mercurows & SuperbWarfare Team**.

---

## 📜 Лицензия

Все права защищены (All Rights Reserved) © Cowax / CowaxMC.
