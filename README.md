# Tetris (Vanilla Java)

A **classic Tetris clone built in plain Java** (no game engine). This project focuses on core fundamentals—game loop timing, keyboard input, collision detection, piece rotation, line clears, and scoring—implemented with a simple Java UI.

## Features

- Classic Tetris gameplay (tetromino/mino pieces)
- Keyboard controls (move, rotate, drop)
- Collision + boundary checks
- Line clearing
- Basic scoring / progression (depending on your implementation)

## Tech Stack

- **Language:** Java
- **Build Tool:** Maven
- **IDE:** Works great in VS Code, IntelliJ, or Eclipse

## Project Structure

```text
src/main/java/
  main/   # Core game logic, panels, input handling, entry point
  mino/   # Tetromino (mino) shape definitions / piece logic
```

## Getting Started

### Prerequisites

- Java (JDK) installed (Java 17+ recommended)
- Maven installed (or use the Maven wrapper if you add one later)

### Build

```bash
mvn clean package
```

### Run

Run the game by starting the **main class** in your IDE, or from the command line (if your `pom.xml` specifies the entry point via a plugin).

If you run it from an IDE:

1. Open the project folder
2. Import as a Maven project
3. Locate the class containing `public static void main(String[] args)`
4. Run it

## Controls

> Update these to match your key bindings.

- **Left / Right:** Move piece
- **Down:** Soft drop
- **Up / X:** Rotate
- **Space:** Hard drop
- **P:** Pause (if implemented)

## How It Works (High Level)

- The **main package** contains the game loop, rendering panels, and input handling.
- The **mino package** defines the different tetromino shapes and their rotation states.
- Each frame/tick updates the active piece, checks collisions, locks pieces when they land, and clears completed lines.

## Contributing

If you’d like to improve this project:

1. Fork the repo
2. Create a feature branch
3. Commit your changes
4. Open a pull request

## License

Add a license if you plan to share or reuse this code publicly (MIT is a common choice).
