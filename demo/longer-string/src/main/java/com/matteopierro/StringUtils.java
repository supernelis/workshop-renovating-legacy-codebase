package com.matteopierro;

public class StringUtils {

    public static String longer(String a, String b) {
        if (a.length() > b.length()) {
            return a;
        } else {
            return b;
        }
    }
}
