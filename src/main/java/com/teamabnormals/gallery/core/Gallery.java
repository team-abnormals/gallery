package com.teamabnormals.gallery.core;

import com.teamabnormals.blueprint.core.util.registry.RegistryHelper;
import com.teamabnormals.gallery.common.network.C2SPaintingVariantMessage;
import com.teamabnormals.gallery.core.data.client.GalleryAssetsRemolderProvider;
import com.teamabnormals.gallery.core.data.client.GalleryItemModelProvider;
import com.teamabnormals.gallery.core.data.server.tags.GalleryPaintingVariantTagsProvider;
import com.teamabnormals.gallery.core.other.GalleryClientCompat;
import com.teamabnormals.gallery.core.registry.GalleryMenuTypes;
import com.teamabnormals.gallery.core.registry.GalleryPaintingVariants;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.concurrent.CompletableFuture;

@Mod(Gallery.MOD_ID)
public class Gallery {
	public static final String MOD_ID = "gallery";
	public static final String NETWORK_PROTOCOL = "GAL1";
	public static final RegistryHelper REGISTRY_HELPER = new RegistryHelper(MOD_ID);

	public static final SimpleChannel PLAY = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(MOD_ID, "play")).networkProtocolVersion(() -> NETWORK_PROTOCOL).clientAcceptedVersions(NETWORK_PROTOCOL::equals).serverAcceptedVersions(NETWORK_PROTOCOL::equals).simpleChannel();

	public Gallery() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		this.setupPlayMessages();

		MinecraftForge.EVENT_BUS.register(this);
		REGISTRY_HELPER.register(bus);

		GalleryPaintingVariants.PAINTING_VARIANTS.register(bus);
		GalleryMenuTypes.MENU_TYPES.register(bus);

		bus.addListener(this::commonSetup);
		bus.addListener(this::clientSetup);
		bus.addListener(this::dataSetup);
	}

	private void commonSetup(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {

		});
	}

	private void clientSetup(FMLClientSetupEvent event) {
		event.enqueueWork(() -> {
			GalleryClientCompat.registerItemProperties();
			GalleryMenuTypes.registerScreenFactories();
		});
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput output = generator.getPackOutput();
		CompletableFuture<Provider> provider = event.getLookupProvider();
		ExistingFileHelper helper = event.getExistingFileHelper();

		boolean server = event.includeServer();
		generator.addProvider(server, new GalleryPaintingVariantTagsProvider(output, provider, helper));

		boolean client = event.includeClient();
		generator.addProvider(client, new GalleryItemModelProvider(MOD_ID, output, helper));
		generator.addProvider(client, new GalleryAssetsRemolderProvider(MOD_ID, output, provider));
	}

	private void setupPlayMessages() {
		PLAY.registerMessage(4, C2SPaintingVariantMessage.class, C2SPaintingVariantMessage::serialize, C2SPaintingVariantMessage::deserialize, C2SPaintingVariantMessage::handle);
	}
}