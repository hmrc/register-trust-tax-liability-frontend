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
 import forms.YesNoFormProviderWithArguments
 import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear, NormalMode, TaxYear, TaxYearRange}
 import navigation.Navigator
 import org.mockito.Matchers.any
 import org.mockito.Mockito.when
 import org.scalatestplus.mockito.MockitoSugar
 import pages.DidDeclareTaxToHMRCYesNoPage
 import play.api.inject.bind
 import play.api.mvc.Call
 import play.api.test.FakeRequest
 import play.api.test.Helpers._
 import repositories.SessionRepository
 import views.html.DidDeclareTaxToHMRCYesNoView

 import scala.concurrent.Future

class DidDeclareTaxToHMRCControllerSpec extends SpecBase with MockitoSugar {

  override def onwardRoute = Call("GET", "/foo")

  val formProvider = new YesNoFormProviderWithArguments()
  def form(arguments: Seq[Any]) = formProvider.withPrefix("didDeclareToHMRC", arguments)

  def didDeclareRoute(year: TaxYear) = routes.DidDeclareTaxToHMRCController.onPageLoad(NormalMode, year).url

  "DidDeclareTaxToHMRC Controller" when {

    "for previous tax year" must {
      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus1TaxYear))

        val formWithArgs = form(Seq("6 April 2019", "5 April 2020"))

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithArgs, CYMinus1TaxYear, TaxYearRange(CYMinus1TaxYear).toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val userAnswers = emptyUserAnswers.set(DidDeclareTaxToHMRCYesNoPage(CYMinus1TaxYear), true).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val formWithArgs = form(Seq("6 April 2019", "5 April 2020"))

        val request = FakeRequest(GET, didDeclareRoute(CYMinus1TaxYear))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithArgs.fill(true), CYMinus1TaxYear, TaxYearRange(CYMinus1TaxYear).toRange, NormalMode)(fakeRequest, messages).toString

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
          FakeRequest(POST, didDeclareRoute(CYMinus1TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus1TaxYear))
            .withFormUrlEncodedBody(("value", ""))

        val formWithArgs = form(Seq("6 April 2019", "5 April 2020"))

        val boundForm = formWithArgs.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, CYMinus1TaxYear, TaxYearRange(CYMinus1TaxYear).toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus1TaxYear))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus1TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "for current tax year minus 2" must {
      "return OK and the correct view for a GET" in {

        val range = TaxYearRange(CYMinus2TaxYear)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus2TaxYear))

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val formWithArgs = form(Seq("6 April 2018", "5 April 2019"))

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithArgs, CYMinus2TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val range = TaxYearRange(CYMinus2TaxYear)

        val userAnswers = emptyUserAnswers.set(DidDeclareTaxToHMRCYesNoPage(CYMinus2TaxYear), true).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus2TaxYear))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        status(result) mustEqual OK

        val formWithArgs = form(Seq("6 April 2018", "5 April 2019"))

        contentAsString(result) mustEqual
          view(formWithArgs.fill(true), CYMinus2TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

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
          FakeRequest(POST, didDeclareRoute(CYMinus2TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val range = TaxYearRange(CYMinus2TaxYear)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus2TaxYear))
            .withFormUrlEncodedBody(("value", ""))

        val formWithArgs = form(Seq("6 April 2018", "5 April 2019"))

        val boundForm = formWithArgs.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, CYMinus2TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus2TaxYear))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus2TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "for current tax year minus 3" must {
      "return OK and the correct view for a GET" in {

        val range = TaxYearRange(CYMinus3TaxYear)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus3TaxYear))

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val formWithArgs = form(Seq("6 April 2017", "5 April 2018"))

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithArgs, CYMinus3TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val range = TaxYearRange(CYMinus3TaxYear)

        val userAnswers = emptyUserAnswers.set(DidDeclareTaxToHMRCYesNoPage(CYMinus3TaxYear), true).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus3TaxYear))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        val formWithArgs = form(Seq("6 April 2017", "5 April 2018"))

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithArgs.fill(true), CYMinus3TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

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
          FakeRequest(POST, didDeclareRoute(CYMinus3TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val range = TaxYearRange(CYMinus3TaxYear)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus3TaxYear))
            .withFormUrlEncodedBody(("value", ""))

        val formWithArgs = form(Seq("6 April 2017", "5 April 2018"))

        val boundForm = formWithArgs.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, CYMinus3TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus3TaxYear))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus3TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "for current tax year minus 4" must {
      "return OK and the correct view for a GET" in {

        val range = TaxYearRange(CYMinus4TaxYear)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus4TaxYear))

        val result = route(application, request).value

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val formWithArgs = form(Seq("6 April 2016", "5 April 2017"))

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithArgs, CYMinus4TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "populate the view correctly on a GET when the question has previously been answered" in {

        val range = TaxYearRange(CYMinus4TaxYear)

        val userAnswers = emptyUserAnswers.set(DidDeclareTaxToHMRCYesNoPage(CYMinus4TaxYear), true).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus4TaxYear))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        val formWithArgs = form(Seq("6 April 2016", "5 April 2017"))

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(formWithArgs.fill(true), CYMinus4TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

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
          FakeRequest(POST, didDeclareRoute(CYMinus4TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val range = TaxYearRange(CYMinus4TaxYear)

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus4TaxYear))
            .withFormUrlEncodedBody(("value", ""))

        val formWithArgs = form(Seq("6 April 2016", "5 April 2017"))

        val boundForm = formWithArgs.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[DidDeclareTaxToHMRCYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, CYMinus4TaxYear, range.toRange, NormalMode)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, didDeclareRoute(CYMinus4TaxYear))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, didDeclareRoute(CYMinus4TaxYear))
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

  }
}
