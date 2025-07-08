package com.functionalgraphs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.functionalgraphs.GraphOperations.*

class GraphOperationsSpec extends AnyFlatSpec with Matchers:

  val simpleGraph = DirectedGraph[Int]()
    .addEdge(1, 2, 1.0)
    .addEdge(1, 3, 1.0)
    .addEdge(2, 4, 1.0)
    .addEdge(3, 4, 1.0)
    .addEdge(4, 5, 1.0)

  val cyclicGraph = DirectedGraph[Int]()
    .addEdge(1, 2, 1.0)
    .addEdge(2, 3, 1.0)
    .addEdge(3, 1, 1.0) // Cycle here

  val weightedGraph = DirectedGraph[String]()
    .addEdge("A", "B", 10.0)
    .addEdge("A", "C", 3.0)
    .addEdge("B", "C", 1.0)
    .addEdge("B", "D", 2.0)
    .addEdge("C", "B", 4.0)
    .addEdge("C", "D", 8.0)
    .addEdge("C", "E", 2.0)
    .addEdge("D", "E", 7.0)
    .addEdge("E", "D", 9.0)

  "DFS" should "traverse the graph in depth-first order" in {
    val path = depthSearch(simpleGraph, 1)
    // One possible DFS path. The exact order of siblings can vary.
    path should (equal(List(1, 3, 4, 5, 2)) or equal(List(1, 2, 4, 5, 3)))
  }

  "BFS" should "traverse the graph in breadth-first order" in {
    val path = breadthSearch(simpleGraph, 1)
    // One possible BFS path.
    path should (equal(List(1, 2, 3, 4, 5)) or equal(List(1, 3, 2, 4, 5)))
  }

  "Cycle Detection" should "find a cycle in a cyclic graph" in {
    hasCycleV2(cyclicGraph) shouldBe true
  }

  it should "not find a cycle in an acyclic graph" in {
    hasCycleV2(simpleGraph) shouldBe false
  }

  "Dijkstra's Algorithm" should "find the shortest paths from a source in a standard test graph" in {
    val graph = DirectedGraph[Char]()
      .addEdge('A', 'B', 7.0)
      .addEdge('A', 'C', 9.0)
      .addEdge('A', 'F', 14.0)
      .addEdge('B', 'C', 10.0)
      .addEdge('B', 'D', 15.0)
      .addEdge('C', 'D', 11.0)
      .addEdge('C', 'F', 2.0)
      .addEdge('D', 'E', 6.0)
      .addEdge('E', 'F', 9.0)
    
    val paths = dijkstra(graph, 'A')

    paths.get('A') shouldBe Some((0.0, None))

    paths.get('B') shouldBe Some((7.0, Some('A')))

    paths.get('C') shouldBe Some((9.0, Some('A')))

    paths.get('D') shouldBe Some((20.0, Some('C')))
    
    paths.get('E') shouldBe Some((26.0, Some('D')))

    paths.get('F') shouldBe Some((11.0, Some('C')))
  }

  it should "handle a graph with no path between nodes" in {
    val disconnectedGraph = DirectedGraph[Int]()
      .addEdge(1, 2, 1.0) // Component 1
      .addEdge(3, 4, 1.0) // Component 2

    val paths = dijkstra(disconnectedGraph, 1)
    
    paths.get(2) shouldBe Some((1.0, Some(1)))
    
    paths.contains(3) shouldBe false
    
    paths.contains(4) shouldBe false
  }

  it should "return only the start node if it has no outgoing edges" in {
    val graph = DirectedGraph[String]()
      .addEdge("B", "A", 5.0) // Edge going INTO A, not out

    val paths = dijkstra(graph, "A")

    // The result should contain only the starting node.
    paths should have size 1
    paths.get("A") shouldBe Some((0.0, None))
  }

