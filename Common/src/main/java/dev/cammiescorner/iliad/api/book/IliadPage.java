package dev.cammiescorner.iliad.api.book;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.cammiescorner.iliad.Iliad;
import net.minecraft.resources.ResourceLocation;

public record IliadPage(String text, ResourceLocation picture) {
	public static final ResourceLocation NO_PICTURE = Iliad.id("no_picture");
	public static final Codec<IliadPage> CODEC = RecordCodecBuilder.create(pageInstance -> pageInstance.group(
		Codec.STRING.optionalFieldOf("text_key", "").forGetter(IliadPage::text),
		ResourceLocation.CODEC.optionalFieldOf("picture_location", NO_PICTURE).forGetter(IliadPage::picture)
	).apply(pageInstance, IliadPage::new));

	@Override
	public ResourceLocation picture() {
		return picture.withPrefix("textures/iliad/book_pictures/").withSuffix(".png");
	}
}
