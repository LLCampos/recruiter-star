package test_data.linkedin_basic.full_profile

object Example7_PageAlreadyWithTechExperienceSummary {

  val example: String =
    """<main>
       <div class='profile-detail'>

        <div class="pv-oc ember-view" id="tech-experience-summary">
            <section class="pv-profile-section pv-about-section artdeco-card p5 mt4 ember-view"><header class="pv-profile-section__card-header">
                <h2 class="pv-profile-section__card-heading">Tech Experience Summary</h2>
                <!----></header>
                <p class="pv-about__summary-text mt4 t-14 ember-view">
                <div><b>Python - </b> 3 years and 3 months<br></div></p>
            </section>
         </div>

       </div>

       <section id="experience-section">
         <ul>
           <li>
             <div class='pv-entity__summary-info'>
               <h3>Developer</h3>
             </div>

             <div class='pv-entity__bullet-item-v2'>4 yrs 4 mos</div>

             <div class='pv-entity__description'>
               I was a Java developer
             </div>
           </li>
         </ul>
       </section>
      </main>""".stripMargin
}
