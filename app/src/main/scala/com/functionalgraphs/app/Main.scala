package com.functionalgraphs.app

import com.functionalgraphs.DirectedGraph
import com.functionalgraphs.Graph
import com.functionalgraphs.GraphOperations._
import zio._
import zio.Console._

object Main extends ZIOAppDefault {

  /**
   * Programme de démonstration utilisé pour les tests unitaires :
   * Construit un petit graphe, affiche les résultats étape par étape, puis retourne ()
   */
  val demo: ZIO[Any, Throwable, Unit] = for {
    _     <- printLine("=== Functional Graphs ZIO App ===")

    graph = DirectedGraph[Int]()
      .addEdge(1, 2, 3.0)
      .addEdge(2, 3, 1.5)
      .addEdge(3, 1, 2.2)

    _     <- printLine(s"Sommets : ${graph.vertices}")
    _     <- printLine(s"Arêtes  : ${graph.edges}")

    dfs   = depthSearch(graph, 1)
    bfs   = breadthSearch(graph, 1)
    cyc   = hasCycleV2(graph)
    dij   = dijkstra(graph, 1)
    _     <- printLine(s"DFS depuis 1 : $dfs")
    _     <- printLine(s"BFS depuis 1 : $bfs")
    _     <- printLine(s"Cyclique ?   : $cyc")
    _     <- printLine(s"Dijkstra 1→* : $dij")
  } yield ()

  /**
   * Point d'entrée interactif principal : démarre la boucle de commandes
   * La méthode run retourne finalement un ExitCode
   */
  override def run: URIO[Any, ExitCode] =
    loop(DirectedGraph[String]())
      .exitCode  // Dès que la boucle se termine, retourne success ou failure

  private def loop(graph: Graph[String]): ZIO[Any, Throwable, Unit] =
    for {
      _      <- printLine(
        "\nCommandes : add u v w | remove u v | show vertices | show edges\n" +
          "           dfs u | bfs u | cycle? | dijkstra u | exit"
      )
      line   <- readLine
      tokens  = line.trim.split("\\s+").toList
      _      <- tokens match {
        case "add" :: u :: v :: w :: Nil =>
          val g2 = graph.addEdge(u, v, w.toDouble)
          printLine(s"Arête ajoutée : $u -> $v (poids $w)") *> loop(g2)

        case "remove" :: u :: v :: Nil =>
          val g2 = graph.removeEdge(u, v)
          printLine(s"Arête supprimée : $u -> $v") *> loop(g2)

        case "show" :: "vertices" :: Nil =>
          printLine(s"Sommets : ${graph.vertices}") *> loop(graph)

        case "show" :: "edges" :: Nil =>
          printLine(s"Arêtes  : ${graph.edges}") *> loop(graph)

        case "dfs" :: start :: Nil =>
          val p = depthSearch(graph, start)
          printLine(s"DFS depuis $start : $p") *> loop(graph)

        case "bfs" :: start :: Nil =>
          val p = breadthSearch(graph, start)
          printLine(s"BFS depuis $start : $p") *> loop(graph)

        case "cycle?" :: Nil =>
          val c = hasCycleV2(graph)
          printLine(s"Cyclique ? $c") *> loop(graph)

        case "dijkstra" :: start :: Nil =>
          val m = dijkstra(graph, start)
          printLine(s"Dijkstra depuis $start : $m") *> loop(graph)

        case "exit" :: Nil =>
          printLine("Au revoir !")   // Fin de boucle, .exitCode retournera success

        case _ =>
          printLine("Commande non reconnue, réessayez.") *> loop(graph)
      }
    } yield ()
}
