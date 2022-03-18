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

import com.google.inject.Inject
import config.FrontendAppConfig
import controllers.actions.Actions
import models.TaskStatus.Completed
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.TrustsStoreService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import views.html.CheckYourAnswersView

import scala.concurrent.ExecutionContext.Implicits._

class CheckYourAnswersController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: CheckYourAnswersView,
                                            checkYourAnswersHelper: CheckYourAnswersHelper,
                                            actions: Actions,
                                            registrationsRepository: RegistrationsRepository,
                                            val appConfig: FrontendAppConfig,
                                            trustsStoreService: TrustsStoreService
                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>

      Ok(view(checkYourAnswersHelper(request.userAnswers), draftId))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions.authWithData(draftId).async {
    implicit request =>

      for {
        _ <- trustsStoreService.updateTaskStatus(draftId, Completed)
        _ <- registrationsRepository.set(request.userAnswers)
      } yield {
        Redirect(appConfig.registrationProgressUrl(draftId))
      }
  }
}
