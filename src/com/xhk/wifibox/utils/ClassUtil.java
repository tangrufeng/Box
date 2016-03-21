/* 
 * @Title:  ClassUtil.java 
 * @Copyright:  jc-yt Co., Ltd. Copyright 2009-2015,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  Tom 
 * @data:  2015-9-10 下午5:49:36 
 * @version:  V1.0 
 */
package com.xhk.wifibox.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.PathClassLoader;

import android.util.Log;

/**
 * @author tang
 * 
 */
public class ClassUtil {
	private final static String TAG = ClassUtil.class.getSimpleName();

	public static List<Class<?>> getAllAssignedClass(Class<?> cls){
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (Class<?> c : getClasses(cls)) {
			if (cls.isAssignableFrom(c) && !cls.equals(c)) {
				classes.add(c);
			}
		}
		return classes;
	}

	public static List<Class<?>> getClasses(Class<?> cls) {

		String pk = cls.getPackage().getName();
		String path = pk.replace('.', File.separatorChar);
		ClassLoader classloader = Thread.currentThread()
				.getContextClassLoader();
//		PathClassLoader pathLoader=new PathClassLoader(, parent)
		URL url = classloader.getResource(path+File.separatorChar);
		Log.e(TAG, "========url=="+url+"=========path==="+path);
		return getClasses(new File(url.getFile()), pk);

	}

	private static List<Class<?>> getClasses(File dir, String pk){

		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!dir.exists()) {
			return classes;
		}
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				classes.addAll(getClasses(f, pk + "." + f.getName()));
			}
			String name = f.getName();
			if (name.endsWith(".class")) {
				try {
					classes.add(Class.forName(pk + "."
							+ name.substring(0, name.length() - 6)));
				} catch (ClassNotFoundException e) {
					Log.e(TAG, e.getLocalizedMessage(),e);
					return null;
				}
			}
		}

		return classes;

	}
}
