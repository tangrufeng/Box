package com.eryiche.frame.net.http;

import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

public class RequestParams {
    private static String ENCODING = "UTF-8";

    protected ConcurrentHashMap<String, String> urlParams;
    protected ConcurrentHashMap<String, FileWrapper> fileParams;
    protected ConcurrentHashMap<String, ArrayList<String>> urlParamsWithArray;

    public RequestParams() {
        init();
    }

    public RequestParams(Map<String, String> source) {
        init();

        for(Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public RequestParams(String key, String value) {
        init();

        put(key, value);
    }

    public RequestParams(Object... keysAndValues) {
      init();
      int len = keysAndValues.length;
      if (len % 2 != 0)
        throw new IllegalArgumentException("Supplied arguments must be even");
      for (int i = 0; i < len; i += 2) {
        String key = String.valueOf(keysAndValues[i]);
        String val = String.valueOf(keysAndValues[i + 1]);
        put(key, val);
      }
    }

    public void put(String key, String value){
        if(key != null && value != null) {
            urlParams.put(key, value);
        }
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    public void put(String key, ArrayList<String> values)  {
        if(key != null && values != null) {
            urlParamsWithArray.put(key, values);
        }
    }

    public void put(String key, InputStream stream) {
        put(key, stream, null);
    }

    public void put(String key, InputStream stream, String fileName) {
        put(key, stream, fileName, null);
    }

    public void put(String key, InputStream stream, String fileName, String contentType) {
        if(key != null && stream != null) {
            fileParams.put(key, new FileWrapper(stream, fileName, contentType));
        }
    }

    public void remove(String key){
        urlParams.remove(key);
        fileParams.remove(key);
        urlParamsWithArray.remove(key);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
            if(result.length() > 0)
                result.append("&");

            ArrayList<String> values = entry.getValue();
            for (int i = 0; i < values.size(); i++) {
                if (i != 0)
                    result.append("&");
                result.append(entry.getKey());
                result.append("=");
                result.append(values.get(i));
            }
        }

        return result.toString();
    }

    /**
     * 把当前的请求参数转换成HttpEntity
     * Returns an HttpEntity containing all request parameters
     */
    public HttpEntity getEntity() {
        HttpEntity entity = null;

        // 如果参数里有文件，则使用MultipartEngity来模拟form表达提交
        if(!fileParams.isEmpty()) {
            SimpleMultipartEntity multipartEntity = new SimpleMultipartEntity();

            // Add string params
            for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            // Add dupe params
            for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
                ArrayList<String> values = entry.getValue();
                for (String value : values) {
                    multipartEntity.addPart(entry.getKey(), value);
                }
            }

            // Add file params
            int currentIndex = 0;
            int lastIndex = fileParams.entrySet().size() - 1;
            for(ConcurrentHashMap.Entry<String, FileWrapper> entry : fileParams.entrySet()) {
                FileWrapper file = entry.getValue();
                if(file.inputStream != null) {
                    boolean isLast = currentIndex == lastIndex;
                    if(file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, file.contentType, isLast);
                    } else {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }

            entity = multipartEntity;
        } else {
            try {
                entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }

    private void init(){
        urlParams = new ConcurrentHashMap<String, String>();
        fileParams = new ConcurrentHashMap<String, FileWrapper>();
        urlParamsWithArray = new ConcurrentHashMap<String, ArrayList<String>>();
    }

    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> lparams = new LinkedList<BasicNameValuePair>();

        for(ConcurrentHashMap.Entry<String, String> entry : urlParams.entrySet()) {
            lparams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        for(ConcurrentHashMap.Entry<String, ArrayList<String>> entry : urlParamsWithArray.entrySet()) {
            ArrayList<String> values = entry.getValue();
            for (String value : values) {
                lparams.add(new BasicNameValuePair(entry.getKey(), value));
            }
        }

        return lparams;
    }

    protected String getParamString() {
        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }

    private static class FileWrapper {
        public InputStream inputStream;
        public String fileName;
        public String contentType;

        public FileWrapper(InputStream inputStream, String fileName, String contentType) {
            this.inputStream = inputStream;
            this.fileName = fileName;
            this.contentType = contentType;
        }

        public String getFileName() {
            if(fileName != null) {
                return fileName;
            } else {
                return "nofilename";
            }
        }
    }
}
