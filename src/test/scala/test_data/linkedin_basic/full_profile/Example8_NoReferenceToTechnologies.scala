package test_data.linkedin_basic.full_profile

object Example8_NoReferenceToTechnologies {

  val example: String =
    """<main>
      | <div class='profile-detail'>
      |
      | </div>
      |
      |
      | <section id="experience-section">
      |   <ul>
      |     <li>
      |       <div class='pv-entity__summary-info'>
      |         <h3>Developer</h3>
      |       </div>
      |
      |       <div class='pv-entity__bullet-item-v2'>3 yrs 3 mos</div>
      |
      |       <h4 class="pv-entity__date-range">
      |         <span class="visually-hidden">Dates Employed</span>
      |         <span>Dec 2009 – Nov 2013</span>
      |       </h4>
      |
      |       <div class='pv-entity__description'>
      |         I was a bla developer
      |       </div>
      |     </li>
      |   </ul>
      | </section>
      |</main>""".stripMargin
}
