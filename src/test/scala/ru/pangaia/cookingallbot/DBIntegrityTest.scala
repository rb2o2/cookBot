package ru.pangaia.cookingallbot

import org.scalatest.flatspec.AnyFlatSpec

class DBIntegrityTest extends AnyFlatSpec {
  val fullIndex: Index = Index("data.zip", true)

  "all recipes" should "have parseable cookTimes" in {
    try {
      println(fullIndex.data.sorted(Recipe.timeOrdering.reverse)(1))
    } catch {
      case _: Exception => fail()
    }
  }

  "all recipes" should "have nonempty ingredients list" in {
    assert(!fullIndex.data.exists(_.ingredients.isEmpty))
  }

  "all recipes" should "have nonempty instruction list" in {
    assert(!fullIndex.data.exists(_.steps.isEmpty))
  }
}
