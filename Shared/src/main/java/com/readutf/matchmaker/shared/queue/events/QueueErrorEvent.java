package com.readutf.matchmaker.shared.queue.events;

import com.readutf.matchmaker.shared.queue.QueueEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter @ToString @RequiredArgsConstructor
public class QueueErrorEvent extends QueueEvent {

    private final String queueId;
    private final List<UUID> effectedPlayers;
    private final String error;

}
