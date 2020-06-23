package controllers

import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.IndexView

import scala.concurrent.ExecutionContext

class CYMinusFourEarlierYearsLiabilityController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 view: IndexView
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action {
    implicit request =>
      Ok(view())
  }
}
