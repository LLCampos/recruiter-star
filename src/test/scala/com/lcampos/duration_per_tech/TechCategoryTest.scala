package com.lcampos.duration_per_tech

import com.lcampos.duration_per_tech.TechCategory._
import org.specs2.mutable.Specification

class TechCategoryTest extends Specification {

  "TechCategory" should {
    "be correctly sorted" in {
      List[TechCategory](Other, ProgrammingLanguage, TestAndQA).sorted must be equalTo
        List(ProgrammingLanguage, TestAndQA, Other)
    }
  }
}
