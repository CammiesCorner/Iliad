package dev.cammiescorner.iliad.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Set;

public class MainHelper {
	public static Set<ResourceLocation> getUnlockedEntryIds(Player player) {
		return Set.of(); // TODO set up entry stuff
	}

	public static void unlockEntry(ServerPlayer player, ResourceLocation location) {
		// TODO unlock entries for player
	}
}
