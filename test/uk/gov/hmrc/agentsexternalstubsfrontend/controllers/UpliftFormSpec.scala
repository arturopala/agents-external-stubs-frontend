package uk.gov.hmrc.agentsexternalstubsfrontend.controllers

import uk.gov.hmrc.agentsexternalstubsfrontend.controllers.IdentityVerificationController.UpliftRequest
import uk.gov.hmrc.play.test.UnitSpec

class UpliftFormSpec extends UnitSpec {

  "UpliftForm" should {

    "bind all input fields and return UpliftRequest and fill it back" in {
      val form = IdentityVerificationController.UpliftRequestForm

      val value = UpliftRequest("AB626225C", "success")

      val fieldValues = Map(
        "nino"   -> "AB626225C",
        "option" -> "success"
      )

      form.bind(fieldValues).value shouldBe Some(value)
      form.fill(value).data shouldBe fieldValues
    }
  }
}
