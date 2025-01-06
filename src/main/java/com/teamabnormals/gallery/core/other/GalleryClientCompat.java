package com.teamabnormals.gallery.core.other;

import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.Optional;

@EventBusSubscriber(modid = Gallery.MOD_ID, value = Dist.CLIENT)
public class GalleryClientCompat {

	public static void registerItemProperties() {
		BuiltInRegistries.PAINTING_VARIANT.registryKeySet().forEach(variant -> {
			ItemProperties.register(Items.PAINTING, variant.location(), (stack, level, entity, hash) -> {
				Optional<Holder<PaintingVariant>> block = Painting.loadVariant(stack.getOrCreateTagElement("EntityTag"));
				return block.isPresent() && block.get().is(variant) ? 1.0F : 0.0F;
			});
		});
	}
}
