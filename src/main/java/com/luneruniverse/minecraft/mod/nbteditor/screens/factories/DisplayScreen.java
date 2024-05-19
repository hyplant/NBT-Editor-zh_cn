package com.luneruniverse.minecraft.mod.nbteditor.screens.factories;

import java.nio.file.Path;
import java.util.List;

import com.luneruniverse.minecraft.mod.nbteditor.localnbt.LocalBlock;
import com.luneruniverse.minecraft.mod.nbteditor.localnbt.LocalEntity;
import com.luneruniverse.minecraft.mod.nbteditor.localnbt.LocalItem;
import com.luneruniverse.minecraft.mod.nbteditor.localnbt.LocalNBT;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.MVMisc;
import com.luneruniverse.minecraft.mod.nbteditor.multiversion.TextInst;
import com.luneruniverse.minecraft.mod.nbteditor.nbtreferences.NBTReference;
import com.luneruniverse.minecraft.mod.nbteditor.nbtreferences.itemreferences.ItemReference;
import com.luneruniverse.minecraft.mod.nbteditor.screens.LocalEditorScreen;
import com.luneruniverse.minecraft.mod.nbteditor.screens.widgets.FormattedTextFieldWidget;
import com.luneruniverse.minecraft.mod.nbteditor.screens.widgets.ImageToLoreWidget;
import com.luneruniverse.minecraft.mod.nbteditor.util.Lore;
import com.luneruniverse.minecraft.mod.nbteditor.util.Lore.LoreConsumer;

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.util.Formatting;

public class DisplayScreen<L extends LocalNBT> extends LocalEditorScreen<L, NBTReference<L>> {
	
	private FormattedTextFieldWidget name;
	private FormattedTextFieldWidget lore;
	
	public DisplayScreen(NBTReference<L> ref) {
		super(TextInst.of("Display"), ref);
	}
	
	@Override
	protected void initEditor() {
		MVMisc.setKeyboardRepeatEvents(true);
		
		Style baseNameStyle = Style.EMPTY;
		if (localNBT instanceof LocalItem item)
			baseNameStyle = baseNameStyle.withFormatting(Formatting.ITALIC, item.getItem().getRarity().formatting);
		else if (localNBT instanceof LocalBlock)
			;
		else if (localNBT instanceof LocalEntity)
			baseNameStyle = baseNameStyle.withFormatting(Formatting.WHITE);
		else
			throw new IllegalStateException("DisplayScreen doesn't support " + localNBT.getClass().getName());
		
		name = FormattedTextFieldWidget.create(name, 16, 64, width - 32, 24 + textRenderer.fontHeight * 3,
				localNBT.getName(), false, baseNameStyle, text -> {
			localNBT.setName(text);
			checkSave();
		}).setOverscroll(false).setShadow(localNBT instanceof LocalItem);
		
		int nextY = 64 + 24 + textRenderer.fontHeight * 3 + 4;
		
		if (localNBT instanceof LocalItem item) {
			lore = FormattedTextFieldWidget.create(lore, 16, nextY, width - 32, height - 16 - 20 - 4 - nextY,
					new Lore(item.getItem()).getLore(), Style.EMPTY.withFormatting(Formatting.ITALIC, Formatting.DARK_PURPLE), lines -> {
				if (lines.size() == 1 && lines.get(0).getString().isEmpty())
					new Lore(item.getItem()).clearLore();
				else
					new Lore(item.getItem()).setAllLines(lines);
				checkSave();
			});
			addSelectableChild(name);
			addSelectableChild(lore);
			addDrawableChild(MVMisc.newButton(16, height - 16 - 20, 100, 20, TextInst.translatable("nbteditor.hide_flags"),
					btn -> closeSafely(() -> client.setScreen(new HideFlagsScreen((ItemReference) ref)))));
			addDrawable(lore);
		} else
			addSelectableChild(name);
		
		if (localNBT instanceof LocalEntity entity) {
			addDrawableChild(MVMisc.newButton(16, nextY, 150, 20,
					TextInst.translatable("nbteditor.display.custom_name_visible." +
							(entity.getOrCreateNBT().getBoolean("CustomNameVisible") ? "enabled" : "disabled")), btn -> {
				boolean customNameVisible = !entity.getOrCreateNBT().getBoolean("CustomNameVisible");
				entity.getNBT().putBoolean("CustomNameVisible", customNameVisible);
				btn.setMessage(TextInst.translatable("nbteditor.display.custom_name_visible." + (customNameVisible ? "enabled" : "disabled")));
				checkSave();
			}));
		}
	}
	
	@Override
	protected void renderEditor(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		matrices.push();
		matrices.translate(0.0, 0.0, 1.0);
		name.render(matrices, mouseX, mouseY, delta);
		matrices.pop();
		
		renderTip(matrices, "nbteditor.formatted_text.tip");
	}
	
	@Override
	public void filesDragged(List<Path> paths) {
		if (!(localNBT instanceof LocalItem item))
			return;
		LoreConsumer loreConsumer = LoreConsumer.createAppend(item.getItem());
		ImageToLoreWidget.openImportFiles(paths, file -> loreConsumer, () -> lore.setText(new Lore(item.getItem()).getLore()));
	}
	
	@Override
	public void removed() {
		MVMisc.setKeyboardRepeatEvents(false);
	}
	
}
