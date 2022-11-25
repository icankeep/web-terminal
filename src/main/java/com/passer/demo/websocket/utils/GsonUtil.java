package com.passer.demo.websocket.utils;

import com.google.gson.Gson;

/**
 * @author passer
 * @time 2022/11/20 18:20
 */
public class GsonUtil {
    private final static Gson GSON = new Gson();

    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }
}
