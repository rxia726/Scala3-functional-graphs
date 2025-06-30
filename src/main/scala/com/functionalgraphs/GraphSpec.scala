package com.functionalgraphs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.json._

class GraphSpec extends AnyFlatSpec with Matchers:

  "DirectedGraph" should "ajouter et lister sommets et arêtes" in {
    val g = DirectedGraph[Int]()
      .addEdge(1, 2, 3.5)
      .addEdge(2, 3, 1.0)

    g.vertices should contain allElementsOf Set(1,2,3)
    g.edges should contain allElementsOf Set((1,2,3.5),(2,3,1.0))
    g.neighbors(1) shouldBe Map(2 -> 3.5)
  }

  it should "supprimer les arêtes correctement" in {
    val g1 = DirectedGraph[Int]().addEdge(1,2,2.0)
    val g2 = g1.removeEdge(1,2)
    g2.edges shouldBe empty
  }

  "UndirectedGraph" should "gérer les arêtes dans les deux sens" in {
    val ug = UndirectedGraph[String]()
      .addEdge("A","B", 1.0)

    ug.edges should contain only ("A","B",1.0)
    ug.neighbors("B") should contain ("A" -> 1.0)
  }

  "JsonCodecs" should "sérialiser et désérialiser en JSON" in {
    import JsonCodecs._
    val g = DirectedGraph[Int]().addEdge(1,2,4.2)
    val dto = toDto(g)
    val json = dto.toJson
    val parsed = json.fromJson[GraphDto[Int]].toOption.get
    val restored = fromDto(parsed)
    restored.edges shouldBe g.edges
  }

  "GraphViz" should "produire du DOT valide" in {
    val g = DirectedGraph[Char]().addEdge('X','Y',2.5)
    val dot = g.toDot("Test")
    dot should include ("digraph Test")
    dot should include (""""X" -> "Y" [label="2.5"]""")
  }
