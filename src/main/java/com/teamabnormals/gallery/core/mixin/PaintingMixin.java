package com.teamabnormals.gallery.core.mixin;

import com.teamabnormals.gallery.core.GalleryConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Painting.class)
public abstract class PaintingMixin extends HangingEntity {

	protected PaintingMixin(EntityType<? extends HangingEntity> entityType, Level level) {
		super(entityType, level);
	}

	@Redirect(method = "dropItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/Painting;spawnAtLocation(Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"))
	public ItemEntity spawnAtLocation(Painting painting, ItemLike item, @Nullable Entity entity) {
		ItemStack stack = new ItemStack(item);
		if (entity instanceof Player player && GalleryConfig.COMMON.paintingsDropVariants.get()) {
			ItemStack tool = player.getItemInHand(player.getUsedItemHand());
			boolean shears = GalleryConfig.COMMON.requiresShears.get();
			boolean silkTouch = GalleryConfig.COMMON.requiresSilkTouch.get();
			if ((!shears && !silkTouch) || (shears && tool.is(Tags.Items.SHEARS)) || (silkTouch && EnchantmentHelper.hasSilkTouch(tool))) {
				CompoundTag tag = stack.getOrCreateTagElement("EntityTag");
				Painting.storeVariant(tag, ((Painting) (Object) this).getVariant());

				if (!player.level().isClientSide()) {
					tool.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(EquipmentSlot.MAINHAND));
				}

				return this.spawnAtLocation(stack);
			}
		}

		stack.getOrCreateTagElement("EntityTag");
		return this.spawnAtLocation(stack);
	}

	@Inject(method = "getPickResult", at = @At("RETURN"), cancellable = true)
	public void getPickResult(CallbackInfoReturnable<ItemStack> cir) {
		ItemStack stack = new ItemStack(Items.PAINTING);
		CompoundTag tag = stack.getOrCreateTagElement("EntityTag");
		Painting.storeVariant(tag, ((Painting) (Object) this).getVariant());
		cir.setReturnValue(stack);
	}
}
