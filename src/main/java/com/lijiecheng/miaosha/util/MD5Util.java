package com.lijiecheng.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author: myname
 * @Date: 2019/2/19 2:08
 */
public class MD5Util {
    public static String md5(String src) {
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";

    public static String inputPassFormPass(String inputPass){
        String str = ""+salt.charAt(0)+salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static void main(String[] args) {
        System.out.println(inputPassFormPass("123456"));
    }
}
