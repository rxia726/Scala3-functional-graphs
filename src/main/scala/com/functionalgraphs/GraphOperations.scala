package com.functionalgraphs

import scala.annotation.tailrec

object GraphOperations:

  def depthSearch[A](graph: Graph[A], start: A):  List[A] =

    @tailrec
    def depthSearcherHelper(toVisit: List[A], visited: Set[A], path: List[A]): List[A] =
      toVisit match {
        case Nil => path.reverse
        case head :: tail if visited.contains(head) => depthSearcherHelper(tail, visited, path)
        case head :: tail =>
          val neighborsToVisit = graph.neighbors(head).keys.toList.filterNot(visited.contains)
          depthSearcherHelper(neighborsToVisit ::: tail, visited + head, head :: path)
      }

    depthSearcherHelper(List(start), Set.empty, List.empty)


  def breadthSearch[A](graph: Graph[A], start: A): List[A] =

    @tailrec
    def breadthSearcherHelper(toVisit: List[A], visited: Set[A], path: List[A]): List[A] =
      toVisit match {
        case Nil => path.reverse
        case head :: tail if visited.contains(head) => breadthSearcherHelper(tail, visited, path)
        case head :: tail =>
          val neighborsToVisit = graph.neighbors(head).keys.toList.filterNot(visited.contains)
          breadthSearcherHelper(tail ::: neighborsToVisit, visited + head, head :: path)
      }

    breadthSearcherHelper(List(start), Set.empty, List.empty)

  def hasCycleV2[A](graph: Graph[A]): Boolean = {

    def search(
                u: A,
                white: Set[A],
                gray: Set[A],
                black: Set[A]
              ): (Set[A], Set[A], Set[A], Boolean) = {

      // Move `u` from white set to gray set to mark it as "currently visiting".
      val updatedWhite = white - u
      val updatedGray = gray + u

      val (finalWhite, finalGray, finalBlack, cycleFound) =
        graph.neighbors(u).keys.foldLeft((updatedWhite, updatedGray, black, false)) {
          case (state@(_, _, _, true), _) => state

          // Process the next neighbor `v`.
          case ((w, g, b, false), v) =>
            if (g.contains(v)) {
              // **CYCLE DETECTED**
              (w, g, b, true)
            } else if (w.contains(v)) {
              search(v, w, g, b)
            } else {
              (w, g, b, false)
            }
        }

      if (cycleFound) {
        (finalWhite, finalGray, finalBlack, true)
      } else {
        // No cycle found through `u`. We are done with `u`.
        (finalWhite, finalGray - u, finalBlack + u, false)
      }
    }

    // iterate through all vertices to handle disconnected components
    val initialState = (graph.vertices, Set.empty[A], Set.empty[A], false)

    val finalState = graph.vertices.foldLeft(initialState) {
      // If cycle has been found, stop searching.
      case (state@(_, _, _, true), _) => state

      // If `v` hasn't been visited yet (is in the white set), start a new search.
      case (state@(white, _, _, _), v) if white.contains(v) =>
        search(v, state._1, state._2, state._3)

      // Otherwise, Skip it.
      case (state, _) => state
    }

    finalState._4
  }

  def dijkstra[A](graph: Graph[A], start: A): Map[A, (Double, Option[A])] = {

    @tailrec
    def dijkstraHelper(
                        distances: Map[A, (Double, Option[A])],
                        unvisited: Set[A]
                      ): Map[A, (Double, Option[A])] = {

      // If there are no more nodes to visit, we are done
      if (unvisited.isEmpty) {
        distances
      } else {
        // 1. Find the unvisited node `u` with the smallest known distance.
        // If unvisited is not empty, this `minBy` is safe.
        val u = unvisited.minBy(node => distances.getOrElse(node, (Double.PositiveInfinity, None))._1)
        val distU = distances(u)._1

        // "Relax" edges for all neighbors of `u`.
        val newDistances = graph.neighbors(u).foldLeft(distances) {
          case (currentDistances, (v, weight)) =>
            // Only update for nodes that are still unvisited.
            if (unvisited.contains(v)) {
              val newDistToV = distU + weight
              val existingDistToV = currentDistances.get(v).map(_._1).getOrElse(Double.PositiveInfinity)

              // If we found a shorter path to `v` through `u`, update its entry.
              if (newDistToV < existingDistToV) {
                currentDistances.updated(v, (newDistToV, Some(u)))
              } else {
                currentDistances
              }
            } else {
              // `v` has already been visited
              currentDistances
            }
        }

        //Recurse, passing the NEW distances
        dijkstraHelper(newDistances, unvisited - u)
      }
    }
    
    // - Distances map contains all vertices. Start is 0, others are infinity.
    // - This is a more standard initialization for Dijkstra's.
    val initialDistances = graph.vertices.map(v => v -> (Double.PositiveInfinity, None)).toMap + (start -> (0.0, None))
    val initialUnvisited = graph.vertices

    // Start the recursive process.
    val finalDistances = dijkstraHelper(initialDistances, initialUnvisited)
    
    finalDistances.filter { case (_, (dist, _)) => dist.isFinite }
  }