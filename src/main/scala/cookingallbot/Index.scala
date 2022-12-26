package ru.pangaia
package cookingallbot

import scala.io.Source

class Index(file: String) {
  val data: List[Recipe] = {
    val src = Source.fromFile(file)
    src.getLines().map(Recipe(_)).toList    
  }
  def collectIgredientTypes: List[IngredientName] = {
    data.foldRight(Set.empty)(
      (r: Recipe, acc: Set[IngredientName]) =>
        acc ++ r.ingredients.map(new IngredientName(_))).toList
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

  def searchOneKeywordInAny(keyword: Set[String]): List[Recipe] = {
    data.filter((r: Recipe) =>
      r.ingredients
        .map(_.name.split(" +")
          .map(_.toLowerCase).toSet)
        .contains(keyword.map(_.toLowerCase)))
  }
}
