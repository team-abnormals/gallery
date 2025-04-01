package com.teamabnormals.gallery.core;

import com.teamabnormals.gallery.common.network.UpdatePaintingVariant;
import com.teamabnormals.gallery.core.data.client.GalleryItemModelProvider;
import com.teamabnormals.gallery.core.registry.GalleryMenuTypes;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.concurrent.CompletableFuture;

@Mod(Gallery.MOD_ID)
public class Gallery {
	public static final String MOD_ID = "gallery";

	public Gallery(IEventBus bus, ModContainer container) {
		bus.addListener(this::registerPayloadHandlers);

		GalleryMenuTypes.MENU_TYPES.register(bus);

		bus.addListener(this::dataSetup);

		container.registerConfig(ModConfig.Type.CLIENT, GalleryConfig.CLIENT_SPEC);
		container.registerConfig(ModConfig.Type.COMMON, GalleryConfig.COMMON_SPEC);
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<Provider> provider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		boolean client = event.includeClient();
		generator.addProvider(client, new GalleryItemModelProvider(MOD_ID, output, helper, provider));
	}

	private void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar registrar = event.registrar("1");
		registrar.playToServer(UpdatePaintingVariant.TYPE, UpdatePaintingVariant.STREAM_CODEC, UpdatePaintingVariant::handle);
	}

	public static ResourceLocation location(String path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
	}
}