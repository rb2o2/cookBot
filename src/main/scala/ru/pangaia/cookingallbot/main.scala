package ru.pangaia.cookingallbot


import com.pengrad.telegrambot.TelegramBot
import Util.*

import scala.util.Random

@main
def main(profile: String*): Unit = {
  val cp = Util.parseCodepage(profile)
  println("Hello world!")
  val index = new Index("data.zip", true)
  val ingredientIndex = index.collectIngredientTypes

  val token = readToken("token")
  val bot = new CookingBot(token, index)
  new CookingBot(token, index)

//  val ui = new UI(index, ingredientIndex, cp)
//  ui.loop()
}
