package io.github.pseudodistant.minemii.mixin.client;

import io.github.pseudodistant.minemii.WiiMoteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Shadow @Final private MinecraftClient client;

	@ModifyVariable(method = "render", at = @At("STORE"), ordinal = 0)
	private int wiiInsteadOfMouse(int i) {
		if (((WiiMoteClient)this.client).getWiimote() != null && client.currentScreen != null) {
			return (int) ((double) (1023 - ((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[0]) / (double)1024 * (double)MinecraftClient.getInstance().getWindow().getScaledWidth()) + 8;
		}
		return (int)(this.client.mouse.getX() * (double)this.client.getWindow().getScaledWidth() / (double)this.client.getWindow().getWidth());
	}

	@ModifyVariable(method = "render", at = @At("STORE"), ordinal = 1)
	private int wiiInsteadOfMouseAlso(int j) {
		if (((WiiMoteClient)this.client).getWiimote() != null && client.currentScreen != null) {
			return (int) (((double)((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[1]) / (double)768 * (double)MinecraftClient.getInstance().getWindow().getScaledHeight());
		}
		return (int)(this.client.mouse.getY() * (double)this.client.getWindow().getScaledHeight() / (double)this.client.getWindow().getHeight());
	}
}
