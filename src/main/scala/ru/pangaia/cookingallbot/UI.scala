package ru.pangaia
package cookingallbot

import cookingallbot.UI.{greeting, prompt}

import java.nio.charset.Charset
import java.util.Scanner
import scala.annotation.tailrec
import scala.util.Random

class UI(index: Index, ingredientIndex: List[IngredientName], cp: String) {
  private val scanner = new Scanner(System.in, Charset.forName(cp))

  def loop(): Unit = {
    print(greeting(index.data.size, ingredientIndex.size))
    print(index.data(Random.nextInt(index.data.size - 1)))
    innerLoop()
  }

  @tailrec
  private def innerLoop(): Unit = {
    print(prompt)
    val query = scanner.nextLine()
    if query.nonEmpty && !query.toLowerCase.split(" +")(0).equals("3") then
      query.toLowerCase.split(" +")(0) match {
        case "1" =>
          exhaustiveSearch()
          innerLoop()
        case "2" =>
          singleIngredientSearch()
          innerLoop()
        case _ =>
          incorrectCommand()
          innerLoop()
      }
  }

  private def incorrectCommand(): Unit =
    print("Введена неизвестная комманда\n")

  private def exhaustiveSearch(): Unit = {
    print("Введите названия ингредиентов через запятую. " +
      "Я попробую найти рецепты блюд, которые можно приготовить из них. " +
      "Другие продукты не понадобятся.\n")
    val searchString = scanner.nextLine()
    val ingredients = searchString.split(" *, *").toList
    val searchResult = index.searchExhaustive(ingredients.map(Util.tokenSet))
    if searchResult.nonEmpty then {
      println(s"Найдено ${searchResult.size} рецептов")
      loadWithMore(searchResult)
    }
    else print("Ничего не найдено\n")
  }

  private def singleIngredientSearch(): Unit = {
    print("Введите названия ингредиента, я отыщу все рецепты, куда он входит.\n")
    val searchString = scanner.nextLine()
    val searchResult = index.searchOneKeywordInAny(Util.tokenSet(searchString))
    if searchResult.nonEmpty then {
      println(s"Найдено ${searchResult.size} рецептов")
      loadWithMore(searchResult)
    }
    else print("Ничего не найдено\n")
  }

  private def loadWithMore(searchResult: List[Recipe]): Unit = {
    def yn(iterator: Iterator[Recipe], i: Int, max: Int): Boolean = {
      for recipe <- iterator.slice(i * 3, (i + 1) * 3) do
        print(recipe)
      if ((i+1)*3) <= max then {
        print("Загрузить еще (д/Н)?\n")
        scanner.nextLine().toLowerCase.startsWith("д")
      } else false
    }
    val shuffled = Random.shuffle(searchResult)
    val indexMax = searchResult.size - 1
    val iterator = shuffled.iterator
    var i: Int = 0
    var answer = yn(iterator, i, indexMax)
    while answer && i * 3 <= indexMax do
      i += 1
      answer = yn(iterator, i, indexMax)
  }
}

object UI {
  def greeting(size: Int, ingreds: Int): String = "CookingAll Bot v. alpha-1\n" +
    s"В базе $size рецептов из $ingreds ингредиентов\nРецепт дня:\n"
  val prompt: String = "Основное меню:\n" +
    "1 - исчерпывающий поиск\n" +
    "2 - поиск по ингредиенту\n" +
    "3 - выход\n"
}
