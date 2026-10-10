# 🐍 Snake & Ladder Game

A desktop **Snake & Ladder game** built with **Java, JavaFX, and
Maven**. Play with two players, roll the dice, climb ladders, slide down
snakes, and race to square 100.

## ✨ Features

-   **Two-player gameplay** with separate colored player tokens.
-   **10 × 10 game board** with serpentine numbering from square 1 to
    square 100.
-   **Animated dice roll** with a visual dice face.
-   **Animated player movement** across the board.
-   **Snakes and ladders** with animated movement to the destination
    square.
-   **Game history** showing dice rolls and snake/ladder events.
-   **Sound effects** for dice rolls, snakes, ladders, and winning, with
    an ON/OFF toggle.
-   **Winner celebration** with a trophy, confetti, and a Play Again
    button.
-   **New Game** option to reset player positions, dice display, and
    game history.
-   **Styled JavaFX interface** with a green-and-gold theme.

## 🛠️ Built With

-   **Java** --- game logic
-   **JavaFX** --- desktop graphical user interface and animations
-   **Maven** --- project build and dependency management

## 📋 Requirements

Install the following before running the project:

-   A compatible JDK
-   Apache Maven
-   The JavaFX Maven configuration and dependencies specified in this
    project's `pom.xml`

Check your installations from a terminal:

``` bash
java -version
mvn -version
```

## 🚀 Run the Project

1.  Clone or download this repository.
2.  Open the project folder in VS Code or another Java IDE.
3.  Confirm that Java and Maven are installed and that the project
    `pom.xml` is present.
4.  Open a terminal in the project root directory.
5.  Start the game with:

``` bash
mvn clean javafx:run
```

If Maven is not available on your system PATH, run the command using the
full path to your Maven executable.

## 🎮 How to Play

1.  Start a new game. Both players begin at the starting position.
2.  Click **ROLL DICE** on the current player's turn.
3.  The token moves according to the dice result.
4.  Landing on the bottom of a ladder moves the player up to its top.
5.  Landing on a snake's head moves the player down to its tail.
6.  Follow the player labels and **Game History** panel to track
    progress.
7.  The first player to reach square 100 wins. The game requires an
    exact roll to reach the final square.
8.  Use **SOUND: ON/OFF** to control sound effects, **NEW GAME** to
    restart, or **PLAY AGAIN** after a win.

## 🗂️ Project Structure

The main source files are in the `com.snakeandladder` package.

``` text
src/
└── main/
    ├── java/
    │   └── com/
    │       └── snakeandladder/
    │           ├── App.java
    │           ├── Board.java
    │           ├── Game.java
    │           ├── Player.java
    │           ├── Snake.java
    │           ├── Ladder.java
    │           └── SoundManager.java
    └── resources/
pom.xml
```

The exact resource folders and additional files may vary depending on
the current project setup.

## 🧪 Suggested Tests

-   Roll several times and confirm that the correct player moves.
-   Verify that landing on a snake or ladder updates the final position.
-   Toggle sound OFF and ON.
-   Start a new game and confirm that positions and history reset.
-   Reach square 100 with an exact roll and verify the winner
    celebration.
-   Resize or maximize the application and check that the board and
    controls remain visible.

## 🔮 Possible Future Enhancements

-   Add configurable player names and additional board themes.
-   Add an option to choose the board size and snake/ladder positions.
-   Add player statistics and saved game history.
-   Improve responsive layout for different screen sizes.

## 👩‍💻 Author

Created as a Java desktop application project using JavaFX and Maven.

------------------------------------------------------------------------

If you find a bug or have an idea for an improvement, feel free to open
an issue or contribute.