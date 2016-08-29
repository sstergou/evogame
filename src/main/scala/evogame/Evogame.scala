package evogame


import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js.JSApp

object Conway {

  case class Cell(x: Int, y: Int, alive: Boolean)

  trait Generator[A] extends (A => Conway) {
    def rangeMap(rows: Int, cols: Int)(f: (Int, Int) => Cell) =
      for {
        i <- 0 until rows
        j <- 0 until cols
      } yield f(i, j)
  }

  trait Gene extends Generator[Conway]

  case class RandomPercentage(p: Double) extends Gene {
    def apply(in: Conway): Conway = new Conway(rangeMap(in.rows, in.cols) {
      case (i, j) if Math.random() > p => Cell(i, j, alive=true)
      case (i, j) => Cell(i, j, alive=false)
    })
  }

  case class Size(rows: Int, cols: Int) extends Generator[Unit] {
    def apply: Conway = new Conway(rangeMap(rows, cols) { case (i, j) => Cell(i, j, alive=false)})
  }

  class Conway(model: IndexedSeq[Cell]) {

    val (rows, cols) = model.foldLeft((0, 0)) { case ((r, c), Cell(x, y, _)) =>
      (Math.max(r, x), Math.max(c, y))
    }

    def get(i: Int, j: Int): Cell = model(i * cols + j)

    def neighbors(c: Cell): Seq[Cell] =
      for {
        i <- c.x - 1 to c.x + 1
        j <- c.y - 1 to c.y + 1
        if inBounds(i, j) && (i != c.x || j != c.y)
      } yield get(i, j)

    def inBounds(i: Int, j: Int): Boolean =
      i > 0 && i < rows && j > 0 && j < cols

    def evolve: Conway = new Conway(model.map { c =>
      val nb = neighbors(c).count(_.alive)
      if (c.alive) {
        if (nb >= 4 || nb <= 1) Cell(c.x, c.y, alive=false)
        else c
      } else {
        if (nb == 3) Cell(c.x, c.y, alive=true)
        else c
      }
    })

    def render: IndexedSeq[IndexedSeq[Cell]] =
      (0 until rows).map(i => (0 until cols).map(j => get(i, j)))

  }


}


object Evogame extends JSApp {
  def main() = {
    
    val canvas = dom.document.getElementById("canvas").asInstanceOf[html.Canvas]
    val renderer = canvas.getContext("2d").asInstanceOf[dom.CanvasRenderingContext2D]

    canvas.width = canvas.parentElement.clientWidth
    canvas.height = canvas.parentElement.clientHeight

    renderer.fillStyle = "black"

    var down = false
    canvas.onmousedown =
      (e: dom.MouseEvent) => down = true

    canvas.onmouseup =
      (e: dom.MouseEvent) => down = false

    canvas.onmousemove = {
      (e: dom.MouseEvent) =>
        val rect =
          canvas.getBoundingClientRect()
        if (down) renderer.fillRect(
          e.clientX - rect.left,
          e.clientY - rect.top,
          10, 10
        )
    }
  }


}
