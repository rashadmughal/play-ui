/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.play.config

import javax.inject.Inject
import play.api.Configuration

class GTMConfig @Inject()(configuration: Configuration) {
  val url: Option[String] =
    configuration.getString("gtm.container") match {
      case None => None
      case Some(container @ ("transitional" | "main")) =>
        configuration
          .getString(s"gtm.$container.url")
          .orElse(throw new RuntimeException(s"Missing configuration gtm.$container.url"))
      case _ => throw new IllegalArgumentException("gtm.container should be one of: { transitional, main }")
    }
}
