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
import forms.YesNoFormProviderWithArguments
import models.{CYMinus4TaxYears, TaxYearRange}
import navigation.Navigator
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.CYMinusFourYesNoPage
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.language.LanguageUtils
import views.html.CYMinusFourYesNoView

import scala.concurrent.Future

class CYMinusFourLiabilityControllerSpec extends SpecBase with MockitoSugar {

  override def onwardRoute: Call = Call("GET", "/foo")

  private val formProvider = new YesNoFormProviderWithArguments()

  private def form(arguments: Seq[Any]) = formProvider.withPrefix("cyMinusFour.liability", arguments)

  private val languageUtils: LanguageUtils = injector.instanceOf[LanguageUtils]
  private val taxYearStart: String = new TaxYearRange(languageUtils).startDate(CYMinus4TaxYears)
  private val taxYearEnd: String = new TaxYearRange(languageUtils).endDate(CYMinus4TaxYears)

  private val taxYear: String = s"$taxYearStart to $taxYearEnd"

  private lazy val cyMinusFourLiabilityControllerRoute = routes.CYMinusFourLiabilityController.onPageLoad(draftId).url

  "CYMinusFourLiability Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val formWithArgs = form(Seq(taxYearStart, taxYearEnd))

      val request = FakeRequest(GET, cyMinusFourLiabilityControllerRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CYMinusFourYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(formWithArgs, draftId, taxYear)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(CYMinusFourYesNoPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val formWithArgs = form(Seq(taxYearStart, taxYearEnd))

      val request = FakeRequest(GET, cyMinusFourLiabilityControllerRoute)

      val view = application.injector.instanceOf[CYMinusFourYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(formWithArgs.fill(true),  draftId, taxYear)(request, messages).toString

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
        FakeRequest(POST, cyMinusFourLiabilityControllerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val formWithArgs = form(Seq(taxYearStart, taxYearEnd))

      val request =
        FakeRequest(POST, cyMinusFourLiabilityControllerRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = formWithArgs.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CYMinusFourYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId, taxYear)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, cyMinusFourLiabilityControllerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, cyMinusFourLiabilityControllerRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
