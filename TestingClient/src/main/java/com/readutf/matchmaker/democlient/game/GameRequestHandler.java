package com.readutf.matchmaker.democlient.game;

import com.readutf.matchmaker.client.match.MatchRequestHandler;
import com.readutf.matchmaker.match.MatchRequest;
import com.readutf.matchmaker.match.MatchResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameRequestHandler implements MatchRequestHandler {

    private final GameManager gameManager;

    @Override
    public MatchResponse handleMatchRequest(MatchRequest matchRequest) {

        String queueId = matchRequest.getQueueId();

        try {
            Game game = gameManager.startGame();
            return MatchResponse.success(matchRequest.getRequestId(), game.getGameId());
        } catch (Exception e) {
            e.printStackTrace();
            return MatchResponse.failure(matchRequest.getRequestId(), e.getMessage());
        }

    }
}
