
package com.snakeandladder;

import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class App extends Application {

    private Game game;
    private Board board;
    private SoundManager soundManager;

    private Label turnLabel;
    private Label diceLabel;
    private Label diceFaceLabel;
    private Label player1Label;
    private Label player2Label;
    private Label winnerLabel;
    private Label soundStatusLabel;

    private Button rollButton;
    private Button newGameButton;
    private Button soundButton;

    private Timeline diceAnimation;

    private ObservableList<String> historyItems;
    private ListView<String> historyList;

    private StackPane appRoot;
    private Pane confettiPane;

    private boolean winnerShown = false;

    private final String[] diceFaces = {
        "⚀", "⚁", "⚂", "⚃", "⚄", "⚅"
    };

    private final Random random = new Random();

    @Override
    public void start(Stage stage) {

        game = new Game();
        board = new Board();
        soundManager = new SoundManager();

        historyItems =
                FXCollections.observableArrayList();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // TITLE
        Label title =
                new Label("🐍 Snake & Ladder 🪜");

        title.setStyle(
                "-fx-font-size: 32px;"
                + "-fx-font-weight: bold;"
        );

        // PLAYER INFORMATION
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

        // TURN LABEL
        turnLabel =
                new Label("Player 1's Turn");

        turnLabel.setStyle(
                "-fx-font-size: 21px;"
                + "-fx-font-weight: bold;"
        );

        // DICE FACE
        diceFaceLabel = new Label("⚀");
        diceFaceLabel.setMinWidth(100);

        diceFaceLabel.setAlignment(
                Pos.CENTER
        );

        diceFaceLabel.setStyle(
                "-fx-font-size: 64px;"
                + "-fx-font-weight: bold;"
        );

        // DICE MESSAGE
        diceLabel =
                new Label("🎲 Roll the dice!");

        diceLabel.setWrapText(true);
        diceLabel.setMaxWidth(280);

        diceLabel.setAlignment(
                Pos.CENTER
        );

        diceLabel.setStyle(
                "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
        );

        // ROLL BUTTON
        rollButton =
                new Button("🎲 ROLL DICE");

        rollButton.setPrefWidth(190);
        rollButton.setPrefHeight(55);

        rollButton.setStyle(
                "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        // NEW GAME BUTTON
        newGameButton =
                new Button("🔄 NEW GAME");

        newGameButton.setPrefWidth(190);
        newGameButton.setPrefHeight(55);

        newGameButton.setStyle(
                "-fx-font-size: 17px;"
                + "-fx-font-weight: bold;"
        );

        // SOUND ON/OFF BUTTON
        soundButton =
                new Button("🔊 SOUND: ON");

        soundButton.setPrefWidth(190);
        soundButton.setPrefHeight(45);

        soundButton.setStyle(
                "-fx-font-size: 16px;"
                + "-fx-font-weight: bold;"
        );

        soundStatusLabel =
                new Label("Sound effects enabled");

        soundStatusLabel.setStyle(
                "-fx-font-size: 14px;"
        );

        soundButton.setOnAction(event -> {

            boolean turnSoundOn =
                    !soundManager.isEnabled();

            soundManager.setEnabled(turnSoundOn);

            if (turnSoundOn) {

                soundButton.setText("🔊 SOUND: ON");

                soundStatusLabel.setText(
                        "Sound effects enabled"
                );

            } else {

                soundButton.setText("🔇 SOUND: OFF");

                soundStatusLabel.setText(
                        "Sound effects muted"
                );
            }
        });

        // GAME HISTORY
        Label historyTitle =
                new Label("📜 Game History");

        historyTitle.setStyle(
                "-fx-font-size: 20px;"
                + "-fx-font-weight: bold;"
        );

        historyList =
                new ListView<>(historyItems);

        historyList.setPrefSize(300, 240);
        historyList.setMinHeight(180);

        historyList.setStyle(
                "-fx-font-size: 14px;"
        );

        // ROLL DICE ACTION
        rollButton.setOnAction(event -> {

            rollButton.setDisable(true);
            newGameButton.setDisable(true);
            soundButton.setDisable(true);

            Player currentPlayer =
                    game.getCurrentPlayer();

            int oldPosition =
                    currentPlayer.getPosition();

            int roll =
                    game.rollDice();

            int newPosition =
                    currentPlayer.getPosition();

            animateDiceRoll(
                    roll,
                    () -> {

                        addHistory(
                                "🎲 "
                                + currentPlayer.getName()
                                + " rolled "
                                + roll
                                + ": "
                                + oldPosition
                                + " → "
                                + newPosition
                        );

                        updatePlayerInformation();

                        animatePlayerMove(
                                currentPlayer,
                                oldPosition,
                                newPosition
                        );
                    }
            );
        });

        // NEW GAME ACTION
        newGameButton.setOnAction(
                event -> resetGame()
        );

        // SIDE PANEL
        VBox sidePanel = new VBox(18);

        sidePanel.setAlignment(
                Pos.CENTER
        );

        sidePanel.setPadding(
                new Insets(20)
        );

        sidePanel.setPrefWidth(320);

        sidePanel.getChildren().addAll(
                player1Label,
                player2Label,
                diceFaceLabel,
                diceLabel,
                soundButton,
                soundStatusLabel,
                newGameButton,
                rollButton,
                turnLabel,
                historyTitle,
                historyList
        );

        root.setTop(title);

        BorderPane.setAlignment(
                title,
                Pos.CENTER
        );

        root.setCenter(board.createBoard());
        root.setRight(sidePanel);

        // CONFETTI LAYER
        confettiPane = new Pane();
        confettiPane.setMouseTransparent(true);
        confettiPane.setPickOnBounds(false);

        // WINNER CARD
        Label trophyLabel =
                new Label("🏆");

        trophyLabel.setStyle(
                "-fx-font-size: 76px;"
        );

        Label congratulationsLabel =
                new Label("CONGRATULATIONS!");

        congratulationsLabel.setStyle(
                "-fx-font-size: 28px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #174B35;"
        );

        winnerLabel =
                new Label("Player 1 WINS!");

        winnerLabel.setWrapText(true);

        winnerLabel.setAlignment(
                Pos.CENTER
        );

        winnerLabel.setStyle(
                "-fx-font-size: 25px;"
                + "-fx-font-weight: bold;"
                + "-fx-text-fill: #B8860B;"
        );

        Label finishedLabel =
                new Label("You reached square 100!");

        finishedLabel.setStyle(
                "-fx-font-size: 17px;"
        );

        Button playAgainButton =
                new Button("🎲 PLAY AGAIN");

        playAgainButton.setPrefWidth(220);
        playAgainButton.setPrefHeight(50);

        playAgainButton.setStyle(
                "-fx-font-size: 18px;"
                + "-fx-font-weight: bold;"
                + "-fx-background-color: #218838;"
                + "-fx-text-fill: white;"
                + "-fx-background-radius: 10;"
        );

        playAgainButton.setOnAction(
                event -> resetGame()
        );

        VBox winnerCard = new VBox(18);

        winnerCard.setAlignment(
                Pos.CENTER
        );

        winnerCard.setPadding(
                new Insets(35)
        );

        winnerCard.setMaxWidth(480);

        winnerCard.setStyle(
                "-fx-background-color: white;"
                + "-fx-background-radius: 24;"
                + "-fx-border-color: #E0B84C;"
                + "-fx-border-width: 4;"
                + "-fx-border-radius: 24;"
        );

        winnerCard.getChildren().addAll(
                trophyLabel,
                congratulationsLabel,
                winnerLabel,
                finishedLabel,
                playAgainButton
        );

        StackPane winnerOverlay =
                new StackPane();

        winnerOverlay.setStyle(
                "-fx-background-color: rgba(0, 0, 0, 0.72);"
        );

        winnerOverlay.setPadding(
                new Insets(25)
        );

        winnerOverlay.getChildren().add(
                winnerCard
        );

        winnerOverlay.setVisible(false);
        winnerOverlay.setManaged(false);

        // COMBINE LAYERS
        appRoot = new StackPane();

        appRoot.getChildren().addAll(
                root,
                confettiPane,
                winnerOverlay
        );

        StackPane.setAlignment(
                winnerOverlay,
                Pos.CENTER
        );

        Scene scene =
                new Scene(
                        appRoot,
                        1200,
                        850
                );

        stage.setTitle("Snake & Ladder Game");
        stage.setScene(scene);
        stage.show();
    }

    // ==================================
    // DICE ANIMATION AND SOUND
    // ==================================

    private void animateDiceRoll(
            int actualRoll,
            Runnable onFinished
    ) {

        diceLabel.setText("🎲 Rolling...");

        soundManager.playDiceRoll();

        diceAnimation = new Timeline();

        for (int i = 1; i <= 10; i++) {

            double time = i * 80;

            diceAnimation.getKeyFrames().add(
                    new KeyFrame(
                            Duration.millis(time),
                            event -> {

                                int randomIndex =
                                        random.nextInt(6);

                                diceFaceLabel.setText(
                                        diceFaces[randomIndex]
                                );
                            }
                    )
            );
        }

        diceAnimation.setOnFinished(event -> {

            diceFaceLabel.setText(
                    diceFaces[actualRoll - 1]
            );

            diceLabel.setText(
                    "🎲 Rolled: " + actualRoll
            );

            diceAnimation = null;

            if (onFinished != null) {
                onFinished.run();
            }
        });

        diceAnimation.play();
    }

    // ==================================
    // GAME HISTORY
    // ==================================

    private void addHistory(String message) {

        historyItems.add(message);

        historyList.scrollTo(
                historyItems.size() - 1
        );
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
    // NORMAL PLAYER MOVEMENT
    // ==================================

    private void animatePlayerMove(
            Player player,
            int oldPosition,
            int newPosition
    ) {

        Runnable afterMovement = () -> {

            int finalPosition =
                    game.checkSnakeOrLadder(newPosition);

            if (finalPosition != newPosition) {

                if (finalPosition > newPosition) {

                    diceLabel.setText(
                            "🪜 "
                            + player.getName()
                            + " climbed a ladder! "
                            + newPosition
                            + " → "
                            + finalPosition
                    );

                    addHistory(
                            "🪜 "
                            + player.getName()
                            + " climbed ladder: "
                            + newPosition
                            + " → "
                            + finalPosition
                    );

                    soundManager.playLadder();

                } else {

                    diceLabel.setText(
                            "🐍 "
                            + player.getName()
                            + " got bitten by a snake! "
                            + newPosition
                            + " → "
                            + finalPosition
                    );

                    addHistory(
                            "🐍 "
                            + player.getName()
                            + " slid down snake: "
                            + newPosition
                            + " → "
                            + finalPosition
                    );

                    soundManager.playSnake();
                }

                animateSnakeOrLadder(
                        player,
                        newPosition,
                        finalPosition
                );

            } else {

                finishTurn(player);
            }
        };

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
            int newPosition
    ) {

        Runnable afterJump = () -> {

            player.setPosition(newPosition);

            updatePlayerInformation();

            finishTurn(player);
        };

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

    private void finishTurn(Player player) {

        updatePlayerInformation();

        if (game.hasWon(player)) {

            turnLabel.setText(
                    "🏆 " + player.getName() + " WINS!"
            );

            diceLabel.setText(
                    "🎉 Congratulations!"
            );

            addHistory(
                    "🏆 "
                    + player.getName()
                    + " won the game!"
            );

            rollButton.setDisable(true);
            newGameButton.setDisable(false);
            soundButton.setDisable(false);

            showWinnerCelebration(player);

            return;
        }

        game.nextTurn();

        turnLabel.setText(
                game.getCurrentPlayer().getName()
                + "'s Turn"
        );

        rollButton.setDisable(false);
        newGameButton.setDisable(false);
        soundButton.setDisable(false);
    }

    // ==================================
    // WINNER CELEBRATION
    // ==================================

    private void showWinnerCelebration(Player player) {

        winnerShown = true;

        winnerLabel.setText(
                "🎉 " + player.getName() + " WINS! 🎉"
        );

        soundManager.playWin();

        StackPane overlay =
                (StackPane) appRoot.getChildren().get(2);

        overlay.setManaged(true);
        overlay.setVisible(true);

        createConfetti();
    }

    // ==================================
    // CONFETTI
    // ==================================

    private void createConfetti() {

        confettiPane.getChildren().clear();

        double width = appRoot.getWidth();
        double height = appRoot.getHeight();

        if (width <= 0 || height <= 0) {
            width = 1200;
            height = 850;
        }

        Color[] colors = {
            Color.GOLD,
            Color.RED,
            Color.DODGERBLUE,
            Color.LIMEGREEN,
            Color.HOTPINK,
            Color.ORANGE,
            Color.PURPLE
        };

        for (int i = 0; i < 100; i++) {

            Rectangle piece =
                    new Rectangle(
                            8 + random.nextInt(7),
                            10 + random.nextInt(10)
                    );

            piece.setArcWidth(3);
            piece.setArcHeight(3);

            piece.setFill(
                    colors[random.nextInt(colors.length)]
            );

            piece.setRotate(
                    random.nextInt(360)
            );

            piece.setLayoutX(
                    random.nextDouble() * width
            );

            piece.setLayoutY(
                    -random.nextDouble() * height
            );

            confettiPane.getChildren().add(piece);

            TranslateTransition fall =
                    new TranslateTransition(
                            Duration.millis(
                                    2200 + random.nextInt(1800)
                            ),
                            piece
                    );

            fall.setByY(height + 200);

            fall.setByX(
                    random.nextInt(240) - 120
            );

            FadeTransition fade =
                    new FadeTransition(
                            Duration.millis(3500),
                            piece
                    );

            fade.setFromValue(1.0);
            fade.setToValue(0.25);

            ParallelTransition animation =
                    new ParallelTransition(fall, fade);

            animation.setDelay(
                    Duration.millis(
                            random.nextInt(1000)
                    )
            );

            animation.setOnFinished(event ->
                    confettiPane.getChildren().remove(piece)
            );

            animation.play();
        }
    }

    // ==================================
    // RESET GAME
    // ==================================

    private void resetGame() {

        if (diceAnimation != null) {
            diceAnimation.stop();
            diceAnimation = null;
        }

        game.resetGame();
        board.resetTokens();

        diceFaceLabel.setText("⚀");
        diceLabel.setText("🎲 Roll the dice!");

        updatePlayerInformation();

        turnLabel.setText("Player 1's Turn");

        historyItems.clear();

        rollButton.setDisable(false);
        newGameButton.setDisable(false);
        soundButton.setDisable(false);

        winnerShown = false;

        StackPane overlay =
                (StackPane) appRoot.getChildren().get(2);

        overlay.setVisible(false);
        overlay.setManaged(false);

        confettiPane.getChildren().clear();
    }

    // ==================================
    // MAIN
    // ==================================

    public static void main(String[] args) {
        launch(args);
    }
}
