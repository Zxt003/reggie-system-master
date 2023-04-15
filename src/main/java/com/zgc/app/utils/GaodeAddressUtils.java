package com.zgc.app.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static com.alibaba.fastjson2.JSONObject.*;
import static net.sf.json.JSONObject.fromObject;

@Slf4j
@Component
public class GaodeAddressUtils {
//    @Value("${gaode.key}")
//    private static String key;




        private static String SUCCESS_FLAG="1";

        public static String getLonAndLatByAddress(String address){
            String key = "f2158f5dc9ddc18e6dedd60aaf37bb72";

            try {
                address=URLEncoder.encode(address,"utf-8");
                key=URLEncoder.encode(key,"utf-8");
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            String GD_URL="https://restapi.amap.com/v3/geocode/geo?address="+address+"&key="+key;
            String location="";

            String.format("%s,%s,%s",GD_URL, address, key);



            //高德接口返回的是JSON格式的字符串
//            net.sf.json.JSONObject jsonobject = fromObject(getHttpResponse(GD_URL));
//            JSONArray pathArray = JSONArray.of(jsonobject.getJSONArray("geocodes"));
//            location = pathArray.getJSONObject(0).getString("location");
            String queryResult = getResponse(GD_URL);
            JSONObject obj = JSONObject.parseObject(queryResult);

          if(String.valueOf(obj.get("status")).equals(SUCCESS_FLAG)){
              JSONObject jobJSON = JSONObject.parseObject(obj.get("geocodes").toString().substring(1, obj.get("geocodes").toString().length() - 1));
              location = String.valueOf(jobJSON.get("location"));
            }else{
                throw new RuntimeException("地址转换经纬度失败，错误码：" + obj.get("infocode"));
            }
            return location;
        }
        /**
         * 发送请求
         *
         * @param serverUrl 请求地址
         */
        private static String getResponse(String serverUrl) {
            // 用JAVA发起http请求，并返回json格式的结果
            StringBuffer result = new StringBuffer();
            try {
                URL url = new URL(serverUrl);
                URLConnection conn = url.openConnection();
//                conn.setRequestProperty("Content-type", "text/html");
//                conn.setRequestProperty("Accept-Charset", "utf-8");
//                conn.setRequestProperty("content-type", "utf-8");
//                conn.connect();
//                System.out.println(1);
//                System.out.println(conn.getInputStream());
//                System.out.println(2);
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
                System.out.println(in);
                String line;//获取值
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result.toString();
        }


    //ic static String getHttpResponse(String allConfigUrl) {
    //BufferedReader in = null;
    //StringBuffer result = null;
    //try {
    //    // url请求中如果有中文，要在接收方用相应字符转码
    //    URI uri = new URI(allConfigUrl);
    //    URL url = uri.toURL();
    //    URLConnection connection = url.openConnection();
    //    connection.setRequestProperty("Content-type", "text/html");
    //    connection.setRequestProperty("Accept-Charset", "utf-8");
    //    connection.setRequestProperty("contentType", "utf-8");
    //    connection.connect();
    //    result = new StringBuffer();
    //    // 读取URL的响应
    //    in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
    //    String line;
    //    while ((line = in.readLine()) != null) {
    //        result.append(line);
    //    }
    //    return result.toString();
    //} catch (Exception e) {
    //    e.printStackTrace();
    //} finally {
    //    try {
    //        if (in != null) {
    //            in.close();
    //        }
    //    } catch (Exception e2) {
    //        e2.printStackTrace();
    //    }
    //}
    //return null;

    //}
}
