package dev.cammiescorner.iliad.neoforge.server;

import dev.cammiescorner.iliad.Iliad;
import dev.cammiescorner.iliad.api.IliadRegistries;
import dev.cammiescorner.iliad.api.book.IliadBook;
import dev.cammiescorner.iliad.api.book.IliadEntry;
import dev.cammiescorner.iliad.api.book.IliadTab;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Iliad.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ServerEvents {
	@SubscribeEvent
	public static void registerDynamicRegistries(DataPackRegistryEvent.NewRegistry event) {
		event.dataPackRegistry(IliadRegistries.BOOK, IliadBook.DIRECT_CODEC, IliadBook.DIRECT_CODEC);
		event.dataPackRegistry(IliadRegistries.TAB, IliadTab.DIRECT_CODEC, IliadTab.DIRECT_CODEC);
		event.dataPackRegistry(IliadRegistries.ENTRY, IliadEntry.DIRECT_CODEC, IliadEntry.DIRECT_CODEC);
	}
}
