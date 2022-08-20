package io.github.pseudodistant.minemii.mixin.client;

import io.github.pseudodistant.minemii.WiiMoteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(DebugHud.class)
public class DebugHudMixin {
	String nullSafeExtension() {
		return ((WiiMoteClient)MinecraftClient.getInstance()).getWiimote().getExtension() != null ? ((WiiMoteClient)MinecraftClient.getInstance()).getWiimote().getExtension().toString() : "None";
	}

	@Inject(method = "getRightText", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILHARD)
	private void injectWiimoteValues(CallbackInfoReturnable<List<String>> cir, long l, long m, long n, long o, List<String> list) {
		if (((WiiMoteClient) MinecraftClient.getInstance()).getWiimote() != null) {
			list.add("");
			list.add("Wiimote readout:");
			list.add("X: " + ((WiiMoteClient) MinecraftClient.getInstance()).getAccel()[0]);
			list.add("Y: " + ((WiiMoteClient) MinecraftClient.getInstance()).getAccel()[1]);
			list.add("Z: " + ((WiiMoteClient) MinecraftClient.getInstance()).getAccel()[2]);
			list.add("Extension: " + nullSafeExtension());
			list.add("Buttons:");
			list.add("A: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().a + ", B: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().b);
			list.add("1: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().one + ", 2: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().two);
			list.add("+: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().plus + ", -: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().minus);
			list.add("Up: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().up + ", Down: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().down);
			list.add("Left: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().left + ", Right: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().right);
			list.add("Home: " + ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().home);
			list.add("P1 pointer coords: " + ((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[0] + ", " + ((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[1]);
		}
	}
}
