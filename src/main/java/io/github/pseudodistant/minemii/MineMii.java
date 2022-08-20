package io.github.pseudodistant.minemii;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MineMii implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("minemii");
	public static Identifier WeSpot = new Identifier("minemii", "wiisports");
	public static SoundEvent WiiSports = new SoundEvent(WeSpot);

	@Override
	public void onInitializeClient() {
		Registry.register(Registry.SOUND_EVENT, WeSpot, WiiSports);
		LOGGER.info("Hello Fabric world!");
	}
}
