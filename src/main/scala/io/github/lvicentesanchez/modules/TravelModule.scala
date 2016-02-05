package io.github.lvicentesanchez.modules

import io.github.lvicentesanchez.models.User

/**
 * Created by luissanchez on 05/02/2016.
 */
trait TravelModule {

  def double(user: User): User
}

object TravelModule extends TravelModule {

  override def double(user: User): User = user.copy(age = user.age * 2)
}
