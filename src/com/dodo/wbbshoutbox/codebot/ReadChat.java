/* **************************************************************
 * Copyright: (c) 2013 Jonathan Berg (Dodovogel) & Phillip Nowak (xChucky) from codebot.de
 * License: license.txt
 * *************************************************************/

package com.dodo.wbbshoutbox.codebot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.content.Context;
//import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.ProgressBar;

public class ReadChat
		extends
		AsyncTask<String, Map<String, ArrayList<String>>, Map<String, ArrayList<String>>> {
	ArrayList<String> usernames = new ArrayList<String>();
	ArrayList<String> messages = new ArrayList<String>();
	ArrayList<String> times = new ArrayList<String>();
	Context context;

	public ReadChat(Context c) {
		context = c;
	}

	public void onPreExecute() {
		MainActivity.Refreshbutton.setEnabled(false);
		if (MainActivity.refreshanimation == 1) {
			MainActivity.pbReadChat.setVisibility(ProgressBar.VISIBLE);
		}
	}

	public Map<String, ArrayList<String>> doInBackground(String... strings) {
		String response = strings[0];

		Pattern p = Pattern.compile("<username>(.*?)</username>");
		Matcher m = p.matcher(response);

		while (m.find()) {
			Pattern p2 = Pattern.compile("<!\\[CDATA\\[(.*?)\\]\\]");
			Matcher m2 = p2.matcher(m.group());

			while (m2.find()) {
				Pattern p5 = Pattern.compile("\\]\\](.*?)\\[");
				Matcher m5 = p5.matcher(MainActivity.umkehren(m2.group()));

				while (m5.find()) {
					if (MainActivity.umkehren(m5.group()).contains(
							"color: #0A74DE")) { // Admin
						usernames
								.add(MainActivity
										.umkehren(m5.group())
										.replace("]]", "")
										.replace("[", "")
										.replace("<span>", "")
										.replace("<strong>", "")
										.replace("</strong>", "</font>")
										.replace(
												"<strong style=\"color: #0A74DE; text-shadow: 1px 1px 2px rgba(0, 119, 238, 1);\">",
												"<font color='#0A74DE'>"));
					} else if (MainActivity.umkehren(m5.group()).contains(
							"color: #B21F00")) { // Mods
						usernames.add(MainActivity
								.umkehren(m5.group())
								.toString()
								.replace("]]", "")
								.replace("[", "")
								.replace("<span>", "")
								.replace("<strong>", "")
								.replace("</strong>", "")
								.replace("<span style=\"color: #B21F00\">",
										"<font color='#B21F00'>")
								.replace("</span>", "</font>"));
					} else if (MainActivity.umkehren(m5.group()).contains(
							"color: #990000")) { // Techniker
						usernames.add(MainActivity
								.umkehren(m5.group())
								.toString()
								.replace("]]", "")
								.replace("[", "")
								.replace("<span>", "")
								.replace("<strong>", "")
								.replace("</strong>", "")
								.replace("<span style=\"color: #990000\">",
										"<font color='#990000'>")
								.replace("</span>", "</font>"));
					} else if (MainActivity.umkehren(m5.group()).contains(
							"color: #A614C5")) { // Donator
						usernames.add(MainActivity
								.umkehren(m5.group())
								.replace("]]", "")
								.replace("[", "")
								.replace("<span>", "")
								.replace("<strong>", "")
								.replace("</strong>", "")
								.replace("<span style=\"color: #A614C5\">",
										"<font color='#A614C5'>")
								.replace("</span>", "</font>"));
					} else if (MainActivity.umkehren(m5.group()).contains(
							"color: #FBC731")) { // Donator2
						usernames
								.add(MainActivity
										.umkehren(m5.group())
										.replace("]]", "")
										.replace("[", "")
										.replace("<span>", "")
										.replace("<strong>", "")
										.replace("</strong>", "</font>")
										.replace(
												"<strong style=\"color: #FBC731; text-shadow: 1px 1px 1px rgba(0, 0, 0, 1);\">",
												"<font color='#FBC731'>"));
					} else if (MainActivity.umkehren(m5.group()).contains(
							"color: #00994B")) { // Donator3
						usernames
								.add(MainActivity
										.umkehren(m5.group())
										.replace("]]", "")
										.replace("[", "")
										.replace("<span>", "")
										.replace("<strong>", "")
										.replace("</strong>", "</font>")
										.replace(
												"<strong style=\"color: #00994B; text-shadow: 1px 1px 1px rgba(0, 0, 0, 1);\">",
												"<font color='#00994B'>"));
					} else {
						usernames.add(MainActivity.umkehren(m5.group())
								.replace("]]", "").replace("[", "")
								.replace("<span>", "").replace("<strong>", "")
								.replace("</strong>", "")
								.replace("</span>", ""));
					}
				}
			}
		}

		// -------------- MESSAGE --------------- \\

		Pattern p3 = Pattern.compile("<message>(.*?)</message>");
		Matcher m3 = p3.matcher(response);

		while (m3.find()) {
			Pattern p4 = Pattern.compile("<!\\[CDATA\\[(.*?)\\]\\]>");
			Matcher m4 = p4.matcher(m3.group());

			while (m4.find()) {
				if (m4.group().contains("<img src")) {
					String message_aktuell = "";

					Pattern p6 = Pattern.compile("<img src(.*?)/>");
					Matcher m6 = p6.matcher(m4.group());

					while (m6.find()) {
						Document doc;

						doc = Jsoup.parse(m6.group());

						Element image = doc.select("img").first();

						if (message_aktuell.equals("")) {
							message_aktuell = m4.group().replace(m6.group(),
									image.attr("alt"));
						} else {
							message_aktuell = message_aktuell.replace(
									m6.group(), image.attr("alt"));
						}
					}
					messages.add(message_aktuell.replace("<![CDATA[", "")
							.replace("]]>", ""));
				} else {
					messages.add(m4.group().replace("<![CDATA[", "")
							.replace("]]>", ""));
				}
			}
		}

		// ---------- TIME ----------------- \\

		Pattern p7 = Pattern.compile("<time>(.*?)</time>");
		Matcher m7 = p7.matcher(response);

		while (m7.find()) {
			Calendar time = GregorianCalendar.getInstance();
			time.setTimeInMillis((long) Long.valueOf(m7.group()
					.replace("<time>", "").replace("</time>", "")) * 1000);

			StringBuffer timestamp = new StringBuffer();
			if (time.get(Calendar.HOUR_OF_DAY) >= 10) {
				timestamp.append(time.get(Calendar.HOUR_OF_DAY));
			} else {
				timestamp.append("0" + time.get(Calendar.HOUR_OF_DAY));
			}

			timestamp.append(":");

			if (time.get(Calendar.MINUTE) >= 10) {
				timestamp.append(time.get(Calendar.MINUTE));
			} else {
				timestamp.append("0" + time.get(Calendar.MINUTE));
			}

			times.add(String.valueOf(timestamp));
		}

		Map<String, ArrayList<String>> result = new HashMap<String, ArrayList<String>>();
		result.put("usernames", usernames);
		result.put("messages", messages);
		result.put("times", times);

		return result;
	}

	public void onPostExecute(Map<String, ArrayList<String>> result) {
		super.onPostExecute(result);
		MainActivity.Refreshbutton.setEnabled(true);
		if (MainActivity.refreshanimation == 1) {
			MainActivity.pbReadChat.setVisibility(ProgressBar.INVISIBLE);
		}
		MainActivity.lblVerlauf.setText("");

		if (MainActivity.changechatdirection == 1) {
			if (MainActivity.showTime == 0) {
				for (int i = 0; i <= result.get("usernames").toArray().length - 1; i++) {
					MainActivity.lblVerlauf
							.append(Smilies.getSmiledText(
									context,
									Html.fromHtml("<b>"
											+ result.get("usernames").get(i)
											+ "</b>: "
											+ result.get("messages").get(i)
											+ "<br />")));
				}
			} else {
				for (int i = 0; i <= result.get("usernames").toArray().length - 1; i++) {
					MainActivity.lblVerlauf
							.append(Smilies.getSmiledText(
									context,
									Html.fromHtml(result.get("times").get(i)
											+ " - <b>"
											+ result.get("usernames").get(i)
											+ "</b>: "
											+ result.get("messages").get(i)
											+ "<br />")));
				}
			}
			MainActivity.lblVerlauf.scrollTo(0,
					MainActivity.lblVerlauf.getScrollY());
		} else {
			if (MainActivity.showTime == 0) {
				for (int i = result.get("usernames").toArray().length - 1; i >= 0; i--) {
					MainActivity.lblVerlauf
							.append(Smilies.getSmiledText(
									context,
									Html.fromHtml("<b>"
											+ result.get("usernames").get(i)
											+ "</b>: "
											+ result.get("messages").get(i)
											+ "<br />")));
				}
			} else {
				for (int i = result.get("usernames").toArray().length - 1; i >= 0; i--) {
					MainActivity.lblVerlauf
							.append(Smilies.getSmiledText(
									context,
									Html.fromHtml(result.get("times").get(i)
											+ " - <b>"
											+ result.get("usernames").get(i)
											+ "</b>: "
											+ result.get("messages").get(i)
											+ "<br />")));
				}
			}
			MainActivity.lblVerlauf.scrollTo(0, 0);
		}
	}

}
