package dev.cammiescorner.iliad.fabric.entrypoints;

import dev.cammiescorner.iliad.api.IliadRegistries;
import dev.cammiescorner.iliad.api.book.IliadBook;
import dev.cammiescorner.iliad.api.book.IliadEntry;
import dev.cammiescorner.iliad.api.book.IliadTab;
import dev.upcraft.sparkweave.api.annotation.CalledByReflection;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;

@CalledByReflection
public class FabricMain implements ModInitializer {
	@Override
	public void onInitialize() {
		DynamicRegistries.registerSynced(IliadRegistries.BOOK, IliadBook.DIRECT_CODEC);
		DynamicRegistries.registerSynced(IliadRegistries.TAB, IliadTab.DIRECT_CODEC);
		DynamicRegistries.registerSynced(IliadRegistries.ENTRY, IliadEntry.DIRECT_CODEC);
	}
}
