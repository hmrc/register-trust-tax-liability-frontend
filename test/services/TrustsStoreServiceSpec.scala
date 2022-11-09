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

package services

import base.SpecBase
import connectors.TrustsStoreConnector
import models.Task
import models.TaskStatus.Completed
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class TrustsStoreServiceSpec extends SpecBase {

  private val mockConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  private val featureFlagService = new TrustsStoreService(mockConnector)

  private implicit val hc: HeaderCarrier = HeaderCarrier()

  "TrustsStoreService" when {

    ".updateTaskStatus" must {
      "call trusts store connector" in {

        when(mockConnector.updateTaskStatus(any(), any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(OK, "")))

        val result = featureFlagService.updateTaskStatus(draftId, Completed)

        whenReady(result) { res =>
          res.status mustBe OK
          verify(mockConnector).updateTaskStatus(eqTo(draftId), eqTo(Completed))(any(), any())
        }
      }
    }

    ".getTaskStatus" must {
      "call trusts store connector" in {

        when(mockConnector.getTaskStatus(any())(any(), any()))
          .thenReturn(Future.successful(Task(Completed)))

        val result = featureFlagService.getTaskStatus(draftId)

        whenReady(result) { res =>
          res mustBe Completed
          verify(mockConnector).getTaskStatus(eqTo(draftId))(any(), any())
        }
      }
    }
  }
}
