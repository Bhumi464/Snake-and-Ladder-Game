package com.snakeandladder;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

    private Game game;
    private Board board;

    private Label turnLabel;
    private Label diceLabel;
    private Label player1Label;
    private Label player2Label;

    @Override
    public void start(Stage stage) {

        // -------------------------
        // CREATE GAME
        // -------------------------
        game = new Game();

        // -------------------------
        // CREATE BOARD
        // -------------------------
        board = new Board();

        // -------------------------
        // MAIN LAYOUT
        // -------------------------
        BorderPane root = new BorderPane();

        root.setPadding(new Insets(20));

        // -------------------------
        // TITLE
        // -------------------------
        Label title =
                new Label("🐍 Snake & Ladder 🪜");

        title.setStyle(
                "-fx-font-size: 32px;"
                + "-fx-font-weight: bold;"
        );

        // -------------------------
        // PLAYER INFORMATION
        // -------------------------
        player1Label =
                new Label("🔴 Player 1: Position 0");

        player1Label.setStyle(
                "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
        );

        player2Label =
                new Label("🔵 Player 2: Position 0");

        player2Label.setStyle(
                "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
        );

        // -------------------------
        // TURN LABEL
        // -------------------------
        turnLabel =
                new Label("Player 1's Turn");

        turnLabel.setStyle(
                "-fx-font-size: 21px;"
                + "-fx-font-weight: bold;"
        );

        // -------------------------
        // DICE LABEL
        // -------------------------
        diceLabel =
                new Label("🎲 Roll the dice!");

        diceLabel.setStyle(
                "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
        );

        // -------------------------
        // ROLL BUTTON
        // -------------------------
        Button rollButton =
                new Button("🎲 ROLL DICE");

        rollButton.setPrefWidth(190);
        rollButton.setPrefHeight(55);

        rollButton.setStyle(
                "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        // -------------------------
        // NEW GAME BUTTON
        // -------------------------
        Button newGameButton =
                new Button("🔄 NEW GAME");

        newGameButton.setPrefWidth(190);
        newGameButton.setPrefHeight(55);

        newGameButton.setStyle(
                "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        // -------------------------
        // ROLL BUTTON ACTION
        // -------------------------
        rollButton.setOnAction(event -> {

            // Prevent multiple clicks
            // while animation is running
            rollButton.setDisable(true);

            Player currentPlayer =
                    game.getCurrentPlayer();

            int oldPosition =
                    currentPlayer.getPosition();

            int roll =
                    game.rollDice();

            int newPosition =
                    currentPlayer.getPosition();

            diceLabel.setText(
                    "🎲 Rolled: " + roll
            );

            updatePlayerInformation();

            // Normal movement animation
            animatePlayerMove(
                    currentPlayer,
                    oldPosition,
                    newPosition,
                    rollButton
            );
        });

        // -------------------------
        // NEW GAME BUTTON ACTION
        // -------------------------
        newGameButton.setOnAction(event -> {

            game.resetGame();

            // Reset visual tokens
            board.resetTokens();

            updatePlayerInformation();

            diceLabel.setText(
                    "🎲 Roll the dice!"
            );

            turnLabel.setText(
                    "Player 1's Turn"
            );

            rollButton.setDisable(false);
        });

        // -------------------------
        // SIDE PANEL
        // -------------------------
        VBox sidePanel =
                new VBox(25);

        sidePanel.setAlignment(
                Pos.CENTER
        );

        sidePanel.setPadding(
                new Insets(20)
        );

        sidePanel.getChildren().addAll(
                player1Label,
                player2Label,
                diceLabel,
                newGameButton,
                rollButton,
                turnLabel
        );

        // -------------------------
        // MAIN LAYOUT
        // -------------------------
        root.setTop(title);

        BorderPane.setAlignment(
                title,
                Pos.CENTER
        );

        // Create board ONLY ONCE
        root.setCenter(
                board.createBoard()
        );

        root.setRight(sidePanel);

        // -------------------------
        // SCENE
        // -------------------------
        Scene scene =
                new Scene(
                        root,
                        1100,
                        800
                );

        stage.setTitle(
                "Snake & Ladder Game"
        );

        stage.setScene(scene);

        stage.show();
    }

    // ==================================
    // UPDATE PLAYER INFORMATION
    // ==================================

    private void updatePlayerInformation() {

        player1Label.setText(
                "🔴 Player 1: Position "
                + game.getPlayer1().getPosition()
        );

        player2Label.setText(
                "🔵 Player 2: Position "
                + game.getPlayer2().getPosition()
        );
    }

    // ==================================
    // MAIN
    // ==================================

    public static void main(String[] args) {

        launch(args);
    }

    // ==================================
    // NORMAL PLAYER MOVEMENT
    // ==================================

    private void animatePlayerMove(
            Player player,
            int oldPosition,
            int newPosition,
            Button rollButton
    ) {

        Runnable afterMovement = () -> {

            // Check the square where the
            // normal dice movement ended
            int finalPosition =
                    game.checkSnakeOrLadder(
                            newPosition
                    );

            // Snake or ladder found
            if (finalPosition != newPosition) {

                // Ladder
                if (finalPosition > newPosition) {

                    diceLabel.setText(
                            "🪜 "
                            + player.getName()
                            + " climbed a ladder! "
                            + newPosition
                            + " → "
                            + finalPosition
                    );

                }

                // Snake
                else {

                    diceLabel.setText(
                            "🐍 "
                            + player.getName()
                            + " got bitten by a snake! "
                            + newPosition
                            + " → "
                            + finalPosition
                    );
                }

                animateSnakeOrLadder(
                        player,
                        newPosition,
                        finalPosition,
                        rollButton
                );

            } else {

                // No snake or ladder
                finishTurn(
                        player,
                        rollButton
                );
            }
        };

        // IMPORTANT:
        // Normal dice movement uses
        // square-by-square animation.

        if (player == game.getPlayer1()) {

            board.movePlayer1Animated(
                    oldPosition,
                    newPosition,
                    afterMovement
            );

        } else {

            board.movePlayer2Animated(
                    oldPosition,
                    newPosition,
                    afterMovement
            );
        }
    }

    // ==================================
    // SNAKE / LADDER MOVEMENT
    // ==================================

    private void animateSnakeOrLadder(
            Player player,
            int oldPosition,
            int newPosition,
            Button rollButton
    ) {

        Runnable afterJump = () -> {

            // Update player's actual position
            player.setPosition(newPosition);

            updatePlayerInformation();

            // Finish turn after animation
            finishTurn(
                    player,
                    rollButton
            );
        };

        // Special snake/ladder animation
        board.movePlayerAlongSnakeOrLadderAnimated(
                player == game.getPlayer1(),
                oldPosition,
                newPosition,
                afterJump
        );
    }

    // ==================================
    // FINISH TURN
    // ==================================

    private void finishTurn(
            Player player,
            Button rollButton
    ) {

        updatePlayerInformation();

        // -------------------------
        // CHECK WINNER
        // -------------------------
        if (game.hasWon(player)) {

            turnLabel.setText(
                    "🏆 "
                    + player.getName()
                    + " WINS!"
            );

            diceLabel.setText(
                    "🎉 Congratulations!"
            );

            rollButton.setDisable(true);

            return;
        }

        // -------------------------
        // NEXT PLAYER
        // -------------------------
        game.nextTurn();

        turnLabel.setText(
                game.getCurrentPlayer()
                        .getName()
                + "'s Turn"
        );

        // Enable next roll
        rollButton.setDisable(false);
    }
}