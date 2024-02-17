package com.readutf.matchmaker.api;

import com.google.gson.Gson;
import com.readutf.matchmaker.ErosServer;
import com.readutf.matchmaker.api.annotation.*;
import com.readutf.matchmaker.matches.MatchManager;
import com.readutf.matchmaker.matches.api.MatchEndpoints;
import com.readutf.matchmaker.queue.api.QueueEndpoints;
import com.readutf.matchmaker.server.api.ServerEndpoints;
import com.readutf.matchmaker.server.socket.ServerUpdateManager;
import com.readutf.matchmaker.server.socket.ServerUpdateSocket;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.json.JavalinGson;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class EndpointManager {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EndpointManager.class);

    private final Javalin javalin;
    private final Gson gson;

    public EndpointManager(ErosServer erosServer, Gson gson) {
        this.gson = gson;

        QueueEndpoints queueEndpoints = new QueueEndpoints(erosServer.getQueueManager());
        MatchEndpoints matchEndpoints = erosServer.getMatchManager().getMatchEndpoints();
        ServerUpdateManager serverUpdateManager = erosServer.getServerUpdateManager();
        MatchManager matchManager = erosServer.getMatchManager();
        ServerEndpoints serverEndpoints = new ServerEndpoints(erosServer.getServerManager());

        this.javalin = Javalin
                .create(config -> {
                    config.jsonMapper(new JavalinGson());
                })
                .ws("/serverinfo/{category}", ws -> {
                    ws.onConnect(new ServerUpdateSocket(serverUpdateManager));
                })
//                .put("/queue/create", queueEndpoints.createQueue())
                .get("/match/create", matchManager.getMatchEndpoints().createMatch())
                .get("/server/list", serverEndpoints.listServers())
                .start(8080);
    }

    /**
     * Scans each function annotated with @RestEndpoint
     * @param object
     */
    public void registerObject(Object object) {

        Class<?> clazz = object.getClass();

        if (!clazz.isAnnotationPresent(RestEndpoint.class))
            throw new IllegalArgumentException("Class is not a RestEndpoint");

        RestEndpoint restEndpoint = clazz.getAnnotation(RestEndpoint.class);

        String endpointPath = restEndpoint.value();


        for (Method method : clazz.getMethods()) {

            if (!method.isAnnotationPresent(MappingPath.class)) continue;

            MappingPath mappingPath = method.getAnnotation(MappingPath.class);
            String methodPath = mappingPath.value();

            String absolutePath = endpointPath + methodPath;

            Parameter[] parameters = method.getParameters();

            Handler handler = getHandler(object, method, parameters);

            if (method.isAnnotationPresent(GET.class)) {
                javalin.get(absolutePath, handler);
                logger.info("Registered GET " + absolutePath);
            } else if (method.isAnnotationPresent(PUT.class)) {
                javalin.put(absolutePath, handler);
                logger.info("Registered PUT " + absolutePath);
            }

        }


    }

    @NotNull
    private Handler getHandler(Object object, Method method, Parameter[] parameters) {
        return ctx -> {
            try {
                List<String> providedArgs = getProvidedArgs(ctx, parameters);

                Object[] args = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    Class<?> type = parameter.getType();

                    args[i] = gson.fromJson(providedArgs.get(i), type);
                }

                Object result = method.invoke(object, args);
                ApiResponse<Object> response = ApiResponse.success(result);
                ctx.json(response);

            } catch (InvocationTargetException invocationException) {
                Throwable targetException = invocationException.getTargetException();
                logger.error("Error while invoking method " + method.getName(), targetException);
                ApiResponse<Object> response = ApiResponse.error(targetException.getMessage());
                ctx.json(response);
            } catch (Exception e) {
                logger.error("Error while invoking method " + method.getName(), e);
                ApiResponse<Object> response = ApiResponse.error(e.getMessage());
                ctx.json(response);
            }
        };
    }

    private static @NotNull List<String> getProvidedArgs(Context ctx, Parameter[] parameters) throws Exception {
        List<String> providedArgs = new ArrayList<>();
        for (Parameter parameter : parameters) {
            String name;
            if (parameter.isAnnotationPresent(RequestParameter.class)) {
                name = parameter.getAnnotation(RequestParameter.class).value();
            } else {
                name = parameter.getName();
            }

            String string = ctx.queryParam(name);
            if (string == null) throw new IllegalArgumentException("Missing parameter " + name);
            providedArgs.add(string);
        }
        return providedArgs;
    }

}
