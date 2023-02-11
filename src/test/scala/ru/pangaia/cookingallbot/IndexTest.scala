package ru.pangaia
package cookingallbot

import org.scalatest.flatspec.AnyFlatSpec

class IndexTest extends AnyFlatSpec {
  val index  = new Index("test_small.csv", false)
  //
  //CONSTANTS
  //
  val keywords: List[String] = List(
    "Молоко",
    "Яичный желток",
    "Сахар",
    "Пшеничная мука",
    "Сахарная пудра",
    "Ванильный стручок",
    "Картофель",
    "Куриное яйцо",
    "Соленые огурцы",
    "Морковь",
    "Консервированный зеленый горошек",
    "Репчатый лук",
    "Майонез",
    "Свежие шампиньоны",
    "Куриное филе",
    "Филе куриного бедра",
    "Говяжий жир",
    "Репчатый лук",
    "Молотый черный перец",
    "Сметана"
  )
  val recipesInDataset1 = 3
  val keyword2 = "Куриное яйцо"
  val recipesInDataset2 = 7//+1 fillet
  val keyword2Short = "Яйцо"
  val keyWord2ShortInDataset = 6
  val keywords4searchByName = "с шампиньонами"


  def partitionKeywords(kwlist: List[String]): List[Set[String]] =
    kwlist.map(Util.tokenSet)
  

  //
  //TEST CASES
  //
  "index size for small set" should "be 14 exactly" in {
    assert(index.data.size === 14 )
  }
  "searchExhaustive" should
    "return all recipes which ingredients are subset of query keywords" in {
    val keywords = partitionKeywords(this.keywords)
    assert(index.searchExhaustive(keywords).size === recipesInDataset1 )
  }
  "searchOneKeywordInAny" should "return all recipes containing keyword ingredient" in {
    assert(index.searchOneKeywordInAny(
      Util.tokenSet(keyword2)
    ).size === recipesInDataset2)
  }
  "searchOneKeywordInAny" should "match short keywords too" in {
    assert(index.searchOneKeywordInAny(Util.tokenSet(keyword2Short)).size === keyWord2ShortInDataset)
  }
  "searchKeywordsListInAny" should "return all recipes with all ingreds mentioned" in {
    val keys = partitionKeywords(List("Куриное яйцо", "Сахар"))
    assert(keys.contains(Set("куриное","яйцо")))
    assert(keys.contains(Set("сахар")))
    val recipesInDataset3 = 4
    assert(index.searchKeywordsListInAny(keys).size === recipesInDataset3)
  }
  "searchKeywordsListInAny" should "behave same as searchOneKywordInAny in case of a single keyword" in {
    val key = keyword2Short
    assert(index.searchOneKeywordInAny(Util.tokenSet(key)).size ===
      index.searchKeywordsListInAny(List(Util.tokenSet(key))).size)
  }
  "search by name" should "find all recipes having keywords in the mealName" in {
    val key = keywords4searchByName
    assert(index.searchByRecipeName(List(Util.tokenSet(key))).size === 2)
  }
}
