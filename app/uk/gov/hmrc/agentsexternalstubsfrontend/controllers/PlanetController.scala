package uk.gov.hmrc.agentsexternalstubsfrontend.controllers

import com.google.inject.Provider
import javax.inject.{Inject, Singleton}
import play.api.Configuration
import play.api.mvc._
import uk.gov.hmrc.agentsexternalstubsfrontend.connectors.AgentsExternalStubsConnector
import uk.gov.hmrc.auth.core.AuthConnector
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PlanetController @Inject()(
  val authConnector: AuthConnector,
  agentsExternalStubsConnector: AgentsExternalStubsConnector,
  ecp: Provider[ExecutionContext]
)(implicit val configuration: Configuration)
    extends FrontendController with AuthActions {

  implicit val ec: ExecutionContext = ecp.get

  val destroyPlanet: Action[AnyContent] = Action.async { implicit request =>
    authorised()
      .retrieve(Retrievals.credentialsWithPlanetId) { credentials =>
        agentsExternalStubsConnector
          .destroyPlanet(credentials.planetId)
          .map(_ => Redirect(routes.UserController.start()).withNewSession)
      }
  }

}
