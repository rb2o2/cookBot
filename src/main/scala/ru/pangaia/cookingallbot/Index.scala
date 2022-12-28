package ru.pangaia
package cookingallbot

import cookingallbot.Util.intersects

import scala.io.{Codec, Source}

class Index(file: String, fromZip: Boolean) {
  val data: List[Recipe] = {
    if !fromZip then
      val src = Source.fromResource(file)(Codec.UTF8)
      src.getLines().map(Recipe(_)).toList
    else
      Util.uncompress(file)
  }
  def collectIngredientTypes: List[IngredientName] = {
    data.foldRight(Set.empty)(
      (r: Recipe, acc: Set[IngredientName]) =>
        acc ++ r.ingredients.map(IngredientName(_))).toList
  }

  def searchExhaustive(keywords: List[Set[String]]): List[Recipe] = {
    def enrichKeywords(value: List[Set[String]]) = {
      value.map(_.map(_.toLowerCase)) ++ List(Set("соль"), Set("вода"))
    }
    val saltedKeywords = enrichKeywords(keywords)
    def superset(a: List[Set[String]], b: List[Set[String]]) = {
      b.toSet.subsetOf(a.toSet)
    }
    data.filter((r: Recipe) =>
      superset(saltedKeywords,
        r.ingredients.map(_.name.split(" +").map(_.toLowerCase).toSet)))
  }

  def searchOneKeywordInAny(tokens: Set[String]): List[Recipe] = {
    data.filter((r: Recipe) =>
      r.ingredients.exists((i: Ingredient) => intersects(
        Util.tokenSet(i.name), tokens)))
  }
}
