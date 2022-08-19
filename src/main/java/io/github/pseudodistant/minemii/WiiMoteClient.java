package io.github.pseudodistant.minemii;

import motej.Mote;
import motej.MoteFinder;

public interface WiiMoteClient {
	public boolean isDiscoveryEnabled();

	public void setDiscovery(boolean bl);

	public Mote getWiimote();

	public int[] getAccel();

	public MoteFinder getFinder();

	public WiimoteInput getButtOns();

	public void startWiimoteDiscovery(boolean titleScreen);
}
