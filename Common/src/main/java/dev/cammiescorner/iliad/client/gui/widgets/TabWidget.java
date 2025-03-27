package dev.cammiescorner.iliad.client.gui.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.cammiescorner.iliad.api.book.IliadBook;
import dev.cammiescorner.iliad.api.IliadRegistries;
import dev.cammiescorner.iliad.api.book.IliadTab;
import dev.cammiescorner.iliad.client.gui.screens.IliadBookScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class TabWidget extends AbstractButton {
	private final RegistryAccess access = Minecraft.getInstance().level.registryAccess();
	private final boolean top;
	private final Holder.Reference<IliadTab> iliadTab;
	private final Item item;
	private final IliadBook book;
	private final OnPress onPress;
	private int topScrollOffset, bottomScrollOffset;
	private float yPos;

	public TabWidget(int x, int y, boolean top, ResourceLocation iliadEntryId, Item item, IliadBook book, OnPress onPress) {
		super(x, y, 24, 40, Component.empty());
		this.top = top;
		this.iliadTab = access.lookupOrThrow(IliadRegistries.TAB).getOrThrow(ResourceKey.create(IliadRegistries.TAB, iliadEntryId));
		this.item = item;
		this.book = book;
		this.onPress = onPress;
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
	public boolean isFocused() {
		return Minecraft.getInstance().screen instanceof IliadBookScreen screen && screen.tabId.equals(iliadTab.key().location());
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		PoseStack poseStack = guiGraphics.pose();

		isHovered = Minecraft.getInstance().screen instanceof IliadBookScreen screen && isInsideBorder(screen, mouseX, mouseY) &&
			mouseX >= getX() + getScrollOffset() && mouseY >= getY() + getOffsetY() &&
			mouseX < getX() + getScrollOffset() + width && mouseY < getY() + height;
		yPos = isHoveredOrFocused() ? isFocused() ? 10 : Math.min(10, yPos + 1) : Math.max(0, yPos - 1);

		float lerp = Mth.lerp(yPos / 10f, 0f, 17f);
		int u = isHoveredOrFocused() ? 0 : 24;

		poseStack.pushPose();

		if(top) {
			poseStack.translate(topScrollOffset, lerp, 0);
			guiGraphics.blit(book.iconsTexture(), getX(), getY() - 14, 104 + u, 0, width, height);
			guiGraphics.renderItem(new ItemStack(item), getX() + 4, getY() - 2);
		}
		else {
			poseStack.translate(bottomScrollOffset, -lerp, 0);
			guiGraphics.blit(book.iconsTexture(), getX(), getY() + 25, 152 + u, 0, width, height);
			guiGraphics.renderItem(new ItemStack(item), getX() + 4, getY() + 34);
		}

		poseStack.popPose();
	}

	@Override
	public void onPress() {
		onPress.onPress(this);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
		defaultButtonNarrationText(narrationElementOutput);
	}

	public void renderTooltip(GuiGraphics guiGraphics, PoseStack poseStack, int mouseX, int mouseY) {
		if(Minecraft.getInstance().screen instanceof IliadBookScreen screen && isHovered() && isInsideBorder(screen, mouseX, mouseY)) {
			poseStack.pushPose();
			poseStack.translate(-getScrollOffset(), 0, 0);
			guiGraphics.renderTooltip(Minecraft.getInstance().font, Component.translatable(IliadTab.getTranslationKey(iliadTab)), mouseX, mouseY);
			poseStack.popPose();
		}
	}

	private boolean isInsideBorder(IliadBookScreen screen, int mouseX, int mouseY) {
		return mouseX >= screen.leftPos + 16 && mouseY >= screen.topPos + 16 && mouseX < screen.leftPos + 362 && mouseY < screen.topPos + 234;
	}

	public void setScrollOffsets(int topOffset, int bottomOffset) {
		topScrollOffset = topOffset;
		bottomScrollOffset = bottomOffset;
	}

	public int getScrollOffset() {
		return top ? topScrollOffset : bottomScrollOffset;
	}

	public int getOffsetY() {
		return top ? 15 : 8;
	}

	public boolean isTop() {
		return top;
	}

	public Holder.Reference<IliadTab> getIliadTab() {
		return iliadTab;
	}

	public interface OnPress {
		void onPress(TabWidget widget);
	}
}
