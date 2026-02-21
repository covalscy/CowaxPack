package com.cowax.cowaxpack.init;


import com.cowax.cowaxpack.CowaxPack;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class CowaxSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, CowaxPack.MODID);

    public static final RegistryObject<SoundEvent> ZENIT_2C6_FIRE_1P = REGISTRY.register("zenit_2c6_fire_1p", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("zenit_2c6_fire_1p")));
    public static final RegistryObject<SoundEvent> ZENIT_2C6_FIRE_3P = REGISTRY.register("zenit_2c6_fire_3p", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("zenit_2c6_fire_3p")));
    
    // Звуки выстрелов для переключения стволов
    public static final RegistryObject<SoundEvent> SHOOT_30M = REGISTRY.register("shoot_30m", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("shoot_30m")));
    public static final RegistryObject<SoundEvent> SHOOT2_30MM = REGISTRY.register("shoot2_30mm", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("shoot2_30mm")));
    
    // Parachute sounds
    public static final RegistryObject<SoundEvent> PARACHUTE_OPEN = REGISTRY.register("parachute_open", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("parachute_open")));
    public static final RegistryObject<SoundEvent> PARACHUTE_CLOSE = REGISTRY.register("parachute_close", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("parachute_close")));
}
