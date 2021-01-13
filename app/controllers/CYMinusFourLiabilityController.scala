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
import models.{CYMinus4TaxYear, Mode, TaxYearRange}
import navigation.Navigator
import pages.CYMinusFourYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.CYMinusFourYesNoView

import scala.concurrent.{ExecutionContext, Future}

class CYMinusFourLiabilityController @Inject()(
                                                val controllerComponents: MessagesControllerComponents,
                                                @TaxLiability navigator: Navigator,
                                                actions: Actions,
                                                formProvider: YesNoFormProviderWithArguments,
                                                sessionRepository: RegistrationsRepository,
                                                view: CYMinusFourYesNoView
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def form(ranges: Seq[String]) = formProvider.withPrefix("cyMinusFour.liability", ranges)

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>

      val range = TaxYearRange(CYMinus4TaxYear)

      val f = form(Seq(range.startYear, range.endYear))

      val preparedForm = request.userAnswers.get(CYMinusFourYesNoPage) match {
        case None => f
        case Some(value) => f.fill(value)
      }

      Ok(view(preparedForm, draftId, range.toRange, mode))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions.authWithData(draftId).async {
    implicit request =>

      val range = TaxYearRange(CYMinus4TaxYear)

      val f = form(Seq(range.startYear, range.endYear))

      f.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, draftId, range.toRange, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CYMinusFourYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CYMinusFourYesNoPage, draftId, mode, updatedAnswers))
      )
  }
}
