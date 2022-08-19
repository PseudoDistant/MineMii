package io.github.pseudodistant.minemii;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MineMii implements ClientModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("minemii");

	@Override
	public void onInitializeClient() {
		LOGGER.info("Hello Fabric world!");
	}
}
