# Eros Matchmaking

## Summary
Eros is a multiplatform matchmaking system designed around problems I faced when building matchmaking for minecraft servers.
This attempts to solve the problem of horizontal scaling, and can be applied to any game.
Also implements a system that allows for players to be pooled together, and a matchmaking algorithm applied to determine teams, and start a match.

![modal-diagram.png](modal-diagram.png)

## Orchestrator

Game Server connections are handled over tcp, their state and connection info are stored locally,
and their status (online, offline, etc.)

Client connections are handled over web requests and web sockets, and allow you to access the state of all servers, 
and manage active matchmaking queues.

## Game Server 

A game server connects to the orchestrator, forwards information about the state of the active games hosted on that server,
and accepts game requests.