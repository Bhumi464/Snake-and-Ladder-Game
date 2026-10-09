
package com.snakeandladder;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Board {

    private static final int SIZE = 10;
    private static final int CELL_SIZE = 70;

    private static final double TOKEN_OFFSET =
            CELL_SIZE * 0.16;

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

    // ==================================
    // CREATE BOARD
    // ==================================

    public StackPane createBoard() {

        GridPane grid = new GridPane();

        grid.setAlignment(Pos.CENTER);

        // Create the 100 numbered cells
        for (int gridRow = 0;
             gridRow < SIZE;
             gridRow++) {

            int logicalRow = SIZE - 1 - gridRow;

            int startNumber =
                    logicalRow * SIZE + 1;

            for (int col = 0;
                 col < SIZE;
                 col++) {

                int cellNumber;

                // Serpentine numbering
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

        // Overlay for snakes, ladders and tokens
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

        // Draw snakes and ladders
        addSnakes(overlay);
        addLadders(overlay);

        // Create glossy player tokens
        player1Token =
                createPlayerToken(Color.CRIMSON);

        player2Token =
                createPlayerToken(Color.DODGERBLUE);

        overlay.getChildren().addAll(
                player1Token,
                player2Token
        );

        // Position zero is outside the board
        player1Token.setVisible(false);
        player2Token.setVisible(false);

        // Add an outer frame around the board
        StackPane board = new StackPane();

        board.setAlignment(Pos.CENTER);

        board.setStyle(
                "-fx-background-color: #244D3A;"
                + "-fx-padding: 5;"
                + "-fx-background-radius: 5;"
        );

        board.getChildren().addAll(
                grid,
                overlay
        );

        return board;
    }

    // ==================================
    // CREATE CELL
    // ==================================

    private StackPane createCell(int number) {

        Rectangle rectangle =
                new Rectangle(
                        CELL_SIZE,
                        CELL_SIZE
                );

        // Special colors for start and finish
        if (number == 1) {

            rectangle.setFill(
                    Color.web("#B8E6A3")
            );

        } else if (number == 100) {

            rectangle.setFill(
                    Color.web("#FFD166")
            );

        } else if ((number / 10) % 2 == 0) {

            rectangle.setFill(
                    Color.web("#F5F0D8")
            );

        } else if (number % 2 == 0) {

            rectangle.setFill(
                    Color.web("#CDE8C5")
            );

        } else {

            rectangle.setFill(
                    Color.web("#E0F0D8")
            );
        }

        rectangle.setStroke(
                Color.web("#9CB99A")
        );

        rectangle.setStrokeWidth(1.2);

        Label label =
                new Label(String.valueOf(number));

        label.setStyle(
                "-fx-font-size: 14px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #294735;"
        );

        StackPane cell = new StackPane();

        cell.setPrefSize(
                CELL_SIZE,
                CELL_SIZE
        );

        cell.getChildren().addAll(
                rectangle,
                label
        );

        // Highlight the beginning and finish
        if (number == 1 || number == 100) {

            label.setStyle(
                    "-fx-font-size: 17px;"
                    + "-fx-font-weight: bold;"
                    + "-fx-text-fill: #163D29;"
            );
        }

        return cell;
    }

    // ==================================
    // CREATE PLAYER TOKEN
    // ==================================

    private Circle createPlayerToken(Color color) {

        Circle token =
                new Circle(
                        CELL_SIZE * 0.20,
                        color
                );

        token.setStroke(Color.WHITE);
        token.setStrokeWidth(2.5);

        DropShadow shadow = new DropShadow();

        shadow.setRadius(5);
        shadow.setOffsetX(1);
        shadow.setOffsetY(2);
        shadow.setColor(
                Color.rgb(0, 0, 0, 0.35)
        );

        token.setEffect(shadow);

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

        Circle token = playerOne
                ? player1Token
                : player2Token;

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

        Timeline timeline = new Timeline();

        int duration = 1200;
        int frames = 30;

        // Check whether the movement is a snake
        Snake selectedSnake = null;

        for (Snake snake : snakes) {

            if (snake.getHead() == oldPosition
                    && snake.getTail() == newPosition) {

                selectedSnake = snake;
                break;
            }
        }

        // SNAKE: curved movement
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

                double t = (double) i / frames;

                double x =
                        Math.pow(1 - t, 3) * start[0]
                        + 3 * Math.pow(1 - t, 2)
                        * t * control1X
                        + 3 * (1 - t) * Math.pow(t, 2)
                        * control2X
                        + Math.pow(t, 3) * end[0];

                double y =
                        Math.pow(1 - t, 3) * start[1]
                        + 3 * Math.pow(1 - t, 2)
                        * t * control1Y
                        + 3 * (1 - t) * Math.pow(t, 2)
                        * control2Y
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

            // LADDER: smooth upward movement
            for (int i = 1; i <= frames; i++) {

                double t = (double) i / frames;

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
    // MOVE TOKEN DIRECTLY
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

        // No movement
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

        Timeline timeline = new Timeline();

        int direction =
                newPosition >= oldPosition ? 1 : -1;

        int steps =
                Math.abs(newPosition - oldPosition);

        // Move one square at a time
        for (int i = 1; i <= steps; i++) {

            int position =
                    oldPosition + (i * direction);

            KeyFrame keyFrame =
                    new KeyFrame(
                            Duration.millis(i * 180),
                            event -> {

                                double[] center =
                                        getTokenPosition(
                                                position,
                                                playerOne
                                        );

                                token.setLayoutX(center[0]);
                                token.setLayoutY(center[1]);
                            }
                    );

            timeline.getKeyFrames().add(keyFrame);
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

        // Keep both players visible in the same cell
        if (playerOne) {

            center[0] -= TOKEN_OFFSET;

        } else {

            center[0] += TOKEN_OFFSET;
        }

        return center;
    }

    // ==================================
    // DRAW SNAKES
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

        // Snake body
        CubicCurve snakeBody = new CubicCurve();

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
                Color.web("#D94F45")
        );

        snakeBody.setStrokeWidth(12);
        snakeBody.setMouseTransparent(true);

        overlay.getChildren().add(snakeBody);

        // Snake head
        Circle headCircle =
                new Circle(
                        head[0],
                        head[1],
                        14,
                        Color.web("#B52F38")
                );

        headCircle.setStroke(
                Color.web("#76252A")
        );

        headCircle.setStrokeWidth(1.5);
        headCircle.setMouseTransparent(true);

        overlay.getChildren().add(headCircle);

        // Eyes
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

        Circle leftPupil =
                new Circle(
                        head[0] - 5,
                        head[1] - 4,
                        1.3,
                        Color.BLACK
                );

        Circle rightPupil =
                new Circle(
                        head[0] + 5,
                        head[1] - 4,
                        1.3,
                        Color.BLACK
                );

        overlay.getChildren().addAll(
                leftEye,
                rightEye,
                leftPupil,
                rightPupil
        );
    }

    // ==================================
    // DRAW LADDERS
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

        double dx = top[0] - bottom[0];
        double dy = top[1] - bottom[1];

        double length =
                Math.sqrt(dx * dx + dy * dy);

        double offsetX = -dy / length * 10;
        double offsetY = dx / length * 10;

        // Left rail
        Line leftRail =
                new Line(
                        bottom[0] + offsetX,
                        bottom[1] + offsetY,
                        top[0] + offsetX,
                        top[1] + offsetY
                );

        // Right rail
        Line rightRail =
                new Line(
                        bottom[0] - offsetX,
                        bottom[1] - offsetY,
                        top[0] - offsetX,
                        top[1] - offsetY
                );

        leftRail.setStroke(
                Color.web("#21865B")
        );

        rightRail.setStroke(
                Color.web("#21865B")
        );

        leftRail.setStrokeWidth(6);
        rightRail.setStrokeWidth(6);

        leftRail.setStrokeLineCap(
                javafx.scene.shape.StrokeLineCap.ROUND
        );

        rightRail.setStrokeLineCap(
                javafx.scene.shape.StrokeLineCap.ROUND
        );

        overlay.getChildren().addAll(
                leftRail,
                rightRail
        );

        // Golden ladder rungs
        int numberOfRungs = 5;

        for (int i = 1;
             i <= numberOfRungs;
             i++) {

            double ratio =
                    (double) i / (numberOfRungs + 1);

            double centerX =
                    bottom[0] + dx * ratio;

            double centerY =
                    bottom[1] + dy * ratio;

            Line rung =
                    new Line(
                            centerX + offsetX,
                            centerY + offsetY,
                            centerX - offsetX,
                            centerY - offsetY
                    );

            rung.setStroke(
                    Color.web("#E6AE42")
            );

            rung.setStrokeWidth(5);

            rung.setStrokeLineCap(
                    javafx.scene.shape.StrokeLineCap.ROUND
            );

            overlay.getChildren().add(rung);
        }
    }

    // ==================================
    // GET CELL CENTER
    // ==================================

    private double[] getCellCenter(int number) {

        int logicalRow =
                (number - 1) / SIZE;

        int positionInRow =
                (number - 1) % SIZE;

        int gridRow =
                SIZE - 1 - logicalRow;

        int column;

        if (logicalRow % 2 == 0) {

            column = positionInRow;

        } else {

            column =
                    SIZE - 1 - positionInRow;
        }

        double x =
                column * CELL_SIZE
                + CELL_SIZE / 2.0;

        double y =
                gridRow * CELL_SIZE
                + CELL_SIZE / 2.0;

        return new double[]{x, y};
    }
}
