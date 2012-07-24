package de.questmaster.fatremote.datastructures;


/**
 * This Datastructure represents an event send or received from/to the FAT+. 
 * 
 * @author daniel
 *
 */
public class FATRemoteEvent {
	
	/**
	 * Command code used by FAT+.
	 */
	private short[] mCommand = { 0x48, 0x12, 0x00, 0x00 };
	
	/**
	 * Payload that is included in the command (optional).
	 */
	private short[] mPayload = new short[0];
	
	/**
	 * Creates a new event with the two parameters as 3rd and 4th command byte.
	 * 
	 * @param cmd2 Command-byte three
	 * @param cmd3 Command-byte four
	 */
	public FATRemoteEvent(short cmd2, short cmd3) {
		this(cmd2, cmd3, null);
	}

	/**
	 * Creates a new event with the two parameters as 3rd and 4th command byte and an optional payload.
	 * 
	 * @param cmd2 Command-byte three
	 * @param cmd3 Command-byte four
	 * @param payload Command payload
	 */
	public FATRemoteEvent(short cmd2, short cmd3, short[] payload) {
		mCommand[2] = cmd2;
		mCommand[3] = cmd3;
		
		if (payload != null) {
			mPayload = payload.clone();
		}
	}

	/**
	 * Returns the command bytes as array.
	 * 
	 * @return Command-bytes in short array
	 */
	public short[] getRemoteCode() {
		return mCommand.clone();
	}
	
	/**
	 * Returns the payload of the event.
	 * 
	 * @return Payload
	 */
	public short[] getCodePayload() {
		return mPayload.clone();
	}
	
}
