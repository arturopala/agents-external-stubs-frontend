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

package uk.gov.hmrc.agentsexternalstubsfrontend.models
import play.api.libs.json.{Format, Json}

case class SpecialCase(
  requestMatch: SpecialCase.RequestMatch,
  response: SpecialCase.Response,
  id: Option[String] = None)

object SpecialCase {

  case class RequestMatch(
    path: String,
    method: String = "GET",
    body: Option[String] = None,
    contentType: Option[String] = None)

  case class Header(name: String, value: String)

  case class Response(status: Int, body: Option[String] = None, headers: Option[Seq[Header]] = None)

  implicit val formats1: Format[RequestMatch] = Json.format[RequestMatch]
  implicit val formats2: Format[Header] = Json.format[Header]
  implicit val formats3: Format[Response] = Json.format[Response]
  implicit val formats: Format[SpecialCase] = Json.format[SpecialCase]

  val httpStatusCodes: Map[Int, String] = Map(
    200 -> "OK",
    201 -> "Created",
    202 -> "Accepted",
    203 -> "Non-authoritative Information",
    204 -> "No Content",
    205 -> "Reset Content",
    206 -> "Partial Content",
    207 -> "Multi-Status",
    208 -> "Already Reported",
    226 -> "IM Used",
    300 -> "Multiple Choices",
    301 -> "Moved Permanently",
    302 -> "Found",
    303 -> "See Other",
    304 -> "Not Modified",
    305 -> "Use Proxy",
    307 -> "Temporary Redirect",
    308 -> "Permanent Redirect",
    400 -> "Bad Request",
    401 -> "Unauthorized",
    402 -> "Payment Required",
    403 -> "Forbidden",
    404 -> "Not Found",
    405 -> "Method Not Allowed",
    406 -> "Not Acceptable",
    407 -> "Proxy Authentication Required",
    408 -> "Request Timeout",
    409 -> "Conflict",
    410 -> "Gone",
    411 -> "Length Required",
    412 -> "Precondition Failed",
    413 -> "Payload Too Large",
    414 -> "Request-URI Too Long",
    415 -> "Unsupported Media Type",
    416 -> "Requested Range Not Satisfiable",
    417 -> "Expectation Failed",
    418 -> "I'm a teapot",
    421 -> "Misdirected Request",
    422 -> "Unprocessable Entity",
    423 -> "Locked",
    424 -> "Failed Dependency",
    426 -> "Upgrade Required",
    428 -> "Precondition Required",
    429 -> "Too Many Requests",
    431 -> "Request Header Fields Too Large",
    444 -> "Connection Closed Without Response",
    451 -> "Unavailable For Legal Reasons",
    499 -> "Client Closed Request",
    500 -> "Internal Server Error",
    501 -> "Not Implemented",
    502 -> "Bad Gateway",
    503 -> "Service Unavailable",
    504 -> "Gateway Timeout",
    505 -> "HTTP Version Not Supported",
    506 -> "Variant Also Negotiates",
    507 -> "Insufficient Storage",
    508 -> "Loop Detected",
    510 -> "Not Extended",
    511 -> "Network Authentication Required",
    599 -> "Network Connect Timeout Error"
  )

  val httpStatusCodeOptions = httpStatusCodes.map { case (k, v) => s"$k" -> s"$k $v" }.toSeq.sortBy(_._1)
}
