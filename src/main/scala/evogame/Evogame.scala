package evogame

import evogame.evolution.types._
import evogame.graphics.Canvas

import scala.scalajs.js.JSApp


object C extends App {
//  val positions = Set(
//    (5, 1), (5, 2), (6, 1), (6, 2), (5, 11), (6, 11), (7, 11), (4, 12),
//    (3, 13), (3, 14), (8, 12), (9, 13), (9, 14), (6, 15), (4, 16), (5, 17),
//    (6, 17), (7, 17), (6, 18), (8, 16), (3, 21), (4, 21), (5, 21), (3, 22),
//    (4, 22), (5, 22), (2, 23), (6, 23), (1, 25), (2, 25), (6, 25), (7, 25), (3, 35), (4, 35), (3, 36), (4, 36))
//
//  val grid = Grid(40, Grid.range(40).map(p => if (positions.contains(p.swap)) true else false))


  val positions = Set((1, 1), (1, 2), (2, 1), (2, 2), (3, 3), (3, 4), (4, 3), (4, 4))

  val grid = Grid(6, Grid.range(6).map(p => if (positions.contains(p.swap)) true else false))


  var c = new Conway(grid, null)
  def const[A](v: A): ((Int, Int) => A) = { case (x, y) => v }

  val empty = new Conway(Grid.const(5, false), null)
  var k = Ratio(0.3, const(true), const(false)).transform(empty)
  k = Symmetry[Boolean](1).transform(k)

  (1 to 10) foreach {_ =>
    println(k.state.cells.count(_ == true))
    k = k.grow
  }

}

object Evogame extends JSApp {

  def main() = {
    val canvas = new Canvas("canvas")

    val positions = Set(
      (5, 1), (5, 2), (6, 1), (6, 2), (5, 11), (6, 11), (7, 11), (4, 12),
      (3, 13), (3, 14), (8, 12), (9, 13), (9, 14), (6, 15), (4, 16), (5, 17),
      (6, 17), (7, 17), (6, 18), (8, 16), (3, 21), (4, 21), (5, 21), (3, 22),
      (4, 22), (5, 22), (2, 23), (6, 23), (1, 25), (2, 25), (6, 25), (7, 25), (3, 35), (4, 35), (3, 36), (4, 36)).map { case (y, x) => (y, x) }

    val grid = Grid(80, Grid.range(80).map(p => positions.contains(p.swap)))

    val c = new Conway(grid, null)
    canvas.draw(c.state)
    canvas.draw(Scatter(1, { case (x, y) => false }).transform(c).state)
    def const[A](v: A): ((Int, Int) => A) = { case (x, y) => v }
    val empty = new Conway(Grid.const(41, false), null)
    val k = Symmetry[Boolean](1).transform(Ratio(0.2, const(true), const(false)).transform(empty))
    canvas.animate(k.generations, 300)

  }


}
