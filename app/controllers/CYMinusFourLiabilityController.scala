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

import java.time.format.DateTimeFormatter

import config.annotations.TaxLiability
import controllers.actions.Actions
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.CYMinusFourYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import views.html.CYMinusFourYesNoView

import scala.concurrent.{ExecutionContext, Future}

class CYMinusFourLiabilityController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 @TaxLiability navigator: Navigator,
                                 actions: Actions,
                                 formProvider: YesNoFormProvider,
                                 sessionRepository: SessionRepository,
                                 view: CYMinusFourYesNoView
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("cyMinusFour.liability")

  val fullDatePattern: String = "d MMMM yyyy"
  val taxYearStart: String = TaxYear.current.back(4).starts.toString(fullDatePattern)
  val taxYearEnd: String = TaxYear.current.back(4).finishes.toString(fullDatePattern)

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithData {
    implicit request =>
      val messages = request.messages
      val taxRange = messages("taxYearRange", taxYearStart, taxYearEnd)

      val preparedForm = request.userAnswers.get(CYMinusFourYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, taxRange, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithData.async {
    implicit request =>
      val messages = request.messages
      val taxRange = messages("taxYearRange", taxYearStart, taxYearEnd)

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, taxRange, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CYMinusFourYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CYMinusFourYesNoPage, mode, updatedAnswers))
      )
  }
}
