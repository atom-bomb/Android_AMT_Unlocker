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
 * AMT_Unlocker.java
 *
 * This bit of code is responsible for communicating with the web service to unlock the door
 * when i say web service, i mean straight up port 80, html web service--
 * all this thing does is log in to the AMT drupal site and send your PIN number to the form
 * that DavR added to force unlocking the door without a rfid badge. 
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
//import android.util.Log;

public class AMT_Unlocker {
	static final String LogTag = "AMTUnlocker" ;
	
	public static boolean UnlockDoor(String Username, String Password, String DoorPIN) throws Exception {
		boolean bSuccess = false ;
		
		//Log.d(LogTag, "Login");   
		DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpPost httpost = new HttpPost("http://acemonstertoys.org/node?destination=node");
             
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("op", "Log in"));
        nvps.add(new BasicNameValuePair("name", Username));
        nvps.add(new BasicNameValuePair("pass", Password));
        nvps.add(new BasicNameValuePair("openid.return_to", "http://acemonstertoys.org/openid/authenticate?destination=node"));
        nvps.add(new BasicNameValuePair("form_id", "user_login_block"));
        nvps.add(new BasicNameValuePair("form_build_id", "form-4d6478bc67a79eda5e36c01499ba4c88"));       

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = httpclient.execute(httpost);
        HttpEntity entity = response.getEntity();

        //Log.d(LogTag, "Login form get: " + response.getStatusLine());
        if (entity != null) {
            entity.consumeContent();
        }

        //Log.d(LogTag, "Post Login cookies:");
        // look for drupal_uid and fail out if it isn't there
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            //Log.d(LogTag, "None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                //Log.d(LogTag, "- " + cookies.get(i).toString());
                
                if (cookies.get(i).getName().equals("drupal_uid")) {
                	bSuccess = true ;
                }
            }
        }
        
        if (bSuccess) {
	        HttpPost httpost2 = new HttpPost("http://acemonstertoys.org/membership");
	        
	        List <NameValuePair> nvps2 = new ArrayList <NameValuePair>();
	        nvps2.add(new BasicNameValuePair("doorcode", DoorPIN));
	        nvps2.add(new BasicNameValuePair("forceit", "Open Door"));

	        httpost2.setEntity(new UrlEncodedFormEntity(nvps2, HTTP.UTF_8));

	        response = httpclient.execute(httpost2);
	        entity = response.getEntity();

	        //Log.d(LogTag, "Unlock form get: " + response.getStatusLine());
	        if (entity != null) {
	            entity.consumeContent();
	        }
        }
        
        httpclient.getConnectionManager().shutdown();   
        
        return bSuccess ;
    }
}
