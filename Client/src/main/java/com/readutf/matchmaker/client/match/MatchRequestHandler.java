package com.readutf.matchmaker.client.match;

import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;

public interface MatchRequestHandler {

    MatchResponse handleMatchRequest(MatchRequest matchRequest);

}
