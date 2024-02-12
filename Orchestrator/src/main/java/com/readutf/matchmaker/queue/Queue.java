package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.server.Server;

import java.util.*;
import java.util.function.Predicate;

public class Queue {

    private final String name;
    private final MatchMaker matchMaker;
    private final Predicate<Server> filter;
    private final Collection<QueueEntry> inQueue;

    public Queue(String name, MatchMaker matchMaker, Predicate<Server> filter) {
        this.name = name;
        this.matchMaker = matchMaker;
        this.filter = filter;
        this.inQueue = new ArrayDeque<>();
    }

    public void tick() {

        QueueResult queueResult = matchMaker.onIteration(inQueue);
        if(queueResult == null) return;

        



    }

}
