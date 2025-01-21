package com.teamabnormals.gallery.core.data.server.tags;

import com.teamabnormals.gallery.core.Gallery;
import com.teamabnormals.gallery.core.registry.GalleryPaintingVariants;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.PaintingVariantTagsProvider;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.CompletableFuture;

public class GalleryPaintingVariantTagsProvider extends PaintingVariantTagsProvider {

	public GalleryPaintingVariantTagsProvider(PackOutput output, CompletableFuture<Provider> provider, ExistingFileHelper helper) {
		super(output, provider, Gallery.MOD_ID, helper);
	}

	@Override
	public void addTags(Provider provider) {
		TagAppender<PaintingVariant> appender = this.tag(PaintingVariantTags.PLACEABLE);
		for (RegistryObject<PaintingVariant> variant : GalleryPaintingVariants.PAINTING_VARIANTS.getEntries()) {
			appender.add(variant.getKey());
		}
	}
}