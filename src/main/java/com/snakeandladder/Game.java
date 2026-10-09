package com.snakeandladder;

import java.util.ArrayList;
import java.util.List;

public class Game {

    private final Player player1;
    private final Player player2;

    private final Dice dice;

    private final List<Snake> snakes;
    private final List<Ladder> ladders;

    private int currentPlayer;

    public Game() {

        player1 = new Player(
                "Player 1",
                javafx.scene.paint.Color.RED
        );

        player2 = new Player(
                "Player 2",
                javafx.scene.paint.Color.BLUE
        );

        dice = new Dice();

        snakes = new ArrayList<>();

        snakes.add(new Snake(99, 54));
        snakes.add(new Snake(95, 75));
        snakes.add(new Snake(70, 55));
        snakes.add(new Snake(52, 42));
        snakes.add(new Snake(25, 10));

        ladders = new ArrayList<>();

        ladders.add(new Ladder(4, 25));
        ladders.add(new Ladder(13, 46));
        ladders.add(new Ladder(33, 49));
        ladders.add(new Ladder(50, 69));
        ladders.add(new Ladder(62, 81));

        currentPlayer = 1;
    }

    public Player getCurrentPlayer() {

        if (currentPlayer == 1) {
            return player1;
        }

        return player2;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public int rollDice() {

        int roll = dice.roll();

        Player player = getCurrentPlayer();

        int currentPosition
                = player.getPosition();

        int newPosition
                = currentPosition + roll;

        // Don't allow the player to go beyond 100
        if (newPosition <= 100) {

            player.setPosition(newPosition);
        }

        return roll;
    }

    public void nextTurn() {

        if (currentPlayer == 1) {
            currentPlayer = 2;
        } else {
            currentPlayer = 1;
        }
    }

    public int checkSnakeOrLadder(int position) {

        // Check snakes
        for (Snake snake : snakes) {

            if (position == snake.getHead()) {
                return snake.getTail();
            }
        }

        // Check ladders
        for (Ladder ladder : ladders) {

            if (position == ladder.getBottom()) {
                return ladder.getTop();
            }
        }

        // No snake or ladder
        return position;
    }

    public boolean hasWon(Player player) {
        return player.getPosition() == 100;
    }
    public void resetGame() {

    player1.setPosition(0);
    player2.setPosition(0);

    currentPlayer = 1;
}
}
