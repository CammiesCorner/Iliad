package dev.cammiescorner.iliad.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.cammiescorner.iliad.Iliad;
import dev.cammiescorner.iliad.api.book.IliadBook;
import dev.cammiescorner.iliad.api.book.IliadTab;
import dev.cammiescorner.iliad.api.IliadRegistries;
import dev.cammiescorner.iliad.api.book.IliadEntry;
import dev.cammiescorner.iliad.client.IliadClient;
import dev.cammiescorner.iliad.client.gui.widgets.EntryWidget;
import dev.cammiescorner.iliad.client.gui.widgets.TabWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec2;
import org.joml.Matrix4f;

import java.util.*;

public class IliadBookScreen extends Screen {
	public final IliadBook book;
	public final ResourceLocation frameTexture;
	public final ResourceLocation iconsTexture;
	private final List<TabWidget> tabWidgets = new ArrayList<>();
	private final Map<EntryWidget, IliadTab> entryWidgets = new HashMap<>();
	private RegistryAccess access;
	public ResourceLocation tabId = Iliad.id("empty");
	public int leftPos, topPos, topTabs, bottomTabs, topTabOffset, bottomTabOffset;
	private float entryOffsetX, entryOffsetY;

	public IliadBookScreen(IliadBook book) {
		super(Component.empty());
		this.book = book;
		this.frameTexture = book.frameTexture();
		this.iconsTexture = book.iconsTexture();
	}

	@Override
	protected void init() {
		super.init();
		Vec2 offsets = IliadClient.GUIDEBOOK_TAB_OFFSETS.getOrDefault(IliadClient.lastGuideBookTab, new Vec2(0, 0));
		access = Minecraft.getInstance().level.registryAccess();
		leftPos = (width - 378) / 2;
		topPos = (height - 250) / 2;
		tabId = IliadClient.lastGuideBookTab;
		entryOffsetX = offsets.x;
		entryOffsetY = offsets.y;

		access.registryOrThrow(IliadRegistries.TAB).stream().sorted(Comparator.comparingInt(IliadTab::order)).forEach(iliadTab -> {
			ResourceLocation location = iliadTab.getId(access);

			if(book.tabs().contains(location)) {
				Item item = iliadTab.icon().getItem();

				if(iliadTab.isTop())
					addTabWidget(new TabWidget(leftPos + 23 + (28 * topTabs), topPos + 2, true, location, item, book, this::clickTab));
				else
					addTabWidget(new TabWidget(leftPos + 23 + (28 * bottomTabs), topPos + 200, false, location, item, book, this::clickTab));
			}

			access.registryOrThrow(IliadRegistries.ENTRY).forEach(iliadEntry -> {
				// TODO automatically find place for widgets if posX and posY are equal to 0xf00f
				if(iliadEntry.tab().is(location))
					addEntryWidget(iliadEntry, new EntryWidget(leftPos + iliadEntry.x(), topPos + iliadEntry.y(), iliadEntry.getId(access), book, IliadClient::entryWidgetClick));
			});
		});
	}

	@Override
	public void onClose() {
		IliadClient.lastGuideBookTab = tabId;
		IliadClient.GUIDEBOOK_TAB_OFFSETS.put(tabId, new Vec2(entryOffsetX, entryOffsetY));
		super.onClose();
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		PoseStack poseStack = guiGraphics.pose();

		drawBackground(guiGraphics);

		guiGraphics.enableScissor(leftPos + 16, topPos + 16, leftPos + 362, topPos + 234);
		poseStack.pushPose();
		poseStack.translate(leftPos, topPos, 0);

		super.render(guiGraphics, mouseX, mouseY, partialTick);
		drawWidgets(guiGraphics, poseStack, mouseX, mouseY, partialTick);

		poseStack.popPose();
		guiGraphics.disableScissor();

		drawForeground(guiGraphics, mouseX, mouseY);
		drawWidgetTooltips(guiGraphics, poseStack, mouseX, mouseY);
	}

	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
		if(button == 0 && entryWidgets.containsValue(access.registryOrThrow(IliadRegistries.TAB).get(tabId))) {
			int minX = entryWidgets.keySet().stream().filter(widget -> widget.visible && entryWidgets.get(widget).getId(access).equals(tabId)).max(Comparator.comparingInt(AbstractWidget::getX)).orElseThrow().getX();
			int minY = entryWidgets.keySet().stream().filter(widget -> widget.visible && entryWidgets.get(widget).getId(access).equals(tabId)).max(Comparator.comparingInt(AbstractWidget::getY)).orElseThrow().getY();
			int maxX = entryWidgets.keySet().stream().filter(widget -> widget.visible && entryWidgets.get(widget).getId(access).equals(tabId)).min(Comparator.comparingInt(AbstractWidget::getX)).orElseThrow().getX();
			int maxY = entryWidgets.keySet().stream().filter(widget -> widget.visible && entryWidgets.get(widget).getId(access).equals(tabId)).min(Comparator.comparingInt(AbstractWidget::getY)).orElseThrow().getY();

			entryOffsetX = (float) Mth.clamp(entryOffsetX + dragX, (leftPos - minX) + 32, (leftPos + 378 - maxX) - 62);
			entryOffsetY = (float) Mth.clamp(entryOffsetY + dragY, (topPos - minY) + 32, (topPos + 250 - maxY) - 62);
		}

		return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
	}

	@Override
	protected void removeWidget(GuiEventListener listener) {
		super.removeWidget(listener);

		if(listener instanceof TabWidget tab) {
			tabWidgets.remove(listener);

			if(tab.isTop())
				topTabs--;
			else
				bottomTabs--;
		}
		if(listener instanceof EntryWidget) {
			entryWidgets.remove(listener);
		}
	}

	@Override
	protected void clearWidgets() {
		super.clearWidgets();
		tabWidgets.clear();
		entryWidgets.clear();
		topTabs = 0;
		bottomTabs = 0;
	}

	protected void drawBackground(GuiGraphics guiGraphics) {
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		guiGraphics.blit(frameTexture, leftPos, topPos, 0, 256, 378, 250, 512, 512);
	}

	protected void drawForeground(GuiGraphics guiGraphics, int mouseX, int mouseY) {
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
		guiGraphics.blit(frameTexture, leftPos, topPos, 200, 0, 0, 378, 250, 512, 512); // main frame

		if(topTabs > 12) {
			if(topTabOffset < 0)
				guiGraphics.blit(frameTexture, leftPos + 21, topPos + 7, 200, 384, scrollTopTabs(mouseX, mouseY) == ScrollDirection.LEFT ? 8 : 0, 24, 8, 512, 512); // top left arrow
			if(topTabOffset > -Math.max(topTabs - 12, 0) * 28)
				guiGraphics.blit(frameTexture, leftPos + 333, topPos + 7, 200, 408, scrollTopTabs(mouseX, mouseY) == ScrollDirection.RIGHT ? 8 : 0, 24, 8, 512, 512); // top right arrow
		}

		if(bottomTabs > 12) {
			if(bottomTabOffset < 0)
				guiGraphics.blit(frameTexture, leftPos + 21, topPos + 235, 200, 384, scrollBottomTabs(mouseX, mouseY) == ScrollDirection.LEFT ? 32 : 24, 24, 8, 512, 512); // bottom left arrow
			if(bottomTabOffset > -Math.max(bottomTabs - 12, 0) * 28)
				guiGraphics.blit(frameTexture, leftPos + 333, topPos + 235, 200, 408, scrollBottomTabs(mouseX, mouseY) == ScrollDirection.RIGHT ? 32 : 24, 24, 8, 512, 512); // bottom right arrow
		}
	}

	protected void drawWidgets(GuiGraphics guiGraphics, PoseStack poseStack, int mouseX, int mouseY, float delta) {
		poseStack.pushPose();
		poseStack.translate(-leftPos + entryOffsetX, -topPos + entryOffsetY, -200);

		for(EntryWidget widget : entryWidgets.keySet()) {
			if(tabId.equals(entryWidgets.get(widget).getId(access))) {
				for(EntryWidget parent : getParents(widget))
					drawLine(poseStack, parent.getX() + 15, parent.getY() + 15, widget.getX() + 15, widget.getY() + 15);
			}
		}

		for(EntryWidget widget : entryWidgets.keySet()) {
			if(tabId.equals(entryWidgets.get(widget).getId(access))) {
				widget.setOffset(entryOffsetX, entryOffsetY);
				widget.render(guiGraphics, mouseX, mouseY, delta);
			}
		}

		poseStack.popPose();

		poseStack.pushPose();
		poseStack.translate(-leftPos, -topPos, 0);

		for(TabWidget widget : tabWidgets) {
			widget.setScrollOffsets(topTabOffset, bottomTabOffset);
			widget.render(guiGraphics, mouseX, mouseY, delta);
		}

		poseStack.popPose();
	}

	protected void drawWidgetTooltips(GuiGraphics guiGraphics, PoseStack poseStack, int mouseX, int mouseY) {
		poseStack.pushPose();
		poseStack.translate(entryOffsetX, entryOffsetY, 0);

		for(EntryWidget widget : entryWidgets.keySet()) {
			if(tabId.equals(entryWidgets.get(widget).getId(access)))
				widget.renderTooltip(guiGraphics, poseStack, mouseX, mouseY);
		}

		poseStack.popPose();

		for(TabWidget widget : tabWidgets) {
			poseStack.pushPose();
			poseStack.translate(widget.isTop() ? topTabOffset : bottomTabOffset, 0, 0);

			widget.renderTooltip(guiGraphics, poseStack, mouseX, mouseY);

			poseStack.popPose();
		}
	}

	public <T extends TabWidget> void addTabWidget(T drawable) {
		if(drawable.isTop())
			topTabs++;
		else
			bottomTabs++;

		tabWidgets.add(drawable);
		addWidget(drawable);
	}

	public <T extends EntryWidget> void addEntryWidget(IliadEntry iliadEntry, T drawable) {
		entryWidgets.put(drawable, iliadEntry.tab().value());
		addWidget(drawable);
	}

	private void drawLine(PoseStack poseStack, float x1, float y1, float x2, float y2) {
		RenderSystem.setShader(GameRenderer::getPositionShader);
		RenderSystem.setShaderColor(0.224f, 0.196f, 0.175f, 1f);
		Matrix4f matrix = poseStack.last().pose();
		Vec2 startPos = new Vec2(x1, y1);
		Vec2 midPos = new Vec2(x1 + (x2 - x1) * 0.5f, y1 + (y2 - y1) * 0.5f);
		Vec2 endPos = new Vec2(x2, y2);
		float angle = (float) (Math.atan2(y2 - y1, x2 - x1) - (Math.PI * 0.5));
		float prevDelta = 0;
		int segmentCount = 8;

		if(startPos.x == endPos.x || startPos.y == endPos.y)
			segmentCount = 1;

		if(segmentCount > 1) {
			float offset = Mth.sqrt(startPos.distanceToSqr(endPos)) * 0.25f;
			float angleDeg = (float) Math.toDegrees(angle) + 270;

			// top
			if(angleDeg < 90 && angleDeg > 45)
				midPos = midPos.add(new Vec2(-offset, 0));
			if(angleDeg < 135 && angleDeg > 90)
				midPos = midPos.add(new Vec2(offset, 0));

			// right
			if(angleDeg < 180 && angleDeg > 135)
				midPos = midPos.add(new Vec2(0, -offset));
			if(angleDeg < 225 && angleDeg > 180)
				midPos = midPos.add(new Vec2(0, offset));

			// bottom
			if(angleDeg < 270 && angleDeg > 225)
				midPos = midPos.add(new Vec2(offset, 0));
			if(angleDeg < 315 && angleDeg > 270)
				midPos = midPos.add(new Vec2(-offset, 0));

			// left
			if(angleDeg < 360 && angleDeg > 315)
				midPos = midPos.add(new Vec2(0, offset));
			if(angleDeg < 45 && angleDeg > 0)
				midPos = midPos.add(new Vec2(0, -offset));
		}

		for(int i = 1; i <= segmentCount; i++) {
			float delta = i / (float) segmentCount;
			Vec2 a1 = lerp(prevDelta, startPos, midPos);
			Vec2 b1 = lerp(prevDelta, midPos, endPos);
			Vec2 c1 = lerp(prevDelta, a1, b1);
			Vec2 a2 = lerp(delta, startPos, midPos);
			Vec2 b2 = lerp(delta, midPos, endPos);
			Vec2 c2 = lerp(delta, a2, b2);
			float angle2 = (float) (Mth.atan2(c2.y - c1.y, c2.x - c1.x) - (Math.PI * 0.5));
			float dx = Mth.cos(angle2) * 2;
			float dy = Mth.sin(angle2) * 2;

			Tesselator tesselator = Tesselator.getInstance();
			BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferBuilder.addVertex(matrix, c2.x - dx, c2.y - dy, 0).setColor(0);
			bufferBuilder.addVertex(matrix, c2.x + dx, c2.y + dy, 0).setColor(0);
			bufferBuilder.addVertex(matrix, c1.x + dx, c1.y + dy, 0).setColor(0);
			bufferBuilder.addVertex(matrix, c1.x - dx, c1.y - dy, 0).setColor(0);
			MeshData data = bufferBuilder.build();

			if(data != null)
				BufferUploader.drawWithShader(data);

			prevDelta = delta - 0.004f;
		}
	}

	private Vec2 lerp(float delta, Vec2 pos1, Vec2 pos2) {
		return pos1.scale(1 - delta).add(pos2.scale(delta));
	}

	private List<EntryWidget> getParents(EntryWidget widget) {
		List<EntryWidget> parents = new ArrayList<>();

		if(widget.visible) {
			Holder.Reference<IliadEntry> iliadEntry = widget.getIliadEntry();

			for(EntryWidget parent : entryWidgets.keySet())
				if(parent.visible && iliadEntry.value().parentIds().contains(parent.getIliadEntry().key().location()))
					parents.add(parent);
		}

		return parents;
	}

	private void clickTab(TabWidget widget) {
		Vec2 offsets = IliadClient.GUIDEBOOK_TAB_OFFSETS.getOrDefault(widget.getIliadTab().value().getId(access), new Vec2(0, 0));
		IliadClient.GUIDEBOOK_TAB_OFFSETS.put(tabId, new Vec2(entryOffsetX, entryOffsetY));

		tabId = widget.getIliadTab().value().getId(access);
		entryOffsetX = offsets.x;
		entryOffsetY = offsets.y;
	}

	private ScrollDirection scrollTopTabs(int mouseX, int mouseY) {
		if(mouseX >= leftPos + 20 && mouseY >= topPos + 6 && mouseX < leftPos + 46 && mouseY < topPos + 16) {
			topTabOffset = Math.min(topTabOffset + 2, 0);
			return ScrollDirection.LEFT;
		}
		if(mouseX >= leftPos + 332 && mouseY >= topPos + 6 && mouseX < leftPos + 358 && mouseY < topPos + 16) {
			topTabOffset = Math.max(topTabOffset - 2, -Math.max(topTabs - 12, 0) * 28);
			return ScrollDirection.RIGHT;
		}

		return ScrollDirection.NONE;
	}

	private ScrollDirection scrollBottomTabs(int mouseX, int mouseY) {
		if(mouseX >= leftPos + 20 && mouseY >= topPos + 234 && mouseX < leftPos + 46 && mouseY < topPos + 244) {
			bottomTabOffset = Math.min(bottomTabOffset + 2, 0);
			return ScrollDirection.LEFT;
		}
		if(mouseX >= leftPos + 332 && mouseY >= topPos + 234 && mouseX < leftPos + 358 && mouseY < topPos + 244) {
			bottomTabOffset = Math.max(bottomTabOffset - 2, -Math.max(bottomTabs - 12, 0) * 28);
			return ScrollDirection.RIGHT;
		}

		return ScrollDirection.NONE;
	}

	private enum ScrollDirection {
		LEFT, RIGHT, NONE
	}
}
