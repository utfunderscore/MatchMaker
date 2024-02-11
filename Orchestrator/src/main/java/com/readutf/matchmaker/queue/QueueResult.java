package com.readutf.matchmaker.queue;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@AllArgsConstructor
public class QueueResult {

    private final UUID serverId;
    private Map<Integer, List<String>> gameResult;

}
