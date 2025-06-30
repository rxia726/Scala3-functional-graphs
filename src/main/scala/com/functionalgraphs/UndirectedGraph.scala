package com.functionalgraphs

import scala.math.Ordering

/** Graphe non orienté : on ajoute/supprime chaque arête dans les deux sens */
case class UndirectedGraph[A](
                               adj: Map[A, Map[A, Double]] = Map.empty[A, Map[A, Double]]
                             )(implicit ord: Ordering[A]) extends Graph[A]:

  // on construit un graphe dirigé de référence
  private val dg = DirectedGraph[A](adj)

  /** Sommets identiques à ceux du graphe dirigé */
  override def vertices: Set[A] =
    dg.vertices

  /** Normalise chaque arête en (min, max, w) puis déduplique via Set */
  override def edges: Set[(A, A, Double)] =
    dg.edges.foldLeft(Set.empty[(A, A, Double)]) {
      case (acc, (u, v, w)) =>
        val normed =
          if ord.lteq(u, v) then (u, v, w)
          else                   (v, u, w)
        acc + normed
    }

  /** Voisins identiques */
  override def neighbors(v: A): Map[A, Double] =
    dg.neighbors(v)

  /** Ajoute l’arête dans les deux sens */
  override def addEdge(u: A, v: A, w: Double): Graph[A] =
    UndirectedGraph(
      dg.addEdge(u, v, w)
        .addEdge(v, u, w)
        .asInstanceOf[DirectedGraph[A]]
        .adj
    )

  /** Supprime l’arête dans les deux sens */
  override def removeEdge(u: A, v: A): Graph[A] =
    UndirectedGraph(
      dg.removeEdge(u, v)
        .removeEdge(v, u)
        .asInstanceOf[DirectedGraph[A]]
        .adj
    )
