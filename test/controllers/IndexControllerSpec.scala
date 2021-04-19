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

import java.time.LocalDate
import base.SpecBase
import connectors.SubmissionDraftConnector
import models.Status.Completed
import models.{NormalMode, StartDate}
import org.joda.time.{DateTime, DateTimeUtils}
import org.mockito.Matchers.any
import org.mockito.Mockito.{times, verify, when}
import pages.{TaxLiabilityTaskStatus, TrustStartDatePage}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LocalDateService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {

    def setCurrentDate(date: LocalDate): LocalDateService = new LocalDateService {
      override def now: LocalDate = date
    }

    def setCurrentDateTime(date: LocalDate) = {
      DateTimeUtils.setCurrentMillisFixed(new DateTime(date.toString).getMillis)
    }

    val draftId = "draftId"
    
    "for an existing session" when {

      "continue session if trust start date is not changed" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        setCurrentDateTime(dateBeforeDec23rd)

        val initialStartDate = LocalDate.of(2015, 5, 1)

        val existingUserAnswers = emptyUserAnswers.set(TrustStartDatePage, initialStartDate).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(initialStartDate))))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode, draftId).url)

        verify(registrationsRepository, times(0)).resetCache(any())(any(), any())

        application.stop()
      }

      "redirect to user answers if trust start date is not changed and answers previously completed" in {
        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        setCurrentDateTime(dateBeforeDec23rd)

        val initialStartDate = LocalDate.of(2015, 5, 1)

        val existingUserAnswers = emptyUserAnswers
          .set(TrustStartDatePage, initialStartDate).success.value
          .set(TaxLiabilityTaskStatus, Completed).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(initialStartDate))))

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

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        setCurrentDateTime(dateBeforeDec23rd)

        val initialStartDate = LocalDate.of(2015, 5, 1)

        val existingUserAnswers = emptyUserAnswers.set(TrustStartDatePage, initialStartDate).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val newStartDate = LocalDate.of(2018, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(newStartDate))))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode, draftId).url)

        verify(registrationsRepository, times(1)).resetCache(any())(any(), any())

        application.stop()
      }
    }

    "redirect back to register task list if no start date" in {

      val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

      val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

      setCurrentDateTime(dateBeforeDec23rd)

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
        .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
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

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        setCurrentDateTime(dateBeforeDec23rd)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2015, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode, draftId).url)

        application.stop()
      }
    }

    "redirect to CY-4 liability controller" when {

      "trust start date is four years ago and current date is before 23rd Dec" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        setCurrentDateTime(dateBeforeDec23rd)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val startDate = LocalDate.of(2016, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourLiabilityController.onPageLoad(NormalMode, draftId).url)

        application.stop()
      }
    }

    "redirect to CY-3 Earlier years liability controller" when {

      "trust start date is more than three years ago and current date is after 23rd Dec" in {

        val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

        val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

        setCurrentDateTime(dateAfterDec23rd)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
          .build()

        val startDate = LocalDate.of(2015, 5, 1)

        when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

        when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(NormalMode, draftId).url)

        application.stop()
      }
    }

    "redirect to CY-3 liability controller" when {

      "trust start date is three years ago" when {

        "current date is before 23rd Dec" in {

          val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

          val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

          setCurrentDateTime(dateBeforeDec23rd)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
            .build()

          val trustStartDate = LocalDate.of(2017, 5, 1)

          when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(trustStartDate))))

          when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode, draftId).url)

          application.stop()
        }

        "current date is after 23rd Dec" in {

          val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

          val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

          setCurrentDateTime(dateAfterDec23rd)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
            .build()

          val startDate = LocalDate.of(2017, 5, 1)

          when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

          when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode, draftId).url)

          application.stop()
        }
      }
    }

    "redirect to CY-2 liability controller" when {

      "trust start date is two years ago" when {

        "current date is before 23rd Dec" in {

          val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

          val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

          setCurrentDateTime(dateBeforeDec23rd)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
            .build()

          val startDate = LocalDate.of(2018, 5, 1)

          when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

          when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode, draftId).url)

          application.stop()
        }

        "current date is after 23rd Dec" in {

          val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

          val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

          setCurrentDateTime(dateAfterDec23rd)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
            .build()

          val startDate = LocalDate.of(2018, 5, 1)

          when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

          when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode, draftId).url)

          application.stop()
        }
      }
    }

    "redirect to CY-1 liability controller" when {

      "trust start date is one years ago" when {

        "current date is before 23rd Dec" in {

          val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

          val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

          setCurrentDateTime(dateBeforeDec23rd)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
            .build()

          val startDate = LocalDate.of(2019, 5, 1)

          when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

          when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusOneLiabilityController.onPageLoad(NormalMode, draftId).url)

          application.stop()
        }

        "current date is after 23rd Dec" in {

          val mockSubmissionDraftConnector = mock[SubmissionDraftConnector]

          val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

          setCurrentDateTime(dateAfterDec23rd)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[SubmissionDraftConnector].toInstance(mockSubmissionDraftConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
            .build()

          val startDate = LocalDate.of(2019, 5, 1)

          when(mockSubmissionDraftConnector.getTrustStartDate(any())(any(), any())).thenReturn(Future.successful(Some(StartDate(startDate))))

          when(registrationsRepository.resetCache(any())(any(), any())).thenReturn(Future.successful(true))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusOneLiabilityController.onPageLoad(NormalMode, draftId).url)

          application.stop()
        }
      }
    }
  }
}
