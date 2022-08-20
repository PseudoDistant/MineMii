package io.github.pseudodistant.minemii;

import motej.Mote;
import motej.MoteFinder;

public interface WiiMoteClient {
	boolean isDiscoveryEnabled();

	void setDiscovery(boolean bl);

	Mote getWiimote();

	int[] getAccel();

	MoteFinder getFinder();

	WiimoteInput getButtOns();

	void startWiimoteDiscovery(boolean titleScreen);

	double[] getIrCoords();
}
