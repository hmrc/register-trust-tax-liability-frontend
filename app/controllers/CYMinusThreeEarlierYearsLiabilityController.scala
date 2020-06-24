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
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.CYMinusThreeEarlierYearsYesNoPage
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import views.html.CYMinusThreeEarlierYearsYesNoView

import scala.concurrent.{ExecutionContext, Future}

class CYMinusThreeEarlierYearsLiabilityController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 navigator: Navigator,
                                 actions: Actions,
                                 formProvider: YesNoFormProvider,
                                 sessionRepository: SessionRepository,
                                 view: CYMinusThreeEarlierYearsYesNoView
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider.withPrefix("CYMinusThree.EarlierYearsLiability")

  val taxYear: String = TaxYear.current.back(3).startYear.toString

  def onPageLoad(mode: Mode): Action[AnyContent] = actions.authWithData {
    implicit request =>

      val preparedForm = request.userAnswers.get(CYMinusThreeEarlierYearsYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, taxYear, mode))
  }

  def onSubmit(mode: Mode): Action[AnyContent] = actions.authWithData.async {
    implicit request =>

      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, taxYear, mode))),

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CYMinusThreeEarlierYearsYesNoPage, value))
            _              <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CYMinusThreeEarlierYearsYesNoPage, mode, updatedAnswers))
      )
  }
}
