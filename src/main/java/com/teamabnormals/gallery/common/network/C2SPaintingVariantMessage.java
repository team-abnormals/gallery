package com.teamabnormals.gallery.common.network;

import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SPaintingVariantMessage {
	private final ResourceLocation location;

	public C2SPaintingVariantMessage(ResourceLocation newIndex) {
		this.location = newIndex;
	}

	public void serialize(FriendlyByteBuf buffer) {
		buffer.writeResourceLocation(location);
	}

	public static C2SPaintingVariantMessage deserialize(FriendlyByteBuf buf) {
		return new C2SPaintingVariantMessage(buf.readResourceLocation());
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		NetworkEvent.Context context = ctx.get();

		if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
			context.enqueueWork(() -> {
				Player player = context.getSender();
				if (player != null) {
					if (player.containerMenu instanceof PaintingSelectorMenu paintingSelectorMenu) {
						paintingSelectorMenu.setSelectedPainting(this.location);
					}
				}
			});
			context.setPacketHandled(true);
		}
	}
}