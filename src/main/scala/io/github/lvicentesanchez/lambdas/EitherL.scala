package io.github.lvicentesanchez.lambdas

import scalaz.\/

sealed trait EitherL[A] {
  type T[B] = A \/ B
}
