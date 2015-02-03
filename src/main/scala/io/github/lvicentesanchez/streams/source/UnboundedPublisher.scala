package io.github.lvicentesanchez.streams.source

import akka.actor.{ ActorLogging, Props }
import akka.stream.actor.{ ActorPublisherMessage, ActorPublisher }
import scala.reflect.ClassTag
import scalaz.std.boolean._

final class UnboundedPublisher[A: ClassTag]() extends ActorPublisher[A] with ActorLogging {
  override val receive: Receive =
    loop(Seq())

  def loop(pending: Seq[A]): Receive = {
    case ActorPublisherMessage.Request(_) ⇒
      produce(pending)

    case ActorPublisherMessage.Cancel ⇒
    // do nothing

    case element: A ⇒
      produce(pending :+ element)
  }

  def produce(pending: Seq[A]): Unit =
    when(isActive && totalDemand > 0 && pending.size > 0) {
      val (head, rest) = pending.splitAt(math.min(totalDemand, pending.size).toInt)
      head.foreach(onNext)
      context.become(loop(rest))
    }
}

object UnboundedPublisher {
  def props[A: ClassTag] =
    Props(new UnboundedPublisher[A]())
}