package com.teamabnormals.gallery.core.other;

import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import com.teamabnormals.gallery.core.GalleryConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Gallery.MOD_ID)
public class GalleryEvents {
	private static final Component CONTAINER_TITLE = Component.translatable("container.gallery.painting_selector");

	@SubscribeEvent
	public static void rightClickWithPainting(RightClickBlock event) {
		doPaintingLogic(event, true);
	}

	@SubscribeEvent
	public static void rightClickWithPainting(RightClickItem event) {
		doPaintingLogic(event, false);
	}

	public static void doPaintingLogic(PlayerInteractEvent event, boolean block) {
		if (event.getItemStack().is(Items.PAINTING) && GalleryConfig.COMMON.paintingSelector.get()) {
			if (block && GalleryConfig.COMMON.paintingSelectorOpensOnBlocks.get() && !event.getEntity().isCrouching())
				return;

			if (!block && GalleryConfig.COMMON.paintingSelectorRequiresCrouching.get() && !event.getEntity().isCrouching())
				return;

			if (!event.getLevel().isClientSide()) {
				event.getEntity().openMenu(new SimpleMenuProvider((id, inventory, player) -> new PaintingSelectorMenu(id, inventory), CONTAINER_TITLE));
			}
			event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide()));
			event.setCanceled(true);
		}
	}
}
