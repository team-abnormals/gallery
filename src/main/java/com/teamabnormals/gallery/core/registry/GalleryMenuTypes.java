package com.teamabnormals.gallery.core.registry;

import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class GalleryMenuTypes {
	public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, Gallery.MOD_ID);

	public static final DeferredHolder<MenuType<?>, MenuType<PaintingSelectorMenu>> PAINTING_SELECTOR = MENU_TYPES.register("painting_selector", () -> new MenuType<>(PaintingSelectorMenu::new, FeatureFlags.VANILLA_SET));
}