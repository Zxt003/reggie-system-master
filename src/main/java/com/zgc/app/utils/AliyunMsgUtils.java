package com.zgc.app.utils;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AliyunMsgUtils {

    @Value("${aliyun.msg.accessKeyId}")
    private String accessKeyId;
    @Value("${aliyun.msg.accessKeySecret}")
    private String accessKeySecret;

    @Value("${aliyun.msg.signName}")
    private String signName;
    @Value("${aliyun.msg.templateCode}")
    private String templateCode;

    public Client createClient() throws Exception {
        Config config = new Config()
                // 必填，您的 AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 必填，您的 AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 短信验证码发送方法
     * @param number
     * @param code
     * @return
     */
    public String sendCodeMsg(String number){
        try {
            String code = getCode();
            Client client = createClient();
            SendSmsRequest sendSmsRequest = new SendSmsRequest()
                    .setPhoneNumbers(number)//你的测试号码
                    .setSignName(signName)
                    .setTemplateParam("{'code':'"+code+"'}")// 接受到的参数值
                    .setTemplateCode(templateCode);
            client.sendSmsWithOptions(sendSmsRequest, new RuntimeOptions());
            return code;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private String getCode() {
        return (Math.random() + "").substring(3, 9);
    }

}
