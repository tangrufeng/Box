package com.eryiche.frame.dataparser;




/**
 * 数据解析器
 * 
 * 可以实现不同类型的数据解析，而且统一解析成Map形式的数据
 * 
 * @author EX-XIAOFANQING001
 *
 */
public interface DataParser {

	/**
	 * 解析数据
	 * @param datas 要解析的数据
	 * @return 解析后的树形HashMap结构
	 */
	public TreeHashMap<String, Object> parseData(byte[] datas) throws Exception;
}
