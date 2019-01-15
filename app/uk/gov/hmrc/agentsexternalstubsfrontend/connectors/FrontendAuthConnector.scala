package uk.gov.hmrc.agentsexternalstubsfrontend.connectors

import java.net.URL

import akka.actor.ActorSystem
import com.typesafe.config.Config
import javax.inject.{Inject, Named, Singleton}
import play.api.Configuration
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.http.HttpPost
import uk.gov.hmrc.play.http.ws.WSPost

@Singleton
class FrontendAuthConnector @Inject()(
  @Named("auth-baseUrl") baseUrl: URL,
  config: Configuration,
  _actorsSystem: ActorSystem)
    extends PlayAuthConnector {

  override val serviceUrl = baseUrl.toString

  override def http = new HttpPost with WSPost {
    override val hooks = NoneRequired
    override def configuration: Option[Config] = Some(config.underlying)
    override def actorSystem: ActorSystem = _actorsSystem
  }
}
