package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.server.Server;

import java.util.function.Predicate;

public abstract class Queue {

    private final String name;
    private final MatchMaker matchMaker;
    private final Predicate<Server> filter;

    public Queue(String name, MatchMaker matchMaker, Predicate<Server> filter) {
        this.name = name;
        this.matchMaker = matchMaker;
        this.filter = filter;
    }
}
