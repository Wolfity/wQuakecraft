package me.wolf.wquakecraft.game;

import me.wolf.wquakecraft.arena.Arena;

public class Game {

    private final Arena arena;
    private GameState gameState;

    public Game(final Arena arena) {
        this.arena = arena;
        this.gameState = GameState.PREGAME;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public Arena getArena() {
        return arena;
    }
}
