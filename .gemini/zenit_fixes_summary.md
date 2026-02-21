# Zenit_2C6Entity Compilation Fixes Summary

## Критические исправления:

### 1. Удалить дублирующиеся методы (Matrix4f версии):
- `getBarrelTransform(float)` на строках ~912-944 (оставить только Matrix4d версию)
- `getTurretTransform(float)` - если есть Matrix4f версия
- `getGunTransform(float)` - если есть Matrix4f версия

### 2. Исправить типы в методах трансформации:
- Все `Matrix4f` -> `Matrix4d`
- Все `Vector4f` -> `Vector4d`  
- Все `Vector3f` -> `Vector3d` (для OBB)
- Все `Quaternionf` -> `Quaterniond` (для OBB)

### 3. Удалить метод `getBarrelTransformFromTurret`:
- Заменить вызовы на `getBarrelTransform`

### 4. Исправить поля ввода:
- `forwardInputDown` -> `forwardInputDown()`
- И аналогично для других

### 5. Закомментировать `releaseSmokeDecoy` полностью:
- Весь код внутри метода в комментарий

### 6. Удалить `@Override` где методы не переопределяются

### 7. Исправить кастинг double->float:
- `new Vector3f((float)worldPosition.x, (float)worldPosition.y, (float)worldPosition.z)`
