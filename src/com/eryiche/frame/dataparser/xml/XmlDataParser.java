package com.eryiche.frame.dataparser.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.eryiche.frame.dataparser.DataParser;
import com.eryiche.frame.dataparser.TreeHashMap;


/**
 * Xml解析器
 * @author EX-XIAOFANQING001
 *
 */
public final class XmlDataParser extends DefaultHandler implements DataParser {

    private static final String[] sIgnoreList = new String[] { "Data" };
    
	private Stack<String> mTagStack = new Stack<String>();
	
	private Stack<TreeHashMap<String, Object>> mMapStack = new Stack<TreeHashMap<String, Object>>();

	private TreeHashMap<String, Object> mMaps;

	/**
	 * 当前存储数据的Map
	 */
	private TreeHashMap<String, Object> mCurrentMaps;

	private String mEndTagName = "";
	private String mTagValue = "";

	/**
	 * 标示当前解析TAG位于XML标签的第几层
	 */
	private int mTagDepth = 0;

	private StringBuffer buffer = new StringBuffer();

	/**
	 * 返回解析后的Map对象
	 * @return
	 */
	public TreeHashMap<String, Object> getObjectMap() {
		return mCurrentMaps;
	}

	/**
	 * 解析XML数据
	 */
	public TreeHashMap<String, Object> parseData(byte[] data) throws OutOfMemoryError, ParserConfigurationException, SAXException, IOException {
		ByteArrayInputStream byteArrayStream = new ByteArrayInputStream(data);

		SAXParserFactory saf = SAXParserFactory.newInstance();
		SAXParser parser = saf.newSAXParser();
		parser.parse(byteArrayStream, this);
		
		byteArrayStream.close();
		
		return mCurrentMaps;
	}

	/**
	 * 是否是忽略解析的标签
	 * @param tagName
	 * @return
	 */
	private boolean dealWitchIgnoreTag(String tagName) {
		boolean isIgnore = false;
		for (String str : sIgnoreList) {
			if (str.equalsIgnoreCase(tagName)) {
				isIgnore = true;
				break;
			}
		}
		return isIgnore;
	}

	@Override
	public void characters(char[] ch, int start, int length) {
		buffer.append(ch, start, length);
	}

	@Override
	public void startDocument() throws SAXException {
		// 初始化第一级Maps目录
		mMaps = new TreeHashMap<String, Object>();
		mCurrentMaps = mMaps;
	}

	@Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        String tagName = "".equals(localName) ? qName : localName;
        
        // 忽略标签
        if (dealWitchIgnoreTag(tagName)) {
            return;
        }
        
        // 清掉数据
        buffer.delete(0, buffer.length());
        
        // 当前进入下一层的时候把当前的Map入栈，并把深度加1
        if (mTagDepth + 1 == mTagStack.size()) {
            
            System.out.println("mTagDepth:" + mTagDepth);
            
            mMapStack.push(mCurrentMaps);
            
            mCurrentMaps = new TreeHashMap<String, Object>();
            mTagDepth += 1;
        }
        
        // 遇见标签则把标签入栈
        mTagStack.push(tagName);
        
//        System.out.println("mTagStack:" + mTagStack);
    }
	
	@Override
	public void endElement(String uri, String localName, String qName) {
		String tagName = localName == "" ? qName : localName;
		
		// 忽略标签
		if (dealWitchIgnoreTag(tagName)) {
			return;
		}

		mEndTagName = tagName;
		mTagValue = buffer.toString().trim();
		
		try {
		    // 取出栈定元素
			String stackTopTag = "";
			if (!mTagStack.empty()) {
				stackTopTag = mTagStack.peek().toString();
			}
			
			// 如果栈定元素和当前的结束节点相同
			if (stackTopTag.equalsIgnoreCase(mEndTagName)) {
			    
			    // 弹出节点
				mTagStack.pop();
				
				// 如果当前的深度和Tag栈的大小相同，标示当前遇见的是一个文本节点
				if (mTagDepth == mTagStack.size()) { 
					if (mCurrentMaps.containsKey(mEndTagName)) { // 
					    int num = getElementNum(mCurrentMaps, mEndTagName);
					    
						mCurrentMaps.put(mEndTagName + num, mTagValue);
					} else {
					    mCurrentMaps.put(mEndTagName, mTagValue);
					}
				}
				
				if (mTagDepth - 1 == mTagStack.size()) {    // 包含子节点的元素
					mMaps = mCurrentMaps;
					mCurrentMaps = mMapStack.pop();
					addElementToMap();
					mTagDepth -= 1;
				}
			}
		} catch (EmptyStackException e) {
		    
		} catch (Exception e) {
		
		}
	}

	private void addElementToMap() {
		if(mCurrentMaps.containsKey(mEndTagName)) {
			int num = getElementNum(mCurrentMaps, mEndTagName);
			mCurrentMaps.put(mEndTagName+num, mMaps);
		} else {
			mCurrentMaps.put(mEndTagName, mMaps);
		}
	}
	
	/**
	 * 
	 * [获取当前map中elmentName元素名称]<BR>
	 * @param currentMap currentMap
	 * @param elementName elementName
	 * @return int 
	 */
	@SuppressWarnings("unchecked")
    private int getElementNum(TreeHashMap<String, Object> currentMap, String elementName)
	{
		HashMap<String, Object> maps = (HashMap<String, Object>) currentMap.clone();
		
		Iterator<Entry<String, Object>> iterator = maps.entrySet().iterator();
		
		int num = 0;
		String keyName="";
		
		while (iterator.hasNext()) {
			Entry<String, Object>  entry=iterator.next();
			keyName = entry.getKey();
		
			if(keyName.startsWith(elementName)) {
				num+=1;
			}
		}
		
		return num;
	}

	@Override
	public void endDocument() {
	    // do nothing
	}
}
