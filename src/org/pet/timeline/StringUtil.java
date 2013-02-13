package org.pet.timeline;

import java.util.Locale;
import java.util.Map;

import android.content.Context;

public class StringUtil {

	public static final String getString(Context context, int resId, Map<String, String> replacement) {
		String s = context.getString(resId);
		if(s != null && !s.equals("") && replacement != null) {
			for(String key : replacement.keySet()) {
				String actualKey = "\\{" + key.toUpperCase(Locale.US) + "\\}";
				s = s.replaceAll(actualKey, replacement.get(key));
			}
		}
		return s;
	}
	
}
