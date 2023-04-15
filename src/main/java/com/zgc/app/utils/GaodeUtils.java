package com.zgc.app.utils;

import com.alibaba.fastjson2.JSONObject;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.HashMap;

@Slf4j
@Component
public class GaodeUtils {

//    @Value("${gaode.key}")
//    private String key;

  private String origin;
    @Autowired
    private RestTemplate restTemplate;

    public HashMap<String, String> getDistanceAndDuration(String zb) {
        String key = "f2158f5dc9ddc18e6dedd60aaf37bb72";
        origin = "109.219073,23.783063";

        try {
            key=URLEncoder.encode(key,"utf-8");
        }
        catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        String GD_URL="https://restapi.amap.com/v3/direction/riding?key="+key+"&origin="+origin+"&destination="+zb;
        // 通过RestTemplate 发送HTTP请求到对应接口
        System.out.println(GD_URL);
//        JSONObject info = restTemplate.getForObject("https://restapi.amap.com/v3/direction/riding" +
//                "?key="+key+
//                "&origin="+origin+
//                "&destination="+zb, JSONObject.class);
        GD_URL= String.format("%s",GD_URL);
        System.out.println(GD_URL);
        JSONObject info = restTemplate.getForObject(GD_URL,JSONObject.class);
        System.out.println(info);
        assert info != null;
        JSONObject path = info.getJSONObject("route").getJSONArray("paths").getJSONObject(0);
        // 获取相应的返回结果
        BigDecimal distance = path.getBigDecimal("distance");
        distance = distance.divide(new BigDecimal("1000"),2, RoundingMode.HALF_UP);
        // 距离4舍5入，小数点后保留2位
        int duration = path.getIntValue("duration");
        // 对结果进行计算
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("distance", distance.toString());
        //配送时间＋十分钟
        hashMap.put("duration",(duration/60)+10+"");
        int a = (int)((duration/60)+10);
        a= a>25? (int) ((0.5 * (a - 25)) + 4) :4;
        System.out.println(a);
        hashMap.put("price", String.valueOf(a));
        System.out.println(hashMap);
        return hashMap;
    }
}
