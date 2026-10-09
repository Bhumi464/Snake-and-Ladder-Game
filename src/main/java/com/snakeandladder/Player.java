package com.snakeandladder;

import javafx.scene.paint.Color;

public class Player {

    private final String name;
    private int position;
    private final Color color;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        this.position = 0;
    }

    public String getName() {
        return name;
    }

    public int getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void move(int steps) {
        this.position += steps;
    }
}