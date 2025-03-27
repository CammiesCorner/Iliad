package dev.cammiescorner.iliad.api.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.cammiescorner.iliad.api.IliadRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// TODO add default interactions
public record IliadEntry(ItemStack icon, boolean isHidden, boolean knownByDefault, Set<ResourceLocation> parentIds, Holder<IliadTab> tab, int x, int y, List<IliadPage> pages) {
	public static final Codec<Holder<IliadEntry>> CODEC = RegistryFixedCodec.create(IliadRegistries.ENTRY);
	public static final Codec<IliadEntry> DIRECT_CODEC = RecordCodecBuilder.create(entryInstance -> entryInstance.group(
			ItemStack.CODEC.fieldOf("item_icon").forGetter(IliadEntry::icon),
			Codec.BOOL.optionalFieldOf("hidden", false).forGetter(IliadEntry::isHidden),
			Codec.BOOL.optionalFieldOf("known_by_default", false).forGetter(IliadEntry::knownByDefault), // TODO replace with conditions for unlocking
			ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("parents", Set.of()).forGetter(IliadEntry::parentIds),
			IliadTab.CODEC.fieldOf("tab").forGetter(IliadEntry::tab),
			Codec.INT.optionalFieldOf("posX", 0xf00f).forGetter(IliadEntry::x),
			Codec.INT.optionalFieldOf("posY", 0xf00f).forGetter(IliadEntry::y),
			IliadPage.CODEC.listOf().optionalFieldOf("pages", List.of()).forGetter(IliadEntry::pages)
	).apply(entryInstance, IliadEntry::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IliadEntry>> STREAM_CODEC = ByteBufCodecs.holderRegistry(IliadRegistries.ENTRY);

	public static IliadEntry get(ResourceLocation id, RegistryAccess access) {
		return access.registry(IliadRegistries.ENTRY).orElseThrow().get(id);
	}

	public ResourceLocation getId(RegistryAccess access) {
		return access.registry(IliadRegistries.ENTRY).orElseThrow().getKey(this);
	}

	public Set<IliadEntry> getParents(HolderLookup.Provider provider) {
		HolderLookup.RegistryLookup<IliadEntry> lookup = provider.lookupOrThrow(IliadRegistries.ENTRY);
		return parentIds.stream().map(id -> ResourceKey.create(IliadRegistries.ENTRY, id)).map(lookup::getOrThrow).map(Holder.Reference::value).collect(Collectors.toSet());
	}

	public static String getTranslationKey(Holder.Reference<IliadEntry> iliadEntry) {
		return Util.makeDescriptionId("iliad_entry", iliadEntry.key().location());
	}

	@Override
	public String toString() {
		return "IliadEntry[" +
				"icon=" + icon + ", " +
				"isHidden=" + isHidden + ", " +
				"knownByDefault=" + knownByDefault + ", " +
				"parentIds=" + parentIds + ", " +
				"tab=" + tab + ", " +
				"x=" + x + ", " +
				"y=" + y + ", " +
				"pages=" + pages + ']';
	}
}
