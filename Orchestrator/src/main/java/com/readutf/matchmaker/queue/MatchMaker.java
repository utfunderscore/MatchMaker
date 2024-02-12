package com.readutf.matchmaker.queue;

import java.util.Collection;
import java.util.List;

public interface MatchMaker {

    QueueResult onIteration(Collection<QueueEntry> queueEntry);

}
