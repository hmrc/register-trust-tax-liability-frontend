/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.actions

import models.UserAnswers
import org.mockito.ArgumentMatchers._
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import repositories.RegistrationsRepository

import scala.concurrent.{ExecutionContext, Future}

class FakeDraftIdRetrievalActionProvider(dataToReturn : Option[UserAnswers])
  extends DraftIdRetrievalActionProvider with MockitoSugar {

  private implicit val executionContext: ExecutionContext =
    scala.concurrent.ExecutionContext.Implicits.global

  private val mockedRepository: RegistrationsRepository = mock[RegistrationsRepository]

  when(mockedRepository.get(any())(any())).thenReturn(Future.successful(dataToReturn))

  override def apply(draftId : String) =
    new DraftIdDataRetrievalAction(draftId, mockedRepository, executionContext)

}




