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
 * AMT_UnlockerSettings.java
 *
 * This here thing is responsible for managing the app settings.
 * The settings are stored in a simple file with NULLs between fields.
 * Since the only settings are username and password, it's unlikely that NULLs will appear in valid data.
 */

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.content.Context;

public class AMT_UnlockerSettings {
	private final static String SettingsFileName = "Settings" ;
	
	private static Context theContext ;
	
	public String Username ;
	public String Password ;
	
	public AMT_UnlockerSettings(Context appContext) {
		theContext = appContext ;
	}
	
	public void Delete() {
		theContext.deleteFile(SettingsFileName) ;
	}
	
    public boolean Load() {
    	int theByte ;
    	ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    	try {
    		FileInputStream theFile = theContext.openFileInput(SettingsFileName) ;
    	    
    		while (0 < (theByte = (char)theFile.read())) {
    			byteArrayOutputStream.write(theByte) ;
    	    }
    	    Username = byteArrayOutputStream.toString() ;
    	    
    	    byteArrayOutputStream.reset() ;
    	    
    	    while (0 < (theByte = (char)theFile.read())) {
    			byteArrayOutputStream.write(theByte) ;
    	    }
    	    Password = byteArrayOutputStream.toString() ;
    	    
    	    theFile.close() ;
    		return true ;
    	} catch (Exception e) {
    		return false ;
    	}
    }
    
    public boolean Save() {
    	try {
    		FileOutputStream theFile = theContext.openFileOutput(SettingsFileName, Context.MODE_PRIVATE) ;
    		theFile.write(Username.getBytes());
    		theFile.write(0) ;
    		theFile.write(Password.getBytes());
    		theFile.write(0) ;
    		theFile.close();
    		return true ;
    	} catch (Exception e) {
    		return false ;
    	}
    }
}
