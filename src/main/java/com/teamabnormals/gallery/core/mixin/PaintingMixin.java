package com.teamabnormals.gallery.core.mixin;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(Painting.class)
public abstract class PaintingMixin extends HangingEntity {

	protected PaintingMixin(EntityType<? extends HangingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Shadow
	public abstract Holder<PaintingVariant> getVariant();

	@Redirect(method = "dropItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/Painting;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
	public ItemEntity spawnAtLocation(Painting painting, ItemLike item, @Nullable Entity entity) {
		if (entity instanceof Player player) {
			ItemStack tool = player.getItemInHand(player.getUsedItemHand());
			if (tool.is(Tags.Items.SHEARS) || EnchantmentHelper.hasSilkTouch(tool)) {
				ItemStack stack = new ItemStack(item);
				CompoundTag compoundtag = stack.getOrCreateTagElement("EntityTag");
				Painting.storeVariant(compoundtag, this.getVariant());
				return this.spawnAtLocation(stack);
			}
		}

		return this.spawnAtLocation(item);
	}
}
