package test_data.linkedin_premium.experience_section

object ExampleBreakTags {

  val example: String =
    """<div id="profile-experience" class="module primary-module">
      |    <div class="module-header">
      |        <h2 class="title">Experience</h2>
      |    </div>
      |    <div class="module-body">
      |        <ul>
      |                <li class="position">
      |                    <div class="position-header">
      |                        <h4 class="searchable">
      |                            <a href="https://www.linkedin.com/recruiter/search?jobTitle=Consultor%2Bde%2BTI&amp;updateSearchHistory=true&amp;decorateHits=true&amp;decorateFacets=false&amp;doFacetCounting=true&amp;resetFacets=false&amp;doResultCaching=false&amp;forceResultFromCache=false&amp;origin=PPSJ&amp;doProjectBasedCounting=false&amp;count=10&amp;start=0">Backend Developer</a>
      |                        </h4>
      |                        <h5 class="searchable">
      |                            <a target="_blank" href="https://www.linkedin.com/recruiter/company/165591">PT Inovação - Altice Labs</a>
      |                        </h5>
      |                        <p class="date-range">February 2017 – March 2019
      |                            <span class="duration">(2 years 1 month)</span>
      |                            <span class="location">Aveiro e Região, Portugal</span>
      |                        </p>
      |                    </div>
      |                    <a target="_blank" href="https://www.linkedin.com/recruiter/company/165591">
      |                        <img class="company-logo" src="https://media-exp1.licdn.com/dms/image/C4E0BAQHZafruFUNdsA/company-logo_100_100/0/1519871061074?e=1623283200&amp;v=beta&amp;t=64340pJR6hLMX80poMm-HeyLfwwIAo4tgLAM2Weour0" width="60" height="60">
      |                        </a>
      |                        <p class="description searchable">Python<br>Java</br>JavaScript</p>
      |                    </li>
      |                        </ul>
      |                        </div>
      |                    </div>""".stripMargin
}
