package uk.gov.hmrc.agentsexternalstubsfrontend

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.scaladsl.{Flow, Tcp}
import akka.util.ByteString
import javax.inject.{Inject, Named, Singleton}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try

@Singleton
class TcpProxies @Inject()(
  @Named("proxies.start") startProxies: String,
  @Named("company-auth-frontend.port") companyAuthFrontendPort: Int,
  @Named("http.port") httpPort: String
)(implicit system: ActorSystem, materializer: Materializer) {

  if (startProxies == "true") {
    println("Starting TCP proxies ...")

    implicit val ec: ExecutionContext = system.dispatcher

    val agentsExternalStubsFrontendPort = Try(httpPort.toInt).toOption.getOrElse(9009)

    val tcpOutgoingConnection: Flow[ByteString, ByteString, Future[Tcp.OutgoingConnection]] =
      Tcp().outgoingConnection("localhost", agentsExternalStubsFrontendPort)

    val tcpProxy = Flow[ByteString].via(tcpOutgoingConnection)

    Tcp(system)
      .bindAndHandle(tcpProxy, interface = "localhost", port = companyAuthFrontendPort)
      .map(s => println(s"Listening for company-auth-frontend requests on ${s.localAddress}"))

  } else {
    println("TCP proxies feature switched off")
  }

}
