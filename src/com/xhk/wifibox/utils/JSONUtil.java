package com.xhk.wifibox.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONUtil {
	static final String TAG = JSONUtil.class.getSimpleName();

	public static long getLong(JSONObject json, String key, int defaultValue) {
		long result = defaultValue;

		try {
			if (json != null) {
				String resultStr = !json.isNull(key) ? json.getString(key)
						: String.valueOf(defaultValue);
				result = Long.parseLong(resultStr);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return result;
	}

	public static long getLong(JSONObject json, String key) {
		return getLong(json, key, 0);
	}

	public static int getInt(JSONObject json, String key, int defaultValue) {
		int result = defaultValue;
		try {
			if (json != null) {
				String resultStr = !json.isNull(key) ? json.getString(key)
						: String.valueOf(defaultValue);
				result = Integer.parseInt(resultStr);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}

		return result;
	}

	public static int getInt(JSONObject json, String key) {
		return getInt(json, key, -100);
	}

	public static String getString(JSONObject json, String key) {
		try {
			if (json != null) {
				return !json.isNull(key) ? json.getString(key) : "";
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return "";
	}

	public static double getDouble(JSONObject json, String key,
			double defaultValue) {
		double result = defaultValue;

		try {
			if (json != null) {
				String resultStr = !json.isNull(key) ? json.getString(key)
						: String.valueOf(defaultValue);
				result = Double.parseDouble(resultStr);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return result;
	}

	public static double getDouble(JSONObject json, String key) {
		return getDouble(json, key, 0);
	}

	public static boolean getBoolean(JSONObject json, String key,
			boolean defaultValue) {
		boolean result = defaultValue;

		try {

			if (json != null) {
				result = !json.isNull(key) ? json.getBoolean(key)
						: defaultValue;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return result;
	}

	public static JSONArray getJSONArray(JSONObject json, String key) {
		try {
			if (json != null && !json.isNull(key)) {
				Object o = json.get(key);
				if (o instanceof JSONArray) {
					return (JSONArray) o;
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return new JSONArray();
	}

	public static JSONObject getJSONObject(JSONObject json, String key) {
		try {
			if (json != null && !json.isNull(key)) {
				Object o = json.get(key);
				if (o instanceof JSONObject) {
					return (JSONObject) o;
				}
			}
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (NullPointerException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return new JSONObject();
	}

	public static JSONObject getJSONObject(JSONArray JSONArr, int index) {
		try {
			if (JSONArr != null && !JSONArr.isNull(index))
				return JSONArr.getJSONObject(index);
		} catch (JSONException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return new JSONObject();
	}

	public static boolean getBoolean(JSONObject json, String key) {
		return getBoolean(json, key, false);
	}

	/**
	 * JSON对象到java对象的映射 这个方法要求json中的属性和对象的属性名称一致才能够被自动映射
	 * 
	 * @param t
	 *            要映射的对象,必须要有默认的构造方法
	 * @param json
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createOject(Class<T> classT, JSONObject json) {

		T t = null;
		try {
			t = classT.newInstance();
		} catch (InstantiationException e1) {
			Log.e(TAG, e1.getMessage(), e1);
		} catch (IllegalAccessException e1) {
			Log.e(TAG, e1.getMessage(), e1);
		}

		if (t == null) {
			return null;
		}

		// 循环查找json中的所有属性，并同时在类中找对应的属性
		// 并把json中的值填充到对象中
		Iterator<String> it = json.keys();
		while (it.hasNext()) {
			String key = it.next();
			try {
				Object obj = json.get(key);

				Field field = getDeclaredField(classT, key);
				if (field != null) {
					makeAccessible(field);
					field.set(t, obj);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (IllegalAccessException e) {
				Log.e(TAG, e.getMessage(), e);
			} catch (IllegalArgumentException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}

		return t;
	}

	/**
	 * 循环向上转型,获取类的DeclaredField.
	 */
	@SuppressWarnings("rawtypes")
	private static Field getDeclaredField(final Class clazz,
			final String fieldName) {
		for (Class superClass = clazz; superClass != Object.class; superClass = superClass
				.getSuperclass()) {
			try {
				return superClass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
		return null;
	}

	/**
	 * 强制转换fileld可访问.
	 */
	private static void makeAccessible(Field field) {
		if (!Modifier.isPublic(field.getModifiers())
				|| !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
			field.setAccessible(true);
		}
	}

}
