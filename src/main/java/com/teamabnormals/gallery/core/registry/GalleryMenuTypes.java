package com.teamabnormals.gallery.core.registry;

import com.teamabnormals.gallery.client.gui.screens.inventory.PaintingSelectorScreen;
import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class GalleryMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Gallery.MOD_ID);

	public static final RegistryObject<MenuType<PaintingSelectorMenu>> PAINTING_SELECTOR = MENU_TYPES.register("painting_selector", () -> new MenuType<>(PaintingSelectorMenu::new, FeatureFlags.VANILLA_SET));

	public static void registerScreenFactories() {
		MenuScreens.register(PAINTING_SELECTOR.get(), PaintingSelectorScreen::new);
	}
}