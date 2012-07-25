package de.questmaster.fatremote;

import android.os.Build;

/**
 * Helper class to define global debug flags.
 * 
 * @author daniel
 *
 */
public final class DebugHelper {

	public static final boolean ON_EMULATOR = Build.PRODUCT.contains("sdk");
	
	public static final boolean SHOW_DEBUG_SCREEN = true;
	
	private DebugHelper() {
		
	}
	
}
