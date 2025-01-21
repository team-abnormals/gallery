package com.teamabnormals.gallery.client.gui.screens.inventory;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamabnormals.gallery.common.inventory.PaintingSelectorMenu;
import com.teamabnormals.gallery.core.Gallery;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.compress.utils.Lists;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class PaintingSelectorScreen extends AbstractContainerScreen<PaintingSelectorMenu> {
	private static final ResourceLocation BG_LOCATION = new ResourceLocation(Gallery.MOD_ID, "textures/gui/painting_selector.png");
	private float scrollOffs;
	private boolean scrolling;
	private int startIndex;
	private EditBox searchBox;
	private boolean ignoreTextInput;

	public PaintingSelectorScreen(PaintingSelectorMenu p_99310_, Inventory p_99311_, Component p_99312_) {
		super(p_99310_, p_99311_, p_99312_);
		--this.titleLabelY;
		this.imageHeight = 126;
	}

	public void render(GuiGraphics p_281735_, int p_282517_, int p_282840_, float p_282389_) {
		super.render(p_281735_, p_282517_, p_282840_, p_282389_);
		this.renderTooltip(p_281735_, p_282517_, p_282840_);
	}

	protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
		p_281635_.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
	}

	public void init() {
		super.init();
		this.searchBox = new EditBox(this.font, this.leftPos + 89, this.topPos + 10, 65, 9, Component.translatable("itemGroup.search"));
		this.searchBox.setMaxLength(50);
		this.searchBox.setBordered(false);
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(16777215);
		this.searchBox.setCanLoseFocus(true);
		this.searchBox.setFocused(false);
		this.addWidget(this.searchBox);

		this.refreshSearchResults();
	}

	public void resize(Minecraft p_98595_, int p_98596_, int p_98597_) {
		float scrollOffset = this.scrollOffs;
		int startIndex = this.startIndex;
		String s = this.searchBox.getValue();
		Holder<PaintingVariant> selected = this.menu.getSelectedPainting();
		this.init(p_98595_, p_98596_, p_98597_);
		this.searchBox.setValue(s);
		if (!this.searchBox.getValue().isEmpty()) {
			this.refreshSearchResults();
		}

		this.menu.setSelectedPainting(selected);

		this.scrollOffs = scrollOffset;
		this.startIndex = startIndex;
	}

	public boolean charTyped(char p_98521_, int p_98522_) {
		if (this.ignoreTextInput) {
			return false;
		} else {
			String s = this.searchBox.getValue();
			if (this.searchBox.charTyped(p_98521_, p_98522_)) {
				if (!Objects.equals(s, this.searchBox.getValue())) {
					this.refreshSearchResults();
				}

				return true;
			} else {
				return false;
			}
		}
	}

	public boolean keyPressed(int p_98547_, int p_98548_, int p_98549_) {
		this.ignoreTextInput = false;

		boolean flag1 = InputConstants.getKey(p_98547_, p_98548_).getNumericKeyValue().isPresent();
		if (flag1 && this.checkHotbarKeyPressed(p_98547_, p_98548_)) {
			this.ignoreTextInput = true;
			return true;
		} else {
			String s = this.searchBox.getValue();
			if (this.searchBox.keyPressed(p_98547_, p_98548_, p_98549_)) {
				if (!Objects.equals(s, this.searchBox.getValue())) {
					this.refreshSearchResults();
				}

				return true;
			} else {
				return this.searchBox.isFocused() && this.searchBox.isVisible() && p_98547_ != 256 ? true : super.keyPressed(p_98547_, p_98548_, p_98549_);
			}
		}
	}

	public boolean keyReleased(int p_98612_, int p_98613_, int p_98614_) {
		this.ignoreTextInput = false;
		return super.keyReleased(p_98612_, p_98613_, p_98614_);
	}

	private void refreshSearchResults() {
		this.menu.setupPaintingLists();

		String s = this.searchBox.getValue().toLowerCase(Locale.ROOT);
		if (!s.isEmpty()) {
			List<Holder<PaintingVariant>> searched = Lists.newArrayList();
			for (Holder<PaintingVariant> variant : this.menu.getAllPaintings()) {
				variant.unwrapKey().ifPresent(key -> {
					String title = Component.translatable(key.location().toLanguageKey("painting", "title")).getString().toLowerCase(Locale.ROOT);
					String author = Component.translatable(key.location().toLanguageKey("painting", "author")).getString().toLowerCase(Locale.ROOT);
					String dimensions = Component.translatable("painting.dimensions", Mth.positiveCeilDiv(variant.value().getWidth(), 16), Mth.positiveCeilDiv(variant.value().getHeight(), 16)).getString().toLowerCase(Locale.ROOT);
					if (title.contains(s) || author.contains(s) || dimensions.contains(s)) {
						searched.add(variant);
					}
				});
			}

			this.menu.paintings = searched;
		}

		this.scrollOffs = 0.0F;
		this.startIndex = 0;
	}


	public void containerTick() {
		super.containerTick();
		if (this.minecraft != null) {
			this.searchBox.tick();
		}
	}

	protected void renderBg(GuiGraphics gui, float p_282453_, int p_282940_, int p_282328_) {
		this.renderBackground(gui);
		int i = this.leftPos;
		int j = this.topPos;
		gui.blit(BG_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
		int k = (int) (74.0F * this.scrollOffs);
		gui.blit(BG_LOCATION, i + 155, j + 22 + k, 176 + (this.isScrollBarActive() ? 0 : 12), 0, 12, 15);
		int l = this.leftPos + 88;
		int i1 = this.topPos + 21;
		int j1 = this.startIndex + 20;
		this.renderButtons(gui, p_282940_, p_282328_, l, i1, j1);
		this.renderRecipes(gui, l, i1, j1);

		if (this.menu.getSelectedPainting() != null) {
			PaintingVariant variant = this.menu.getSelectedPainting().get();
			gui.blit(i + 13, j + 19, 0, variant.getWidth(), variant.getHeight(), Minecraft.getInstance().getPaintingTextures().get(variant));
		}
		this.searchBox.render(gui, p_282940_, p_282328_, p_282453_);
	}

	protected void renderTooltip(GuiGraphics gui, int p_283157_, int p_282258_) {
		super.renderTooltip(gui, p_283157_, p_282258_);
		int i = this.leftPos + 88;
		int j = this.topPos + 21;
		int k = this.startIndex + 20;
		List<Holder<PaintingVariant>> list = this.menu.getPaintings();

		for (int l = this.startIndex; l < k && l < this.menu.getNumPaintings(); ++l) {
			int i1 = l - this.startIndex;
			int j1 = i + i1 % 4 * 16;
			int k1 = j + i1 / 4 * 18 + 2;
			if (p_283157_ >= j1 && p_283157_ < j1 + 16 && p_282258_ >= k1 && p_282258_ < k1 + 18) {
				ItemStack stack = new ItemStack(Items.PAINTING);
				CompoundTag tag = stack.getOrCreateTagElement("EntityTag");
				Painting.storeVariant(tag, list.get(l));
				gui.renderTooltip(this.font, stack, p_283157_, p_282258_);
			}
		}

	}

	private void renderButtons(GuiGraphics gui, int p_282136_, int p_282147_, int p_281987_, int p_281276_, int p_282688_) {
		for (int i = this.startIndex; i < p_282688_ && i < this.menu.getNumPaintings(); ++i) {
			int j = i - this.startIndex;
			int k = p_281987_ + j % 4 * 16;
			int l = j / 4;
			int i1 = p_281276_ + l * 18 + 2;
			int j1 = this.imageHeight;
			if (this.menu.getSelectedPainting() != null && i == this.menu.getPaintings().indexOf(this.menu.getSelectedPainting())) {
				j1 += 18;
			} else if (p_282136_ >= k && p_282147_ >= i1 && p_282136_ < k + 16 && p_282147_ < i1 + 18) {
				j1 += 36;
			}

			gui.blit(BG_LOCATION, k, i1 - 1, 0, j1, 16, 18);
		}

	}

	private void renderRecipes(GuiGraphics gui, int p_282658_, int p_282563_, int slots) {
		List<Holder<PaintingVariant>> list = this.menu.getPaintings();

		for (int i = this.startIndex; i < slots && i < this.menu.getNumPaintings(); ++i) {
			int j = i - this.startIndex;
			int k = p_282658_ + j % 4 * 16;
			int l = j / 4;
			int i1 = p_282563_ + l * 18 + 2;
			ItemStack stack = new ItemStack(Items.PAINTING);
			CompoundTag tag = stack.getOrCreateTagElement("EntityTag");
			Painting.storeVariant(tag, list.get(i));
			gui.renderItem(stack, k, i1);
		}

	}

	public boolean mouseClicked(double p_99318_, double p_99319_, int p_99320_) {
		this.scrolling = false;
		int i = this.leftPos + 88;
		int j = this.topPos + 21;
		int k = this.startIndex + 20;

		for (int l = this.startIndex; l < k; ++l) {
			int i1 = l - this.startIndex;
			double d0 = p_99318_ - (double) (i + i1 % 4 * 16);
			double d1 = p_99319_ - (double) (j + i1 / 4 * 18);
			if (d0 >= 0.0D && d1 >= 0.0D && d0 < 16.0D && d1 < 18.0D && this.menu.clickMenuButton(this.minecraft.player, l)) {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
				this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
				this.searchBox.setFocused(false);
				return true;
			}
		}

		i = this.leftPos + 155;
		j = this.topPos + 9;
		if (p_99318_ >= (double) i && p_99318_ < (double) (i + 20) && p_99319_ >= (double) j && p_99319_ < (double) (j + 112)) {
			this.scrolling = true;
			this.searchBox.setFocused(false);
		}

		return super.mouseClicked(p_99318_, p_99319_, p_99320_);
	}

	public boolean mouseDragged(double p_99322_, double p_99323_, int p_99324_, double p_99325_, double p_99326_) {
		if (this.scrolling && this.isScrollBarActive()) {
			int i = this.topPos + 14;
			int j = i + 92;
			this.scrollOffs = ((float) p_99323_ - (float) i - 7.5F) / ((float) (j - i) - 15.0F);
			this.scrollOffs = Mth.clamp(this.scrollOffs, 0.0F, 1.0F);
			this.startIndex = (int) ((double) (this.scrollOffs * (float) this.getOffscreenRows()) + 0.5D) * 4;
			return true;
		} else {
			return super.mouseDragged(p_99322_, p_99323_, p_99324_, p_99325_, p_99326_);
		}
	}

	public boolean mouseScrolled(double p_99314_, double p_99315_, double p_99316_) {
		if (this.isScrollBarActive()) {
			int i = this.getOffscreenRows();
			float f = (float) p_99316_ / (float) i;
			this.scrollOffs = Mth.clamp(this.scrollOffs - f, 0.0F, 1.0F);
			this.startIndex = (int) ((double) (this.scrollOffs * (float) i) + 0.5D) * 4;
		}

		return true;
	}

	private boolean isScrollBarActive() {
		return this.menu.getNumPaintings() > 20;
	}

	protected int getOffscreenRows() {
		return (this.menu.getNumPaintings() + 4 - 1) / 4 - 5;
	}
}