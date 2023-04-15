package com.zgc.app.common;

import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

public class Constant {

    public static Map<String,Long> map= new HashMap();
    public static final int EMPLOYEE_NORMAL = 1;
    public static final int EMPLOYEE_BLOCK = 0;
    public static final  String EMPLOYEE_SEESION = "EMPLOYEE";
    public static final  String EMPLOYEE_NOT_LOGIN = "NOTLOGIN";
    public static final  String EMPLOYEE_DEFAULT_PASSWORD = DigestUtils.md5DigestAsHex("123456".getBytes());
}
