package com.teamabnormals.gallery.core.registry;

import com.teamabnormals.gallery.client.gui.screens.inventory.PaintingSelectorScreen;
import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = Gallery.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class GalleryMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Gallery.MOD_ID);

	public static final DeferredHolder<MenuType<?>, MenuType<PaintingSelectorMenu>> PAINTING_SELECTOR = MENU_TYPES.register("painting_selector", () -> new MenuType<>(PaintingSelectorMenu::new, FeatureFlags.VANILLA_SET));

	@SubscribeEvent
	public static void registerScreens(RegisterMenuScreensEvent event) {
		event.register(GalleryMenuTypes.PAINTING_SELECTOR.get(), PaintingSelectorScreen::new);
	}
}