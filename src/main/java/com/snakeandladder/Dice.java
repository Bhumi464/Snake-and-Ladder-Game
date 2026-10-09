package com.snakeandladder;

import java.util.Random;

public class Dice {

    private final Random random;

    public Dice() {
        random = new Random();
    }

    public int roll() {
        return random.nextInt(6) + 1;
    }
}