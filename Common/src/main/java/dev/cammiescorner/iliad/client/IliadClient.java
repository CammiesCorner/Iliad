package dev.cammiescorner.iliad.client;

import dev.cammiescorner.iliad.Iliad;
import dev.cammiescorner.iliad.api.book.IliadEntry;
import dev.cammiescorner.iliad.client.gui.screens.BookPageScreen;
import dev.cammiescorner.iliad.client.gui.screens.IliadBookScreen;
import dev.cammiescorner.iliad.client.gui.widgets.EntryWidget;
import dev.cammiescorner.iliad.common.MainHelper;
import dev.upcraft.sparkweave.api.entrypoint.ClientEntryPoint;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;

import java.util.HashMap;
import java.util.Map;

public class IliadClient implements ClientEntryPoint {
	public static final Map<ResourceLocation, Vec2> GUIDEBOOK_TAB_OFFSETS = new HashMap<>();
	public static ResourceLocation lastGuideBookTab = Iliad.id("empty");
	public static Screen lastGuideBookScreen = new IliadBookScreen(null); // TODO set these by book

	@Override
	public void onInitializeClient(ModContainer mod) {

	}

	public static void entryWidgetClick(EntryWidget widget) {
		Player player = Minecraft.getInstance().player;

		if(player != null) {
			Holder.Reference<IliadEntry> iliadEntry = widget.getIliadEntry();

			if(MainHelper.getUnlockedEntryIds(player).contains(iliadEntry.key().location())) {

				Minecraft.getInstance().setScreen(new BookPageScreen(null, iliadEntry));
				return;
			}

			// TODO do default interaction
		}
	}
}
