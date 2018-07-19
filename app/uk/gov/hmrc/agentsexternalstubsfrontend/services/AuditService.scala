package uk.gov.hmrc.agentsexternalstubsfrontend.services

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.mvc.Request
import uk.gov.hmrc.agentmtdidentifiers.model.Arn
import uk.gov.hmrc.agentsexternalstubsfrontend.models.AgentsExternalStubsFrontendModel
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.audit.AuditExtensions._
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.model.DataEvent
import uk.gov.hmrc.play.http.logging.MdcLoggingExecutionContext._

import scala.concurrent.Future
import scala.util.Try

object AgentsExternalStubsFrontendEvent extends Enumeration {
  val AgentsExternalStubsFrontendSomethingHappened = Value
  type AgentsExternalStubsFrontendEvent = Value
}

@Singleton
class AuditService @Inject()(val auditConnector: AuditConnector) {

  import AgentsExternalStubsFrontendEvent._

  def sendAgentsExternalStubsFrontendSomethingHappened(model: AgentsExternalStubsFrontendModel, agentReference: Arn)(
    implicit hc: HeaderCarrier,
    request: Request[Any]): Unit =
    auditEvent(
      AgentsExternalStubsFrontendEvent.AgentsExternalStubsFrontendSomethingHappened,
      "agents-external-stubs-frontend-something-happened",
      Seq(
        "agentReference"  -> agentReference.value,
        "parameter1"      -> model.parameter1,
        "telephoneNumber" -> model.telephoneNumber.getOrElse(""),
        "emailAddress"    -> model.emailAddress.getOrElse("")
      )
    )

  private[services] def auditEvent(
    event: AgentsExternalStubsFrontendEvent,
    transactionName: String,
    details: Seq[(String, Any)] = Seq.empty)(implicit hc: HeaderCarrier, request: Request[Any]): Future[Unit] =
    send(createEvent(event, transactionName, details: _*))

  private[services] def createEvent(
    event: AgentsExternalStubsFrontendEvent,
    transactionName: String,
    details: (String, Any)*)(implicit hc: HeaderCarrier, request: Request[Any]): DataEvent = {

    val detail = hc.toAuditDetails(details.map(pair => pair._1 -> pair._2.toString): _*)
    val tags = hc.toAuditTags(transactionName, request.path)
    DataEvent(auditSource = "agents-external-stubs-frontend", auditType = event.toString, tags = tags, detail = detail)
  }

  private[services] def send(events: DataEvent*)(implicit hc: HeaderCarrier): Future[Unit] =
    Future {
      events.foreach { event =>
        Try(auditConnector.sendEvent(event))
      }
    }

}