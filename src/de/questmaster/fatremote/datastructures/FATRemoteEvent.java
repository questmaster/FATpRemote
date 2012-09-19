/*
 * Copyright (C) 2010 Daniel Jacobi
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package de.questmaster.fatremote.datastructures;

import java.util.Arrays; 
 
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
	 * @param pCmd2 Command-byte three
	 * @param pCmd3 Command-byte four
	 */
	public FATRemoteEvent(short pCmd2, short pCmd3) {
		this(pCmd2, pCmd3, null);
	}

	/**
	 * Creates a new event with the two parameters as 3rd and 4th command byte and an optional payload.
	 * 
	 * @param pCmd2 Command-byte three
	 * @param pCmd3 Command-byte four
	 * @param pPayload Command payload
	 */
	public FATRemoteEvent(short pCmd2, short pCmd3, byte[] pPayload) {
		short cmd2 = pCmd2;
		short cmd3 = pCmd3;
		
		// filter shorts
		cmd2 *= cmd2 < 0 ? -1 : 1; // convert negative numbers 
		cmd3 *= cmd3 < 0 ? -1 : 1; 
		cmd2 %= 256; // shrink large numbers
		cmd3 %= 256; 

		mCommand[2] = cmd2;
		mCommand[3] = cmd3;
		
		
		setPayload(pPayload);
	}

	public FATRemoteEvent() {
		// nothing to init
	}

	/**
	 * Returns the command bytes as array.
	 * 
	 * @return Command-bytes in short array
	 */
	public short[] getCommandCode() {
		return mCommand.clone();
	}
	
	/**
	 * Returns the payload of the event.
	 * 
	 * @return Payload
	 */
	public short[] getPayload() {
		return mPayload.clone();
	}
	
	/**
	 * Returns a string representation of the object. In general, 
	 * the {@code toString} method returns a string that "textually represents" 
	 * this object. The result should be a concise but informative 
	 * representation that is easy for a person to read.
     *
	 * The {@code toString} method for class {@code FATRemoteEvent} returns a 
	 * string consisting of the four RemoteCode entries.
	 * In other words, this method returns a string equal to the value of:
     *
	 * {@code "{" + command[0] + "," + command[1] + "," + command[2] + "," + command[3] + "}"}
	 *
	 * @returns a string representation of the object.
	 */
	@Override
	public String toString() {
		short command[] = this.getCommandCode();
		return "{" + command[0] + "," + command[1] + "," + command[2] + "," + command[3] + "}";
	}

	/**
	 * {@inheritDoc}
	 *
     * @param other {@inheritDoc}
	 * @returns {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if ( other instanceof FATRemoteEvent) {
			FATRemoteEvent evt = (FATRemoteEvent) other;
			if ( Arrays.equals(this.getCommandCode(), evt.getCommandCode())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @returns {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		int hash = 0;
		
		for (short s: this.getPayload()) {
			hash += Short.valueOf(s).hashCode();	
		}
		
		return hash;
	}

	/**
	 * Set the remote code of this event. This is used to store a received event.
	 * @param code remote code
	 */
	public void setCommandCode(byte[] code) {
		if (code != null && code.length == mCommand.length) {
			for (int i = 0; i < code.length; i++) {
				mCommand[i] = (short) (code[i] < 0 ? 256 + code[i] : code[i]);
			}
		}
	}

	/**
	 * Set the payload of this event. This is used to store the received payload.
	 * @param code payload of event
	 */
	public void setPayload(byte[] code) {
		if (code != null) {
			mPayload = new short[code.length];
			for (int i = 0; i < code.length; i++) {
				mPayload[i] = (short) (code[i] < 0 ? 256 + code[i] : code[i]);
			}
		}
	}

	/**
	 * Checks if this event includes payload.
	 * @return true - if payload exists, false - otherwise
	 */
	public boolean hasPayload() {
		boolean result = false;
		
		if (mPayload != null && mPayload.length > 0) {
			result =  true;
		}
		
		return result;
	}

}
