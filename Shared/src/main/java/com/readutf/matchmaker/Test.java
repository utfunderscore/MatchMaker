package com.readutf.matchmaker;

import com.google.gson.Gson;
import com.readutf.matchmaker.server.serverfilter.ServerFilterData;

import java.util.List;

public class Test {

    public static void main(String[] args) {

        Gson gson = new Gson();

        System.out.println(gson.toJson(new ServerFilterData("hub_category", "category", List.of("hub"))));

    }
}
