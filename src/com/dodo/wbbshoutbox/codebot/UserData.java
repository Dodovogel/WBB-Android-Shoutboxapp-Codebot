/* **************************************************************
* Copyright: (c) 2013 Jonathan Berg (Dodovogel) & Phillip Nowak (xChucky) from codebot.de
* License: license.txt
* *************************************************************/

package com.dodo.wbbshoutbox.codebot;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserData {

	public static String readPref(String key, Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		String value = preferences.getString(key, "");

		return value;
	}

	public static void writePref(String key, String value, Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void deletePref(String key, Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.remove(key);
		editor.commit();
	}
}
