package com.teamabnormals.gallery.client.model;

import com.teamabnormals.gallery.core.Gallery;
import com.teamabnormals.gallery.core.GalleryConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Gallery.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class PaintingItemModel implements BakedModel {
	private final BakedModel model;
	private final ItemOverrides overrideList;

	public PaintingItemModel(Map<ModelResourceLocation, BakedModel> modelManager) {
		this.model = modelManager.get(ModelResourceLocation.inventory(BuiltInRegistries.ITEM.getKey(Items.PAINTING)));
		this.overrideList = new Overrides(modelManager);
	}

	@Override
	public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
		return this.model.getQuads(state, side, rand);
	}

	@Override
	public boolean useAmbientOcclusion() {
		return this.model.useAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return this.model.isGui3d();
	}

	@Override
	public boolean usesBlockLight() {
		return this.model.usesBlockLight();
	}

	@Override
	public boolean isCustomRenderer() {
		return this.model.isCustomRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleIcon() {
		return this.model.getParticleIcon();
	}

	@Override
	public ItemOverrides getOverrides() {
		return this.overrideList;
	}

	private static class Overrides extends ItemOverrides {
		private final Map<ModelResourceLocation, BakedModel> modelManager;
		private final BakedModel model;
		private final Map<ResourceLocation, ModelResourceLocation> locationCache;
		private final Map<ModelResourceLocation, ModelResourceLocation> modelLocations;

		private Overrides(Map<ModelResourceLocation, BakedModel> modelManager) {
			this.modelManager = modelManager;
			this.model = modelManager.get(ModelResourceLocation.inventory(BuiltInRegistries.ITEM.getKey(Items.PAINTING)));
			this.locationCache = new HashMap<>();
			this.modelLocations = new HashMap<>();
			for (ResourceLocation location : Minecraft.getInstance().getResourceManager().listResources("models/item/painting", s -> s.getPath().endsWith(".json")).keySet())
				this.modelLocations.put(
						ModelResourceLocation.inventory(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().substring("models/item/painting/".length(), location.getPath().length() - ".json".length()))),
						ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().substring("models/".length(), location.getPath().length() - ".json".length())))
				);
		}

		@Nullable
		@Override
		public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int p_173469_) {
			ClientLevel level = world;
			if (level == null){
				level = Minecraft.getInstance().level;
			}

			CustomData data = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
			if (!data.isEmpty() && level != null) {
				Optional<Holder<PaintingVariant>> holder = data.read(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), Painting.VARIANT_MAP_CODEC).result();
				if (holder.isPresent()) {
					ResourceLocation variant = holder.get().getKey().location();
					if (variant.getNamespace().equals("minecraft")) {
						variant = ResourceLocation.fromNamespaceAndPath(Gallery.MOD_ID, variant.getPath());
					}
					ModelResourceLocation location = this.locationCache.computeIfAbsent(variant, ModelResourceLocation::inventory);
					if (this.modelLocations.containsKey(location))
						return this.modelManager.get(this.modelLocations.get(location));
				}
			}

			return this.model;
		}
	}

	@SubscribeEvent
	public static void onEvent(ModelEvent.RegisterAdditional event) {
		if (GalleryConfig.CLIENT.paintingIcons.get()) {
			for (ResourceLocation location : Minecraft.getInstance().getResourceManager().listResources("models/item/painting", s -> s.getPath().endsWith(".json")).keySet()) {
				event.register(ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(location.getNamespace(), location.getPath().substring("models/".length(), location.getPath().length() - ".json".length()))));
			}
		}
	}

	@SubscribeEvent
	public static void onEvent(ModelEvent.ModifyBakingResult event) {
		if (GalleryConfig.CLIENT.paintingIcons.get()) {
			event.getModels().put(ModelResourceLocation.inventory(BuiltInRegistries.ITEM.getKey(Items.PAINTING)), new PaintingItemModel(event.getModels()));
		}
	}
}
