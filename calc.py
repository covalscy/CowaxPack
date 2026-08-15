# -*- coding: utf-8 -*-
"""
Калькулятор координат и точек вращения (Pivot Points) для SuperbWarfare / CowaxPack.
Подробная документация: docs/COORDINATES_AND_CALC_GUIDE.md

Правила преобразования:
- Blockbench использует пиксели (1 блок Minecraft = 16 пикселей).
- В SuperbWarfare оси X и Z инвертированы по сравнению с Blockbench.
- Ось Y сохраняет направление.
"""

import sys

# Обеспечиваем корректный вывод UTF-8 на Windows-консоли
if sys.platform == "win32":
    sys.stdout.reconfigure(encoding="utf-8")


def blockbench_to_game(x: float, y: float, z: float):
    """
    Конвертирует абсолютные координаты из Blockbench в игровые единицы (блоки).
    (x, y, z) в пикселях -> (-x/16, y/16, -z/16) в блоках.
    """
    return -x / 16.0, y / 16.0, -z / 16.0


def relative_position(target_x: float, target_y: float, target_z: float,
                      pivot_x: float, pivot_y: float, pivot_z: float):
    """
    Вычисляет локальное смещение точки target относительно центра вращения pivot.
    Все входные координаты задаются в пикселях Blockbench.
    """
    rel_x = (target_x - pivot_x) / 16.0
    rel_y = (target_y - pivot_y) / 16.0
    rel_z = (target_z - pivot_z) / 16.0
    return -rel_x, rel_y, -rel_z


def size_from_blockbench(width: float, height: float, depth: float):
    """
    Конвертирует габаритные размеры хитбокса (OBB) из пикселей в блоки.
    """
    return width / 16.0, height / 16.0, depth / 16.0


def format_vec(vec):
    return f"[{vec[0]:.6f}, {vec[1]:.6f}, {vec[2]:.6f}]"


def run_example():
    print("=" * 60)
    print("  SuperbWarfare / CowaxPack Coordinate Calculator")
    print("=" * 60)

    # 1. Задайте координаты из Blockbench (в пикселях)
    turret_pivot = (0.0, 31.0107, -9.4632)      # Пивот башни в модели
    barrel_pivot = (0.0, 48.1665, -26.1027)     # Пивот орудия в модели
    target_pos = (-13.6662, 49.9302, -28.3568)  # Точка выстрела / спавна снаряда
    bb_size = (4.5233, 4.5908, 94.34)          # Размеры хитбокса (OBB)

    # 2. Расчет глобальных координат
    turret_game = blockbench_to_game(*turret_pivot)
    barrel_game = blockbench_to_game(*barrel_pivot)

    # 3. Расчет относительных координат
    barrel_rel_turret = relative_position(*barrel_pivot, *turret_pivot)
    relative_turret = relative_position(*target_pos, *turret_pivot)
    relative_barrel = relative_position(*target_pos, *barrel_pivot)

    # 4. Расчет размеров
    game_size = size_from_blockbench(*bb_size)

    print("\n[1] Базовые пивоты в игре:")
    print(f"    Turret Pivot (в игре) : {format_vec(turret_game)}")
    print(f"    Barrel Pivot (в игре) : {format_vec(barrel_game)}")

    print("\n[2] Относительные смещения (для JSON конфига):")
    print(f"    Barrel относительно Turret : {format_vec(barrel_rel_turret)}")
    print(f"    Target относительно Turret : {format_vec(relative_turret)}")
    print(f"    Target относительно Barrel : {format_vec(relative_barrel)}")

    print("\n[3] Размеры OBB / хитбокса:")
    print(f"    Blockbench size : {bb_size}")
    print(f"    Game size       : {format_vec(game_size)}")
    print("=" * 60)


if __name__ == "__main__":
    run_example()
