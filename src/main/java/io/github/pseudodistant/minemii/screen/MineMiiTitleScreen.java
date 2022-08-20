package io.github.pseudodistant.minemii.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.pseudodistant.minemii.MineMii;
import io.github.pseudodistant.minemii.WiiMoteClient;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.CubeMapRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.RotatingCubeMapRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.sound.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

public class MineMiiTitleScreen extends Screen {
	private final RotatingCubeMapRenderer backgroundRenderer;
	public static final CubeMapRenderer PANORAMA_CUBE_MAP = new CubeMapRenderer(new Identifier("textures/gui/title/background/panorama"));
	private static final Identifier PANORAMA_OVERLAY = new Identifier("textures/gui/title/background/panorama_overlay.png");
	private static final Identifier MINECRAFT_TITLE_TEXTURE = new Identifier("textures/gui/title/minecraft.png");
	private static final Identifier EDITION_TITLE_TEXTURE = new Identifier("textures/gui/title/edition.png");
	private long backgroundFadeStart;

	protected MineMiiTitleScreen() {
		super(new TranslatableText("minemii.title"));
		this.backgroundRenderer = new RotatingCubeMapRenderer(PANORAMA_CUBE_MAP);
	}

	@Override
	public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		boolean doBackgroundFade = true;
		if (this.backgroundFadeStart == 0L && doBackgroundFade) {
			this.backgroundFadeStart = Util.getMeasuringTimeMs();
		}

		float f = doBackgroundFade ? (float)(Util.getMeasuringTimeMs() - this.backgroundFadeStart) / 1000.0F : 1.0F;
		this.backgroundRenderer.render(delta, MathHelper.clamp(f, 0.0F, 1.0F));
		int i = 1;
		int j = this.width / 2 - 137;
		int k = 1;
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, PANORAMA_OVERLAY);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, doBackgroundFade ? (float)MathHelper.ceil(MathHelper.clamp(f, 0.0F, 1.0F)) : 1.0F);
		drawTexture(matrices, 0, 0, this.width, this.height, 0.0F, 0.0F, 16, 128, 16, 128);
		float g = doBackgroundFade ? MathHelper.clamp(f - 1.0F, 0.0F, 1.0F) : 1.0F;
		int l = MathHelper.ceil(g * 255.0F) << 24;
		if ((l & -67108864) != 0) {
			RenderSystem.setShader(GameRenderer::getPositionTexShader);
			RenderSystem.setShaderTexture(0, MINECRAFT_TITLE_TEXTURE);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, g);
			this.drawWithOutline(j, 30, (x, y) -> {
				this.drawTexture(matrices, x, y, 0, 0, 155, 44);
				this.drawTexture(matrices, x + 155, y, 0, 45, 155, 44);
			});

			RenderSystem.setShaderTexture(0, EDITION_TITLE_TEXTURE);

			drawTexture(matrices, j + 88, 67, 0.0F, 0.0F, 98, 14, 128, 16);

			drawCenteredText(matrices, this.textRenderer, new LiteralText("Press A + B to start"), this.width / 2, this.height - 38, 16777215);

			super.render(matrices, mouseX, mouseY, delta);
		}
	}

	@Override
	protected void init() {
		MinecraftClient.getInstance().getSoundManager().stopAll();
		MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(MineMii.WiiSports, 1.0f));
	}

	@Override
	public void tick() {
		if (((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().a && ((WiiMoteClient) MinecraftClient.getInstance()).getButtOns().b) {
			MinecraftClient.getInstance().setScreen(new TitleScreen(true));
		}
	}
}
