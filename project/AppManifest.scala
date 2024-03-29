import chrome.permissions.Permission
import chrome.permissions.Permission.API
import chrome.{Background, BrowserAction, ContentScript, ExtensionManifest}
import com.alexitc.Chrome

object AppManifest {

  def generate(appName: String, appVersion: String): ExtensionManifest = {
    new ExtensionManifest {
      override val name: String = appName
      override val version: String = appVersion

      override val description: Option[String] = Some(
        "Enhance your sourcing experience!"
      )
      override val icons: Map[Int, String] = Chrome.icons("icons", "app.png", Set(16, 48, 96, 128))

      override val permissions: Set[Permission] = Set[Permission](
        API.Tabs,
        API.Storage
      )

      override val defaultLocale: Option[String] = Some("en")

      override val browserAction: Option[BrowserAction] =
        Some(BrowserAction(icons, Some(appName), Some("popup.html")))

      // scripts used on all modules
      val commonScripts = List("scripts/common.js", "main-bundle.js")

      override val background: Background = Background(
        scripts = commonScripts ::: List("scripts/background-script.js")
      )

      override val contentScripts: List[ContentScript] = List(
        ContentScript(
          matches = List(
            "https://www.linkedin.com/*"
          ),
          css = List("css/active-tab.css"),
          js = commonScripts ::: List("scripts/active-tab-script.js")
        )
      )

      override val webAccessibleResources = List("icons/*")
    }
  }
}
