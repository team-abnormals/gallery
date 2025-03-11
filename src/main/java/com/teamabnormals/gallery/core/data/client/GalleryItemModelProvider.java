package com.teamabnormals.gallery.core.data.client;

import com.teamabnormals.blueprint.core.data.client.BlueprintItemModelProvider;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;


public class GalleryItemModelProvider extends BlueprintItemModelProvider {
	private final CompletableFuture<Provider> provider;
	private final String location;

	public GalleryItemModelProvider(String modid, PackOutput output, ExistingFileHelper helper, CompletableFuture<Provider> provider) {
		super(output, Gallery.MOD_ID, helper);
		this.provider = provider;
		this.location = modid;
	}

	@Override
	protected void registerModels() {
		try {
			this.provider.get().lookupOrThrow(Registries.PAINTING_VARIANT).listElementIds().filter(key -> {
				String namespace = key.location().getNamespace();
				return namespace.equals(this.location) || this.location.equals(Gallery.MOD_ID) && namespace.equals("minecraft");
			}).forEach(variant -> {
				String name = ResourceLocation.fromNamespaceAndPath(this.location, "item/painting/" + variant.location().getPath()).toString();
				this.withExistingParent(name, "item/generated").texture("layer0", name);
			});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}