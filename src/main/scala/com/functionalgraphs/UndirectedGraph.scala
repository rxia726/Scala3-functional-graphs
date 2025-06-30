package com.functionalgraphs

/** Graphe non orienté : on ajoute/supprime chaque arête dans les deux sens */

case class UndirectedGraph[A](adj: Map[A, Map[A, Double]] = Map.empty[A, Map[A, Double]]) extends Graph[A]:
  private val dg = DirectedGraph(adj)

  /** Sommets identiques à ceux du graphe dirigé sous-jacent */
  override def vertices: Set[A] = dg.vertices

  /** On ne garde qu’une seule orientation par arête pour éviter les doublons */
  override def edges: Set[(A, A, Double)] =
    dg.edges.filter { case (u, v, _) => u.hashCode <= v.hashCode }

  /** Voisins identiques */
  override def neighbors(v: A): Map[A, Double] = dg.neighbors(v)

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
