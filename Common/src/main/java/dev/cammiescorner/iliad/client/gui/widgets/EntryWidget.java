package dev.cammiescorner.iliad.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.cammiescorner.iliad.api.book.IliadBook;
import dev.cammiescorner.iliad.api.book.IliadEntry;
import dev.cammiescorner.iliad.api.IliadRegistries;
import dev.cammiescorner.iliad.client.gui.screens.IliadBookScreen;
import dev.cammiescorner.iliad.common.MainHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Set;

public class EntryWidget extends AbstractButton {
	private final RegistryAccess access = Minecraft.getInstance().level.registryAccess();
	private final Holder.Reference<IliadEntry> iliadEntry;
	private final IliadBook book;
	private final OnPress onPress;
	private float offsetX, offsetY;

	public EntryWidget(int x, int y, ResourceLocation entryId, IliadBook book, OnPress onPress) {
		super(x, y, 30, 30, Component.empty());
		this.iliadEntry = access.lookupOrThrow(IliadRegistries.ENTRY).getOrThrow(ResourceKey.create(IliadRegistries.ENTRY, entryId));
		this.book = book;
		this.onPress = onPress;

		Set<ResourceLocation> playerEntries = MainHelper.getUnlockedEntryIds(Minecraft.getInstance().player);

		if(playerEntries.contains(iliadEntry.key().location())) {
			active = true;
			visible = true;
		}
		else if(playerEntries.containsAll(iliadEntry.value().parentIds())) {
			active = true;
			visible = true;
		}
		else {
			active = false;

			if(iliadEntry.value().isHidden() || iliadEntry.value().getParents(access).stream().anyMatch(IliadEntry::isHidden))
				visible = false;
		}
	}

	@Override
	protected boolean clicked(double mouseX, double mouseY) {
		return active && visible && isHovered;
	}

	@Override
	public boolean isMouseOver(double mouseX, double mouseY) {
		return active && visible && isHovered;
	}

	@Override
	public void onPress() {
		onPress.onPress(this);
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		Set<ResourceLocation> playerEntries = MainHelper.getUnlockedEntryIds(Minecraft.getInstance().player);

		if(iliadEntry != null) {
			if(playerEntries.contains(iliadEntry.key().location()))
				visible = true;
			else if(playerEntries.containsAll(iliadEntry.value().parentIds()))
				visible = true;
			else if(iliadEntry.value().isHidden() || iliadEntry.value().getParents(access).stream().anyMatch(IliadEntry::isHidden))
				visible = false;

			if(visible) {
				isHovered = mouseX >= getX() + offsetX && mouseY >= getY() + offsetY &&
					mouseX < getX() + offsetX + width && mouseY < getY() + offsetY + height;
				renderButton(guiGraphics, playerEntries);
			}
		}
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		defaultButtonNarrationText(narrationElementOutput);
	}

	public void renderButton(GuiGraphics guiGraphics, Set<ResourceLocation> playerEntries) {
		ItemStack stack = iliadEntry.value().icon();
		int u;

		if(playerEntries.contains(iliadEntry.key().location())) {
			u = 60;
			active = true;
		}
		else if(playerEntries.containsAll(iliadEntry.value().parentIds())) {
			u = 30;
			active = true;
		}
		else {
			u = 0;
			active = false;
		}

		guiGraphics.blit(book.iconsTexture(), getX(), getY(), u, 0, width, height);

		if(playerEntries.containsAll(iliadEntry.value().parentIds()) || playerEntries.contains(iliadEntry.key().location()) || iliadEntry.value().parentIds().isEmpty())
			guiGraphics.renderItem(stack, getX() + 7, getY() + 7);
		else
			guiGraphics.blit(book.iconsTexture(), getX() + 7, getY() + 7, 0, 32, 16, 16);
	}

	public void renderTooltip(GuiGraphics guiGraphics, PoseStack poseStack, int mouseX, int mouseY) {
		if(Minecraft.getInstance().screen instanceof IliadBookScreen screen && isHovered() && active && isInsideBorder(screen, mouseX, mouseY)) {
			poseStack.pushPose();
			poseStack.translate(-offsetX, -offsetY, 0);
			guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable(IliadEntry.getTranslationKey(iliadEntry)), mouseX, mouseY);
			poseStack.popPose();
		}
	}

	private boolean isInsideBorder(IliadBookScreen screen, int mouseX, int mouseY) {
		return mouseX >= screen.leftPos + 16 && mouseY >= screen.topPos + 16 && mouseX < screen.leftPos + 362 && mouseY < screen.topPos + 234;
	}

	public void setOffset(float offsetX, float offsetY) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
	}

	public Holder.Reference<IliadEntry> getIliadEntry() {
		return iliadEntry;
	}

	public interface OnPress {
		void onPress(EntryWidget widget);
	}
}
