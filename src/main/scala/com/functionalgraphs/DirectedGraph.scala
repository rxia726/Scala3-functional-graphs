package com.functionalgraphs

/** Implémentation de graphe dirigé basée sur un dictionnaire d’adjacence */
case class DirectedGraph[A](adj: Map[A, Map[A, Double]] = Map.empty[A, Map[A, Double]]) extends Graph[A]:

  /** Ensemble des sommets (clés + cibles) */
  override def vertices: Set[A] =
    adj.keySet ++ adj.values.flatMap(_.keySet)

  /** Ensemble des arêtes sous forme (u, v, poids) */
  override def edges: Set[(A, A, Double)] =
    for
      (u, m) <- adj.toSet
      (v, w) <- m
    yield (u, v, w)

  /** Liste des voisins de v avec poids */
  override def neighbors(v: A): Map[A, Double] =
    adj.getOrElse(v, Map.empty)

  /** Renvoie un nouveau graphe avec l’arête ajoutée */
  override def addEdge(u: A, v: A, w: Double): Graph[A] =
    val nbrs = adj.getOrElse(u, Map.empty)
    DirectedGraph(adj.updated(u, nbrs.updated(v, w)))

  /** Renvoie un nouveau graphe sans l’arête u -> v */
  override def removeEdge(u: A, v: A): Graph[A] =
    adj.get(u) match
      case None    => this
      case Some(m) => DirectedGraph(adj.updated(u, m - v))
