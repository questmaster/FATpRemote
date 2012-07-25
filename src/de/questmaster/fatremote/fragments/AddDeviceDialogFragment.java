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

package de.questmaster.fatremote.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;
import de.questmaster.fatremote.R;

public class AddDeviceDialogFragment extends DialogFragment {
	
	private static class CancelListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int whichButton) {
            dialog.cancel();
        }

	}
	
	public static AddDeviceDialogFragment newInstance(int title) {
        AddDeviceDialogFragment frag = new AddDeviceDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }
	
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        final View dialogLayout = getActivity().getLayoutInflater().inflate(R.layout.adddevice_dialog_fragment, null);
        
        return new AlertDialog.Builder(getActivity())
//                .setIcon(R.drawable.alert_dialog_icon)
                .setTitle(title)
                .setView(dialogLayout)
                .setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        	String name = (String) ((TextView) dialogLayout.findViewById(R.id.dialog_adddev_name_edit)).getText().toString().trim();
                        	String ip = (String) ((TextView) dialogLayout.findViewById(R.id.dialog_adddev_ip_edit)).getText().toString().trim();
                        	
                            ((SelectFATFragment)getTargetFragment()).doPositiveClick(name, ip);
                        }
                    }
                )
                .setNegativeButton(android.R.string.cancel,
                	new CancelListener()
                )
                .create();
    }}
