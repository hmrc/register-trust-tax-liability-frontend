package models

import uk.gov.hmrc.time.TaxYear

case class TaxLiabilityYear(firstYearAvailable: TaxYear, earlierYears: Boolean)
