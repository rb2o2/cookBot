package ru.pangaia.cookingallbot


import com.pengrad.telegrambot.TelegramBot
import ru.pangaia.cookingallbot.Util.*

import scala.util.Random

@main
def main(profile: String*): Unit = {
  val cp = Util.parseCodepage(profile)
  println("Hello world!")
  val index = new Index("data.zip", true)
  val ingredientIndex = index.collectIngredientTypes
  println(s"В базе ${index.data.size} рецептов из ${ingredientIndex.size} ингредиентов")

  val token = readToken("token")
  val bot = new CookingBot(token, index)
  println("press Enter to stop")

//  val ui = new UI(index, ingredientIndex, cp)
//  ui.loop()
}
