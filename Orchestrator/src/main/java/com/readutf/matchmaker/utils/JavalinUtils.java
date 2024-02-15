package com.readutf.matchmaker.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.javalin.http.Context;

public class JavalinUtils {

    private static final Gson gson = new Gson();

    public static <T> T pathParamFromJson(Context context, String key, TypeToken<T> token) {
        return gson.fromJson(context.pathParam(key), token.getType());
    }

    public static <T> T queryParamFromJson(Context context, String key, TypeToken<T> token) {
        return gson.fromJson(context.queryParam(key), token.getType());
    }

}
