package uk.gov.hmrc.agentsexternalstubsfrontend.controllers
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import play.api.data.Forms.{mapping, text}
import play.api.data.Mapping
import play.api.data.validation.Constraint

import scala.util.control.NonFatal

object DateFieldHelper {

  def validateDate(value: String): Boolean = if (parseDate(value)) true else false

  val dateTimeFormat = DateTimeFormat.forPattern("yyyy-MM-dd")

  def parseDate(date: String): Boolean =
    try {
      dateTimeFormat.parseDateTime(date)
      true
    } catch {
      case _: Throwable => false
    }

  def parseDateIntoFields(date: String): Option[(String, String, String)] =
    try {
      val l = dateTimeFormat.parseLocalDate(date)
      Some((l.getYear.toString, l.getMonthOfYear.toString, l.getDayOfMonth.toString))
    } catch {
      case NonFatal(_) => None
    }

  val formatDateFromFields: (String, String, String) => String = {
    case (y, m, d) =>
      if (y.isEmpty || m.isEmpty || d.isEmpty) ""
      else {
        val month = if (m.length == 1) "0" + m else m
        val day = if (d.length == 1) "0" + d else d
        s"$y-$month-$day"
      }
  }

  def dateFieldsMapping(constraintDate: Constraint[String]): Mapping[LocalDate] =
    mapping(
      "year"  -> text.verifying("error.year.invalid-format", y => y.isEmpty || y.matches("^[0-9]{1,4}$")),
      "month" -> text.verifying("error.month.invalid-format", m => m.isEmpty || m.matches("^[0-9]{1,2}$")),
      "day"   -> text.verifying("error.day.invalid-format", d => d.isEmpty || d.matches("^[0-9]{1,2}$"))
    )(formatDateFromFields)(parseDateIntoFields)
      .verifying(constraintDate)
      .transform(LocalDate.parse, _.toString("yyyy-MM-dd"))

  val validDobDateFormat: Constraint[String] =
    ValidateHelper.validateField("error.date-of-birth.required", "enter.date-of-birth.invalid-format")(date =>
      validateDate(date))
}