/* **************************************************************
* Copyright: (c) 2013 Jonathan Berg (Dodovogel) & Phillip Nowak (xChucky) from codebot.de
* License: license.txt
* *************************************************************/

package com.dodo.wbbshoutbox.codebot;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.dodo.wbbshoutbox.codebot.R;

public class Smilies {
	private static final Factory spannableFactory = Spannable.Factory
			.getInstance();

	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
		addPattern(emoticons, ":)", R.drawable.smile);
		addPattern(emoticons, ":(", R.drawable.sad);
		addPattern(emoticons, ";)", R.drawable.wink);
		addPattern(emoticons, "^^", R.drawable.squint);
		addPattern(emoticons, ":P", R.drawable.tongue);
		addPattern(emoticons, "8)", R.drawable.cool);
		addPattern(emoticons, ":D", R.drawable.biggrin);
		addPattern(emoticons, ";(", R.drawable.crying);
		addPattern(emoticons, ":rolleyes:", R.drawable.rolleyes);
		addPattern(emoticons, ":huh:", R.drawable.huh);
		addPattern(emoticons, ":S", R.drawable.unsure);
		addPattern(emoticons, ":love:", R.drawable.love);
		addPattern(emoticons, "X(", R.drawable.angry);
		addPattern(emoticons, "8|", R.drawable.blink);
		addPattern(emoticons, "?(", R.drawable.confused);
		addPattern(emoticons, ":cursing:", R.drawable.cursing);
		addPattern(emoticons, " :|", R.drawable.mellow);
		addPattern(emoticons, ":thumbdown:", R.drawable.thumbdown);
		addPattern(emoticons, ":thumbsup:", R.drawable.thumbsup);
		addPattern(emoticons, ":thumbup:", R.drawable.thumbup);
		addPattern(emoticons, "8o", R.drawable.w00t);
		addPattern(emoticons, ":pinch:", R.drawable.pinch);
		addPattern(emoticons, ":sleeping:", R.drawable.sleeping);
		addPattern(emoticons, ":wacko:", R.drawable.wacko);
		addPattern(emoticons, ":whistling:", R.drawable.whistling);
		addPattern(emoticons, ":evil:", R.drawable.evil);
		addPattern(emoticons, ":?:", R.drawable.question);
		addPattern(emoticons, ":!:", R.drawable.attention);
		addPattern(emoticons, ":facepalm:", R.drawable.facepalm);
		addPattern(emoticons, ":beer:", R.drawable.beer);
		addPattern(emoticons, ":burns:", R.drawable.burns);
		addPattern(emoticons, ":leech:", R.drawable.leech);
		addPattern(emoticons, ":rofl:", R.drawable.rofl);
		addPattern(emoticons, ":lol:", R.drawable.lol);
	}

	private static void addPattern(Map<Pattern, Integer> map, String smile,
			int resource) {
		map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	public static boolean addSmiles(Context context, Spannable spannable) {
		boolean hasChanges = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
			Matcher matcher = entry.getKey().matcher(spannable);
			while (matcher.find()) {
				boolean set = true;
				for (ImageSpan span : spannable.getSpans(matcher.start(),
						matcher.end(), ImageSpan.class))
					if (spannable.getSpanStart(span) >= matcher.start()
							&& spannable.getSpanEnd(span) <= matcher.end())
						spannable.removeSpan(span);
					else {
						set = false;
						break;
					}
				if (set) {
					hasChanges = true;
					spannable.setSpan(new ImageSpan(context, entry.getValue()),
							matcher.start(), matcher.end(),
							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}
		}
		return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
		Spannable spannable = spannableFactory.newSpannable(text);
		addSmiles(context, spannable);
		return spannable;
	}
}
