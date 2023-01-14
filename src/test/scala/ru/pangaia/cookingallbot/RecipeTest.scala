package ru.pangaia
package cookingallbot

import org.scalatest.flatspec.AnyFlatSpec

import scala.io.Source
import scala.util.Random

class RecipeTest extends AnyFlatSpec {
  val rndSeed = 13367
  val fixture: String = "$Напитки$Фруктовый пунш$Европейская кухня$" +
    "4$30 минут$Абрикосовый нектар (850 мл), Маракуйя (5 штук), Клубника (125 г), Лимонад (по вкусу)$" +
    "В графине смешайте абрикосовый нектар, мякоть маракуйи и мелко нарезанную клубнику. Охладите.$" +
    "Перед подачей наполните высокие бокалы на половину абрикосовой смесью и залейте до верху лимонадом.$" +
    "$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$"

  "A fixture" should "parse into proper recipe" in {
    val recipe = Recipe(fixture)
    assert(recipe.steps.length === 2)
    assert(recipe.meals === "4")
    assert(recipe.mealName === "Фруктовый пунш")
  }

  "Datafile" should "be accessible" in {
    val src = Source.fromFile("src/data/data.csv")
    assert(src.getLines().length === 49131)
  }

  "Recipe" should "print pretty" in {
    println(Recipe(fixture))
    assert(true)
  }

  "Recipe.cookTimeOrdering" should "properly order recipes by cook time" in {
    val rec1 = Recipe("","rec1","test","test","7", "10 минут",
      List(Ingredient("Voda", "po vkusu")),
      List("vskipiatit' vodu", "ostudit'"))
    val rec2 = Recipe("","rec2","test","test","7", "2 часа",
      List(Ingredient("Voda", "po vkusu")),
      List("vskipiatit' vodu", "ostudit'"))
    val rec3 = Recipe("","rec3","test","test","7", "1 час 10 минут",
      List(Ingredient("Voda", "po vkusu")),
      List("vskipiatit' vodu", "ostudit'"))
    val rec4 = Recipe("","rec4","test","test","7", "1 час 20 минут",
      List(Ingredient("Voda", "po vkusu")),
      List("vskipiatit' vodu", "ostudit'"))
    val rec5 = Recipe("","rec5","test","test","7", "2 часа 5 минут",
      List(Ingredient("Voda", "po vkusu")),
      List("vskipiatit' vodu", "ostudit'"))
    val list = List[Recipe](rec1,rec2,rec3,rec4,rec5)
    val result = new Random(rndSeed).shuffle(list).sorted(Recipe.timeOrdering)
    assert(result(0)===rec1)
    assert(result(1)===rec3)
    assert(result(2)===rec4)
    assert(result(3)===rec2)
    assert(result(4)===rec5)
  }

  "Recipe.contentsOrdering" should "properly order recipes by contents size" in {
    val rec1 = Recipe("", "rec1", "test", "test", "3", "3 часа",
      List(
        Ingredient("Мука пшеничная", "2 ст.л."),
        Ingredient("Соль", "по вкусу")),
      List("Замешать", "Готовить в духовке 2 часа", "Пейте охлажденным")
    )
    val rec2 = Recipe("", "rec2", "test", "test", "3", "3 часа",
      List(
        Ingredient("Мука пшеничная", "2 ст.л."),
        Ingredient("Соль", "по вкусу"),
        Ingredient("Вода", "по вкусу")),
      List("Замешать", "Готовить в духовке 2 часа", "Подать")
    )
    val rec3 = Recipe("", "rec3", "test", "test", "3", "3 часа",
      List(
        Ingredient("Мука пшеничная", "2 ст.л."),
        Ingredient("Вода", "по вкусу")),
      List("Замешать", "Готовить в духовке 2 часа", "Подавать охлажденным")
    )
    val rec4 = Recipe("", "rec4", "test", "test", "3", "3 часа",
      List(
        Ingredient("Мука пшеничная", "2 ст.л."),
        Ingredient("Соль", "по вкусу"),
        Ingredient("Имбирь", "по вкусу"),
        Ingredient("Черный перец", "по вкусу"),
        Ingredient("Вода", "по вкусу"),
        Ingredient("Гвоздика", "по вкусу")),
      List("Замешать", "Готовить в духовке 2 часа")
    )
    val rec5 = Recipe("", "rec5", "test", "test", "3", "3 часа",
      List(
        Ingredient("Мука пшеничная", "2 ст.л."),
        Ingredient("Соль", "по вкусу"),
        Ingredient("Вода", "по вкусу"),
        Ingredient("Черный перец", "по вкусу"),
        Ingredient("Гвоздика", "по вкусу")),
      List("Замешать", "Готовить в духовке 2 часа")
    )
    val list = List[Recipe](rec1, rec2, rec3, rec4, rec5)
    val result = new Random(rndSeed).shuffle(list).sorted(Recipe.contentsOrdering)
    assert(result(2)===rec2)
    assert(result(3)===rec5)
    assert(result(4)===rec4)
  }

  "Recipe.complexityOrdering" should "properly order recipes by cooking steps size" in {
    val rec1 = Recipe("", "rec1", "test", "test", "2", "12 минут",
      List(
        Ingredient("Соль", "1 ч. л."),
        Ingredient("Вода", "по вкусу")),
      List("Сварить", "Посолить", "Подать"))
    val rec2 = Recipe("", "rec1", "test", "test", "2", "12 минут",
      List(
        Ingredient("Соль", "1 ч. л."),
        Ingredient("Вода", "по вкусу")),
      List("Отварить", "Посолить", "Остудить", "Подать с сыром"))
    val rec3 = Recipe("", "rec1", "test", "test", "2", "12 минут",
      List(
        Ingredient("Соль", "1 ч. л."),
        Ingredient("Вода", "по вкусу")),
      List("Сварить", "Посолить", "Подать", "Накрыть", "Съесть"))
    val rec4 = Recipe("", "rec1", "test", "test", "2", "12 минут",
      List(
        Ingredient("Соль", "1 ч. л."),
        Ingredient("Вода", "по вкусу")),
      List("Сварить"))
    val rec5 = Recipe("", "rec1", "test", "test", "2", "12 минут",
      List(
        Ingredient("Соль", "1 ч. л."),
        Ingredient("Вода", "по вкусу")),
      List("Сварить", "Посолить"))
    val list: List[Recipe] = List[Recipe](rec1, rec2, rec3, rec4, rec5)
    val result = new Random(rndSeed).shuffle(list).sorted(Recipe.complexityOrdering)
    assert(result(0) === rec4)
    assert(result(1) === rec5)
    assert(result(2) === rec1)
    assert(result(3) === rec2)
    assert(result(4) === rec3)
  }
}
