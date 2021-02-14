import chrome.permissions.Permission
import chrome.permissions.Permission.API
import chrome.{Background, BrowserAction, ContentScript, ExtensionManifest}
import com.alexitc.Chrome

object AppManifest {

  def generate(appName: String, appVersion: String): ExtensionManifest = {
    new ExtensionManifest {
      override val name = appName
      override val version = appVersion

      override val description = Some(
        "TO BE UPDATED" // TODO: REPLACE ME
      )
      override val icons = Chrome.icons("icons", "app.png", Set(48, 96, 128))

      // TODO: REPLACE ME, use only the minimum required permissions
      override val permissions = Set[Permission](
        API.Storage,
        API.Notifications
      )

      override val defaultLocale: Option[String] = Some("en")

      // TODO: REPLACE ME
      override val browserAction: Option[BrowserAction] =
        Some(BrowserAction(icons, Some("TO BE DEFINED - POPUP TITLE"), Some("popup.html")))

      // scripts used on all modules
      val commonScripts = List("scripts/common.js", "main-bundle.js")

      override val background = Background(
        scripts = commonScripts ::: List("scripts/background-script.js")
      )

      override val contentScripts: List[ContentScript] = List(
        ContentScript(
          matches = List(
            "https://www.linkedin.com/in/*"
          ),
          css = List("css/active-tab.css"),
          js = commonScripts ::: List("scripts/active-tab-script.js")
        )
      )

      override val webAccessibleResources = List("icons/*")
    }
  }
}
