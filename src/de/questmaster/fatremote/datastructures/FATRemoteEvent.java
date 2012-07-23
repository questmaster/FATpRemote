package de.questmaster.fatremote.datastructures;


/**
 * @author daniel
 *
 */
public class FATRemoteEvent {
	
	private short[] mCommand = { 0x48, 0x12, 0x00, 0x00 };
	private short[] mPayload = null;
	
	/**
	 * 
	 * @param cmd2
	 * @param cmd3
	 */
	public FATRemoteEvent(short cmd2, short cmd3, short[] payload) {
		mCommand[2] = cmd2;
		mCommand[3] = cmd3;
		
		mPayload = payload.clone();
	}

	public short[] getRemoteCode() {
		return mCommand.clone();
	}
	
	public short[] getCodePayload() {
		return mPayload;
	}
	
}
