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

package utils

import base.SpecBase
import controllers.routes
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, NormalMode}
import pages._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelperSpec extends SpecBase {

  "Check your answers helper" when {

    "earlier years" must {

      "render answers for tax before 4 years" ignore {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val userAnswers = emptyUserAnswers
          .set(CYMinusFourEarlierYearsYesNoPage, true).success.value

        val result = cyaHelper.earlierThan4YearsAnswers(userAnswers)

        result.value mustBe AnswerSection(
          headingKey = Some("Tax liability before 2016"),
          rows = Seq(
            AnswerRow(label = "Did the trust need to pay tax before 2016?", answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            )
          )
        )

      }

      "render answers for tax before 3 years" ignore {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val userAnswers = emptyUserAnswers
          .set(CYMinusThreeEarlierYearsYesNoPage, true).success.value

        val result = cyaHelper.earlierThan3YearsAnswers(userAnswers)

        result.value mustBe AnswerSection(
          headingKey = Some("Tax liability before 2017"),
          rows = Seq(
            AnswerRow(label = "Did the trust need to pay tax before 2017?", answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            )
          )
        )
      }

    }

    "CY-4" must {

      "render answers for CY-4 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus4TaxYear

        val userAnswers = emptyUserAnswers
          .set(CYMinusFourYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some("Tax liability 6 April 2016 to 5 April 2017"),
          rows = Seq(
            AnswerRow(
              label = "Did the trust need to pay any tax from 6 April 2016 to 5 April 2017?",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusFourLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = "Was the tax from 6 April 2016 to 5 April 2017 declared?",
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

    "CY-3" must {

      "render answers for CY-3 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus3TaxYear

        val userAnswers = emptyUserAnswers
          .set(CYMinusThreeYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some("Tax liability 6 April 2017 to 5 April 2018"),
          rows = Seq(
            AnswerRow(
              label = "Did the trust need to pay any tax from 6 April 2017 to 5 April 2018?",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = "Was the tax from 6 April 2017 to 5 April 2018 declared?",
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

    "CY-2" must {

      "render answers for CY-2 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus2TaxYear

        val userAnswers = emptyUserAnswers
          .set(CYMinusTwoYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some("Tax liability 6 April 2018 to 5 April 2019"),
          rows = Seq(
            AnswerRow(
              label = "Did the trust need to pay any tax from 6 April 2018 to 5 April 2019?",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = "Was the tax from 6 April 2018 to 5 April 2019 declared?",
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

    "CY-1" must {

      "render answers for CY-1 liability and declared" in {
        val cyaHelper = injector.instanceOf[CheckYourAnswersHelper]

        val taxYear = CYMinus1TaxYear

        val userAnswers = emptyUserAnswers
          .set(CYMinusOneYesNoPage, true).success.value
          .set(DidDeclareTaxToHMRCYesNoPage(taxYear), true).success.value

        val result = cyaHelper.cyMinusTaxYearAnswers(userAnswers, taxYear)

        result.value mustBe AnswerSection(
          headingKey = Some("Tax liability 6 April 2019 to 5 April 2020"),
          rows = Seq(
            AnswerRow(
              label = "Did the trust need to pay any tax from 6 April 2019 to 5 April 2020?",
              answer = Html("Yes"),
              changeUrl = Some(routes.CYMinusOneLiabilityController.onPageLoad(NormalMode ,draftId).url),
              canEdit = true
            ),
            AnswerRow(
              label = "Was the tax from 6 April 2019 to 5 April 2020 declared?",
              answer = Html("Yes"),
              changeUrl = Some(routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode ,draftId, taxYear).url),
              canEdit = true
            )
          )
        )
      }
    }

  }

}
