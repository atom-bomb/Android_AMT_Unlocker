package com.abombrecords.amt_unlocker;

/*
 * Copyright 2012, Adam Briggs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 
 *  AMT_UnlockerActivity.java
 * 
 *  This is the main (only) activity for the AMT Unlocker app
 *  the code mostly just handles UI interaction, with a little bit of simple parameter checking and error handling
 */

import android.app.Activity;
import android.content.Context;
//import android.util.Log ;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast; 
import android.widget.EditText ;

public class AMT_UnlockerActivity extends Activity {
	
	//private static final String LogTag = "AMTActivity" ;
	
	private AMT_UnlockerSettings unlockerSettings ;
	
	private EditText theUsernameText ;
	private EditText thePasswordText ;
	private CheckBox theCheckBox ;
	private EditText thePinText ;
	private Context  theContext ;
	
	// UnlockTask uses an AsyncTask to communicate with AMT_Unlocker (the service that unlocks the door)
	// so that long walks on the beach or over the internet don't stall the UI thread
	private class UnlockTask extends AsyncTask<String, Integer, Integer> {
		
		@Override
		protected void onPreExecute() {
			setProgressBarIndeterminateVisibility(true) ;
		}
		
		@Override
		protected Integer doInBackground(String... Args) {
			try {
				if (true == AMT_Unlocker.UnlockDoor(Args[0], Args[1], Args[2])) {
					return 1 ;
				} else
					return 0 ;
			} catch (Exception e) {
				return -1 ;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			setProgressBarIndeterminateVisibility(false);
			switch (result) {
			case 1:
				break ;
			case 0:
				Toast.makeText(theContext, R.string.auth_failure_string, Toast.LENGTH_LONG).show() ;
				break ;
			case -1:
			default:
				Toast.makeText(theContext, R.string.http_exception_string, Toast.LENGTH_LONG).show() ;
				break ;
			}
			
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // this window feature lets us have a spinning busy icon in the menu bar
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);
        
        unlockerSettings = new AMT_UnlockerSettings(this) ;

    	theUsernameText = (EditText) findViewById(R.id.userText) ;
		thePasswordText = (EditText) findViewById(R.id.passwordText) ;
		theCheckBox = (CheckBox) findViewById(R.id.rememberCheckBox) ;
		thePinText = (EditText) findViewById(R.id.pinText) ;
		theContext = (Context)this ;
		
        if (unlockerSettings.Load()) {
        	//Log.d(LogTag, "Loading Settings") ;
        	
    		theUsernameText.setText(unlockerSettings.Username,
    				TextView.BufferType.EDITABLE) ;
    		
    		thePasswordText.setText(unlockerSettings.Password,
    				TextView.BufferType.EDITABLE) ;
    		
    		// if settings loaded, then the last user must have wanted them to be saved.
    		theCheckBox.setChecked(true) ;
        }
    }
    
    public void myClickHandler(View theView) {
        	
    	switch (theView.getId()) {
    	
    	// Called when the check box is checked or un-checked
    	// NB that not remembering settings also deletes any old settings
    	case R.id.rememberCheckBox:
    		if (theCheckBox.isChecked()) {
    	        //Log.d(LogTag, "Saving Settings");
    			unlockerSettings.Username = theUsernameText.getText().toString() ;
    			unlockerSettings.Password = thePasswordText.getText().toString() ;
    			unlockerSettings.Save();
    		} else {
    			//Log.d(LogTag, "Deleting Settings") ;
    			unlockerSettings.Delete();
    		}
    		break ;
    	
    	// Called when the unlock button is clicked
    	case R.id.doit_button:
    		
    		if (theUsernameText.getText().length() == 0) {
    		  Toast.makeText(this, R.string.enter_username_string, Toast.LENGTH_LONG).show() ;
    		  return ;
    		}
    		
    		if (thePasswordText.getText().length() == 0) {
    		  Toast.makeText(this, R.string.enter_password_string, Toast.LENGTH_LONG).show() ;
    		  return ;
    		}
    		
    		if (thePinText.getText().length() != 4) {
    		  Toast.makeText(this, R.string.enter_pin_string, Toast.LENGTH_LONG).show();
    		  return ;
    		}
    		
    		//Log.d(LogTag, "Unlocking Door") ;
    		
    		// Launch the task to unlock the door
    	    new UnlockTask().execute(theUsernameText.getText().toString(),
    	    		  thePasswordText.getText().toString(),
    	    		  thePinText.getText().toString()) ;
    		break ;
    	}
    }
}