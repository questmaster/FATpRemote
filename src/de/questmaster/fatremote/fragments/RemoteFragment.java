package de.questmaster.fatremote.fragments;

import java.net.ConnectException;

import de.questmaster.fatremote.DebugHelper;
import de.questmaster.fatremote.FatRemoteSettings;
import de.questmaster.fatremote.NetworkProxy;
import de.questmaster.fatremote.R;
import de.questmaster.fatremote.SelectFATActivity;
import de.questmaster.fatremote.FatRemoteSettings.Settings;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RemoteFragment extends Fragment implements View.OnClickListener {

	private static final String LOG_TAG = "RemoteFragment";
	
	protected static final String INTENT_ALLFATS = "de.questmaster.fatremote.fats";

	private static final int INTENT_SELECT_FAT = 0;
	public static final int ON_SETTINGS_CHANGE = 1;

	private Settings mSettings = new FatRemoteSettings.Settings();
	private AudioManager audioManager;

	private boolean showDebugView = false;
	private Activity c = null;
	private short keyCode;
	private short keyModifier;

	// TODO Listener interface
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// enable options menu
		this.setHasOptionsMenu(true);
		
		// set activity
		c = this.getActivity();

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

        if (container == null) {
            // We have different layouts, and in one of them this
            // fragment's containing frame doesn't exist.  The fragment
            // may still be created from its saved state, but there is
            // no reason to try to create its view hierarchy because it
            // won't be displayed.  Note this is not needed -- we could
            // just run the code below, where we would create and return
            // the view hierarchy; it would just never be used.
            return null;
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.remote_fragment, container, false);
}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		/* start audio */
		audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
		audioManager.loadSoundEffects();

		/* read Settings */
		mSettings.readSettings(c);

		/* setup onClickListener for remote buttons */
		((ImageView) c.findViewById(R.id.remote_button_power)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_up)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_home)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_left)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_ok)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_right)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_back)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_down)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_info)).setOnClickListener(this);

		((ImageView) c.findViewById(R.id.textButton)).setOnClickListener(this);

		((ImageView) c.findViewById(R.id.remote_button_rew)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_menu)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_fwd)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_prev)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_play)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_next)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_volup)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_stop)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_zoomup)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_voldown)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_mute)).setOnClickListener(this);
		((ImageView) c.findViewById(R.id.remote_button_zoomdown)).setOnClickListener(this);
		
		/* setup key listener */
		EditText text = (EditText) c.findViewById(R.id.enterText);
		text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
	        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
	            if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
	            	hideKeyboard();
	        		
	        		return true;
	            }
	            return false;
	        }
	    });
		text.addTextChangedListener(new TextWatcher()
        {
                public void  afterTextChanged (Editable s) { 
                } 

                public void  beforeTextChanged  (CharSequence s, int start, int count, int after) { 
                } 
                
                public void  onTextChanged  (CharSequence s, int start, int before, int count) 
                { 
                	if (s.length() > 0) {
                		if (before == 0) {
                			onTextEditKeyDown(Character.codePointAt(s, start), null);
                		} else {
                			onTextEditKeyDown(8, null); // backspace
                		}
                	}
                }
        });
		text.setFocusable(true);

		// check if WIFI available
		if (!NetworkProxy.getInstance(c).isWifiEnabled()) {
			Toast.makeText(c, R.string.app_err_enwifi, Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);

		inflater.inflate(R.menu.options_menu, menu);

		// For testing purpose
		if (DebugHelper.SHOW_DEBUG_SCREEN) {
			MenuItem mi = menu.findItem(R.id.MENU_ITEM_DEBUG);
			mi.setVisible(true);
		}
	}
	
	/**
	 * Define menu action
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.MENU_ITEM_SELECTFAT: {
			Intent selectFAT = new Intent(Intent.ACTION_PICK);
			selectFAT.setClass(c, SelectFATActivity.class);
			startActivityForResult(selectFAT, INTENT_SELECT_FAT);

			break;
		}
		case R.id.MENU_ITEM_SETTINGS: {
			Intent iSettings = new Intent();
			iSettings.setClass(c, FatRemoteSettings.class);
			startActivityForResult(iSettings, ON_SETTINGS_CHANGE);

			break;
		}
		case R.id.MENU_ITEM_DEBUG: {
			c.setContentView(R.layout.debug);
			break;
		}
		default:
			// should not happen
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
//		case INTENT_SELECT_FAT: {
//			if (resultCode == Activity.RESULT_OK) {
//			}
//			break;
//		}
		case INTENT_SELECT_FAT:
		case ON_SETTINGS_CHANGE: {
			mSettings.readSettings(c);
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
		short in3 = Short.decode(((TextView) c.findViewById(R.id.pos3)).getText().toString());
		short in4 = Short.decode(((TextView) c.findViewById(R.id.pos4)).getText().toString());

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
		Log.i(LOG_TAG, "KeyEvent: " + keyId + ", unicode: " + unicode);

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
		// show edittext and give it focus
		ImageView button = (ImageView) c.findViewById(R.id.textButton);
		EditText text = (EditText) c.findViewById(R.id.enterText);
		button.setVisibility(View.GONE);
		text.setVisibility(View.VISIBLE);
		text.requestFocus();
		
		// show keyboard
		InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.toggleSoftInput(0, 0);
	}
	
	private void hideKeyboard() {		
		EditText text = (EditText) c.findViewById(R.id.enterText);
		ImageView button = (ImageView) c.findViewById(R.id.textButton);

		// clear entered Text
		text.setText("");

		// hide text field
		text.setVisibility(View.GONE);
		button.setVisibility(View.VISIBLE);
		
		// hide keyboard
    	InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
    	mgr.hideSoftInputFromWindow(text.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onClick(View v) {

		// show button again
		EditText text = (EditText) c.findViewById(R.id.enterText);
		if (text.getVisibility() == View.VISIBLE) {
			hideKeyboard();
		}
		
		// solve to code
		if (v instanceof ImageView) {
			ImageView bv = (ImageView) v;
			String t = (String) bv.getTag();

			if (t.equals("text")) {
				showKeyboard();
				return;
			} else {
				keyCode = Integer.decode(t).shortValue();	
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
			Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(25);
		}

		// Set sending image
		ImageView sending = (ImageView) c.findViewById(R.id.sendLED);
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
					public void run() { // reset send image
						ImageView sending = (ImageView) c.findViewById(R.id.sendLED);
						sending.setImageResource(R.drawable.light_normal);
					}
				});
			}
		}).start();
	}
}
