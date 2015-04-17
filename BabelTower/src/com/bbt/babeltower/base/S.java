package com.bbt.babeltower.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class S {

	public static final String DeviceName = "DeviceName";
	public static final String DeviceAddress = "DeviceAddress";
	public static final String sharedPreferences_mainName = "user";

	public static final String regularEx = "#~";

	public static String getString(Context c, String key) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		return preferences.getString(key, "");
	}

	public static String[] getStringSet(Context c, String key) {
		// 将数据保存至SharedPreferences
		String[] str = null;
		SharedPreferences sp = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		String values;
		values = sp.getString(key, "");
		str = values.split(regularEx);

		return str;
	}

	public static Boolean addStringSet(Context c, String key, String data) {
		String[] tmp = S.getStringSet(c, key);
		String tmp_all = "";
		for (int i = 1; i < tmp.length; i++) {
			tmp_all = tmp_all + regularEx + tmp[i];
		}
		tmp_all = tmp_all + regularEx + data;
		S.put(c, key, tmp_all);
		return true;
	}

	public static Boolean removeStringSet(Context c, String key, String data) {
		String[] tmp = S.getStringSet(c, key);
		Boolean res = false;
		String tmp_all = "";
		for (int i = 1; i < tmp.length; i++) {
			if (tmp[i].equals(data)) {
				res = true;
				i = i + 2; // (改了这里?)
				continue;
			}
			tmp_all = tmp_all + regularEx + tmp[i];
		}
		S.put(c, key, tmp_all);
		return res;
	}

	public static Boolean clearStringSet(Context c, String key) {
		String[] tmp = S.getStringSet(c, key);
		if (tmp != null) {
			String empty = "";
			S.put(c, key, empty);
			return true;
		} else {
			return false;
		}
	}

	public static boolean getBool(Context c, String key) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		return preferences.getBoolean(key, true);
	}

	public static int getInt(Context c, String key) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		return preferences.getInt(key, 0);
	}

	public static long getLong(Context c, String key) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		return preferences.getLong(key, 0);
	}

	public static void put(Context c, String key, int value) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static void put(Context c, String key, boolean value) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static void put(Context c, String key, long value) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static void put(Context c, String key, String value) {
		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static void put(Context c, String key, String[] values) {
		String str = "";
		SharedPreferences sp = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		if (values != null && values.length > 0) {
			for (String value : values) {
				str += value;
				str += regularEx;
			}
			Editor et = sp.edit();
			et.putString(key, str);
			et.commit();
		}
	}

	public static void clear(Context c) {

		SharedPreferences preferences = c.getSharedPreferences(sharedPreferences_mainName,
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.clear().commit();

	}
}