package dev.cammiescorner.iliad;

import dev.upcraft.sparkweave.api.entrypoint.MainEntryPoint;
import dev.upcraft.sparkweave.api.platform.ModContainer;
import net.minecraft.resources.ResourceLocation;

public class Iliad implements MainEntryPoint {
    public static final String MOD_ID = "iliad";

    @Override
    public void onInitialize(ModContainer mod) {
        // TODO register item data for books
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
