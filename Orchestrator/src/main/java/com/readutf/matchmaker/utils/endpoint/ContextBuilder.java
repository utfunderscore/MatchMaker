package com.readutf.matchmaker.utils.endpoint;

import com.google.gson.Gson;
import com.readutf.matchmaker.utils.endpoint.annotation.*;
import io.javalin.Javalin;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class ContextBuilder {

    private final Logger logger = LoggerFactory.getLogger(ContextBuilder.class);

    private final Javalin javalin;
    private final Gson gson;

    public ContextBuilder(Javalin javalin, Gson gson) {
        this.javalin = javalin;
        this.gson = gson;
    }

    public void registerObject(Object object) {

        Class<?> clazz = object.getClass();

        if (!clazz.isAnnotationPresent(RestEndpoint.class))
            throw new IllegalArgumentException("Class is not a RestEndpoint");

        RestEndpoint restEndpoint = clazz.getAnnotation(RestEndpoint.class);

        String endpointPath = restEndpoint.path();


        for (Method method : clazz.getMethods()) {

            if (!method.isAnnotationPresent(MappingPath.class)) continue;

            MappingPath mappingPath = method.getAnnotation(MappingPath.class);
            String methodPath = mappingPath.path();

            String absolutePath = endpointPath + methodPath;

            Parameter[] parameters = method.getParameters();

            Handler handler = ctx -> {
                List<String> providedArgs = new ArrayList<>();
                for (Parameter parameter : parameters) {
                    String name;
                    if (parameter.isAnnotationPresent(RequestParameter.class)) {
                        name = parameter.getAnnotation(RequestParameter.class).name();
                    } else {
                        name = parameter.getName();
                    }

                    String string = ctx.queryParam(name);
                    if (string == null) throw new IllegalArgumentException("Missing parameter " + parameter.getName());
                    providedArgs.add(string);
                }

                Object[] args = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Class<?> type = parameter.getType();

                    args[i] = gson.fromJson(providedArgs.get(i), type);
                }

                try {
                    method.invoke(object, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };

            if (method.isAnnotationPresent(GET.class)) {
                javalin.get(absolutePath, handler);
                logger.info("Registered GET " + absolutePath);
            } else if (method.isAnnotationPresent(PUT.class)) {
                javalin.put(absolutePath, handler);
                logger.info("Registered PUT " + absolutePath);
            }


        }


    }

}
