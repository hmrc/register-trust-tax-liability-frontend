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

import controllers.actions.Actions
import handlers.ErrorHandler
import javax.inject.Inject
import models.requests.OptionalDataRequest
import models.{NormalMode, UserAnswers}
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.SessionRepository
import services.TaxLiabilityService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear

import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 taxLiabilityService: TaxLiabilityService,
                                 actions: Actions,
                                 repository: SessionRepository,
                                 errorHandler: ErrorHandler
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = actions.authWithSession.async {
    implicit request =>

      val userAnswers: UserAnswers = request.userAnswers
        .getOrElse(UserAnswers.startNewSession(request.internalId))

      for {
        _ <- repository.set(userAnswers)
        redirect <- redirect()
      } yield {
        redirect
      }
  }

  private def redirect()(implicit request: OptionalDataRequest[AnyContent]) : Future[Result] = {
      taxLiabilityService.getFirstYearOfTaxLiability().map { taxLiabilityYear =>
        val currentYear = TaxYear.current.startYear
        val startYear = taxLiabilityYear.firstYearAvailable.startYear

        (currentYear - startYear) match {
          case 4 if taxLiabilityYear.earlierYears => Redirect(controllers.routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode))
          case 4 => Redirect(controllers.routes.CYMinusFourLiabilityController.onPageLoad(NormalMode))
          case 3 if taxLiabilityYear.earlierYears => Redirect(controllers.routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad(NormalMode))
          case 3 => Redirect(controllers.routes.CYMinusThreeLiabilityController.onPageLoad(NormalMode))
          case 2 => Redirect(controllers.routes.CYMinusTwoLiabilityController.onPageLoad(NormalMode))
          case 1 => Redirect(controllers.routes.CYMinusOneLiabilityController.onPageLoad(NormalMode))
          case _ => InternalServerError(errorHandler.internalServerErrorTemplate)
        }
      }
  }
}
