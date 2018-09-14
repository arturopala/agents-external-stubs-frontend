package uk.gov.hmrc.agentsexternalstubsfrontend.models
import play.api.libs.json.{Format, Json}

case class Users(users: Seq[User])

object Users {
  implicit def format: Format[Users] = Json.format[Users]
}
