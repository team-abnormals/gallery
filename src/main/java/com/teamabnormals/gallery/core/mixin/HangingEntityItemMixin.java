package com.teamabnormals.gallery.core.mixin;

import net.minecraft.world.item.HangingEntityItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HangingEntityItem.class)
public abstract class HangingEntityItemMixin {

	@Redirect(method = "useOn", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V"))
	public void useOn(ItemStack stack, int size, UseOnContext context) {
		if (!context.getLevel().isClientSide()) {
			stack.shrink(size);
		}
	}
}
