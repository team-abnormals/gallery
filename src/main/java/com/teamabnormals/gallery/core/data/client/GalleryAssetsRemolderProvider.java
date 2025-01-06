package com.teamabnormals.gallery.core.data.client;

import com.google.gson.JsonObject;
import com.teamabnormals.blueprint.common.remolder.Remolder;
import com.teamabnormals.blueprint.common.remolder.RemolderTypes;
import com.teamabnormals.blueprint.common.remolder.SequenceRemolder;
import com.teamabnormals.blueprint.common.remolder.data.RemolderProvider;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.add;
import static com.teamabnormals.blueprint.common.remolder.RemolderTypes.replace;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.target;
import static com.teamabnormals.blueprint.common.remolder.data.DynamicReference.value;

public final class GalleryAssetsRemolderProvider extends RemolderProvider {

	public GalleryAssetsRemolderProvider(PackOutput packOutput, CompletableFuture<Provider> lookupProvider) {
		super(Gallery.MOD_ID, PackOutput.Target.RESOURCE_PACK, packOutput, lookupProvider);
	}

	@Override
	protected void registerEntries(HolderLookup.Provider provider) {
		List<Remolder> remolders = Lists.newArrayList();
		provider.lookup(Registries.PAINTING_VARIANT).get().listElementIds().forEach(variant -> {
			JsonObject ret = new JsonObject();
			JsonObject predicatesJson = new JsonObject();
			predicatesJson.addProperty(variant.location().toString(), 1.0F);
			ret.add("predicate", predicatesJson);
			ret.addProperty("model", new ResourceLocation(this.modId, "item/painting/" + variant.location().getPath()).toString());

			remolders.add(add(
					target("overrides[]"),
					value(ops -> ret)
			));
		});

		this.entry("painting").path("minecraft:models/item/painting").remolder(new SequenceRemolder((remolders)));
	}

}