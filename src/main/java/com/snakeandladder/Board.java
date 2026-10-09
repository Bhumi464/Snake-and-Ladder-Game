package com.snakeandladder;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Board {

    private static final int SIZE = 10;
    private static final int CELL_SIZE = 70;

    // ==================================
    // SNAKES
    // ==================================

    private final Snake[] snakes = {

        new Snake(99, 54),
        new Snake(95, 75),
        new Snake(70, 55),
        new Snake(52, 42),
        new Snake(25, 10)
    };

    // ==================================
    // LADDERS
    // ==================================

    private final Ladder[] ladders = {

        new Ladder(4, 25),
        new Ladder(13, 46),
        new Ladder(33, 49),
        new Ladder(50, 69),
        new Ladder(62, 81)
    };

    // ==================================
    // PLAYER TOKENS
    // ==================================

    private Circle player1Token;
    private Circle player2Token;

    private Pane overlay;

    // Distance between two players when
    // they are on the same cell.
    private static final double TOKEN_OFFSET = CELL_SIZE * 0.16;

    // ==================================
    // CREATE BOARD
    // ==================================

    public StackPane createBoard() {

        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);

        // --------------------------------
        // CREATE BOARD CELLS
        // --------------------------------

        for (int gridRow = 0; gridRow < SIZE; gridRow++) {

            int logicalRow = SIZE - 1 - gridRow;

            int startNumber =
                    logicalRow * SIZE + 1;

            for (int col = 0; col < SIZE; col++) {

                int cellNumber;

                if (logicalRow % 2 == 0) {

                    cellNumber =
                            startNumber + col;

                } else {

                    cellNumber =
                            startNumber
                            + (SIZE - 1 - col);
                }

                StackPane cell =
                        createCell(cellNumber);

                grid.add(
                        cell,
                        col,
                        gridRow
                );
            }
        }

        // --------------------------------
        // OVERLAY
        // --------------------------------

        overlay = new Pane();

        overlay.setPrefSize(
                SIZE * CELL_SIZE,
                SIZE * CELL_SIZE
        );

        overlay.setMinSize(
                SIZE * CELL_SIZE,
                SIZE * CELL_SIZE
        );

        overlay.setMaxSize(
                SIZE * CELL_SIZE,
                SIZE * CELL_SIZE
        );

        overlay.setMouseTransparent(true);

        // --------------------------------
        // SNAKES AND LADDERS
        // --------------------------------

        addSnakes(overlay);
        addLadders(overlay);

        // --------------------------------
        // PLAYER TOKENS
        // --------------------------------

        player1Token =
                createPlayerToken(Color.RED);

        player2Token =
                createPlayerToken(Color.BLUE);

        overlay.getChildren().addAll(
                player1Token,
                player2Token
        );

        // Initially hide tokens
        // because position 0 is outside the board.

        player1Token.setVisible(false);
        player2Token.setVisible(false);

        // --------------------------------
        // COMBINE BOARD + OVERLAY
        // --------------------------------

        StackPane board =
                new StackPane();

        board.setAlignment(Pos.CENTER);

        board.getChildren().addAll(
                grid,
                overlay
        );

        return board;
    }

    // ==================================
    // CELL
    // ==================================

    private StackPane createCell(int number) {

        Rectangle rectangle =
                new Rectangle(
                        CELL_SIZE,
                        CELL_SIZE
                );

        if (number % 2 == 0) {

            rectangle.setFill(
                    Color.LIGHTBLUE
            );

        } else {

            rectangle.setFill(
                    Color.LIGHTYELLOW
            );
        }

        rectangle.setStroke(
                Color.DARKGRAY
        );

        Label label =
                new Label(
                        String.valueOf(number)
                );

        StackPane cell =
                new StackPane();

        cell.getChildren().addAll(
                rectangle,
                label
        );

        return cell;
    }

    // ==================================
    // PLAYER TOKEN
    // ==================================

    private Circle createPlayerToken(
            Color color
    ) {

        Circle token =
                new Circle(
                        CELL_SIZE * 0.20,
                        color
                );

        token.setStroke(Color.WHITE);

        token.setStrokeWidth(2);

        return token;
    }

    // ==================================
    // MOVE PLAYER 1
    // ==================================

    public void movePlayer1Animated(
            int oldPosition,
            int newPosition,
            Runnable onFinished
    ) {

        animateToken(
                player1Token,
                oldPosition,
                newPosition,
                true,
                onFinished
        );
    }

    // ==================================
    // MOVE PLAYER 2
    // ==================================

    public void movePlayer2Animated(
            int oldPosition,
            int newPosition,
            Runnable onFinished
    ) {

        animateToken(
                player2Token,
                oldPosition,
                newPosition,
                false,
                onFinished
        );
    }

    // ==================================
    // SNAKE / LADDER ANIMATION
    // ==================================

    public void movePlayerAlongSnakeOrLadderAnimated(
            boolean playerOne,
            int oldPosition,
            int newPosition,
            Runnable onFinished
    ) {

        Circle token;

        if (playerOne) {

            token = player1Token;

        } else {

            token = player2Token;
        }

        // IMPORTANT:
        // Use the split position so that
        // both players stay separated.

        double[] start =
                getTokenPosition(
                        oldPosition,
                        playerOne
                );

        double[] end =
                getTokenPosition(
                        newPosition,
                        playerOne
                );

        token.setVisible(true);

        Timeline timeline =
                new Timeline();

        int duration = 1200;
        int frames = 30;

        // --------------------------------
        // CHECK IF THIS IS A SNAKE
        // --------------------------------

        Snake selectedSnake = null;

        for (Snake snake : snakes) {

            if (snake.getHead() == oldPosition
                    && snake.getTail() == newPosition) {

                selectedSnake = snake;
                break;
            }
        }

        // --------------------------------
        // SNAKE MOVEMENT
        // --------------------------------

        if (selectedSnake != null) {

            double control1X =
                    start[0]
                    + (end[0] - start[0]) * 0.25
                    + 35;

            double control1Y =
                    start[1]
                    + (end[1] - start[1]) * 0.25;

            double control2X =
                    start[0]
                    + (end[0] - start[0]) * 0.75
                    - 35;

            double control2Y =
                    start[1]
                    + (end[1] - start[1]) * 0.75;

            for (int i = 1; i <= frames; i++) {

                double t =
                        (double) i / frames;

                double x =
                        Math.pow(1 - t, 3) * start[0]
                        + 3 * Math.pow(1 - t, 2) * t * control1X
                        + 3 * (1 - t) * Math.pow(t, 2) * control2X
                        + Math.pow(t, 3) * end[0];

                double y =
                        Math.pow(1 - t, 3) * start[1]
                        + 3 * Math.pow(1 - t, 2) * t * control1Y
                        + 3 * (1 - t) * Math.pow(t, 2) * control2Y
                        + Math.pow(t, 3) * end[1];

                double time =
                        (double) i / frames * duration;

                final double finalX = x;
                final double finalY = y;

                timeline.getKeyFrames().add(
                        new KeyFrame(
                                Duration.millis(time),
                                event -> {

                                    token.setLayoutX(finalX);
                                    token.setLayoutY(finalY);
                                }
                        )
                );
            }

        } else {

            // --------------------------------
            // LADDER MOVEMENT
            // --------------------------------

            for (int i = 1; i <= frames; i++) {

                double t =
                        (double) i / frames;

                double x =
                        start[0]
                        + (end[0] - start[0]) * t;

                double y =
                        start[1]
                        + (end[1] - start[1]) * t;

                double time =
                        (double) i / frames * duration;

                final double finalX = x;
                final double finalY = y;

                timeline.getKeyFrames().add(
                        new KeyFrame(
                                Duration.millis(time),
                                event -> {

                                    token.setLayoutX(finalX);
                                    token.setLayoutY(finalY);
                                }
                        )
                );
            }
        }

        // --------------------------------
        // ANIMATION FINISHED
        // --------------------------------

        timeline.setOnFinished(event -> {

            token.setLayoutX(end[0]);
            token.setLayoutY(end[1]);

            if (onFinished != null) {

                onFinished.run();
            }
        });

        timeline.play();
    }

    // ==================================
    // MOVE TOKEN
    // ==================================

    private void moveToken(
            Circle token,
            int position,
            boolean playerOne
    ) {

        if (position <= 0) {

            token.setVisible(false);

            return;
        }

        double[] center =
                getTokenPosition(
                        position,
                        playerOne
                );

        token.setLayoutX(center[0]);

        token.setLayoutY(center[1]);

        token.setTranslateX(0);

        token.setTranslateY(0);

        token.setVisible(true);
    }

    // ==================================
    // RESET TOKENS
    // ==================================

    public void resetTokens() {

        moveToken(
                player1Token,
                0,
                true
        );

        moveToken(
                player2Token,
                0,
                false
        );
    }

    // ==================================
    // NORMAL MOVEMENT ANIMATION
    // ==================================

    private void animateToken(
            Circle token,
            int oldPosition,
            int newPosition,
            boolean playerOne,
            Runnable onFinished
    ) {

        if (newPosition <= 0) {

            token.setVisible(false);

            if (onFinished != null) {

                onFinished.run();
            }

            return;
        }

        token.setVisible(true);

        // --------------------------------
        // NO MOVEMENT
        // --------------------------------

        if (oldPosition == newPosition) {

            moveToken(
                    token,
                    newPosition,
                    playerOne
            );

            if (onFinished != null) {

                onFinished.run();
            }

            return;
        }

        Timeline timeline =
                new Timeline();

        int direction =
                newPosition >= oldPosition
                        ? 1
                        : -1;

        int steps =
                Math.abs(
                        newPosition - oldPosition
                );

        // --------------------------------
        // MOVE ONE CELL AT A TIME
        // --------------------------------

        for (int i = 1; i <= steps; i++) {

            int position =
                    oldPosition
                    + (i * direction);

            KeyFrame keyFrame =
                    new KeyFrame(
                            Duration.millis(i * 180),
                            event -> {

                                double[] center =
                                        getTokenPosition(
                                                position,
                                                playerOne
                                        );

                                token.setLayoutX(
                                        center[0]
                                );

                                token.setLayoutY(
                                        center[1]
                                );
                            }
                    );

            timeline.getKeyFrames().add(
                    keyFrame
            );
        }

        timeline.setOnFinished(event -> {

            if (onFinished != null) {

                onFinished.run();
            }
        });

        timeline.play();
    }

    // ==================================
    // GET TOKEN POSITION
    // ==================================

    private double[] getTokenPosition(
            int number,
            boolean playerOne
    ) {

        double[] center =
                getCellCenter(number);

        /*
         * If Player 1 and Player 2 are
         * on the same cell:
         *
         * Player 1 -> slightly LEFT
         * Player 2 -> slightly RIGHT
         *
         * This keeps both tokens visible
         * inside the same cell.
         */

        if (playerOne) {

            center[0] -= TOKEN_OFFSET;

        } else {

            center[0] += TOKEN_OFFSET;
        }

        return center;
    }

    // ==================================
    // SNAKES
    // ==================================

    private void addSnakes(Pane overlay) {

        for (Snake snake : snakes) {

            addSnake(
                    overlay,
                    snake.getHead(),
                    snake.getTail()
            );
        }
    }

    private void addSnake(
            Pane overlay,
            int headNumber,
            int tailNumber
    ) {

        double[] head =
                getCellCenter(headNumber);

        double[] tail =
                getCellCenter(tailNumber);

        double control1X =
                head[0]
                + (tail[0] - head[0]) * 0.25
                + 35;

        double control1Y =
                head[1]
                + (tail[1] - head[1]) * 0.25;

        double control2X =
                head[0]
                + (tail[0] - head[0]) * 0.75
                - 35;

        double control2Y =
                head[1]
                + (tail[1] - head[1]) * 0.75;

        CubicCurve snakeBody =
                new CubicCurve();

        snakeBody.setStartX(head[0]);
        snakeBody.setStartY(head[1]);

        snakeBody.setControlX1(control1X);
        snakeBody.setControlY1(control1Y);

        snakeBody.setControlX2(control2X);
        snakeBody.setControlY2(control2Y);

        snakeBody.setEndX(tail[0]);
        snakeBody.setEndY(tail[1]);

        snakeBody.setFill(null);

        snakeBody.setStroke(
                Color.DARKRED
        );

        snakeBody.setStrokeWidth(12);

        snakeBody.setMouseTransparent(true);

        overlay.getChildren().add(
                snakeBody
        );

        // --------------------------------
        // SNAKE HEAD
        // --------------------------------

        Circle headCircle =
                new Circle(
                        head[0],
                        head[1],
                        14,
                        Color.DARKRED
                );

        headCircle.setMouseTransparent(true);

        overlay.getChildren().add(
                headCircle
        );

        // --------------------------------
        // EYES
        // --------------------------------

        Circle leftEye =
                new Circle(
                        head[0] - 5,
                        head[1] - 4,
                        3,
                        Color.WHITE
                );

        Circle rightEye =
                new Circle(
                        head[0] + 5,
                        head[1] - 4,
                        3,
                        Color.WHITE
                );

        overlay.getChildren().addAll(
                leftEye,
                rightEye
        );
    }

    // ==================================
    // LADDERS
    // ==================================

    private void addLadders(Pane overlay) {

        for (Ladder ladder : ladders) {

            addLadder(
                    overlay,
                    ladder.getBottom(),
                    ladder.getTop()
            );
        }
    }

    private void addLadder(
            Pane overlay,
            int bottomNumber,
            int topNumber
    ) {

        double[] bottom =
                getCellCenter(bottomNumber);

        double[] top =
                getCellCenter(topNumber);

        double dx =
                top[0] - bottom[0];

        double dy =
                top[1] - bottom[1];

        double length =
                Math.sqrt(
                        dx * dx
                        + dy * dy
                );

        double offsetX =
                -dy / length * 10;

        double offsetY =
                dx / length * 10;

        // --------------------------------
        // LEFT RAIL
        // --------------------------------

        Line leftRail =
                new Line(
                        bottom[0] + offsetX,
                        bottom[1] + offsetY,
                        top[0] + offsetX,
                        top[1] + offsetY
                );

        // --------------------------------
        // RIGHT RAIL
        // --------------------------------

        Line rightRail =
                new Line(
                        bottom[0] - offsetX,
                        bottom[1] - offsetY,
                        top[0] - offsetX,
                        top[1] - offsetY
                );

        leftRail.setStroke(
                Color.DARKGREEN
        );

        rightRail.setStroke(
                Color.DARKGREEN
        );

        leftRail.setStrokeWidth(6);
        rightRail.setStrokeWidth(6);

        overlay.getChildren().addAll(
                leftRail,
                rightRail
        );

        // --------------------------------
        // RUNGS
        // --------------------------------

        int numberOfRungs = 5;

        for (int i = 1;
             i <= numberOfRungs;
             i++) {

            double ratio =
                    (double) i
                    / (numberOfRungs + 1);

            double centerX =
                    bottom[0]
                    + dx * ratio;

            double centerY =
                    bottom[1]
                    + dy * ratio;

            Line rung =
                    new Line(
                            centerX + offsetX,
                            centerY + offsetY,
                            centerX - offsetX,
                            centerY - offsetY
                    );

            rung.setStroke(
                    Color.DARKGREEN
            );

            rung.setStrokeWidth(5);

            overlay.getChildren().add(
                    rung
            );
        }
    }

    // ==================================
    // GET CELL CENTER
    // ==================================

    private double[] getCellCenter(
            int number
    ) {

        int logicalRow =
                (number - 1) / SIZE;

        int positionInRow =
                (number - 1) % SIZE;

        int gridRow =
                SIZE - 1 - logicalRow;

        int column;

        if (logicalRow % 2 == 0) {

            column =
                    positionInRow;

        } else {

            column =
                    SIZE - 1
                    - positionInRow;
        }

        double x =
                column * CELL_SIZE
                + CELL_SIZE / 2.0;

        double y =
                gridRow * CELL_SIZE
                + CELL_SIZE / 2.0;

        return new double[]{
                x,
                y
        };
    }
}