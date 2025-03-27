package dev.cammiescorner.iliad.api.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.cammiescorner.iliad.api.IliadRegistries;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record IliadTab(ItemStack icon, int order, boolean isTop) {
	public static final Codec<Holder<IliadTab>> CODEC = RegistryFixedCodec.create(IliadRegistries.TAB);
	public static final Codec<IliadTab> DIRECT_CODEC = RecordCodecBuilder.create(tabInstance -> tabInstance.group(
			ItemStack.CODEC.fieldOf("item_icon").forGetter(IliadTab::icon),
			Codec.INT.optionalFieldOf("order", 1000).forGetter(IliadTab::order),
			Codec.BOOL.optionalFieldOf("is_top", true).forGetter(IliadTab::isTop)
	).apply(tabInstance, IliadTab::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, Holder<IliadTab>> STREAM_CODEC = ByteBufCodecs.holderRegistry(IliadRegistries.TAB);

	public static IliadTab get(ResourceLocation id, RegistryAccess access) {
		return access.registry(IliadRegistries.TAB).orElseThrow().get(id);
	}

	public ResourceLocation getId(RegistryAccess access) {
		return access.registry(IliadRegistries.TAB).orElseThrow().getKey(this);
	}


	public static String getTranslationKey(Holder.Reference<IliadTab> iliadTab) {
		return Util.makeDescriptionId("iliad_tab", iliadTab.key().location());
	}

	@Override
	public String toString() {
		return "IliadTab[" +
				"icon=" + icon + ", " +
				"order=" + order + ", " +
				"isTop=" + isTop + ']';
	}
}
