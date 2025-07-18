# Functional Graphs in Scala 3

This project is a comprehensive implementation of a functional graph library in Scala 3. It includes a core library for immutable graph data structures, a suite of classic graph algorithms, and an interactive ZIO-based console application.

## Project Overview

The project is architected to showcase a clean separation of concerns, adherence to functional programming paradigms, and robust testing. It is composed of two primary modules:

*   **`core`**: A reusable, standalone library containing the core logic.
  *   A generic `Graph[A]` trait defining the fundamental contract for a graph.
  *   Immutable `DirectedGraph` and `UndirectedGraph` implementations.
  *   Type-safe, tail-recursive implementations of standard graph algorithms:
    *   Depth-First Search (DFS)
    *   Breadth-First Search (BFS)
    *   Cycle Detection
    *   Dijkstra's Algorithm for shortest paths
  *   Utilities for serialization to JSON (via `zio-json`) and GraphViz DOT format.

*   **`app`**: A terminal-based interactive application.
  *   Built using ZIO 2 to manage application state and side effects (like console I/O) in a purely functional manner.
  *   Provides a command-line interface to interactively build a graph and run algorithms on it.

## Project Organization

The project utilizes SBT subprojects to maintain a clear and logical structure, separating the reusable library from the specific application that consumes it.

*   `core/`: Contains the graph data structures, algorithms, and related utilities. This module has no dependencies on the `app` and could be published as a standalone library.
*   `app/`: Contains the ZIO application. It depends on `core` to perform its graph-related tasks.

This modular design promotes reusability, simplifies dependency management, and makes the codebase easier to navigate and maintain.

## Core Concepts & Design Decisions

This library was built from the ground up with functional programming principles at its core. The following design decisions were made to align with the course's learning objectives and the grading criteria.

### 1. Immutability and Pure Functions

The most fundamental design choice is that all data structures are **immutable**.

*   **Data Structures**: The `DirectedGraph` and `UndirectedGraph` are implemented as `case class`es. All their fields are `val`s.
*   **Operations**: Methods that modify the graph, such as `addEdge(u, v, w)` or `removeEdge(u, v)`, do not change the existing graph instance. Instead, they return a **new graph instance** containing the specified modification.

    *Example (`DirectedGraph.scala`):*
    ```scala
    override def addEdge(u: A, v: A, w: Double): Graph[A] =
      val nbrs = adj.getOrElse(u, Map.empty)
      // .updated returns a new Map, which is used to create a new DirectedGraph
      DirectedGraph(adj.updated(u, nbrs.updated(v, w)))
    ```

*    This approach guarantees that our functions are **pure** (free of side effects), which leads to code that is:
  *   **Predictable and Easy to Reason About**: A given graph value will never change, eliminating entire classes of bugs related to state mutation.
  *   **Thread-Safe**: Immutable data can be safely shared across multiple threads without locks or synchronization, making it ideal for concurrent applications.

### 2. Algebraic Data Types (ADTs) and Generics

The graph structure is defined using an ADT, a core pattern in functional programming for modeling data.

*   **`trait Graph[A]`**: A `trait` defines the common interface, establishing a contract for all graph implementations.
*   **`case class DirectedGraph[A]` / `case class UndirectedGraph[A]`**: These are the concrete implementations, representing the "is a" relationship (`DirectedGraph` is a `Graph`).
*   **Generics `[A]`**: The entire library is parameterized by type `A`. This is **parametric polymorphism**, allowing the graph to store vertices of any type (`Int`, `String`, custom case classes like `City`, etc.) without sacrificing type safety.

### 3. Functional Algorithms and Tail Recursion

All graphs traversal and search algorithms are implemented as pure functions in the `GraphOperations` object.

*   **`@tailrec`**: All recursive helper functions (e.g., `depthSearcherHelper`, `dijkstraHelper`) are annotated with `@tailrec`. This instructs the Scala compiler to verify that the recursion is in tail position.
*   The compiler performs **tail-call optimization**, converting the recursion into a loop at the bytecode level. This prevents `StackOverflowError` for large graphs and allows for processing arbitrary-sized inputs with constant stack space, which is the functional equivalent of using `while` loops in imperative programming.

### 4. Separation of Data and Operations

The graph algorithms (DFS, Dijkstra, etc.) are **not** methods on the `Graph` trait itself. They reside in a separate `object GraphOperations`.

*    This design choice promotes modularity and adheres to the "Open-Closed Principle." The core `Graph` data structure is "closed" for modification but "open" for extension. We can easily add new algorithms in the future (e.g., A\*, Floyd-Warshall) without ever touching the `Graph.scala` or `DirectedGraph.scala` files. It keeps the data structure focused on representing data, and the operations focused on processing it.

### 5. Asynchronous & Concurrent Programming with ZIO

The interactive application is built with ZIO to handle all side effects, such as reading from and writing to the console.

*   **IO Monad**: ZIO wraps effects in a data type, `ZIO[R, E, A]`. This turns side effects into pure, functional values that can be composed and manipulated.
*   **For-Comprehensions**: The main application logic in `Main.scala` is structured as a `for`-comprehension, which provides a clean, sequential-looking way to compose asynchronous operations.

    *Example (`Main.scala`):*
    ```scala
    for {
      _    <- printLine("Enter command:")
      line <- readLine
      // ... process line ...
    } yield ()
    ```
*   **State Management**: The application loop `loop(graph: Graph[String])` is also purely functional. Instead of mutating a global graph variable, each command that modifies the graph recursively calls the `loop` function with the **new graph** as a parameter.

### 6. Extensibility via Type Classes and Extensions

The library is made extensible without modifying core code.

*   **JSON Serialization**: `JsonCodecs.scala` uses the type class pattern with `implicit` `JsonEncoder` and `JsonDecoder` instances. This allows any `Graph[A]` to be serialized to JSON, provided a codec exists for the vertex type `A`.
*   **GraphViz Representation**: `GraphViz.scala` uses a Scala 3 `extension` method, `toDot`, to add visualization functionality directly to the `Graph` type without needing to place the method inside the `Graph` trait itself.

## Instructions for Building and Running

### Prerequisites

*   [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) version 11 or newer.
*   [SBT (Simple Build Tool)](https://www.scala-sbt.org/download.html) installed.

### Build and Test

1.  Clone the repository to your local machine.
2.  Open a terminal or command prompt in the project's root directory.
3.  Launch the SBT interactive shell:
    ```bash
    sbt
    ```
4.  Inside the SBT shell, you can run the following commands:
  *   **Compile all modules**:
      ```sbt
      compile
      ```
  *   **Run all tests**: The tests are located in the `core` module and use ScalaTest.
      ```sbt
      test
      ```

### Run the Interactive Application

1.  While inside the SBT shell, first compile the code:
    ```sbt
    compile
    ```
2.  Switch the scope to the application project:
    ```sbt
    project app
    ```
3.  Run the main class:
    ```sbt
    run
    ```

The application will start, and you will see the command prompt, ready to accept commands to build and query your graph.

## Application Usage and Commands

The interactive console allows you to build a directed graph of `String` vertices and run algorithms on it.

Once the application is running, you can enter any of the following commands. Arguments should be separated by spaces.

#### Graph Modification Commands

*   `add <u> <v> <w>`
  *   **Description**: Adds a directed edge from vertex `u` to vertex `v` with a weight `w`. If the vertices do not exist, they will be created automatically.
  *   **Example**: `add Paris London 95.5`

*   `remove <u> <v>`
  *   **Description**: Removes the directed edge from vertex `u` to vertex `v`.
  *   **Example**: `remove Paris London`

#### Graph Inspection Commands

*   `show vertices`
  *   **Description**: Displays a set of all unique vertices currently in the graph.

*   `show edges`
  *   **Description**: Displays a set of all edges in the graph, represented as `(source, destination, weight)` tuples.

#### Algorithm Commands

*   `dfs <u>`
  *   **Description**: Performs a Depth-First Search starting from vertex `u` and prints the traversal path.
  *   **Example**: `dfs Paris`

*   `bfs <u>`
  *   **Description**: Performs a Breadth-First Search starting from vertex `u` and prints the traversal path.
  *   **Example**: `bfs Paris`

*   `cycle?`
  *   **Description**: It checks the entire graph for the presence of at least one cycle using a three-color DFS algorithm. Prints `true` if a cycle is found, otherwise `false`.

*   `dijkstra <u>`
  *   **Description**: Calculates the shortest path from the starting vertex `u` to all other reachable vertices using Dijkstra's algorithm. The output is a map where each key is a reachable destination, and its value is a tuple of `(total_weight, predecessor_vertex)`.
  *   **Example**: `dijkstra Paris`

#### Application Control

*   `exit`
  *   **Description**: Exits the application.

## Testing Strategy

The project emphasizes robust testing to ensure the correctness of the core library. A comprehensive test suite is provided to cover all major features.

*   **Unit Tests (`GraphSpec.scala`)**: This suite validates the fundamental behavior of the graph data structures (`DirectedGraph`, `UndirectedGraph`). It ensures that adding, removing, and querying edges and vertices works as expected. It also contains tests for the JSON and GraphViz utility extensions, confirming that serialization and visualization outputs are correct.

*   **Algorithm Tests (`GraphOperationsSpec.scala`)**: This suite contains a dedicated set of tests for each implemented algorithm (DFS, BFS, Cycle Detection, and Dijkstra's). These tests use well-defined graphs, including standard textbook examples and specific edge cases, to assert the correctness of the results. Scenarios tested include cyclic graphs, disconnected graphs, and graphs with no outgoing edges.

*   **Application Tests (`AppSpec.scala`)**: A simple test is included to demonstrate how to test ZIO applications. It verifies that the `demo` effect in `Main.scala` runs to completion without any unexpected errors, ensuring the basic integration of the ZIO application is sound.

*   **Test Frameworks**:
  *   **ScalaTest**: Used for writing expressive and readable tests in a BDD (Behavior-Driven Development) style via `AnyFlatSpec` and its rich set of matchers.
  *   **ZIO Test**: Used for testing the ZIO application, integrating seamlessly with the ZIO runtime.