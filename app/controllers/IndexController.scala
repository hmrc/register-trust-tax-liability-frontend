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

import config.FrontendAppConfig
import controllers.actions.Actions
import controllers.routes._
import handlers.ErrorHandler
import models.Status.Completed
import models.requests.OptionalDataRequest
import models.{NormalMode, UserAnswers}
import pages.{TaxLiabilityTaskStatus, TrustStartDatePage}
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import services.TaxLiabilityService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import utils.Session

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 taxLiabilityService: TaxLiabilityService,
                                 actions: Actions,
                                 repository: RegistrationsRepository,
                                 errorHandler: ErrorHandler,
                                 config: FrontendAppConfig
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def startNewSession(draftId: String, startDate: LocalDate)(implicit request: OptionalDataRequest[AnyContent]): Future[Result] = {
    val answers = UserAnswers.startNewSession(draftId, request.internalId)
      .set(TrustStartDatePage, startDate)

    for {
      newSession <- Future.fromTry(answers)
      _ <- repository.resetCache(newSession)
      _ <- repository.set(newSession)
      result <- redirect(draftId)
    } yield result
  }

  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithSession(draftId).async {
    implicit request =>

      taxLiabilityService.startDate(draftId) flatMap {
        case Some(date) =>
          val userAnswers: UserAnswers = request.userAnswers
            .getOrElse(UserAnswers.startNewSession(draftId, request.internalId))

          (userAnswers.get(TrustStartDatePage), userAnswers.get(TaxLiabilityTaskStatus)) match {
            case (Some(cachedDate), Some(Completed)) if cachedDate.isEqual(date.startDate) =>
              logger.info(s"[Session ID: ${Session.id(hc)}] trust start date has not changed and answers previously completed, redirecting to answers")
              Future.successful(Redirect(CheckYourAnswersController.onPageLoad(draftId)))
            case (Some(cachedDate), _) if cachedDate.isEqual(date.startDate) =>
              logger.info(s"[Session ID: ${Session.id(hc)}] trust start date has not changed but answers not previously completed, continuing session")
              redirect(draftId)
            case (Some(_), _) =>
              logger.info(s"[Session ID: ${Session.id(hc)}] trust start date has changed, starting new session")
              startNewSession(draftId, date.startDate)
            case (None, _) =>
              logger.info(s"[Session ID: ${Session.id(hc)}] no existing trust start date saved, starting new session")
              startNewSession(draftId, date.startDate)
          }
        case None =>
          logger.info(s"[Session ID: ${Session.id(hc)}] no start date available, returning to /registration-progress")
          Future.successful(Redirect(config.registrationProgressUrl(draftId)))
      }
  }

  private def redirect(draftId: String)(implicit request: OptionalDataRequest[AnyContent]): Future[Result] = {
    taxLiabilityService.getFirstYearOfTaxLiability(draftId).map { taxLiabilityYear =>

      val currentYear = TaxYear.current.startYear
      val startYear = taxLiabilityYear.firstYearAvailable.startYear

      val numberOfYearsToAsk = currentYear - startYear

      numberOfYearsToAsk match {
        case 4 if taxLiabilityYear.hasEarlierYearsToDeclare => Redirect(CYMinusFourEarlierYearsLiabilityController.onPageLoad(NormalMode, draftId))
        case 4 => Redirect(CYMinusFourLiabilityController.onPageLoad(NormalMode, draftId))
        case 3 if taxLiabilityYear.hasEarlierYearsToDeclare => Redirect(CYMinusThreeEarlierYearsLiabilityController.onPageLoad(NormalMode, draftId))
        case 3 => Redirect(CYMinusThreeLiabilityController.onPageLoad(NormalMode, draftId))
        case 2 => Redirect(CYMinusTwoLiabilityController.onPageLoad(NormalMode, draftId))
        case 1 => Redirect(CYMinusOneLiabilityController.onPageLoad(NormalMode, draftId))
        case _ => InternalServerError(errorHandler.internalServerErrorTemplate)
      }
    }
  }
}
