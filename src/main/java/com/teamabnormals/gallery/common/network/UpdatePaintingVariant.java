package com.teamabnormals.gallery.common.network;

import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record UpdatePaintingVariant(ResourceLocation location) implements CustomPacketPayload {
	public static final Type<UpdatePaintingVariant> TYPE = new Type<>(Gallery.location("update_painting_variant"));

	public static final StreamCodec<ByteBuf, UpdatePaintingVariant> STREAM_CODEC = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, UpdatePaintingVariant::location,
			UpdatePaintingVariant::new
	);

	public static void handle(UpdatePaintingVariant payload, IPayloadContext context) {
		context.enqueueWork(() -> {
			Player player = context.player();
			if (player.containerMenu instanceof PaintingSelectorMenu paintingSelectorMenu) {
				paintingSelectorMenu.setSelectedPainting(payload.location());
			}
		}).exceptionally(e -> null);
	}

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}