package io.github.pseudodistant.minemii.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.pseudodistant.minemii.WiiMoteClient;
import io.github.pseudodistant.minemii.WiimoteInput;
import io.github.pseudodistant.minemii.screen.ConnectWiimoteScreen;
import motej.Mote;
import motej.MoteFinder;
import motej.MoteFinderListener;
import motej.event.*;
import motej.request.ReportModeRequest;
import motejx.extensions.classic.ClassicController;
import motejx.extensions.nunchuk.Nunchuk;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.*;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MinecraftClientMixin implements WiiMoteClient {
	@Shadow @Nullable public Screen currentScreen;
	@Shadow public abstract void setScreen(@Nullable Screen screen);

	@Shadow public abstract void openPauseMenu(boolean pause);

	@Shadow private boolean paused;
	@Shadow private static MinecraftClient instance;

	@Shadow public abstract Window getWindow();

	private static Identifier P1TEXTURE = new Identifier("minemii", "textures/p1hand.png");

	public WiimoteInput butt_ons = new WiimoteInput();
	int[] accel = new int[3];

	double[] irCoords = new double[2];

	Mote wiimote;
	boolean interruptedTitleScreen;
	boolean discoveryEnabled = false;
	MoteFinder finder;

	@Override
	public MoteFinder getFinder() {
		return finder;
	}

	@Override
	public boolean isDiscoveryEnabled() {
		return discoveryEnabled;
	}

	@Override
	public void setDiscovery(boolean bl) {
		discoveryEnabled = bl;
	}

	@Override
	public void startWiimoteDiscovery(boolean titleScreen) {
		discoveryEnabled = true;
		finder.startDiscovery();
		interruptedTitleScreen = titleScreen;
	}

	ExtensionListener extensionListener = new ExtensionListener() {
		@Override
		public void extensionConnected(ExtensionEvent evt) {
			if (!(evt.getExtension() instanceof ClassicController)) {
				if (currentScreen == null) {
					evt.getSource().setReportMode(ReportModeRequest.DATA_REPORT_0x35);
				}
			} else {
				MinecraftClient.getInstance().scheduleStop();
			}
		}

		@Override
		public void extensionDisconnected(ExtensionEvent evt) {
			evt.getSource().setReportMode(ReportModeRequest.DATA_REPORT_0x37);
		}
	};

	IrCameraListener cameraListener = evt -> {
		irCoords[0] = evt.getIrPoint(0).getX();
		irCoords[1] = evt.getIrPoint(0).getY();
	};

	CoreButtonListener buttonListener = evt -> {
		butt_ons.a = evt.isButtonAPressed();
		butt_ons.b = evt.isButtonBPressed();
		butt_ons.one = evt.isButtonOnePressed();
		butt_ons.two = evt.isButtonTwoPressed();
		butt_ons.up = evt.isDPadUpPressed();
		butt_ons.down = evt.isDPadDownPressed();
		butt_ons.left = evt.isDPadLeftPressed();
		butt_ons.right = evt.isDPadRightPressed();
		butt_ons.plus = evt.isButtonPlusPressed();
		butt_ons.minus = evt.isButtonMinusPressed();
		butt_ons.home = evt.isButtonHomePressed();
	};


	AccelerometerListener<Mote> accelerometerListener = evt -> {
		accel[0] = evt.getX();
		accel[1] = evt.getY();
		accel[2] = evt.getZ();
		//System.out.println(evt.getX() + " : " + evt.getY() + " : " + evt.getZ());
	};

	MoteDisconnectedListener<Mote> onDisconnect = evt -> MinecraftClient.getInstance().setScreen(new ConnectWiimoteScreen(false));

	MoteFinderListener listener = new MoteFinderListener() {
		public void moteFound(Mote mote) {
			System.out.println("Found mote: " + mote.getBluetoothAddress());
			boolean[] p1 = new boolean[4];
			p1[0] = true;
			p1[1] = p1[2] = p1[3] = false;
			irCoords[0] = 0.0;
			irCoords[1] = 0.0;
			mote.setPlayerLeds(p1);
			mote.addMoteDisconnectedListener(onDisconnect);
			mote.addExtensionListener(extensionListener);
			mote.addAccelerometerListener(accelerometerListener);
			mote.addCoreButtonListener(buttonListener);
			mote.addIrCameraListener(cameraListener);
			mote.setReportMode(ReportModeRequest.DATA_REPORT_0x31);
			mote.rumble(500L);
			wiimote = mote;
			finder.stopDiscovery();
			discoveryEnabled = false;
		}
	};

	public Mote getWiimote() {
		return wiimote;
	}

	public int[] getAccel() {
		return accel;
	}

	public WiimoteInput getButtOns() {
		return butt_ons;
	}

	public double[] getIrCoords() {
		return irCoords;
	}

	@Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
	private void addWiiRemoteInit(RunArgs args, CallbackInfo ci) {
		finder = MoteFinder.getMoteFinder();
		finder.addMoteFinderListener(listener);
		//setScreen(new ConnectWiimoteScreen(true));
		//discoveryEnabled = true;
	}

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"))
	private void setWiiRemoteScreen(MinecraftClient instance, Screen screen) {
		setScreen(new ConnectWiimoteScreen(true));
	}

	@Inject(method = "setScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;updateWindowTitle()V"))
	private void setWiimoteMode(CallbackInfo ci) {
		if (wiimote != null) {
			if (currentScreen == null) {
				wiimote.disableIrCamera();
			} else {
				wiimote.setReportMode(ReportModeRequest.DATA_REPORT_0x37);
				wiimote.enableIrCamera();
			}
		}
	}
}
