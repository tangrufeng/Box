package com.eryiche.frame.dataparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;


/**
 * 自定义的Map，用来存放结构化数据
 * @author Administrator
 *
 * @param <K>
 * @param <V>
 */
public class TreeHashMap<K, V> extends HashMap<K, V> implements Map<K, V>
{
    private static final long serialVersionUID = -814238145128956406L;

    public TreeHashMap() {
    }

    /**
     * 根据key值把Map中的list Map转换成List 要获取的LIST元素路径 
     * 例如：要得到debitCardList中Element数组，传入key值为responseBody/debitCardList，
     * 传入elmentName为Element
     * @param key List元素所在的路径
     * @param elementName list元素名称
     * @param map 查询数据源
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getListByKey(String key, String elementName) {
        
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        
        Map<String, Object> maps = null;
        
        
        // 指定的节点不是Map的时候直接返回空
        try {
        	maps = (Map<String, Object>) getObjectByKey(key);
        } catch (ClassCastException e) {
        	return null;
        }
        
        if (maps != null && maps.size() > 0) {

            // 获取不带编号的key
            Map<String, Object> tmpMap = (Map<String, Object>) maps.get(elementName);
            if (tmpMap != null) {
                lists.add(tmpMap);
            }

            // 带编号的key
            for (int i = 1; i < maps.size(); ++i) {
                tmpMap = (Map<String, Object>) maps.get(elementName + i);
                if (tmpMap != null) {
                    lists.add(tmpMap);
                }
            }
        }

        return lists;
        
    }
    
    public List<Map<String, Object>> getListByKey(String key) {
    	
    	String elementName = "element";
        
        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        
        Map<String, Object> maps = null;
        
        // 指定的节点不是Map的时候直接返回空
        try {
        	maps = (Map<String, Object>) getObjectByKey(key);
        } catch (ClassCastException e) {
        	return null;
        }
        
        if (maps != null && maps.size() > 0) {

            // 获取不带编号的key
            Map<String, Object> tmpMap = (Map<String, Object>) maps.get(elementName);
            if (tmpMap != null) {
                lists.add(tmpMap);
            }

            // 带编号的key
            for (int i = 1; i < maps.size(); ++i) {
                tmpMap = (Map<String, Object>) maps.get(elementName + i);
                if (tmpMap != null) {
                    lists.add(tmpMap);
                }
            }
        }

        return lists;
        
    }

    /**
     * 根据key值得到object对象
     * 
     * @param key Key值使用如下方式进行组装key1/key2/key3/key4
     * @return object对象，或为Map<String,object>或者是string、int等基本类型
     */
    @SuppressWarnings("unchecked")
    public Object getObjectByKey(String key)
    {
    	// 如果Key值为空直接返回空
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        
        // 使用斜杠分离字符串
        String[] keys = key.split("/");
        int keysLength = keys.length;

        // key值没有带斜杠标
        if (keysLength == 1) {
            return get(keys[0]);
        }
        
        // key值带斜杠，循环从Map中获取数据
        // i表示层级
        Map<Object, Object> childMap = null;
        for (int i=0; i<keysLength; i++) {
            String tmpKey = keys[i];
            if (TextUtils.isEmpty(tmpKey)) {
                continue;
            }
            
            if (i == 0) { // 如果是第0层直接从Map中取值
                childMap = (Map<Object, Object>) get(tmpKey);
            } else if (i < keysLength - 1) {
            	// 如果是中间层，则递归取出中间的Map对象
                childMap = (Map<Object, Object>) childMap.get(tmpKey);
            } else {
                return childMap.get(tmpKey); // 最后一层
            }
        }
        return null;
    }
}
