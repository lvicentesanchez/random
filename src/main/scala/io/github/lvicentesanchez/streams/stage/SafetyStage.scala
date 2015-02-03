package io.github.lvicentesanchez.streams.stage

import akka.stream.stage._
import scala.collection.mutable
import scalaz.{ -\/, \/, \/- }

class SafetyStage[A](capacity: Int) extends PushPullStage[A, A \/ A] {
  final val DoSRate: Double = 0.25
  private val buffer: mutable.Queue[A] = mutable.Queue[A]()

  override def onPush(elem: A, ctx: Context[A \/ A]): Directive =
    if (buffer.size < capacity && math.random < (1.0 - DoSRate)) {
      buffer += elem
      ctx.push(\/-(buffer.dequeue()))
    } else {
      ctx.push(-\/(elem))
    }

  override def onPull(ctx: Context[A \/ A]): Directive =
    if (buffer.size > 0) {
      ctx.push(\/-(buffer.dequeue()))
    } else {
      ctx.pull()
    }
}

object SafetyStage {
  def apply[A](capacity: Int): SafetyStage[A] = new SafetyStage[A](capacity)
}