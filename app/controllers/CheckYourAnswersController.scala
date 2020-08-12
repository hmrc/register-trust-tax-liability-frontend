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

import com.google.inject.Inject
import config.FrontendAppConfig
import connectors.EstatesStoreConnector
import controllers.actions.Actions
import models.{CYMinus1TaxYear, CYMinus2TaxYear, CYMinus3TaxYear, CYMinus4TaxYear}
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TaxLiabilityService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import views.html.CheckYourAnswersView

import scala.concurrent.ExecutionContext.Implicits._

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            checkYourAnswersHelper: CheckYourAnswersHelper,
                                            actions: Actions,
                                            estatesService: TaxLiabilityService,
                                            estatesStoreConnector: EstatesStoreConnector,
                                            val appConfig : FrontendAppConfig
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(): Action[AnyContent] = actions.authWithData {
    implicit request =>
      val taxFor4Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(request.userAnswers, CYMinus4TaxYear)
      val taxFor3Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(request.userAnswers, CYMinus3TaxYear)
      val taxFor2Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(request.userAnswers, CYMinus2TaxYear)
      val taxFor1Years = checkYourAnswersHelper.cyMinusTaxYearAnswers(request.userAnswers, CYMinus1TaxYear)

      val sections = Seq(
        taxFor4Years,
        taxFor3Years,
        taxFor2Years,
        taxFor1Years
      ).flatten

      Ok(view(sections))
  }

  def onSubmit(): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      for {
        _ <- estatesService.submitTaxLiability(request.userAnswers)
        _ <- estatesStoreConnector.setTaskComplete()
      } yield {
        Redirect(appConfig.registerEstateHubOverview)
      }
  }
}
