package com.eryiche.frame.dataparser;

import java.util.HashMap;
import java.util.Map;

import com.eryiche.frame.dataparser.json.JsonDataParser;
import com.eryiche.frame.dataparser.xml.XmlDataParser;

/**
 * 数据解析器工厂
 * @author Administrator
 *
 */
public class DataParserFactory {

	public static final String DATA_PARSER_XML = "xml";
	
	public static final String DATA_PARSER_JSON = "json";
	
	/**
	 * 解析器类型
	 */
	public static String parserType = DATA_PARSER_XML;

	/**
	 * 为了防止重复的过多的创建数据解析器，当数据解析器创建好后先缓存起来
	 */
	private static Map<String, DataParser> mDataParsers = new HashMap<String, DataParser>();
	
	/**
	 * 得到一个数据解析器的实例
	 * @return
	 */
	public static DataParser createDataParser(String type) {
		
		DataParser dataParser = mDataParsers.get(type);
		
		if (dataParser == null) {
			if (DATA_PARSER_XML.equalsIgnoreCase(type)) { // XML解析器
				dataParser = new XmlDataParser();
			} else if (DATA_PARSER_JSON.equalsIgnoreCase(type)) { // JSON解析器
				dataParser = new JsonDataParser();
			}
			
			// TODO 其他类型的数据解析器
			
			
			mDataParsers.put(type, dataParser);
		}
		
		return dataParser;
	}

}
