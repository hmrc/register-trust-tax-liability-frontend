/*
 * Copyright 2024 HM Revenue & Customs
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
import config.annotations.TaxLiability
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import pages.CYMinusThreeEarlierYearsYesNoPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.RegistrationsRepository
import uk.gov.hmrc.time.TaxYear
import views.html.EarlierYearsToPayThanAskedYesNoView

import scala.concurrent.Future

class CYMinusThreeEarlierYearsLiabilityControllerSpec extends SpecBase with MockitoSugar {

  override def onwardRoute: Call = Call("GET", "/foo")

  private val taxYear: String = TaxYear.current.back(3).startYear.toString

  private lazy val cyMinusThreeEarlierYearsLiabilityControllerRoute = routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(draftId).url

  private lazy val submitRoute = routes.CYMinusThreeEarlierYearsLiabilityController.onSubmit(draftId)

  "CYMinusThreeEarlierYearsLiability Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, cyMinusThreeEarlierYearsLiabilityControllerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[EarlierYearsToPayThanAskedYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(taxYear, draftId, submitRoute)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(CYMinusThreeEarlierYearsYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, cyMinusThreeEarlierYearsLiabilityControllerRoute)

      val view = application.injector.instanceOf[EarlierYearsToPayThanAskedYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(taxYear, draftId, submitRoute)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockPlaybackRepository = mock[RegistrationsRepository]

      when(mockPlaybackRepository.set(any())(any(), any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith(classOf[TaxLiability]).toInstance(fakeNavigator))
          .build()

      val request =
        FakeRequest(POST, cyMinusThreeEarlierYearsLiabilityControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cyMinusThreeEarlierYearsLiabilityControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, cyMinusThreeEarlierYearsLiabilityControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
