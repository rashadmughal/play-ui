/*
 * Copyright 2021 HM Revenue & Customs
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

package uk.gov.hmrc.play.views.layouts

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.i18n.{Lang, Messages}
import play.api.inject.guice.GuiceApplicationBuilder
import play.twirl.api.Html
import uk.gov.hmrc.play.views.html.layouts.{headWithTrackingConsent, trackingConsentSnippet}
import uk.gov.hmrc.play.{JsoupHelpers, MessagesSupport}

class HeadWithTrackingConsentSpec
    extends WordSpec
    with Matchers
    with GuiceOneAppPerSuite
    with JsoupHelpers
    with MessagesSupport {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
        Map(
          "play.allowGlobalApplication"             -> "true",
          "optimizely.url"                          -> "https://cdn.optimizely.com/",
          "optimizely.projectId"                    -> "1234567",
          "tracking-consent-frontend.gtm.container" -> "d",
          "assets.url"                              -> "doesnt-matter",
          "assets.version"                          -> "doesnt-matter"
        )
      )
      .build()

  "HeadWithTrackingConsent" should {
    val linkElem    = Some(Html("<script src='doesnt-matter.js'></script>"))
    val headScripts = Some(Html("<link rel='stylesheet' href='doesnt-matter.css' />"))

    "include the tracking script first" in {
      val content = headWithTrackingConsent(linkElem, headScripts)
      val scripts = content.select("script")

      scripts.get(0).attr("id")  should be("tracking-consent-script-tag")
      scripts.get(0).attr("src") should be("http://localhost:12345/tracking-consent/tracking.js")
    }

    "include the tracking consent script tag with the correct language attribute when Welsh" in {
      val welshMessages: Messages = messagesApi.preferred(Seq(Lang("cy")))

      val content = headWithTrackingConsent(linkElem, headScripts)(welshMessages)
      val scripts = content.select("script#tracking-consent-script-tag")
      scripts.first.attr("data-language") should be("cy")
    }

    "include nonce attribute for all scripts" in {
      val scripts = headWithTrackingConsent(linkElem, headScripts, nonce = Some("abcdefghij")).select("script")

      scripts.get(0).attr("nonce") should be("abcdefghij")
      scripts.get(1).attr("nonce") should be("abcdefghij")
      scripts.get(2).attr("nonce") should be("abcdefghij")
    }
  }
}
