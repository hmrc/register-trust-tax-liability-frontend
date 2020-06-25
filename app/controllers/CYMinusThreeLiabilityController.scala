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

import config.annotations.TaxLiability
import controllers.actions.Actions
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.CYMinusThreeYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import views.html.CYMinusThreeYesNoView

import scala.concurrent.{ExecutionContext, Future}

class CYMinusThreeLiabilityController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 @TaxLiability navigator: Navigator,
                                 actions: Actions,
                                 formProvider: YesNoFormProvider,
                                 sessionRepository: SessionRepository,
                                 view: CYMinusThreeYesNoView
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("cyMinusThree.liability")

  val fullDatePattern: String = "d MMMM yyyy"
  val taxYearStart: String = TaxYear.current.back(3).starts.toString(fullDatePattern)
  val taxYearEnd: String = TaxYear.current.back(3).finishes.toString(fullDatePattern)

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val messages = request.messages
      val taxRange = messages("taxYearRange", taxYearStart, taxYearEnd)

      val preparedForm = request.userAnswers.get(CYMinusThreeYesNoPage) match {
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
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CYMinusThreeYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CYMinusThreeYesNoPage, mode, updatedAnswers))
      )
  }
}
