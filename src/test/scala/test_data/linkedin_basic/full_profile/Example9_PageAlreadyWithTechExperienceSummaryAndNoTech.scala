package test_data.linkedin_basic.full_profile

object Example9_PageAlreadyWithTechExperienceSummaryAndNoTech {

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

             <h4 class="pv-entity__date-range">
              <span class="visually-hidden">Dates Employed</span>
              <span>Dec 2009 – Nov 2013</span>
             </h4>

             <div class='pv-entity__description'>
               I was a bumba developer
             </div>
           </li>
         </ul>
       </section>
      </main>""".stripMargin
}
