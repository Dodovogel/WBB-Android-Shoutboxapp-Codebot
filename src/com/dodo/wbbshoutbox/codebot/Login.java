/* **************************************************************
* Copyright: (c) 2013 Jonathan Berg (Dodovogel) & Phillip Nowak (xChucky) from codebot.de
* License: license.txt
* *************************************************************/

package com.dodo.wbbshoutbox.codebot;

import com.dodo.wbbshoutbox.codebot.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;

public class Login extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		if (!UserData.readPref("username", this).equals("")) {
			EditText txtUsername = (EditText) findViewById(R.id.txtNick);

			txtUsername.setText(UserData.readPref("username", this));
		} else if (MainActivity.Usernamefield.getText().toString().trim()
				.length() > 0) {
			EditText txtUsername = (EditText) findViewById(R.id.txtNick);

			txtUsername
					.setText(MainActivity.Usernamefield.getText().toString());
		}

		final Button cmdLogin = (Button) findViewById(R.id.cmdLogin);
		cmdLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				login();
			}
		});
	}

	public void login() {
		EditText txtNick = (EditText) findViewById(R.id.txtNick);
		EditText txtPass = (EditText) findViewById(R.id.txtPass);

		if (txtNick.getText().toString().trim().length() <= 0) {
			txtNick.requestFocus();
			Toast.makeText(this, "Du hast keinen Benutzernamen eingegeben!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (txtPass.getText().toString().trim().length() <= 0) {
			txtPass.requestFocus();
			Toast.makeText(this, "Du hast kein Passwort eingegeben!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		RequestParams params = new RequestParams();

		params.put("loginUsername", txtNick.getText().toString());
		params.put("loginPassword", txtPass.getText().toString());
		params.put("useCookies", "1");
		params.put("url", "");

		postRequestLogin(MainActivity.baseUrl + "index.php?form=UserLogin",
				params);
	}

	public void postRequestLogin(String url, RequestParams params) {
		Request.client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(String response) {
				if (response
						.contains("Bitte geben Sie die untenstehenden Zeichen")) {
					Toast.makeText(getApplicationContext(),
							"Du musst ein Captcha lösen!", Toast.LENGTH_SHORT)
							.show();
				} else {

					if (response.contains("Sie wurden erfolgreich angemeldet")) {
						EditText txtNick = (EditText) findViewById(R.id.txtNick);
						MainActivity.loggedIn = 1;
						MainActivity.updateMenu();

						MainActivity.Usernamefield.setText(txtNick.getText()
								.toString());
						MainActivity.Usernamefield.setEnabled(false);

						if (MainActivity.requireLogin == 1) {
							MainActivity.Sendbutton.setEnabled(true);
							MainActivity.Sendbutton.setText("Senden");
						}

						UserData.writePref("cookies",
								MainActivity.myCookieStore.getCookies()
										.toString(), getApplicationContext());
						UserData.writePref("username_login", txtNick.getText()
								.toString(), getApplicationContext());

						finish();

					} else {
						MainActivity.myCookieStore.clear();
						Toast.makeText(getApplicationContext(),
								"Falsche Login-Daten!", Toast.LENGTH_SHORT)
								.show();
					}
				}
			}

			@Override
			public void onFailure(Throwable e, String errorResponse) {
				Toast.makeText(getApplicationContext(),
						"Verbindung fehlgeschlagen. Fehler: " + e.getMessage(),
						Toast.LENGTH_SHORT).show();
				MainActivity.myCookieStore.clear();
			}
		});
	}
}
