package com.readutf.matchmaker.queue;

import com.readutf.matchmaker.shared.queue.QueueEvent;

public interface QueueEventHandler {

    void onEvent(QueueEvent event);

}
