/*
 * Copyright 2020 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.agentsexternalstubsfrontend.connectors

import java.net.URL

import javax.inject.{Inject, Singleton}
import play.api.libs.json._
import play.mvc.Http.HeaderNames
import uk.gov.hmrc.agentsexternalstubsfrontend.config.FrontendConfig
import uk.gov.hmrc.agentsexternalstubsfrontend.controllers.SignInController.SignInRequest
import uk.gov.hmrc.agentsexternalstubsfrontend.models._
import uk.gov.hmrc.http._
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

case class AuthenticatedSession(
  sessionId: String,
  userId: String,
  authToken: String,
  providerType: String,
  planetId: String,
  newUserCreated: Option[Boolean] = None)

object AuthenticatedSession {
  implicit val reads: Reads[AuthenticatedSession] = Json.reads[AuthenticatedSession]
}

@Singleton
class AgentsExternalStubsConnector @Inject()(appConfig: FrontendConfig, http: HttpClient) {

  val baseUrl = appConfig.aesBaseUrl

  def signIn(
    credentials: SignInRequest)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuthenticatedSession] =
    http
      .POST[SignInRequest, HttpResponse](new URL(s"$baseUrl/agents-external-stubs/sign-in").toExternalForm, credentials)
      .map(
        response =>
          (
            response
              .header(HeaderNames.LOCATION)
              .getOrElse(throw new IllegalStateException()),
            response.status))
      .flatMap {
        case (location, status) =>
          http
            .GET[AuthenticatedSession](new URL(s"$baseUrl$location").toExternalForm)
            .map(_.copy(newUserCreated = Some(status == 201)))
      }

  def signOut()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .GET[HttpResponse](new URL(s"$baseUrl/agents-external-stubs/sign-out").toExternalForm)
      .map(_ => ())

  def currentSession()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AuthenticatedSession] =
    http
      .GET[AuthenticatedSession](new URL(s"$baseUrl/agents-external-stubs/session/current").toExternalForm)

  def getUser(userId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[User] =
    http.GET[User](new URL(s"$baseUrl/agents-external-stubs/users/$userId").toExternalForm)

  def createUser(user: User)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .POST[User, HttpResponse](new URL(s"$baseUrl/agents-external-stubs/users").toExternalForm, user)
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def updateUser(user: User)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .PUT[User, HttpResponse](new URL(s"$baseUrl/agents-external-stubs/users/${user.userId}").toExternalForm, user)
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def getUsers(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Users] =
    http.GET[Users](new URL(s"$baseUrl/agents-external-stubs/users").toExternalForm)

  def removeUser(userId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .DELETE[HttpResponse](new URL(s"$baseUrl/agents-external-stubs/users/$userId").toExternalForm)
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def getRecords(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Records] =
    http.GET[Records](new URL(s"$baseUrl/agents-external-stubs/records").toExternalForm)

  def getRecord(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[JsObject] =
    http.GET[JsObject](new URL(s"$baseUrl/agents-external-stubs/records/$id").toExternalForm)

  def generateRecord(recordType: String, seed: String)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): Future[JsObject] =
    http.GET[JsObject](
      new URL(s"$baseUrl/agents-external-stubs/records/$recordType/generate?seed=$seed&minimal=false").toExternalForm)

  def updateRecord(id: String, record: JsObject)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .PUT[JsValue, HttpResponse](
        new URL(s"$baseUrl/agents-external-stubs/records/$id").toExternalForm,
        record.+("id" -> JsString(id))
      )
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def deleteRecord(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .DELETE[HttpResponse](new URL(s"$baseUrl/agents-external-stubs/records/$id").toExternalForm)
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def createRecord(recordType: String, record: JsObject)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): Future[Option[String]] =
    http
      .POST[JsValue, HttpResponse](
        new URL(s"$baseUrl/agents-external-stubs/records/$recordType").toExternalForm,
        record
      )
      .map(r => (r.json \ "_links" \ 0 \ "href").asOpt[String].map(_.split("/").last))
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def getKnownFacts(enrolmentKey: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[EnrolmentInfo] =
    http.GET[EnrolmentInfo](new URL(s"$baseUrl/agents-external-stubs/known-facts/$enrolmentKey").toExternalForm)

  def getServicesInfo()(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Services] =
    http
      .GET[Services](new URL(s"$baseUrl/agents-external-stubs/config/services").toExternalForm)
      .map(s => s.copy(s.services.sortBy(_.name)))

  def destroyPlanet(planetId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .DELETE[HttpResponse](new URL(s"$baseUrl/agents-external-stubs/planets/$planetId").toExternalForm)
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def getAllSpecialCases(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Seq[SpecialCase]] =
    http
      .GET[Option[Seq[SpecialCase]]](new URL(s"$baseUrl/agents-external-stubs/special-cases").toExternalForm)
      .map {
        case Some(seq) => seq
        case None      => Seq.empty
      }

  def getSpecialCase(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[SpecialCase]] =
    http
      .GET[SpecialCase](new URL(s"$baseUrl/agents-external-stubs/special-cases/$id").toExternalForm)
      .map(Option.apply)
      .recover {
        case _: NotFoundException => None
      }

  def createSpecialCase(specialCase: SpecialCase)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] =
    http
      .POST[SpecialCase, HttpResponse](
        new URL(s"$baseUrl/agents-external-stubs/special-cases").toExternalForm,
        specialCase
      )
      .map(
        r =>
          r.header(HeaderNames.LOCATION)
            .map(_.split("/").last)
            .getOrElse(throw new Exception("Missing location header in the response")))
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def updateSpecialCase(specialCase: SpecialCase)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[String] =
    http
      .PUT[SpecialCase, HttpResponse](
        new URL(s"$baseUrl/agents-external-stubs/special-cases/${specialCase.id.get}").toExternalForm,
        specialCase
      )
      .map(
        r =>
          r.header(HeaderNames.LOCATION)
            .map(_.split("/").last)
            .getOrElse(throw new Exception("Missing location header in the response")))
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def deleteSpecialCase(id: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .DELETE[HttpResponse](new URL(s"$baseUrl/agents-external-stubs/special-cases/$id").toExternalForm)
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

  def storePdvResult(id: String, success: Boolean)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Unit] =
    http
      .POSTEmpty[HttpResponse](new URL(s"$baseUrl/agents-external-stubs/pdv-result/$id/$success").toExternalForm)
      .map(_ => ())
      .recover {
        case Upstream4xxException(e) => throw e
      }

}
