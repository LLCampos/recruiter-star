import org.scalajs.dom.raw._

import scala.scalajs.js.Object.entries

case class ExperienceItem(
  title: String,
  description: String,
  duration: String
)

object ExperienceItem {
  def fromLinkedinExperienceSectionElem(elem: Element): Seq[ExperienceItem] =
    entries(elem.querySelectorAll("li")).map(tuple => tuple._2 match {
      case el: HTMLLIElement => fromExperienceListItem(el)
    }).toSeq

  private def fromExperienceListItem(elem: HTMLLIElement): ExperienceItem = {
    val summary = elem.getElementsByClassName("pv-entity__summary-info").item(0)
    val title = summary.querySelector("h3").textContent
    val description = elem.getElementsByClassName("pv-entity__description").item(0).textContent.trim()
    val employmentDuration = elem.getElementsByClassName("pv-entity__bullet-item-v2").item(0).textContent
    ExperienceItem(
      title,
      description,
      employmentDuration
    )
  }
}


object LinkedinYearsPerTech {
  def getFromLinkedinExperienceSection(elem: Element): Map[String, Int] = {
    Map()
  }
}
