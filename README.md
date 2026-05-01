# Minesweeper

A desktop Minesweeper game built with Java Swing. The game opens a 10x10 board, places mines at random, reveals neighboring empty cells recursively, and tracks win/loss states with simple dialog prompts. It also includes basic quality-of-life controls such as restart, instructions, quitting, and saving/loading progress from a local save file. The project is intentionally lightweight and self-contained, with unit tests covering the core board logic.

## Features

- 10x10 board
- Random mine generation
- Recursive reveal for empty cells
- Win/loss dialogs
- Restart, quit, instructions, and save-game controls
- Save/load support through `minesweeper_save.txt`

## Requirements

You will need Java installed. The project is set up for Java 17, although newer
versions should work too.

Maven is the easiest way to run the project because it uses the `pom.xml` file
to find the main class and test dependencies. If you do not have Maven installed,
you can still compile and run the game directly with `javac` and `java`.

## Running the Game

With Maven:

```bash
mvn compile
mvn exec:java
```

Without Maven, from the project root:

```bash
javac -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes minesweeper.Game
```

If you are using Windows PowerShell, use this version instead of the `find`
command:

```powershell
javac -d target\classes (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
java -cp target\classes minesweeper.Game
```

## Tests

The tests focus on the game logic rather than the Swing UI. They check things
like mine generation, revealing cells, win detection, resetting the board, and
loading/saving game state.

```bash
mvn test
```

## Project Structure

```text
src/
  main/java/minesweeper/
    Cell.java             One square on the board
    Game.java             Main entry point
    Minesweeper.java      Board setup, game rules, save/load, and UI wiring
    RunMinesweeper.java   Starts a new game or loads a saved one

  test/java/minesweeper/
    MinesweeperTest.java  Unit tests for the board logic
```

The game writes saved progress to `minesweeper_save.txt`. That file is ignored
by Git, along with compiled classes and local IDE settings.
