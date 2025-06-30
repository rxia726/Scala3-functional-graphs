package com.functionalgraphs

import zio.json.{DeriveJsonDecoder, DeriveJsonEncoder, JsonDecoder, JsonEncoder}

/** Représentation intermédiaire pour sérialiser le graphe en JSON */
case class GraphDto[A](vertices: List[A], edges: List[(A, A, Double)])

object JsonCodecs:
  /** Génère un encodeur JSON pour GraphDto[A] */
  implicit def dtoEncoder[A: JsonEncoder]: JsonEncoder[GraphDto[A]] =
    DeriveJsonEncoder.gen[GraphDto[A]]

  /** Génère un décodeur JSON pour GraphDto[A] */
  implicit def dtoDecoder[A: JsonDecoder]: JsonDecoder[GraphDto[A]] =
    DeriveJsonDecoder.gen[GraphDto[A]]

  /** Convertit un Graph[A] en GraphDto[A] pour sérialisation */
  def toDto[A](g: Graph[A]): GraphDto[A] =
    GraphDto(g.vertices.toList, g.edges.toList)

  /** Reconstruit un DirectedGraph[A] à partir d’un GraphDto[A] */
  def fromDto[A](dto: GraphDto[A]): Graph[A] =
    dto.edges.foldLeft(DirectedGraph[A](Map.empty): Graph[A]) { case (graph, (u, v, w)) =>
      graph.addEdge(u, v, w)
    }
