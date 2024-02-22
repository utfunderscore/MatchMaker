package com.readutf.matchmaker.api;

import com.google.gson.Gson;
import com.readutf.matchmaker.ErosServer;
import com.readutf.matchmaker.api.annotation.*;
import com.readutf.matchmaker.api.socket.SocketManager;
import com.readutf.matchmaker.api.socket.WebSocket;
import com.readutf.matchmaker.matches.api.MatchEndpoints;
import com.readutf.matchmaker.queue.api.QueueEndpoints;
import com.readutf.matchmaker.queue.socket.QueueListenerSocket;
import com.readutf.matchmaker.server.api.ServerEndpoints;
import com.readutf.matchmaker.server.ServerUpdateSocket;
import com.readutf.matchmaker.shared.api.ApiResponse;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.json.JavalinGson;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class EndpointManager {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(EndpointManager.class);

    private final Javalin javalin;
    private final Gson gson;
    private final SocketManager socketManager;
    private final QueueEndpoints queueEndpoints;
    private final MatchEndpoints matchEndpoints;
    private final ServerEndpoints serverEndpoints;
    private final WebSocket queueSocket;
    private final ServerUpdateSocket serverUpdateSocket;

    public EndpointManager(ErosServer erosServer) {
        this.gson = ErosServer.getGson();
        this.javalin = Javalin.create(config -> config.jsonMapper(new JavalinGson(gson, true))).after(getDebugHandler()).start(8080);
        this.queueEndpoints = registerEndpoint(new QueueEndpoints(erosServer.getQueueManager()));
        this.matchEndpoints = registerEndpoint(new MatchEndpoints(erosServer.getMatchManager()));
        this.serverEndpoints = registerEndpoint(new ServerEndpoints(erosServer.getServerManager()));
        this.socketManager = new SocketManager(javalin);
        this.queueSocket = socketManager.registerSocket(new QueueListenerSocket(gson));
        this.serverUpdateSocket = (ServerUpdateSocket) socketManager.registerSocket(new ServerUpdateSocket(gson, erosServer.getServerUpdateManager()));
    }

    /**
     * Scans each function annotated with @RestEndpoint
     * @param object
     */
    public <T> T registerEndpoint(T object) {

        Class<?> clazz = object.getClass();

        if (!clazz.isAnnotationPresent(RestEndpoint.class))
            throw new IllegalArgumentException("Class " + clazz.getSimpleName() + " is not a RestEndpoint");

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
            } else if (method.isAnnotationPresent(DELETE.class)) {
                javalin.delete(absolutePath, handler);
                logger.info("Registered PUT " + absolutePath);
            }

        }

        return object;
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
                logger.debug("Error while invoking method " + method.getName(), targetException);
                ApiResponse<Object> response = ApiResponse.error(targetException.getMessage());
                ctx.json(response);
            } catch (Exception e) {
                logger.debug("Error while invoking method " + method.getName(), e);
                ApiResponse<Object> response = ApiResponse.error(e.getMessage());
                ctx.json(response);
            }
        };
    }

    public Handler getDebugHandler() {
        return context -> {
            String params = context.queryParamMap().entrySet().stream().map(stringListEntry -> stringListEntry.getKey() + "=" + String.join(",", stringListEntry.getValue())).collect(Collectors.joining("&"));
            logger.info("[%s] %s%s".formatted(context.method(), context.path(), params.isEmpty() ? "" : "?" + params));
            logger.info(context.result());
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
            if (string == null) throw new IllegalArgumentException("Missing parameter '%s'".formatted(name));
            providedArgs.add(string);
        }
        return providedArgs;
    }

}
