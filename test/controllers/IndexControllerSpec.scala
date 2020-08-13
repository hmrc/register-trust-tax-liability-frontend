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

package controllers

import java.time.LocalDate

import base.SpecBase
import connectors.EstatesConnector
import models.NormalMode
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.DateOfDeathPage
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.LocalDateService
import org.mockito.Mockito.{times, verify}
import play.api.libs.json.Json

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  "Index Controller" must {

    def setCurrentDate(date: LocalDate): LocalDateService = new LocalDateService {
      override def now: LocalDate = date
    }

    val draftId = "draftId"
    
    "for an existing session" when {

      "continue session if date of death is not changed" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val initialDateOfDeath = LocalDate.of(2015, 5, 1)

        val existingUserAnswers = emptyUserAnswers.set(DateOfDeathPage, initialDateOfDeath).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(initialDateOfDeath))

        when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode).url)

        verify(sessionRepository, times(0)).resetCache(any())

        application.stop()
      }

      "clear user answers if the user returns and the date of death has changed" in {
        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val initialDateOfDeath = LocalDate.of(2015, 5, 1)

        val existingUserAnswers = emptyUserAnswers.set(DateOfDeathPage, initialDateOfDeath).success.value

        val application = applicationBuilder(userAnswers = Some(existingUserAnswers))
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val newDateOfDeath = LocalDate.of(2018, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(newDateOfDeath))

        when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode).url)

        verify(sessionRepository, times(1)).resetCache(any())

        application.stop()
      }
    }

    "redirect to CY-4 Earlier years liability controller" when {

      "date of death is more than four years ago and current date is before 23rd Dec" in {

        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2015, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode).url)

        application.stop()
      }
    }

    "redirect to CY-4 liability controller" when {

      "date of death is four years ago and current date is before 23rd Dec" in {

        val mockEstatesConnector = mock[EstatesConnector]

        val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2016, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusFourLiabilityController.onPageLoad(NormalMode).url)

        application.stop()
      }
    }

    "redirect to CY-3 Earlier years liability controller" when {

      "date of death is more than three years ago and current date is after 23rd Dec" in {

        val mockEstatesConnector = mock[EstatesConnector]

        val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

        val application = applicationBuilder(userAnswers = None)
          .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
          .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
          .build()

        val dateOfDeath = LocalDate.of(2015, 5, 1)

        when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

        when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(NormalMode).url)

        application.stop()
      }
    }

    "redirect to CY-3 liability controller" when {

      "date of death is three years ago" when {
        "current date is before 23rd Dec" in {

          val mockEstatesConnector = mock[EstatesConnector]

          val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
            .build()

          val dateOfDeath = LocalDate.of(2017, 5, 1)

          when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

          when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode).url)

          application.stop()
        }

        "current date is after 23rd Dec" in {

          val mockEstatesConnector = mock[EstatesConnector]

          val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
            .build()

          val dateOfDeath = LocalDate.of(2017, 5, 1)

          when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

          when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode).url)

          application.stop()
        }
      }
    }

    "redirect to CY-2 liability controller" when {

      "date of death is two years ago" when {
        "current date is before 23rd Dec" in {

          val mockEstatesConnector = mock[EstatesConnector]

          val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
            .build()

          val dateOfDeath = LocalDate.of(2018, 5, 1)

          when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

          when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode).url)

          application.stop()
        }

        "current date is after 23rd Dec" in {

          val mockEstatesConnector = mock[EstatesConnector]

          val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
            .build()

          val dateOfDeath = LocalDate.of(2018, 5, 1)

          when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

          when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode).url)

          application.stop()
        }
      }
    }

    "redirect to CY-1 liability controller" when {

      "date of death is one years ago" when {
        "current date is before 23rd Dec" in {

          val mockEstatesConnector = mock[EstatesConnector]

          val dateBeforeDec23rd = LocalDate.of(2020, 5, 1)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateBeforeDec23rd)))
            .build()

          val dateOfDeath = LocalDate.of(2019, 5, 1)

          when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

          when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusOneLiabilityController.onPageLoad(NormalMode).url)

          application.stop()
        }

        "current date is after 23rd Dec" in {

          val mockEstatesConnector = mock[EstatesConnector]

          val dateAfterDec23rd = LocalDate.of(2020, 12, 25)

          val application = applicationBuilder(userAnswers = None)
            .overrides(bind[EstatesConnector].toInstance(mockEstatesConnector))
            .overrides(bind[LocalDateService].toInstance(setCurrentDate(dateAfterDec23rd)))
            .build()

          val dateOfDeath = LocalDate.of(2019, 5, 1)

          when(mockEstatesConnector.getDateOfDeath()(any(), any())).thenReturn(Future.successful(dateOfDeath))

          when(sessionRepository.resetCache(any())).thenReturn(Future.successful(Some(Json.obj())))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER
          redirectLocation(result) mustBe Some(routes.CYMinusOneLiabilityController.onPageLoad(NormalMode).url)

          application.stop()
        }
      }
    }
  }
}
