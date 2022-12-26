package ru.pangaia
package cookingallbot

import org.scalatest.flatspec.AnyFlatSpec

class UtilTest extends AnyFlatSpec {
  val set1: Set[String] = Set(
    "яйцо",
    "куриное")
  val set2: Set[String] = Set(
    "вареное",
    "куриное",
    "яйцо"
  )
  val keywords: String = "Куриное яйцо"
  val shortIngredientName: String = "Вареное куриное яйцо"
  val shortIngredientNameWithRedundancy: String = "Вареное яйцо яйцо"

  "tokenSet" should "work on short strings" in {
    val tokens: Set[String] = Util.tokenSet(shortIngredientName)
    assert(tokens.contains("вареное"))
    assert(tokens.contains("куриное"))
    assert(tokens.contains("яйцо"))
  }

  "tokenSet" should "count properly" in {
    assert(Util.tokenSet(keywords).size === 2)
    assert(Util.tokenSet(shortIngredientName).size === 3)
    assert(Util.tokenSet(shortIngredientNameWithRedundancy).size === 2)
  }

  "intersects" should "work for short sets" in {
    assert(Util.intersects(set1, set2))
  }

}
