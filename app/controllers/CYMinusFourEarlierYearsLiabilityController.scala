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

import config.annotations.TaxLiability
import controllers.actions.Actions
import models.{CYMinus4TaxYears, TaxYearRange}
import navigation.Navigator
import pages.CYMinusFourEarlierYearsYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.EarlierYearsToPayThanAskedYesNoView

import javax.inject.Inject
import scala.concurrent.Future

class CYMinusFourEarlierYearsLiabilityController @Inject()(
                                                            val controllerComponents: MessagesControllerComponents,
                                                            @TaxLiability navigator: Navigator,
                                                            actions: Actions,
                                                            view: EarlierYearsToPayThanAskedYesNoView,
                                                            taxYearRange: TaxYearRange
                                                          ) extends FrontendBaseController with I18nSupport {

  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>

      val start = taxYearRange.yearAtStart(CYMinus4TaxYears)

      val continueUrl = routes.CYMinusFourEarlierYearsLiabilityController.onSubmit(draftId)

      Ok(view(start, draftId, continueUrl))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions.authWithData(draftId).async {
    implicit request =>
      Future.successful(Redirect(navigator.nextPage(CYMinusFourEarlierYearsYesNoPage, draftId, request.userAnswers)))
  }
}
