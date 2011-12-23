package de.questmaster.fatremote;

import java.net.ConnectException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.questmaster.fatremote.FatRemoteSettings.Settings;

public class RemoteActivity extends Activity {

	protected static final String INTENT_ALLFATS = "de.questmaster.fatremote.fats";

	private static final int INTENT_SELECT_FAT = 0;
	public static final int ON_SETTINGS_CHANGE = 1;

	private Settings mSettings = new FatRemoteSettings.Settings();
	private AudioManager audioManager;

	private boolean showDebugView = false;
	private Activity c = this;
	private short keyCode;
	private short keyModifier;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.remote);

		/* start audio */
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		audioManager.loadSoundEffects();

		/* read IP */
		mSettings.readSettings(this);

		/* setup key listener */
		EditText text = (EditText) findViewById(R.id.enterText);
		text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	        @Override
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
	        		EditText text = (EditText) findViewById(R.id.enterText);
	            	
	        		// clear entered Text
	        		text.setText("");
	        		
	        		// hide keyboard
	            	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	            	mgr.hideSoftInputFromWindow(text.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	            	
	        		// show button again
	        		ImageView button = (ImageView) findViewById(R.id.textButton);
	        		button.setVisibility(View.VISIBLE);
	        		text.setVisibility(View.GONE);
	            }
	            return false;
	        }
	    });
//		text.setOnKeyListener( new OnKeyListener() {
//
//			@Override
//			public boolean onKey(View v, int keyCode, KeyEvent event) {
//				// TODO Send key data
//				if (event.getAction() == KeyEvent.ACTION_DOWN)
//					onTextEditKeyDown(keyCode, event);
//				
//				return false;
//			}
//			
//		});
		text.addTextChangedListener(new TextWatcher()
        {
                public void  afterTextChanged (Editable s){ 
                } 

                public void  beforeTextChanged  (CharSequence s, int start, int 
                        count, int after)
                { 
                } 
                
                public void  onTextChanged  (CharSequence s, int start, int before, 
                        int count) 
                { 
                	if (before == 0)
                		onTextEditKeyDown(Character.codePointAt(s, start), null);
                	else
                		onTextEditKeyDown(8, null); // backspace
                }
        });
		text.setFocusable(true);

		// check if WIFI available
		if (!NetworkProxy.getInstance(this).isWifiEnabled()) {
			Toast.makeText(this, R.string.app_err_enwifi, Toast.LENGTH_LONG).show();
		}

//TODO		
//			AlertDialog.Builder notification = new Builder(this);
//			notification.setIcon(android.R.drawable.ic_menu_manage);
//			notification.setTitle(R.string.dialog_noip_title);
//			notification.setMessage(R.string.dialog_noip_text);
//			notification.show();
//		
	}

	/**
	 * Add menu items
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_menu, menu);

		// For testing purpose
		if (StartActivity.ON_EMULATOR) {
			MenuItem mi = menu.findItem(R.id.MENU_ITEM_DEBUG);
			mi.setVisible(true);
		}

		return true;
	}

	/**
	 * Define menu action
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.MENU_ITEM_SELECTFAT: {
			Intent selectFAT = new Intent(Intent.ACTION_PICK);
			selectFAT.setClass(this, SelectFATActivity.class);
			startActivityForResult(selectFAT, INTENT_SELECT_FAT);

			break;
		}
		case R.id.MENU_ITEM_SETTINGS: {
			Intent iSettings = new Intent();
			iSettings.setClass(this, FatRemoteSettings.class);
			startActivityForResult(iSettings, ON_SETTINGS_CHANGE);

			break;
		}
		case R.id.MENU_ITEM_DEBUG: {
			if (showDebugView) {
				setContentView(R.layout.main);
			} else {
				setContentView(R.layout.debug);
			}
			break;
		}
		default:
			// should not happen
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
//		case INTENT_SELECT_FAT: {
//			if (resultCode == Activity.RESULT_OK) {
//			}
//			break;
//		}
		case INTENT_SELECT_FAT:
		case ON_SETTINGS_CHANGE: {
			mSettings.readSettings(this);
			break;
		}
		default:
			// should not happen
			break;
		}
	}

	public void onDebugButton(View v) {
		// short in1 = Short.decode(((TextView)findViewById(R.id.pos1)).getText().toString());
		// short in2 = Short.decode(((TextView)findViewById(R.id.pos2)).getText().toString());
		short in3 = Short.decode(((TextView) findViewById(R.id.pos3)).getText().toString());
		short in4 = Short.decode(((TextView) findViewById(R.id.pos4)).getText().toString());

		keyCode = in3;
		keyModifier = in4;

		// send keyCode
		invokeSend();
	}

	// TODO: grab keys from 'enterText' view
	public boolean onTextEditKeyDown(int keyId, KeyEvent event) {
		short key = 0;
		int unicode = 0;

		// get key
		if (event == null) {
			key = (short) keyId;
 		} else {
//		unicode = event.getUnicodeChar();
//		key = (short) unicode;
 			key = (short) event.getUnicodeChar(event.getMetaState());
 		}
		Log.i(StartActivity.LOG_TAG, "KeyEvent: " + keyId + ", unicode: " + unicode);

		// WORKAROUND for some keys
		if (key == 0 && keyId == 67) { key = 0x7f; } // delete //8; // Backspace

		// TODO: Some key are not working, e.g. öäü (extended ASCII). Maybe show textfield to get all characters.
		// Send Character
		if (key != 0) {
			keyCode = 0xf9;
			keyModifier = key;

			// send keyCode
			invokeSend();
			return true;
		} /*else {
			return super.onKeyDown(keyId, event);
		}*/
		return false;
	}

	private void showKeyboard() {
		// show keyboard
//		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//		mgr.toggleSoftInput(0, 0);
		
		// show edittext and give it focus
		ImageView button = (ImageView) findViewById(R.id.textButton);
		EditText text = (EditText) findViewById(R.id.enterText);
		button.setVisibility(View.GONE);
		text.setVisibility(View.VISIBLE);
		text.requestFocus();
	}

	public void getKeyCode(View v) {

		// show button again
		EditText text = (EditText) findViewById(R.id.enterText);
		if (text.getVisibility() == View.VISIBLE) {
			ImageView button = (ImageView) findViewById(R.id.textButton);
			button.setVisibility(View.VISIBLE);
			text.setVisibility(View.GONE);
			
    		// clear entered Text
    		text.setText("");

    		// hide keyboard
        	InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        	mgr.hideSoftInputFromWindow(text.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		}
		
		// solve to code
		if (v instanceof ImageView) {
			ImageView bv = (ImageView) v;
			String t = (String) bv.getTag();

			if (t.equals("power")) {
				keyCode = 0x38;
			} else if (t.equals("home")) {
				keyCode = 0x39;
			} else if (t.equals("up")) {
				keyCode = 0x40;
			} else if (t.equals("left")) {
				keyCode = 0x07;
			} else if (t.equals("ok")) {
				keyCode = 0x0E;
			} else if (t.equals("right")) {
				keyCode = 0x06;
			} else if (t.equals("back")) {
				keyCode = 0x1B;
			} else if (t.equals("down")) {
				keyCode = 0x41;
			} else if (t.equals("info")) {
				keyCode = 0x47;
			} else if (t.equals("text")) {
				showKeyboard();
				return;
			} else if (t.equals("rew")) {
				keyCode = 0x52;
			} else if (t.equals("menu")) {
				keyCode = 0x1A;
			} else if (t.equals("ff")) {
				keyCode = 0x51;
			} else if (t.equals("prev")) {
				keyCode = 0x31;
			} else if (t.equals("playpause")) {
				keyCode = 0x4F;
			} else if (t.equals("next")) {
				keyCode = 0x30;
			} else if (t.equals("volup")) {
				keyCode = 0x32;
			} else if (t.equals("stop")) {
				keyCode = 0x54;
			} else if (t.equals("zoomin")) {
				keyCode = 0x35;
			} else if (t.equals("voldown")) {
				keyCode = 0x33;
			} else if (t.equals("mute")) {
				keyCode = 0x59;
			} else if (t.equals("zoomout")) {
				keyCode = 0x08;
			}
		}
		keyModifier = 0x00;

		// send keyCode
		invokeSend();
	}

	private void invokeSend() {
		// ring / vibrate
		if ((!mSettings.mOverride && audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM) != 0) || (mSettings.mOverride && mSettings.mTone)) {
			audioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
		}

		if (mSettings.mVibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(25);
		}

		// Set sending image
		ImageView sending = (ImageView) this.findViewById(R.id.sendLED);
		sending.setImageResource(R.drawable.light_highlight);
		sending.invalidate();

		// start sending in new thread
		new Thread(new Runnable() {
			public void run() {
				try {
					NetworkProxy.getInstance(c).sendCode(new short[] { 0x48, 0x12, keyCode, keyModifier });
				} catch (final ConnectException e) {
					c.runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(c, e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}

				c.runOnUiThread(new Runnable() {
					public void run() {
						// reset send image
						ImageView sending = (ImageView) findViewById(R.id.sendLED);
						sending.setImageResource(R.drawable.light_normal);
					}
				});
			}
		}).start();
	}
}
