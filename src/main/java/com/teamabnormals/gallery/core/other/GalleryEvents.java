package com.teamabnormals.gallery.core.other;

import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.network.chat.Component;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Gallery.MOD_ID)
public class GalleryEvents {
	private static final Component CONTAINER_TITLE = Component.translatable("container.gallery.painting_selector");

	@SubscribeEvent
	public static void rightClickEntity(RightClickItem event) {
		if (event.getItemStack().is(Items.PAINTING) && !event.getLevel().isClientSide()) {
			event.getEntity().openMenu(new SimpleMenuProvider((id, inventory, player) -> new PaintingSelectorMenu(id, inventory), CONTAINER_TITLE));

		}
	}
}
