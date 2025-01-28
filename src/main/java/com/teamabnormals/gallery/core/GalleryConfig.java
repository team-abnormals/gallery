package com.teamabnormals.gallery.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import org.apache.commons.lang3.tuple.Pair;

public class GalleryConfig {

	public static class Common {
		public final ConfigValue<Boolean> paintingsDropVariants;
		public final ConfigValue<Boolean> requiresShears;
		public final ConfigValue<Boolean> requiresSilkTouch;

		public final ConfigValue<Boolean> paintingSelector;
		public final ConfigValue<Boolean> paintingSelectorRequiresCrouching;
		public final ConfigValue<Boolean> paintingSelectorOpensOnBlocks;

		Common(ForgeConfigSpec.Builder builder) {
			builder.push("paintings");
			builder.push("painting_drops");
			paintingsDropVariants = builder.comment("If Paintings drop their variants when broken").define("Paintings drop variants", true);
			requiresShears = builder.comment("If Paintings only drop their variants when broken with Shears").define("Paintings drop variants with Shears", true);
			requiresSilkTouch = builder.comment("If Paintings only drop their variants when broken with Silk Touch").define("Paintings drop variants with Silk Touch", true);
			builder.pop();
			builder.push("painting_selector");
			paintingSelector = builder.comment("If right-clicking with a Painting item opens up a Painting selector menu").define("Painting selector", true);
			paintingSelectorRequiresCrouching = builder.comment("If you must be crouching to open the Painting selector").define("Painting selector requires crouching", false);
			paintingSelectorOpensOnBlocks = builder.comment("If the Painting selector can be opened when crouching while right-clicking a block").define("Painting selector opens on blocks", true);
			builder.pop();
			builder.pop();
		}
	}

	public static class Client {
		public final ConfigValue<Boolean> paintingIcons;
		public boolean paintingIconsEnabled;

		Client(ForgeConfigSpec.Builder builder) {
			builder.push("paintings");
			paintingIcons = builder.comment("If Paintings have item icons").define("Painting icons", true);
			builder.pop();
		}

		public void load() {
			this.paintingIconsEnabled = this.paintingIcons.get();
		}
	}

	public static final ForgeConfigSpec COMMON_SPEC;
	public static final Common COMMON;

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;

	static {
		final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Common::new);
		COMMON_SPEC = commonSpecPair.getRight();
		COMMON = commonSpecPair.getLeft();

		final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
	}
}