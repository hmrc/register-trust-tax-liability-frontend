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

package config

import com.google.inject.AbstractModule
import config.annotations.TaxLiability
import controllers.actions._
import navigation.{Navigator, TaxLiabilityNavigator}
import repositories.{DefaultRegistrationsRepository, DefaultSessionRepository, RegistrationsRepository, SessionRepository}

class Module extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[DraftIdRetrievalActionProvider]).to(classOf[DraftIdDataRetrievalActionProviderImpl]).asEagerSingleton()

    bind(classOf[SessionRepository]).to(classOf[DefaultSessionRepository]).asEagerSingleton()
    bind(classOf[RegistrationsRepository]).to(classOf[DefaultRegistrationsRepository]).asEagerSingleton()

    bind(classOf[Navigator]).annotatedWith(classOf[TaxLiability]).to(classOf[TaxLiabilityNavigator]).asEagerSingleton()
  }
}
