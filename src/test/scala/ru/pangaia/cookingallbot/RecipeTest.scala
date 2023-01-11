package ru.pangaia
package cookingallbot

import org.scalatest.flatspec.AnyFlatSpec

import scala.io.Source

class RecipeTest extends AnyFlatSpec {
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
    assert(src.getLines().length === 49134)
  }

  "Recipe" should "print pretty" in {
    println(Recipe(fixture))
    assert(true)
  }
}
