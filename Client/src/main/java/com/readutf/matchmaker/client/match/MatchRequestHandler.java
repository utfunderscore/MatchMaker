package com.readutf.matchmaker.client.match;

import com.readutf.matchmaker.shared.match.MatchRequest;
import com.readutf.matchmaker.shared.match.MatchResponse;

public interface MatchRequestHandler {

    MatchResponse handleMatchRequest(MatchRequest matchRequest);

}
