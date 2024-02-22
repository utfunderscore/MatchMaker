package com.readutf.matchmaker.shared.queue;

import com.readutf.matchmaker.shared.queue.events.*;
import com.readutf.matchmaker.shared.utils.RuntimeTypeAdapterFactory;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public abstract class QueueEvent {

    public static RuntimeTypeAdapterFactory<QueueEvent> getAdapter() {
        RuntimeTypeAdapterFactory<QueueEvent> typeFactory = RuntimeTypeAdapterFactory.of(QueueEvent.class, "type")
                .registerSubtype(QueueDeleteEvent.class, "QueueDeleteEvent")
                .registerSubtype(QueueErrorEvent.class, "QueueErrorEvent")
                .registerSubtype(QueuePlayerEvent.class, "QueuePlayerEvent")
                .registerSubtype(QueueResultEvent.class, "QueueResultEvent")
                .registerSubtype(QueueUpdateEvent.class, "QueueUpdateEvent");


        return typeFactory;
    }

}
