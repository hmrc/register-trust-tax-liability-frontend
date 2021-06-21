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

import config.annotations.TaxLiability
import controllers.actions.Actions
import forms.YesNoFormProviderWithArguments

import javax.inject.Inject
import models.{Mode, TaxYear, TaxYearRange}
import navigation.Navigator
import pages.DidDeclareTaxToHMRCYesNoPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.DidDeclareTaxToHMRCYesNoView

import scala.concurrent.{ExecutionContext, Future}

class DidDeclareTaxToHMRCController @Inject()(
                                               val controllerComponents: MessagesControllerComponents,
                                               @TaxLiability navigator: Navigator,
                                               actions: Actions,
                                               formProvider: YesNoFormProviderWithArguments,
                                               sessionRepository: RegistrationsRepository,
                                               view: DidDeclareTaxToHMRCYesNoView,
                                               taxYearRange: TaxYearRange
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form(ranges: Seq[String]): Form[Boolean] = formProvider.withPrefix("didDeclareToHMRC", ranges)

  def onPageLoad(mode: Mode, draftId: String, taxYear: TaxYear): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>

      val f = form(Seq(taxYearRange.startYear(taxYear), taxYearRange.endYear(taxYear)))

      val preparedForm = request.userAnswers.get(DidDeclareTaxToHMRCYesNoPage(taxYear)) match {
        case None => f
        case Some(value) => f.fill(value)
      }

      Ok(view(preparedForm, draftId, taxYear, taxYearRange.toRange(taxYear), mode))
  }

  def onSubmit(mode: Mode, draftId: String, taxYear: TaxYear): Action[AnyContent] = actions.authWithData(draftId).async {
    implicit request =>

      form(Seq(taxYearRange.startYear(taxYear), taxYearRange.endYear(taxYear))).bindFromRequest().fold(
        formWithErrors => {
          Future.successful(BadRequest(view(formWithErrors, draftId, taxYear, taxYearRange.toRange(taxYear), mode)))
        },
        value => {
          val page = DidDeclareTaxToHMRCYesNoPage(taxYear)
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(page, value))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(page, draftId, mode, updatedAnswers))
        }
      )
  }

}
