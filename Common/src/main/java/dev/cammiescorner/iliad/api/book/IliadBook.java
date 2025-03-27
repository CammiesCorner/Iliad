package dev.cammiescorner.iliad.api.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.cammiescorner.iliad.Iliad;
import dev.cammiescorner.iliad.api.IliadRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Set;

public record IliadBook(Set<ResourceLocation> tabs, ResourceLocation frameTexture, ResourceLocation iconsTexture) {
	public static final Codec<Holder<IliadBook>> CODEC = RegistryFixedCodec.create(IliadRegistries.BOOK);
	public static final Codec<IliadBook> DIRECT_CODEC = RecordCodecBuilder.create(entryInstance -> entryInstance.group(
			ResourceLocation.CODEC.listOf().xmap(Set::copyOf, List::copyOf).optionalFieldOf("tabs", Set.of()).forGetter(IliadBook::tabs),
			ResourceLocation.CODEC.optionalFieldOf("frame_texture", Iliad.id("textures/gui/default_frame.png")).forGetter(IliadBook::frameTexture),
			ResourceLocation.CODEC.optionalFieldOf("icons_texture", Iliad.id("textures/gui/default_icons.png")).forGetter(IliadBook::iconsTexture)
	).apply(entryInstance, IliadBook::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IliadBook>> STREAM_CODEC = ByteBufCodecs.holderRegistry(IliadRegistries.BOOK);

	public static IliadBook get(ResourceLocation id, RegistryAccess access) {
		return access.registry(IliadRegistries.BOOK).orElseThrow().get(id);
	}

	public ResourceLocation getId(RegistryAccess access) {
		return access.registry(IliadRegistries.BOOK).orElseThrow().getKey(this);
	}


	public static String getTranslationKey(Holder.Reference<IliadBook> iliadBook) {
		return Util.makeDescriptionId("iliad_book", iliadBook.key().location());
	}
}
