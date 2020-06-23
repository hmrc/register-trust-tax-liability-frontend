package controllers

import connectors.EstatesConnector
import handlers.ErrorHandler
import javax.inject.Inject
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import services.TaxLiabilityService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import uk.gov.hmrc.time.TaxYear
import views.html.IndexView

import scala.concurrent.ExecutionContext

class IndexController @Inject()(
                                 val controllerComponents: MessagesControllerComponents,
                                 taxLiabilityService: TaxLiabilityService,
                                 view: IndexView,
                                 errorHandler: ErrorHandler
                               )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  def onPageLoad: Action[AnyContent] = Action.async {
    implicit request =>

      taxLiabilityService.getFirstYearOfTaxLiability().map { taxLiabilityYear =>
        val currentYear = TaxYear.current.startYear
        val startYear = taxLiabilityYear.firstYearAvailable.startYear

        (currentYear - startYear) match {
          case 4 if taxLiabilityYear.earlierYears => Redirect(controllers.routes.CYMinusFourEarlierYearsLiabilityController.onPageLoad())
          case 4 => Redirect(controllers.routes.CYMinusFourLiabilityController.onPageLoad())
          case 3 if taxLiabilityYear.earlierYears => Redirect(controllers.routes.CYMinusThreeEarlierYearsLiabilityController.onPageLoad())
          case 3 => Redirect(controllers.routes.CYMinusThreeLiabilityController.onPageLoad())
          case 2 => Redirect(controllers.routes.CYMinusTwoLiabilityController.onPageLoad())
          case 1 => Redirect(controllers.routes.CYMinusOneLiabilityController.onPageLoad())
          case _ => InternalServerError(errorHandler.internalServerErrorTemplate)

        }
      }
  }
}
