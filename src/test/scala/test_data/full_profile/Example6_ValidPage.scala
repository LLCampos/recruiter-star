package test_data.full_profile

object Example6_ValidPage {

  val example: String =
    """<main id="main">
      | <div class='profile-detail'>
      |
      | </div>
      |
      | <div class='pv-about-section'>
      |   <h2>Title</h2>
      |   <p>
      |     <span></span>
      |     <span></span>
      |     <span></span>
      |   </p>
      | </div>
      |
      | <section id="experience-section">
      |   <ul>
      |     <li>
      |       <div class='pv-entity__summary-info'>
      |         <h3>Developer</h3>
      |       </div>
      |
      |       <div class='pv-entity__bullet-item-v2'>
      |         3 yrs 3 mos
      |       </div>
      |
      |       <div class='pv-entity__description'>
      |         I was a Python developer
      |       </div>
      |     </li>
      |   </ul>
      | </section>
      |</main>""".stripMargin
}
