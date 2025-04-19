package com.teamabnormals.gallery.core.other;

import com.teamabnormals.blueprint.client.model.DynamicItemModel;
import com.teamabnormals.gallery.core.Gallery;
import com.teamabnormals.gallery.core.GalleryConfig;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Gallery.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class GalleryClientCompat {

	@SubscribeEvent
	public static void onEvent(ModelEvent.RegisterAdditional event) {
		if (GalleryConfig.CLIENT.paintingIcons.get()) {
			DynamicItemModel.register(event, "painting");
		}
	}

	@SubscribeEvent
	public static void onEvent(ModelEvent.ModifyBakingResult event) {
		if (GalleryConfig.CLIENT.paintingIcons.get()) {
			DynamicItemModel.bake(event, BuiltInRegistries.ITEM.getKey(Items.PAINTING), "painting", ModelResourceLocation.inventory(ResourceLocation.withDefaultNamespace("painting")), ((stack, level, entity) -> {
				CustomData data = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
				if (!data.isEmpty() && level != null) {
					Optional<Holder<PaintingVariant>> holder = data.read(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), Painting.VARIANT_MAP_CODEC).result();
					if (holder.isPresent()) {
						ResourceLocation variant = holder.get().getKey().location();
						if (variant.getNamespace().equals("minecraft")) {
							variant = Gallery.location(variant.getPath());
						}

						return Optional.of(variant.toString());
					}
				}

				return Optional.empty();
			}));
		}
	}
}
