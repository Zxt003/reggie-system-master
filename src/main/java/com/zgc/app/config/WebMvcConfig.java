package com.zgc.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 目的：映射 D:\reggie-img
 * 为这个路径 设置一个网络访问地址path  /img
 *
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry
                .addResourceHandler("/img/**")//分配的网络路径
                .addResourceLocations("file:C:/Users/庄小天/Desktop/杂物/大三下/实训代码/reggie-system-master1/reggie-system-master/src/main/resources/static/front/images/reggie-img/");//映射的本地磁盘路径
        registry
                .addResourceHandler("/backend/**")//分配的网络路径
                .addResourceLocations("classpath:/static/backend/");
        registry
                .addResourceHandler("/front/**")//分配的网络路径
                .addResourceLocations("classpath:/static/front/");
    }

    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new
                MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到mvc框架的转换器集合中
        converters.add(0,messageConverter);
    }
}
