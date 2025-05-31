package net.mednours.letsgogambling;

import net.fabricmc.api.ModInitializer;

import net.mednours.letsgogambling.item.ModItemGroups;
import net.mednours.letsgogambling.item.ModItems;
import net.mednours.letsgogambling.sounds.ModSounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LetsGoGambling implements ModInitializer {
	public static final String MOD_ID = "lets-go-gambling";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModSounds.registerSounds();
	}
}