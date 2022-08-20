package io.github.pseudodistant.minemii.mixin.client;

import io.github.pseudodistant.minemii.WiiMoteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClickableWidget.class)
public abstract class ClickableWidgetMixin {
	@Shadow protected boolean hovered;

	@Shadow public int x;

	@Shadow public int y;

	@Shadow protected int width;

	@Shadow protected int height;

	@Shadow public abstract void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta);
 //
	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;renderButton(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
	private void WiimoteHover(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		if (!this.hovered && MinecraftClient.getInstance().currentScreen != null) {
			this.hovered = (1025 - ((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[0]) / 1024 * MinecraftClient.getInstance().getWindow().getScaledWidth() >= this.x
					&& (((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[1]) / 768 * MinecraftClient.getInstance().getWindow().getScaledHeight() >= this.y
					&& (1025 - ((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[0]) / 1024 * MinecraftClient.getInstance().getWindow().getScaledWidth() < this.x + this.width
					&& (((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[1]) / 768 * MinecraftClient.getInstance().getWindow().getScaledHeight() < this.y + this.height;
		}
	}

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/ClickableWidget;renderButton(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
	private void wiiMotePos(ClickableWidget instance, MatrixStack matrices, int mouseX, int mouseY, float delta) {
		if (MinecraftClient.getInstance().currentScreen != null){
			renderButton(matrices, (int) (MinecraftClient.getInstance().currentScreen.width * ((-(((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[0]) + 1024) / 1024)), (int) (MinecraftClient.getInstance().currentScreen.height * ((-(((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[0]) + 1024) / 1024)), delta);
		} else {
			renderButton(matrices, mouseX, mouseY, delta);
		}
	}
}
