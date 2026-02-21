"""
Скрипт для конвертации координат из Blockbench в формат SuperbWarfare
Blockbench использует координаты в пикселях (1 блок = 16 пикселей)
SuperbWarfare использует координаты в блоках с инвертированными осями X и Z
"""

def blockbench_to_game(x, y, z):
    """
    Конвертирует координаты из Blockbench в игровые координаты
    x, y, z - координаты в Blockbench (в пикселях)
    Возвращает: (x, y, z) в игровых координатах
    """
    return -x/16, y/16, -z/16

def relative_position(target_x, target_y, target_z, pivot_x, pivot_y, pivot_z):
    """
    Вычисляет относительную позицию target относительно pivot
    Все координаты в Blockbench (в пикселях)
    Возвращает: (x, y, z) относительные координаты в игровых единицах
    """
    rel_x = (target_x - pivot_x) / 16
    rel_y = (target_y - pivot_y) / 16
    rel_z = (target_z - pivot_z) / 16
    return -rel_x, rel_y, -rel_z

def size_from_blockbench(width, height, depth):
    """
    Конвертирует размеры из Blockbench в игровые размеры
    width, height, depth - размеры в Blockbench (в пикселях)
    Возвращает: (width, height, depth) в игровых единицах
    """
    return width/16, height/16, depth/16


# Координаты turret pivot в Blockbench
turret_pivot = (0, 31.0107, -9.4632)
barrel_pivot = (0, 48.1665, -26.1027)  # Координаты barrel pivot в Blockbench
target_pos = (-13.6662, 49.9302, -28.3568)

turret_game = blockbench_to_game(*turret_pivot)
print(f"   Turret в игре: {turret_game[0]:.6f}, {turret_game[1]:.6f}, {turret_game[2]:.6f}")
barrel_game = blockbench_to_game(*barrel_pivot)
print(f"   Barrel в игре: {barrel_game[0]:.6f}, {barrel_game[1]:.6f}, {barrel_game[2]:.6f}")
barrel_rel_turret = relative_position(*barrel_pivot, *turret_pivot)
print(f"   Barrel относительно Turret: {barrel_rel_turret[0]:.6f}, {barrel_rel_turret[1]:.6f}, {barrel_rel_turret[2]:.6f}")


# Относительная позиция от turret
relative_turret = relative_position(*target_pos, *turret_pivot)
print(f"   Относительно Turret: {relative_turret[0]:.6f}, {relative_turret[1]:.6f}, {relative_turret[2]:.6f}")

# Относительная позиция от barrel
relative_barrel = relative_position(*target_pos, *barrel_pivot)
print(f"   Относительно Barrel: {relative_barrel[0]:.6f}, {relative_barrel[1]:.6f}, {relative_barrel[2]:.6f}")

# Пример расчёта размеров
print(f"\n4. Пример расчёта размеров:")
bb_size = (4.5233, 4.5908, 94.34)  # Пример размеров в Blockbench
print(f"   Размер в Blockbench: {bb_size}")
game_size = size_from_blockbench(*bb_size)
print(f"   Размер в игре: {game_size[0]:.6f}, {game_size[1]:.6f}, {game_size[2]:.6f}")

print("\n" + "=" * 60)
