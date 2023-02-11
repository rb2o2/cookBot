package ru.pangaia
package cookingallbot

import cookingallbot.Util.intersects

import scala.io.{Codec, Source}

class Index(file: String, fromZip: Boolean) {
  val data: Map[String, Recipe] = {
    if !fromZip then
      val src = Source.fromResource(file)(Codec.UTF8)
      src.getLines().map(l => {val r = Recipe(l); (r.mealName, r)}).toMap
    else
      Util.uncompress(file)
  }
  def collectIngredientTypes: List[IngredientName] = {
    data.values.foldRight(Set.empty)(
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
    data.values.toList.filter((r: Recipe) =>
      superset(saltedKeywords,
        r.ingredients.map(_.name.split(" +").map(_.toLowerCase).toSet)))
  }

  def searchOneKeywordInAny(tokens: Set[String]): List[Recipe] = {
    data.values.toList.filter((r: Recipe) =>
      r.ingredients.exists((i: Ingredient) =>
        intersects(Util.tokenSet(i.name), tokens)))
  }

  def searchKeywordsListInAny(tokens: List[Set[String]]): List[Recipe] = {
    data.values.toList.filter((r: Recipe) => tokens.forall((tk: Set[String]) =>
      r.ingredients.map((i: Ingredient) => Util.tokenSet(i.name)).exists(intersects(tk, _))))
  }
}
