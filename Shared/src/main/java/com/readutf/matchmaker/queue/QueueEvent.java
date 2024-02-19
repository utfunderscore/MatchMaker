package com.readutf.matchmaker.queue;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class QueueEvent {

    private final String queueId;

}
