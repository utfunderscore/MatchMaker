package com.readutf.matchmaker.queue.serverfilter;

import com.readutf.matchmaker.server.Server;

import java.util.function.Predicate;

public interface ServerFilterCreator {

    Predicate<Server> createFilter(String[] args) throws Exception;

}
