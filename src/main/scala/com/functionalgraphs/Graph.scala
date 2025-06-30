package com.functionalgraphs

/** Interface paramétrée pour représenter un graphe générique */
trait Graph[A]:
  /** Tous les sommets du graphe */
  def vertices: Set[A]
  /** Toutes les arêtes dirigées sous la forme (source, cible, poids) */
  def edges: Set[(A, A, Double)]
  /** Voisins accessibles depuis v avec leur poids */
  def neighbors(v: A): Map[A, Double]
  /** Ajoute une arête dirigée u -> v de poids w */
  def addEdge(u: A, v: A, w: Double): Graph[A]
  /** Supprime l’arête dirigée u -> v */
  def removeEdge(u: A, v: A): Graph[A]
