/* **************************************************************
* Copyright: (c) 2013 Jonathan Berg (Dodovogel) & Phillip Nowak (xChucky) from codebot.de
* License: license.txt
* *************************************************************/

package com.dodo.wbbshoutbox.codebot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Cookies {
	public static String getName(String input) {
		Pattern p = Pattern.compile("\\[name: (.*?)\\]");
		Matcher m = p.matcher(input);

		while (m.find()) {
			return m.group().replace("[name: ", "").replace("]", "");
		}

		return "";
	}

	public static String getValue(String input) {
		Pattern p = Pattern.compile("\\[value: (.*?)\\]");
		Matcher m = p.matcher(input);

		while (m.find()) {
			return m.group().replace("[value: ", "").replace("]", "");
		}

		return "";
	}

	public static String getDomain(String input) {
		Pattern p = Pattern.compile("\\[domain: (.*?)\\]");
		Matcher m = p.matcher(input);

		while (m.find()) {
			return m.group().replace("[domain: ", "").replace("]", "");
		}

		return "";
	}

	public static String getPath(String input) {
		Pattern p = Pattern.compile("\\[path: (.*?)\\]");
		Matcher m = p.matcher(input);

		while (m.find()) {
			return m.group().replace("[path: ", "").replace("]", "");
		}

		return "";
	}

	public static String getExpiry(String input) {
		Pattern p = Pattern.compile("\\[expiry: (.*?)\\]");
		Matcher m = p.matcher(input);

		while (m.find()) {
			return m.group().replace("[expiry: ", "").replace("]", "");
		}

		return "";
	}

	public static int getVersion(String input) {
		Pattern p = Pattern.compile("\\[version: (.*?)\\]");
		Matcher m = p.matcher(input);

		while (m.find()) {
			return Integer.parseInt(m.group().replace("[version: ", "")
					.replace("]", ""));
		}

		return 0;
	}
}
