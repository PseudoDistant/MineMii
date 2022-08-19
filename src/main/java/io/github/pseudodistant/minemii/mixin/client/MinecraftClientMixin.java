package io.github.pseudodistant.minemii.mixin.client;

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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.FurnaceScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.jetbrains.annotations.Nullable;
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
	public WiimoteInput butt_ons = new WiimoteInput();
	int[] accel = new int[3];
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
				evt.getSource().setReportMode(ReportModeRequest.DATA_REPORT_0x31);
			} else {
				MinecraftClient.getInstance().scheduleStop();
			}
		}

		@Override
		public void extensionDisconnected(ExtensionEvent evt) {
			evt.getSource().setReportMode(ReportModeRequest.DATA_REPORT_0x31);
		}
	};

	CoreButtonListener buttonListener = new CoreButtonListener() {
		public void buttonPressed(CoreButtonEvent evt) {
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
		}
	};


	AccelerometerListener<Mote> accelerometerListener = new AccelerometerListener<Mote>() {
		public void accelerometerChanged(AccelerometerEvent<Mote> evt) {
			accel[0] = evt.getX();
			accel[1] = evt.getY();
			accel[2] = evt.getZ();
			//System.out.println(evt.getX() + " : " + evt.getY() + " : " + evt.getZ());
		}
	};

	MoteDisconnectedListener<Mote> onDisconnect = evt -> MinecraftClient.getInstance().setScreen(new ConnectWiimoteScreen(false));

	MoteFinderListener listener = new MoteFinderListener() {
		public void moteFound(Mote mote) {
			System.out.println("Found mote: " + mote.getBluetoothAddress());
			boolean[] p1 = new boolean[4];
			p1[0] = true;
			p1[1] = p1[2] = p1[3] = false;
			mote.setPlayerLeds(p1);
			mote.addMoteDisconnectedListener(onDisconnect);
			mote.addExtensionListener(extensionListener);
			mote.addAccelerometerListener(accelerometerListener);
			mote.addCoreButtonListener(buttonListener);
			mote.setReportMode(ReportModeRequest.DATA_REPORT_0x31);
			mote.rumble(500L);
			wiimote = mote;
			finder.stopDiscovery();
			discoveryEnabled = false;
		}
	};

	public Mote getWiimote() {
		return this.wiimote;
	}

	public int[] getAccel() {
		return this.accel;
	}

	public WiimoteInput getButtOns() {
		return butt_ons;
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
}
