package ru.pangaia
package cookingallbot

import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.*
import com.pengrad.telegrambot.response.*
import com.pengrad.telegrambot.{TelegramBot, UpdatesListener}

import scala.collection.mutable
import scala.util.Random

import scala.jdk.CollectionConverters.*

class CookingBot(token: String, i: Index) {
  private val START = "/start"
  private val INGRED = "/ingred"
  private val SEARCH_ALL = "/all"
  private val bot = new TelegramBot.Builder(token).updateListenerSleep(15000).build()


  bot.setUpdatesListener((updates: java.util.List[Update]) => {
    for {upd <- updates.asScala}
      processSingle(upd)
    UpdatesListener.CONFIRMED_UPDATES_ALL
  })

  private def processSingle(u: Update): Unit = {
    val msg = u.message()
    if msg != null then {
      val chatId = msg.chat().id()
      val text = msg.text()
      if text.startsWith(START) then
        displayMenu(chatId)
      else if text.startsWith(INGRED) then
        searchIngred(text, chatId)
      else if text.startsWith(SEARCH_ALL) then
        searchExhaustive(text, chatId)
      else
        sendIncorrectCommand(text, chatId)
        displayMenu(chatId)
    }
  }

  private def displayMenu(chatId: Long): Unit = {
    val menuText =
      """
        |`/ingred <ингредиент>`
        |для поиска рецептов
        |с конкретным ингредиентом
        |
        |`/all <ингредиент1, ингредиент2, ...>`
        |для поиска рецептов,
        |которые можно приготовить
        |из введенных ингредиентов
        |""".stripMargin
    val req: SendMessage = new SendMessage(chatId, menuText)
    req.parseMode(ParseMode.MarkdownV2)
    val response = bot.execute(req)
  }

  private def searchIngred(text: String, chatId: Long): Unit = {
    val query = text.stripPrefix(INGRED).trim
    val tokenSet = Util.tokenSet(query)
    val result = i.searchOneKeywordInAny(tokenSet)
    val resultShuffled = Random.shuffle(result)

    for {r <- resultShuffled.take(5)}
      val req = new SendMessage(chatId, r.toMarkdown)
      req.parseMode(ParseMode.MarkdownV2)
      val res = bot.execute(req)
  }

  private def searchExhaustive(text: String, chatId: Long): Unit = {
    val query = text.stripPrefix(SEARCH_ALL).trim
    val ingredients = query.split(" *, *").toList
    val result = i.searchExhaustive(ingredients.map(Util.tokenSet))
    val resultShuffled = Random.shuffle(result)

    for {r <- resultShuffled.take(5)}
      val req = new SendMessage(chatId, r.toMarkdown)
      req.parseMode(ParseMode.MarkdownV2)
      val res = bot.execute(req)
  }

  private def sendIncorrectCommand(text: String, chatId: Long): Unit = {
    val errorText = "неизвестная команда:\n" + text + "\n"
    val req = new SendMessage(chatId, errorText)
    val response = bot.execute(req)
  }
}
