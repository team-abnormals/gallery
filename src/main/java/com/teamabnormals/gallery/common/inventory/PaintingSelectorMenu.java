package com.teamabnormals.gallery.common.inventory;

import com.google.common.collect.Lists;
import com.teamabnormals.gallery.common.network.UpdatePaintingVariant;
import com.teamabnormals.gallery.core.registry.GalleryMenuTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;
import java.util.Optional;

public class PaintingSelectorMenu extends AbstractContainerMenu {
	private final Level level;

	public Holder<PaintingVariant> selectedPainting = null;
	public List<Holder<PaintingVariant>> paintings = Lists.newArrayList();
	public List<Holder<PaintingVariant>> allPaintings = Lists.newArrayList();

	public PaintingSelectorMenu(int p_40297_, Inventory p_40298_) {
		super(GalleryMenuTypes.PAINTING_SELECTOR.get(), p_40297_);
		this.level = p_40298_.player.level();

		this.setSelectedPainting(loadVariant(this.level, p_40298_.player.getItemInHand(p_40298_.player.getUsedItemHand())).orElse(null));
		this.resetPaintingList();
		this.setupPaintingLists();
	}

	public Holder<PaintingVariant> getSelectedPainting() {
		return this.selectedPainting;
	}

	public void setSelectedPainting(Holder<PaintingVariant> painting) {
		this.selectedPainting = painting;
	}

	public void setSelectedPainting(ResourceLocation painting) {
		this.setSelectedPainting(this.level.registryAccess().lookup(Registries.PAINTING_VARIANT).get().get(ResourceKey.create(Registries.PAINTING_VARIANT, painting)).get());
	}

	public List<Holder<PaintingVariant>> getPaintings() {
		return this.paintings;
	}

	public List<Holder<PaintingVariant>> getAllPaintings() {
		return this.allPaintings;
	}

	public int getNumPaintings() {
		return this.paintings.size();
	}

	@Override
	public boolean stillValid(Player p_40307_) {
		return true;
	}

	public boolean clickMenuButton(Player player, int index) {
		if (index >= 0 && index < this.getNumPaintings() && level.isClientSide) {
			Holder<PaintingVariant> variant = this.getPaintings().get(index);
			this.setSelectedPainting(variant);
			PacketDistributor.sendToServer(new UpdatePaintingVariant(variant.unwrapKey().get().location()));
		}

		return true;
	}

	@Override
	public ItemStack quickMoveStack(Player p_38941_, int p_38942_) {
		return null;
	}

	public void resetPaintingList() {
		this.paintings = Lists.newArrayList();
	}

	public void setupPaintingLists() {
		this.allPaintings = this.level.registryAccess().lookup(Registries.PAINTING_VARIANT).get().get(PaintingVariantTags.PLACEABLE).get().stream().sorted(CreativeModeTabs.PAINTING_COMPARATOR).toList();
		this.paintings = List.copyOf(this.allPaintings);
	}

	public MenuType<?> getType() {
		return GalleryMenuTypes.PAINTING_SELECTOR.get();
	}

	public void removed(Player player) {
		super.removed(player);

		if (this.getSelectedPainting() != null) {
			InteractionHand hand = player.getUsedItemHand();
			ItemStack stack = player.getItemInHand(hand).copy();
			storeVariant(player.level(), stack, this.getSelectedPainting());
			player.setItemInHand(hand, stack);
		}
	}

	public static Optional<Holder<PaintingVariant>> loadVariant(Level level, ItemStack stack) {
		CustomData data = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
		if (!data.isEmpty()) {
			Optional<Holder<PaintingVariant>> holder = data.read(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), Painting.VARIANT_MAP_CODEC).result();
			return holder;
		}
		return Optional.empty();
	}

	public static ItemStack storeVariant(Level level, ItemStack stack, Holder<PaintingVariant> variant) {
		CustomData data = CustomData.EMPTY
				.update(level.registryAccess().createSerializationContext(NbtOps.INSTANCE), Painting.VARIANT_MAP_CODEC, variant)
				.getOrThrow()
				.update(tag -> tag.putString("id", "minecraft:painting"));
		stack.set(DataComponents.ENTITY_DATA, data);
		return stack;
	}
}