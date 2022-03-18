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

import config.annotations.TaxLiability
import controllers.actions.Actions
import forms.YesNoFormProviderWithArguments
import models.{CYMinus4TaxYears, TaxYearRange}
import navigation.Navigator
import pages.CYMinusFourYesNoPage
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.CYMinusFourYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CYMinusFourLiabilityController @Inject()(
                                                val controllerComponents: MessagesControllerComponents,
                                                @TaxLiability navigator: Navigator,
                                                actions: Actions,
                                                formProvider: YesNoFormProviderWithArguments,
                                                sessionRepository: RegistrationsRepository,
                                                view: CYMinusFourYesNoView,
                                                taxYearRange: TaxYearRange
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form(ranges: Seq[String]): Form[Boolean] = formProvider.withPrefix("cyMinusFour.liability", ranges)

  private val workingTaxYear = CYMinus4TaxYears

  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>

      val f = form(Seq(taxYearRange.startDate(workingTaxYear), taxYearRange.endDate(workingTaxYear)))

      val preparedForm = request.userAnswers.get(CYMinusFourYesNoPage) match {
        case None => f
        case Some(value) => f.fill(value)
      }

      Ok(view(preparedForm, draftId, taxYearRange.toRange(workingTaxYear)))
  }

  def onSubmit(draftId: String): Action[AnyContent] = actions.authWithData(draftId).async {
    implicit request =>

      val f = form(Seq(taxYearRange.startDate(workingTaxYear), taxYearRange.endDate(workingTaxYear)))

      f.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId, taxYearRange.toRange(workingTaxYear)))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CYMinusFourYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CYMinusFourYesNoPage, draftId, updatedAnswers))
      )
  }
}
