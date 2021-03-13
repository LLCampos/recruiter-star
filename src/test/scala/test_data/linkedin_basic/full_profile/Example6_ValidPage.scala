package test_data.linkedin_basic.full_profile

object Example6_ValidPage {

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
      |       <h4 class="pv-entity__date-range">
      |         <span class="visually-hidden">Dates Employed</span>
      |         <span>Jan 2009 – Apr 2012</span>
      |       </h4>
      |
      |       <div class='pv-entity__bullet-item-v2'>3 yrs 3 mos</div>
      |
      |       <div class='pv-entity__description'>
      |         I was a Python developer
      |       </div>
      |     </li>
      |   </ul>
      | </section>
      |</main>""".stripMargin
}
