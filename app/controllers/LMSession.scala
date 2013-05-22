package controllers

import models.User

case class LMSession(key: String, user: User) {
  def defaultAccountId = user.accountIds.headOption
}
