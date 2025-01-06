package com.teamabnormals.gallery.core.data.client;

import com.teamabnormals.blueprint.core.data.client.BlueprintItemModelProvider;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;


public class GalleryItemModelProvider extends BlueprintItemModelProvider {

	public GalleryItemModelProvider(PackOutput output, ExistingFileHelper helper) {
		super(output, Gallery.MOD_ID, helper);
	}

	@Override
	protected void registerModels() {
		BuiltInRegistries.PAINTING_VARIANT.registryKeySet().forEach(variant -> {
			String name = new ResourceLocation(Gallery.MOD_ID, "item/painting/" + variant.location().getPath()).toString();
			this.withExistingParent(name, "item/generated").texture("layer0", name);
		});
	}
}