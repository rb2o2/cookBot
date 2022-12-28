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
    assert(index.searchOneKeywordInAny(Util.tokenSet(keyword2Short)).size === 6)
  }

}
