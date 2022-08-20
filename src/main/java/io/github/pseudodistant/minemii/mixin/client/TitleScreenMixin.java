package io.github.pseudodistant.minemii.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.pseudodistant.minemii.WiiMoteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
	private static Identifier P1TEXTURE = new Identifier("minemii", "textures/p1hand.png");

	protected TitleScreenMixin(Text title) {
		super(title);
	}

	@Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
	private void injectWiiCursor(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
		RenderSystem.setShaderTexture(0, P1TEXTURE);

		drawTexture(matrices, (int) ((1025 - ((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[0]) / (double)1024 * (double)MinecraftClient.getInstance().getWindow().getScaledWidth()),
				(int) ((((WiiMoteClient) MinecraftClient.getInstance()).getIrCoords()[1]) / (double)768 * (double)MinecraftClient.getInstance().getWindow().getScaledHeight()),
				0, 0, 32, 32, 32, 32);
	}
}
