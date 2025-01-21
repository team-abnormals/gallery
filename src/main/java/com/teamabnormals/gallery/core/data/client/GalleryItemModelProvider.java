package com.teamabnormals.gallery.core.data.client;

import com.teamabnormals.blueprint.core.data.client.BlueprintItemModelProvider;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;


public class GalleryItemModelProvider extends BlueprintItemModelProvider {
	protected final String location;

	public GalleryItemModelProvider(String modid, PackOutput output, ExistingFileHelper helper) {
		super(output, Gallery.MOD_ID, helper);
		this.location = modid;
	}

	@Override
	protected void registerModels() {
		BuiltInRegistries.PAINTING_VARIANT.registryKeySet().stream().filter(key -> {
			String namespace = key.location().getNamespace();
			return namespace.equals(this.location) || this.location.equals(Gallery.MOD_ID) && namespace.equals("minecraft");
		}).forEach(variant -> {
			String name = new ResourceLocation(this.location, "item/painting/" + variant.location().getPath()).toString();
			this.withExistingParent(name, "item/generated").texture("layer0", name);
		});
	}
}