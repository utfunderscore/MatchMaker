package com.readutf.matchmaker.shared.queue.serverfilter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor @Getter
public class ServerFilterData {

    private final String filterName;
    private final String creatorId;
    private final List<String> arguments;
}
