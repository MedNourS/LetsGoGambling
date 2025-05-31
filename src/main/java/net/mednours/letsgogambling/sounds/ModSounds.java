package net.mednours.letsgogambling.sounds;

import net.mednours.letsgogambling.LetsGoGambling;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    // Coin hold
    public static final Identifier COIN_HOLD = Identifier.of("lets-go-gambling:coin_hold");
    public static SoundEvent COIN_HOLD_EVENT = SoundEvent.of(COIN_HOLD);

    // Coin success 1
    public static final Identifier COIN_SUCCESS_1 = Identifier.of("lets-go-gambling:coin_success1");
    public static SoundEvent COIN_SUCCESS_1_EVENT = SoundEvent.of(COIN_SUCCESS_1);

    // Coin success 2
    public static final Identifier COIN_SUCCESS_2 = Identifier.of("lets-go-gambling:coin_success2");
    public static SoundEvent COIN_SUCCESS_2_EVENT = SoundEvent.of(COIN_SUCCESS_2);

    // Coin failure
    public static final Identifier COIN_FAIL = Identifier.of("lets-go-gambling:coin_fail");
    public static SoundEvent COIN_FAIL_EVENT = SoundEvent.of(COIN_FAIL);

    public static void registerSounds() {
        LetsGoGambling.LOGGER.info("Registering Mod Sounds for " + LetsGoGambling.MOD_ID);

        Registry.register(Registries.SOUND_EVENT, COIN_HOLD, COIN_HOLD_EVENT);
        Registry.register(Registries.SOUND_EVENT, COIN_SUCCESS_1, COIN_SUCCESS_1_EVENT);
        Registry.register(Registries.SOUND_EVENT, COIN_SUCCESS_2, COIN_SUCCESS_2_EVENT);
        Registry.register(Registries.SOUND_EVENT, COIN_FAIL, COIN_FAIL_EVENT);
    }
}
