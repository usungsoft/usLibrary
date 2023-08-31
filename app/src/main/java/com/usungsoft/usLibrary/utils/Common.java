package com.usungsoft.usLibrary.utils;

public class Common {
    public static String hexToDecimal(String code, int start, int end) {
        return String.valueOf(Long.parseLong(code.substring(start, end), 16));
    }
}
