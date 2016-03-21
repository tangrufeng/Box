package com.eryiche.frame.dataparser.json;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.eryiche.frame.dataparser.DataParser;
import com.eryiche.frame.dataparser.TreeHashMap;


/**
 * Json解析器
 * @author EX-XIAOFANQING001
 *
 */
public final class JsonDataParser  implements DataParser {

	static final String TAG = JsonDataParser.class.getSimpleName();
	
	/**
	 * 解析XML数据
	 * @throws Exception 
	 */
	public TreeHashMap<String, Object> parseData(byte[] data) throws Exception {

		String text = null;;
		text = new String(data, "UTF-8");
	
		JSONObject jsonObject = new JSONObject(text);
		return parse(jsonObject);
	}
	

	/**
	 * 解析json对象
	 * 
	 * 返回json对应的Map结构
	 * 
	 * @param jsonObject 要解析的json对象
	 * @return 解析后的map结构
	 */
	public TreeHashMap<String, Object> parse(JSONObject jsonObject) {
		if (jsonObject == null) {
			return null;
		}
		
		// 要返回的数据
		TreeHashMap<String, Object> map = new TreeHashMap<String, Object>();
		
		// 循环每一个key
		Iterator<?> itr = jsonObject.keys();
		while (itr.hasNext()) {
			String key = (String) itr.next();
			Object value = jsonObject.opt(key);
			
			// 如果value是json对象，则递归解析
			if (value instanceof JSONObject) { 
				map.put(key, parse((JSONObject)value));
			
			// ru
			} else if (value instanceof JSONArray) { // 循环递归
				map.put(key, parse((JSONArray)value)); 
			} else {
				map.put(key, value);
			}
		}
		return map;
	}
	
	/**
	 * 
	 * @param jsonArray
	 * @return
	 */
	public TreeHashMap<String, Object> parse(JSONArray jsonArray) {
		TreeHashMap<String, Object>  map = new TreeHashMap<String, Object>();
		
		String element = "element";
		for (int i=0; i<jsonArray.length(); i++) {

			String key = element;
			if (i != 0) {
				key = element + i;
			}
			
			Object object =jsonArray.opt(i);
			
			if (object instanceof JSONObject) {
				map.put(key,  parse((JSONObject)object));
				
				
			} else if (object instanceof JSONArray) { 
				map.put(key, parse((JSONArray)object));
			} else {
				map.put(key, object);
			}
			
		}
		
		return map;
	}
}
