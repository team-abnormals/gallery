package com.teamabnormals.gallery.core.other;

import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import com.teamabnormals.gallery.core.GalleryConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent.RightClickItem;

@EventBusSubscriber(modid = Gallery.MOD_ID)
public class GalleryEvents {
	private static final Component CONTAINER_TITLE = Component.translatable("container.gallery.painting_selector");

	@SubscribeEvent
	public static void rightClickWithPainting(RightClickBlock event) {
		if (event.getFace() != null && event.getFace().getAxis().isVertical()) {
			if (event.getItemStack().is(Items.PAINTING) && GalleryConfig.COMMON.paintingSelector.get()) {
				if (!GalleryConfig.COMMON.paintingSelectorOpensOnBlocks.get())
					return;

				if (GalleryConfig.COMMON.paintingSelectorRequiresCrouching.get() && !event.getEntity().isCrouching())
					return;

				if (!event.getLevel().isClientSide()) {
					event.getEntity().openMenu(new SimpleMenuProvider((id, inventory, player) -> new PaintingSelectorMenu(id, inventory), CONTAINER_TITLE));
				}

				event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void rightClickWithPainting(RightClickItem event) {
		if (event.getItemStack().is(Items.PAINTING) && GalleryConfig.COMMON.paintingSelector.get()) {
			if (GalleryConfig.COMMON.paintingSelectorRequiresCrouching.get() && !event.getEntity().isCrouching())
				return;

			if (!event.getLevel().isClientSide()) {
				event.getEntity().openMenu(new SimpleMenuProvider((id, inventory, player) -> new PaintingSelectorMenu(id, inventory), CONTAINER_TITLE));
			}

			event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
			event.setCanceled(true);
		}
	}
}
