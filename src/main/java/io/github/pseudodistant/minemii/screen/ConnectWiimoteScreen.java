package io.github.pseudodistant.minemii.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.pseudodistant.minemii.WiiMoteClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

public class ConnectWiimoteScreen extends Screen {

	private static final Identifier TEXTURE = new Identifier("minemii","textures/wiimote_connect_bg.png");
	boolean onStart;
	public ConnectWiimoteScreen(boolean bl) {
		super(new TranslatableText("minemii.connectWiiRemote"));
		onStart = bl;
	}

	@Override
	public boolean isPauseScreen() {
		return true;
	}

	@Override
	protected void init() {
		if (!((WiiMoteClient) MinecraftClient.getInstance()).isDiscoveryEnabled()) {
			((WiiMoteClient) MinecraftClient.getInstance()).setDiscovery(true);
			((WiiMoteClient) MinecraftClient.getInstance()).startWiimoteDiscovery(onStart);
		}
		this.addDrawableChild(new ButtonWidget(0, 0, 256, 20, new TranslatableText("minemii.requestnewinquiry"), new ButtonWidget.PressAction() {
			@Override
			public void onPress(ButtonWidget button) {
				((WiiMoteClient) MinecraftClient.getInstance()).setDiscovery(true);
				((WiiMoteClient) MinecraftClient.getInstance()).startWiimoteDiscovery(onStart);
			}
		}));
		this.addDrawableChild(new ButtonWidget(0, 24, 256, 20, new TranslatableText("minemii.toTitleScreen"), new ButtonWidget.PressAction() {
			@Override
			public void onPress(ButtonWidget button) {
				MinecraftClient.getInstance().setScreen(new TitleScreen(false));
			}
		}));
	}

	@Override
	public void renderBackground(MatrixStack matrices) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0f,1.0f,1.0f,1.0f);
		RenderSystem.setShaderTexture(0,TEXTURE);
		int x = (width - 1280) /2;
		int y = (height - 720) / 2;
		drawTexture(matrices, x, y, 0, 0, 1280, 720);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		this.renderBackground(matrices);
		drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 40, 16777215);

		super.render(matrices, mouseX, mouseY, delta);
	}
}
