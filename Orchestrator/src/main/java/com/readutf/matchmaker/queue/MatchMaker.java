package com.readutf.matchmaker.queue;

import java.util.List;

public interface MatchMaker {

    QueueResult onIteration(List<QueueEntry> queueEntry);

}
