package dev.cammiescorner.iliad;

import dev.cammiescorner.iliad.api.IliadRegistries;
import dev.cammiescorner.iliad.api.book.IliadEntry;
import dev.cammiescorner.iliad.common.MainHelper;
import dev.upcraft.sparkweave.api.entrypoint.MainEntryPoint;
import dev.upcraft.sparkweave.api.event.EntityTickEvents;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class Iliad implements MainEntryPoint {
	public static final String MOD_ID = "iliad";

	@Override
	public void onInitialize(ModContainer mod) {
		// TODO register item data for books

		EntityTickEvents.endTick(ServerPlayer.class).register((player, level) -> {
			RegistryAccess access = level.registryAccess();
			Registry<IliadEntry> registry = access.registryOrThrow(IliadRegistries.ENTRY);

			if(level instanceof ServerLevel serverLevel) {
				for(ResourceLocation location : registry.keySet()) {
					IliadEntry entry = registry.get(location);
					boolean unlockedEntry = MainHelper.getUnlockedEntryIds(player).contains(location);
					boolean hasRequirements = entry.requiredAdvancements().stream().allMatch(loc -> {
						AdvancementHolder advancementHolder = serverLevel.getServer().getAdvancements().get(loc);
						return advancementHolder != null && player.getAdvancements().getOrStartProgress(advancementHolder).isDone();
					});

					if(entry.requiredAdvancements().isEmpty() || (hasRequirements && !unlockedEntry))
						MainHelper.unlockEntry(player, location);
				}
			}
		});
	}

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}
