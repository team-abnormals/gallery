package com.teamabnormals.gallery.common.inventory;

import com.google.common.collect.Lists;
import com.teamabnormals.gallery.common.network.C2SPaintingVariantMessage;
import com.teamabnormals.gallery.core.Gallery;
import com.teamabnormals.gallery.core.registry.GalleryMenuTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class PaintingSelectorMenu extends AbstractContainerMenu {
	private final Level level;

	public Holder<PaintingVariant> selectedPainting = null;
	public List<Holder<PaintingVariant>> paintings = Lists.newArrayList();
	public List<Holder<PaintingVariant>> allPaintings = Lists.newArrayList();

	public PaintingSelectorMenu(int p_40297_, Inventory p_40298_) {
		super(GalleryMenuTypes.PAINTING_SELECTOR.get(), p_40297_);
		this.level = p_40298_.player.level();

		this.setSelectedPainting(Painting.loadVariant(p_40298_.player.getItemInHand(p_40298_.player.getUsedItemHand()).getOrCreateTagElement("EntityTag")).orElse(null));
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
			Gallery.PLAY.sendToServer(new C2SPaintingVariantMessage(variant.unwrapKey().get().location()));
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
		this.allPaintings = this.level.registryAccess().lookup(Registries.PAINTING_VARIANT).get().get(PaintingVariantTags.PLACEABLE).get().stream().toList();
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
			CompoundTag tag = stack.getOrCreateTagElement("EntityTag");
			Painting.storeVariant(tag, this.getSelectedPainting());
			player.setItemInHand(hand, stack);
		}
	}
}