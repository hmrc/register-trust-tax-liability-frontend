/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.Status.Completed
import models.{FirstTaxYearAvailable, StartDate}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.{TaxLiabilityTaskStatus, TrustStartDatePage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._

import java.time.LocalDate
import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {

    val draftId = "draftId"
    val startDate = LocalDate.of(2015, 5, 1)

    "for an existing session" when {

      "continue session if trust start date is not changed" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val existingUserAnswers = emptyUserAnswers.set(TrustStartDatePage, startDate).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(4, earlierYearsToDeclare = true)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(draftId).url)

        verify(registrationsRepository, times(0)).resetCache(any())(any(), any())

        application.stop()
      }

      "redirect to user answers if trust start date is not changed and answers previously completed" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val existingUserAnswers = emptyUserAnswers
          .set(TrustStartDatePage, startDate).success.value
          .set(TaxLiabilityTaskStatus, Completed).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CheckYourAnswersController.onPageLoad(draftId).url)

        verify(registrationsRepository, times(0)).resetCache(any())(any(), any())

        application.stop()
      }

      "clear user answers if the user returns and the trust start date has changed" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val existingUserAnswers = emptyUserAnswers.set(TrustStartDatePage, startDate).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate.plusDays(1)))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(2, earlierYearsToDeclare = false)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(draftId).url)

        verify(registrationsRepository, times(1)).resetCache(any())(any(), any())

        application.stop()
      }
    }

    "redirect back to register task list if no start date" in {

      val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
        .build()

      when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(None))

      when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe "http://localhost:9781/trusts-registration/draftId/registration-progress"

      application.stop()
    }

    "redirect to CY-4 Earlier years liability controller" when {
      "trust start date is more than four years ago and current date is before 23rd Dec" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(4, earlierYearsToDeclare = true)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(draftId).url)

        application.stop()
      }
    }

    "redirect to CY-4 liability controller" when {
      "trust start date is four years ago and current date is before 23rd Dec" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(4, earlierYearsToDeclare = false)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourLiabilityController.onPageLoad(draftId).url)

        application.stop()
      }
    }

    "redirect to CY-3 Earlier years liability controller" when {
      "trust start date is more than three years ago and current date is after 23rd Dec" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(3, earlierYearsToDeclare = true)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(draftId).url)

        application.stop()
      }
    }

    "redirect to CY-3 liability controller" when {
      "trust start date is three years ago" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(3, earlierYearsToDeclare = false)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusThreeLiabilityController.onPageLoad(draftId).url)

        application.stop()
      }
    }

    "redirect to CY-2 liability controller" when {
      "trust start date is two years ago" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(2, earlierYearsToDeclare = false)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(draftId).url)

        application.stop()
      }
    }

    "redirect to CY-1 liability controller" when {
      "trust start date is one year ago" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any()))
          .thenReturn(Future.successful(Some(StartDate(startDate))))

        when(mockSubmissionDraftConnector.getFirstTaxYearAvailable(any())(any(), any()))
          .thenReturn(Future.successful(FirstTaxYearAvailable(1, earlierYearsToDeclare = false)))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusOneLiabilityController.onPageLoad(draftId).url)

        application.stop()
      }
    }
  }
}
