/* **************************************************************
* Copyright: (c) 2013 Jonathan Berg (Dodovogel) & Phillip Nowak (xChucky) from codebot.de
* License: license.txt
* *************************************************************/

package com.dodo.wbbshoutbox.codebot;

import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Toast;

import org.apache.http.impl.cookie.BasicClientCookie;

import com.dodo.wbbshoutbox.codebot.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class MainActivity extends Activity {

	static String baseUrl = "http://www.codebot.de/";
	AsyncHttpClient client2 = new AsyncHttpClient();
	String version = "1.1.3";
	static int loggedIn = 0;
	static int requireLogin = 1;
	static int timeoutchat = 0;
	static int autorefresh = 0;
	static int showTime = 1;
	static int refreshanimation = 0;
	static int changechatdirection = 0;
	static Menu menucopy;
	static Context context;
	static PersistentCookieStore myCookieStore;
	public static TextView Usernamefield;
	public static Button Sendbutton;
	public static Button Refreshbutton;
	public static ProgressBar pbReadChat;
	public static TextView lblVerlauf;
	public static TextView lblAutoRefresh;
	static Runnable runable;
	static Handler mHandler = new Handler();
	Runnable rTimeout;
	Handler mTimeout = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = getApplicationContext();

		checkUpdate();

		Request.client.setUserAgent("Dodo Shoutboxapp");

		myCookieStore = new PersistentCookieStore(this);
		Request.client.setCookieStore(myCookieStore);

		Usernamefield = (TextView) findViewById(R.id.txtUsername);
		Sendbutton = (Button) findViewById(R.id.cmdSend);
		Refreshbutton = (Button) findViewById(R.id.cmdRefresh);
		pbReadChat = (ProgressBar) findViewById(R.id.pbReadChat);
		lblVerlauf = (TextView) findViewById(R.id.lblVerlauf);
		lblAutoRefresh = (TextView) findViewById(R.id.lblAutorefresh);

		if (UserData.readPref("textsize", this).equals("")) {
			UserData.writePref("textsize", "10", this);
		}
		if (UserData.readPref("refreshcircle", this).equals("")) {
			UserData.writePref("refreshcircle", "1", this);
			refreshanimation = 1;
		} else if (UserData.readPref("refreshcircle", this).equals("1")) {
			refreshanimation = 1;
		}
		/*
		 * if(UserData.readPref("changechatdirection", this).equals("")) {
		 * UserData.writePref("changechatdirection", "0", this); } else
		 * if(UserData.readPref("changechatdirection", this).equals("1")) {
		 * changechatdirection = 1; }
		 */
		if (UserData.readPref("showtime", this).equals("")) {
			UserData.writePref("showtime", "1", this);
		} else {
			showTime = Integer.valueOf(UserData.readPref("showtime", this));
		}

		if (!UserData.readPref("username", this).equals("")) {
			Usernamefield.setText(UserData.readPref("username", this));
		}

		if (!UserData.readPref("autorefresh", this).equals("")) {
			Button cmdRefresh = (Button) findViewById(R.id.cmdRefresh);
			TextView lblARefresh = (TextView) findViewById(R.id.lblAutorefresh);

			cmdRefresh.setVisibility(Button.INVISIBLE);
			lblARefresh.setVisibility(TextView.VISIBLE);
			Toast.makeText(this, "Automatisches Laden aktiviert!",
					Toast.LENGTH_SHORT).show();
			autorefresh = 1;
		}

		if (UserData.readPref("ar_intervall", this).equals("")) {
			UserData.writePref("ar_intervall", "30000", this);
		}

		setTextSize();
		setCookies();

		final Button cmdSend = (Button) findViewById(R.id.cmdSend);
		if (requireLogin == 1 && loggedIn == 0) {
			cmdSend.setEnabled(false);
			cmdSend.setText("Zuerst einloggen!");
		}
		cmdSend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				send();
			}
		});

		final Button cmdRefresh = (Button) findViewById(R.id.cmdRefresh);
		cmdRefresh.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				getRequest(baseUrl + "index.php?page=ShoutboxEntryXMLList");
			}
		});

		final Button cmdMenu = (Button) findViewById(R.id.cmdMenu);
		cmdMenu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				// openOptionsMenu();
				Intent myIntent2 = new Intent(getApplicationContext(),
						Settings2.class);
				startActivityForResult(myIntent2, 0);
			}
		});

		TextView lblVerlauf = (TextView) findViewById(R.id.lblVerlauf);
		lblVerlauf.setMovementMethod(LinkMovementMethod.getInstance());
		lblVerlauf.setMovementMethod(new ScrollingMovementMethod());

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		menucopy = menu;
		MenuItem item_login = menucopy.findItem(R.id.login);

		if (!UserData.readPref("cookies", this).equals("")
				&& item_login.isVisible()) {
			updateMenu();
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.login:
			Intent myIntent = new Intent(getApplicationContext(), Login.class);
			startActivityForResult(myIntent, 0);
			return true;
		case R.id.settings:
			Intent myIntent2 = new Intent(getApplicationContext(),
					Settings2.class);
			startActivityForResult(myIntent2, 0);
			return true;
		case R.id.logout:
			logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static void updateMenu() {
		MenuItem item_login = menucopy.findItem(R.id.login);
		MenuItem item_logout = menucopy.findItem(R.id.logout);

		if (loggedIn == 1) {
			item_login.setVisible(false);
			item_logout.setVisible(true);
		} else {
			item_login.setVisible(true);
			item_logout.setVisible(false);
		}
	}

	public void checkUpdate() {
		client2.get("http://dodovogel.censored.de/lock.txt",
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						if (!response.equals("")) {
							alertBoxLocked(response, "Info");
						}
					}
				});
	}

	/*
	 * client2.get(dodo, new AsyncHttpResponseHandler() {
	 * 
	 * @Override public void onSuccess(String response) {
	 * if(!response.equals(version)) { alertBoxUpdate("New version available!",
	 * "Update"); } } }); }
	 */

	public void alertBoxLocked(String message, String title) {
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setTitle(title)
				.setCancelable(false)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								finish();
								System.exit(0);
							}
						}).show();
	}

	public void alertBoxUpdate(String message, String title) {
		new AlertDialog.Builder(this)
				.setMessage(message)
				.setTitle(title)
				.setCancelable(true)
				.setNeutralButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).show();
	}

	public static void refresh() {
		RequestParams params = new RequestParams();
		params.put("page", "ShoutboxEntryXMLList");

		Request.get(baseUrl + "index.php", params,
				new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(String response) {
						try {
							read(response);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(Throwable e, String errorResponse) {
						Toast.makeText(
								context,
								"Verbindung fehlgeschlagen. Fehler: "
										+ e.getMessage(), Toast.LENGTH_SHORT)
								.show();
					}
				});

		if (!UserData.readPref("autorefresh", context).equals("")) {
			autorefresh();
		}
	}

	public static void autorefresh() {
		runable = new Runnable() {
			@Override
			public void run() {
				refresh();
			}
		};
		mHandler.postDelayed(runable,
				Integer.valueOf(UserData.readPref("ar_intervall", context)));
	}

	public void logout() {
		Toast.makeText(getApplicationContext(), "Erfolgreich ausgeloggt!",
				Toast.LENGTH_SHORT).show();
		loggedIn = 0;
		myCookieStore.clear();

		if (!UserData.readPref("cookies", getApplicationContext()).equals("")) {
			UserData.deletePref("cookies", getApplicationContext());
			UserData.deletePref("username_login", getApplicationContext());
		}

		Usernamefield.setEnabled(true);

		if (requireLogin == 1) {
			Sendbutton.setEnabled(false);
			Sendbutton.setText("Zuerst einloggen!");
		}

		timeout();
		updateMenu();
	}

	public void send() {
		EditText Username = (EditText) findViewById(R.id.txtUsername);
		EditText Message = (EditText) findViewById(R.id.txtMessage);

		if (Username.getText().toString().trim().length() <= 0 && loggedIn == 0) {
			Username.requestFocus();
			Toast.makeText(this, "Du hast keinen Benutzernamen eingegeben!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (Message.getText().toString().trim().length() <= 0) {
			Message.requestFocus();
			Toast.makeText(this, "Du hast keine Nachricht eingegeben!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		RequestParams params_send = new RequestParams();

		params_send.put("message", Message.getText().toString());

		if (loggedIn == 0) {
			params_send.put("username", Username.getText().toString()
					+ " (App)");
		} else {
			params_send.put("username", "");
		}
		params_send.put("ajax", "1");

		postRequest(baseUrl + "index.php?action=ShoutboxEntryAdd", params_send);
	}

	public void timeout() {
		if (loggedIn == 0 && timeoutchat > 0) {
			final Button cmdSend = (Button) findViewById(R.id.cmdSend);
			cmdSend.setEnabled(false);

			rTimeout = new Runnable() {
				@Override
				public void run() {
					cmdSend.setEnabled(true);
					mTimeout.removeCallbacks(rTimeout);
				}
			};

			mTimeout.postDelayed(rTimeout, timeoutchat * 1000);
		}
	}

	public static void read(String response) throws InterruptedException,
			ExecutionException {
		new ReadChat(context).execute(response);
	}

	public static String umkehren(String str) {
		String umgekehrt = new String();

		for (int j = str.length() - 1; j >= 0; j--)
			umgekehrt += str.charAt(j);

		return umgekehrt;
	}

	public void getRequest(String url) {
		Request.client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				try {
					read(response);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(Throwable e, String errorResponse) {
				Toast.makeText(getApplicationContext(),
						"Verbindung fehlgeschlagen. Fehler: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	public void postRequest(String url, RequestParams params) {
		Request.client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				EditText txtMessage = (EditText) findViewById(R.id.txtMessage);
				txtMessage.setText("");
				Toast.makeText(getApplicationContext(), "Nachricht gesendet!",
						Toast.LENGTH_SHORT).show();
				if (timeoutchat > 0) {
					timeout();
				}
				getRequest(baseUrl + "index.php?page=ShoutboxEntryXMLList");
			}

			@Override
			public void onFailure(Throwable e, String errorResponse) {
				Toast.makeText(getApplicationContext(),
						"Fehler beim Senden der Nachricht: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
			}
		});

	}

	public static void autorefresh_set() {

		if (!UserData.readPref("autorefresh", context).equals("")) {
			UserData.deletePref("autorefresh", context);
			Refreshbutton.setVisibility(Button.VISIBLE);
			lblAutoRefresh.setVisibility(TextView.INVISIBLE);
			mHandler.removeCallbacks(runable);
			// Toast.makeText(context, "Autorefresh deactivated!",
			// Toast.LENGTH_SHORT).show();
			autorefresh = 0;
		} else {
			UserData.writePref("autorefresh", "on", context);
			Refreshbutton.setVisibility(Button.INVISIBLE);
			lblAutoRefresh.setVisibility(TextView.VISIBLE);
			// Toast.makeText(context, "Autorefresh activated!",
			// Toast.LENGTH_SHORT).show();
			autorefresh = 1;

			autorefresh();
		}
	}

	public void setCookies() {
		if (!UserData.readPref("cookies", this).equals("")) {
			String[] data = UserData.readPref("cookies", this).split("\\,");

			for (String item : data) {
				BasicClientCookie newCookie = new BasicClientCookie(
						Cookies.getName(item), Cookies.getValue(item));
				newCookie.setVersion(Cookies.getVersion(item));
				newCookie.setDomain(Cookies.getDomain(item));
				newCookie.setPath(Cookies.getPath(item));
				myCookieStore.addCookie(newCookie);
			}

			loggedIn = 1;

			String username = UserData.readPref("username_login", this);

			Usernamefield.setText(username);
			Usernamefield.setEnabled(false);

			if (requireLogin == 1) {
				Sendbutton.setEnabled(true);
				Sendbutton.setText("Senden");
			}
		}
	}

	public static void setTextSize() {
		lblVerlauf.setTextSize(Float.valueOf(UserData.readPref("textsize",
				context)));
		refresh();
	}

}
