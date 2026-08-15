package com.cowax.cowaxpack.init;

import com.cowax.cowaxpack.CowaxPack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class CowaxSounds {
    public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CowaxPack.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ZENIT_2C6_FIRE_1P = REGISTRY.register("zenit_2c6_fire_1p", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("zenit_2c6_fire_1p")));
    public static final DeferredHolder<SoundEvent, SoundEvent> ZENIT_2C6_FIRE_3P = REGISTRY.register("zenit_2c6_fire_3p", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("zenit_2c6_fire_3p")));
    
    // Звуки выстрелов для переключения стволов
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOOT_30M = REGISTRY.register("shoot_30m", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("shoot_30m")));
    public static final DeferredHolder<SoundEvent, SoundEvent> SHOOT2_30MM = REGISTRY.register("shoot2_30mm", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("shoot2_30mm")));
    
    // Parachute sounds
    public static final DeferredHolder<SoundEvent, SoundEvent> PARACHUTE_OPEN = REGISTRY.register("parachute_open", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("parachute_open")));
    public static final DeferredHolder<SoundEvent, SoundEvent> PARACHUTE_CLOSE = REGISTRY.register("parachute_close", () -> SoundEvent.createVariableRangeEvent(CowaxPack.loc("parachute_close")));
}
