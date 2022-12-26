package ru.pangaia

import cookingallbot.*

import com.pengrad.telegrambot.{TelegramBot, UpdatesListener}
import cookingallbot.Util.*

import scala.util.Random

@main
def main(): Unit = {
  println("Hello world!")
  val index = new Index("src/data/data.csv")
  val ingredientIndex = index.collectIgredientTypes
  print(index.data(Random.nextInt(index.data.size-1)))

  val token = readToken("src/data/token")
  //------
  val bot: TelegramBot = new CookingBot(token)
  bot.setUpdatesListener(updates => {

    UpdatesListener.CONFIRMED_UPDATES_ALL
  })
  //------
}
