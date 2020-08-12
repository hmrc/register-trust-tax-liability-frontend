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

import base.SpecBase
import config.annotations.TaxLiability
import models.NormalMode
import navigation.Navigator
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.CYMinusFourEarlierYearsYesNoPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import uk.gov.hmrc.time.TaxYear
import views.html.EarlierYearsToPayThanAskedYesNoView

import scala.concurrent.Future

class CYMinusFourEarlierYearsLiabilityControllerSpec extends SpecBase with MockitoSugar {

  override def onwardRoute = Call("GET", "/foo")

  val taxYear: String = TaxYear.current.back(4).startYear.toString

  lazy val cyMinusFourEarlierYearsLiabilityControllerRoute = routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode).url

  lazy val submitRoute = routes.CYMinusFourEarlierYearsLiabilityController.onSubmit(NormalMode)

  "CYMinusFourEarlierYearsLiability Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, cyMinusFourEarlierYearsLiabilityControllerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[EarlierYearsToPayThanAskedYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(taxYear, NormalMode, submitRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(CYMinusFourEarlierYearsYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, cyMinusFourEarlierYearsLiabilityControllerRoute)

      val view = application.injector.instanceOf[EarlierYearsToPayThanAskedYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(taxYear, NormalMode, submitRoute)(fakeRequest, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockPlaybackRepository = mock[SessionRepository]

      when(mockPlaybackRepository.set(any())) thenReturn Future.successful(true)

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[Navigator].qualifiedWith(classOf[TaxLiability]).toInstance(fakeNavigator))
          .build()

      val request =
        FakeRequest(POST, cyMinusFourEarlierYearsLiabilityControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cyMinusFourEarlierYearsLiabilityControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, cyMinusFourEarlierYearsLiabilityControllerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
