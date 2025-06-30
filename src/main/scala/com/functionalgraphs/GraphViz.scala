package com.functionalgraphs

/** Extension pour générer une représentation DOT (GraphViz) */
extension [A](g: Graph[A])
  /** Produit une chaîne DOT nommée name */
  def toDot(name: String = "G"): String =
    val header = s"digraph $name {\n"
    val body = g.edges.foldLeft("") { case (acc, (u, v, w)) =>
      acc + s"""  "${u}" -> "${v}" [label="$w"];\n"""
    }
    header + body + "}\n"
