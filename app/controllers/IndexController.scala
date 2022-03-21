/*
 * Copyright 2022 HM Revenue & Customs
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
import connectors.SubmissionDraftConnector
import controllers.actions.Actions
import controllers.routes._
import handlers.ErrorHandler
import models.TaskStatus.{InProgress, TaskStatus}
import models.UserAnswers
import models.requests.OptionalDataRequest
import pages.TrustStartDatePage
import play.api.Logging
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents, Result}
import repositories.RegistrationsRepository
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.Session

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 submissionDraftConnector: SubmissionDraftConnector,
                                 actions: Actions,
                                 repository: RegistrationsRepository,
                                 errorHandler: ErrorHandler,
                                 config: FrontendAppConfig,
                                 trustsStoreService: TrustsStoreService
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

      submissionDraftConnector.getTrustStartDate(draftId) flatMap {
        case Some(date) =>

          def redirectForTaskStatus(taskStatus: TaskStatus): Future[Result] = {
            val userAnswers: UserAnswers = request.userAnswers
              .getOrElse(UserAnswers.startNewSession(draftId, request.internalId))

            userAnswers.get(TrustStartDatePage) match {
              case Some(cachedDate) if cachedDate.isEqual(date.startDate) =>
                if (taskStatus.isCompleted) {
                  logger.info(s"[Session ID: ${Session.id(hc)}] trust start date has not changed and answers previously completed, redirecting to answers")
                  Future.successful(Redirect(CheckYourAnswersController.onPageLoad(draftId)))
                } else {
                  logger.info(s"[Session ID: ${Session.id(hc)}] trust start date has not changed but answers not previously completed, continuing session")
                  redirect(draftId)
                }
              case Some(_) =>
                logger.info(s"[Session ID: ${Session.id(hc)}] trust start date has changed, starting new session")
                startNewSession(draftId, date.startDate)
              case None =>
                logger.info(s"[Session ID: ${Session.id(hc)}] no existing trust start date saved, starting new session")
                startNewSession(draftId, date.startDate)
            }
          }

          for {
            taskStatus <- trustsStoreService.getTaskStatus(draftId)
            _ <- trustsStoreService.updateTaskStatus(draftId, InProgress)
            result <- redirectForTaskStatus(taskStatus)
          } yield result
        case None =>
          logger.info(s"[Session ID: ${Session.id(hc)}] no start date available, returning to /registration-progress")
          Future.successful(Redirect(config.registrationProgressUrl(draftId)))
      }
  }

  private def redirect(draftId: String)(implicit request: OptionalDataRequest[AnyContent]): Future[Result] = {
    submissionDraftConnector.getFirstTaxYearAvailable(draftId).map {
      firstTaxYearAvailable =>

        firstTaxYearAvailable.yearsAgo match {
          case 4 if firstTaxYearAvailable.earlierYearsToDeclare =>
            Redirect(CYMinusFourEarlierYearsLiabilityController.onPageLoad(draftId))
          case 4 =>
            Redirect(CYMinusFourLiabilityController.onPageLoad(draftId))
          case 3 if firstTaxYearAvailable.earlierYearsToDeclare =>
            Redirect(CYMinusThreeEarlierYearsLiabilityController.onPageLoad(draftId))
          case 3 =>
            Redirect(CYMinusThreeLiabilityController.onPageLoad(draftId))
          case 2 =>
            Redirect(CYMinusTwoLiabilityController.onPageLoad(draftId))
          case 1 =>
            Redirect(CYMinusOneLiabilityController.onPageLoad(draftId))
          case _ =>
            InternalServerError(errorHandler.internalServerErrorTemplate)
        }
    }
  }
}
