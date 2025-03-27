package dev.cammiescorner.iliad.api;

import dev.cammiescorner.iliad.Iliad;
import dev.cammiescorner.iliad.api.book.IliadBook;
import dev.cammiescorner.iliad.api.book.IliadEntry;
import dev.cammiescorner.iliad.api.book.IliadTab;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

// TODO register entries
public class IliadRegistries {
	public static final ResourceKey<Registry<IliadBook>> BOOK = ResourceKey.createRegistryKey(Iliad.id("books"));
	public static final ResourceKey<Registry<IliadTab>> TAB = ResourceKey.createRegistryKey(Iliad.id("book_tabs"));
	public static final ResourceKey<Registry<IliadEntry>> ENTRY = ResourceKey.createRegistryKey(Iliad.id("book_entries"));
}
