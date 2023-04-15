package com.zgc.app.config.security;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.JsonObject;
import com.zgc.app.common.Constant;
import com.zgc.app.common.R;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RenttyPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println(authException.getMessage());
        authException.printStackTrace();
        String jsonR = JSON.toJSONString(R.error().setMsg(Constant.EMPLOYEE_NOT_LOGIN));
        response.setContentType("application/json");
        response.getWriter().print(jsonR);
    }
}
