package com.luneruniverse.minecraft.mod.nbteditor.multiversion;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ExtendableButtonWidget extends PressableWidget {
	
	@FunctionalInterface
	public interface PressAction {
		public void onPress(ExtendableButtonWidget button);
	}
	
	private final PressAction onPress;
	private final MultiVersionTooltip tooltip;
	
	public ExtendableButtonWidget(int x, int y, int width, int height, Text text, PressAction onPress, MultiVersionTooltip tooltip) {
		super(x, y, width, height, text);
		this.onPress = onPress;
		this.tooltip = tooltip;
		if (tooltip != null) {
			switch (Version.get()) {
				case v1_20, v1_19_4, v1_19_3 -> setTooltip(tooltip.toNewTooltip());
				case v1_19, v1_18_v1_17 -> {}
			}
		}
	}
	public ExtendableButtonWidget(int x, int y, int width, int height, Text text, PressAction onPress) {
		this(x, y, width, height, text, onPress, null);
	}
	
	@Override
	public void onPress() {
		onPress.onPress(this);
	}
	
	@Override
	protected void appendClickableNarrations(NarrationMessageBuilder builder) {
		appendDefaultNarrations(builder);
	}
	
	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		MultiVersionDrawableHelper.super_render(ExtendableButtonWidget.class, this, matrices, mouseX, mouseY, delta);
	}
	public final void method_25394(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		render(matrices, mouseX, mouseY, delta);
	}
	@Override
	public final void render(DrawContext context, int mouseX, int mouseY, float delta) {
		render(MultiVersionDrawableHelper.getMatrices(context), mouseX, mouseY, delta);
	}
	
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		switch (Version.get()) {
			case v1_20 -> super.renderButton(MultiVersionDrawableHelper.getDrawContext(matrices), mouseX, mouseY, delta);
			case v1_19_4 -> super_renderButton("method_48579", matrices, mouseX, mouseY, delta);
			case v1_19_3 -> super_renderButton("method_25359", matrices, mouseX, mouseY, delta);
			case v1_19, v1_18_v1_17 -> {
				super_renderButton("method_25359", matrices, mouseX, mouseY, delta);
				if (isSelected()) // Actually isHovered, got renamed
					method_25352(matrices, mouseX, mouseY);
			}
		}
	}
	@Override
	protected final void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
		renderButton(MultiVersionDrawableHelper.getMatrices(context), mouseX, mouseY, delta);
	}
	public final void method_48579(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		renderButton(matrices, mouseX, mouseY, delta);
	}
	public final void method_25359(MatrixStack matrices, int mouseX, int mouseY, float delta) { // renderButton <= 1.19.3
		renderButton(matrices, mouseX, mouseY, delta);
	}
	private void super_renderButton(String intermediary, MatrixStack matrices, int mouseX, int mouseY, float delta) {
		try {
			MethodHandles.lookup().findSpecial(ClickableWidget.class, intermediary,
					MethodType.methodType(void.class, MatrixStack.class, int.class, int.class, float.class), ExtendableButtonWidget.class)
					.invoke(this, matrices, mouseX, mouseY, delta);
		} catch (Throwable e) {
			throw new RuntimeException("Error calling super.renderButton (" + intermediary + ")", e);
		}
	}
	
	public void method_25352(MatrixStack matrices, int mouseX, int mouseY) { // renderTooltip
		if (tooltip != null)
			tooltip.render(matrices, mouseX, mouseY);
	}
	
}
