/* **************************************************************
* Copyright: (c) 2013 Jonathan Berg (Dodovogel) & Phillip Nowak (xChucky) from codebot.de
* License: license.txt
* *************************************************************/

package com.dodo.wbbshoutbox.codebot;

import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import com.dodo.wbbshoutbox.codebot.R;

public class Settings2 extends PreferenceActivity implements
		SharedPreferences.OnSharedPreferenceChangeListener {
	private static int prefs = R.xml.preferences;
	SharedPreferences SP;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT >= 11) {
			try {
				getClass().getMethod("getFragmentManager");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			AddResourceApi11AndGreater();
		} else { // Api < 11
			AddResourceApiLessThan11();
		}

		SP = PreferenceManager.getDefaultSharedPreferences(Settings2.this);
		SharedPreferences.Editor SPEdit = SP.edit();

		SPEdit.putBoolean("cbKeeploggedin", true);
		if (!UserData.readPref("autorefresh", this).equals("")) {
			SPEdit.putBoolean("ar_enable", true);
		}
		if (!UserData.readPref("username", this).equals("")) {
			SPEdit.putBoolean("cbSaveUsername", true);
		}
		if (UserData.readPref("showtime", this).equals("1")) {
			SPEdit.putBoolean("cbShowTime", true);
		}
		if (UserData.readPref("refreshcircle", this).equals("1")) {
			SPEdit.putBoolean("cbShowProgressCircle", true);
		}
		// if(UserData.readPref("changechatdirection", this).equals("1")) {
		// SPEdit.putBoolean("cbChangeChatDirection", true);
		// }

		SPEdit.putString("txtTextSize", UserData.readPref("textsize", this));
		SPEdit.putString("txtArIntervall", String.valueOf(Integer
				.valueOf(UserData.readPref("ar_intervall", this)) / 1000));
		SPEdit.commit();

		SP.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SP.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		SP.unregisterOnSharedPreferenceChangeListener(this);
		System.out.println("Pause rausgehauen!!!");
		onResume();
	}

	@SuppressWarnings("deprecation")
	protected void AddResourceApiLessThan11() {
		addPreferencesFromResource(prefs);
	}

	@TargetApi(11)
	protected void AddResourceApi11AndGreater() {
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PF()).commit();
	}

	@TargetApi(11)
	public static class PF extends PreferenceFragment {
		@Override
		public void onCreate(final Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(Settings2.prefs); // outer class private
															// members seem to
															// be visible for
															// inner class, and
															// making it static
															// made things so
															// much easier
		}
	}

	/*
	 * public void username_save(SharedPreferences sharedPreferences) {
	 * if(!UserData.readPref("username", this).equals("")) {
	 * UserData.deletePref("username", this); Toast.makeText(this,
	 * "Username unsaved!", Toast.LENGTH_SHORT).show(); } else {
	 * if(MainActivity.Usernamefield.getText().toString().trim().length() <= 0
	 * && MainActivity.loggedIn == 0) { Toast.makeText(this,
	 * "You did not enter a username!", Toast.LENGTH_SHORT).show();
	 * SharedPreferences.Editor SPEdit = sharedPreferences.edit();
	 * SPEdit.putBoolean("cbSaveUsername", false); SPEdit.commit(); return; }
	 * UserData.writePref("username",
	 * MainActivity.Usernamefield.getText().toString(), this);
	 * Toast.makeText(this, "Username saved!", Toast.LENGTH_SHORT).show(); } }
	 */

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (key.equals("ar_enable")) {
			MainActivity.autorefresh_set();
		} else if (key.equals("txtArIntervall")) {
			if (sharedPreferences.getString("txtArIntervall", "30").trim()
					.length() <= 0) {
				return;
			}
			if (MainActivity.autorefresh != 1) {
				UserData.writePref("ar_intervall", String.valueOf(Integer
						.valueOf(sharedPreferences.getString("txtArIntervall",
								"10")) * 1000), this);
			} else {
				MainActivity.autorefresh_set();
				UserData.writePref("ar_intervall", String.valueOf(Integer
						.valueOf(sharedPreferences.getString("txtArIntervall",
								"10")) * 1000), this);
				MainActivity.autorefresh_set();
			}
		} else if (key.equals("txtTextSize")) {
			if (sharedPreferences.getString("txtTextSize", "14").trim()
					.length() <= 0) {
				UserData.writePref("textsize", "14", this);
			} else {
				UserData.writePref("textsize",
						sharedPreferences.getString("txtTextSize", "14"), this);
			}
			MainActivity.setTextSize();
		} else if (key.equals("cbShowTime")) {
			if (MainActivity.showTime == 1) {
				UserData.writePref("showtime", "0", this);
				MainActivity.showTime = 0;
				MainActivity.refresh();
			} else {
				UserData.writePref("showtime", "1", this);
				MainActivity.showTime = 1;
				MainActivity.refresh();
			}
		} else if (key.equals("cbShowProgressCircle")) {
			if (MainActivity.refreshanimation == 1) {
				UserData.writePref("refreshcircle", "0", this);
				MainActivity.refreshanimation = 0;
			} else {
				UserData.writePref("refreshcircle", "1", this);
				MainActivity.refreshanimation = 1;
			}
		} else if (key.equals("cbChangeChatDirection")) {
			if (MainActivity.changechatdirection == 1) {
				UserData.writePref("cbChangeChatDirection", "0", this);
				MainActivity.changechatdirection = 0;
			} else {
				UserData.writePref("cbChangeChatDirection", "1", this);
				MainActivity.changechatdirection = 1;
			}
		}
	}
}