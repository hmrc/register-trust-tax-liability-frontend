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

import connectors.EstatesConnector
import handlers.ErrorHandler
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TaxLiabilityService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import views.html.IndexView

import scala.concurrent.ExecutionContext

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 taxLiabilityService: TaxLiabilityService,
                                 view: IndexView,
                                 errorHandler: ErrorHandler
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async {
    implicit request =>

      taxLiabilityService.getFirstYearOfTaxLiability().map { taxLiabilityYear =>
        val currentYear = TaxYear.current.startYear
        val startYear = taxLiabilityYear.firstYearAvailable.startYear

        (currentYear - startYear) match {
          case 4 if taxLiabilityYear.earlierYears => Redirect(controllers.routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad())
          case 4 => Redirect(controllers.routes.CYMinusFourLiabilityController.onPageLoad())
          case 3 if taxLiabilityYear.earlierYears => Redirect(controllers.routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad())
          case 3 => Redirect(controllers.routes.CYMinusThreeLiabilityController.onPageLoad())
          case 2 => Redirect(controllers.routes.CYMinusTwoLiabilityController.onPageLoad())
          case 1 => Redirect(controllers.routes.CYMinusOneLiabilityController.onPageLoad())
          case _ => InternalServerError(errorHandler.internalServerErrorTemplate)

        }
      }
  }
}
