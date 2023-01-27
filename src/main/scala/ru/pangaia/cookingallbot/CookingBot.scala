package ru.pangaia
package cookingallbot

import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.model.request.ParseMode
import com.pengrad.telegrambot.request.*
import com.pengrad.telegrambot.response.*
import com.pengrad.telegrambot.{TelegramBot, UpdatesListener}

import scala.collection.mutable
import scala.jdk.CollectionConverters.*
import scala.util.Random

class CookingBot(token: String, i: Index) {
  private val START = "/start"
  private val START2 = "/старт"
  private val INGRED = "/ingred"
  private val INGRED2 = "/рецепт"
  private val SEARCH_ALL = "/all"
  private val SEARCH_ALL2 = "/всеиз"
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
      if text.startsWith(START) || text.startsWith(START2) then
        displayMenu(chatId)
      else if text.startsWith(INGRED) || text.startsWith(INGRED2) then
        searchIngred(text, chatId)
      else if text.startsWith(SEARCH_ALL) || text.startsWith(SEARCH_ALL2) then
        searchExhaustive(text, chatId)
      else
        sendIncorrectCommand(text, chatId)
        displayMenu(chatId)
    }
  }

  private def displayMenu(chatId: Long): Unit = {
    val menuText =
      """
        |CookingBot версия 0.2-α
        |
        |Я ищу для вас кулинарные рецепты по составу.
        |Команды:
        |
        |`/старт` или `/start`
        |выводит это справочное сообщение.
        |
        |
        |`/рецепт` ингредиент1, ингредиент2, ...
        |или
        |`/ingred` ингредиент1, ингредиент2, ...
        |для поиска рецептов
        |с конкретными ингредиентами.
        |Я найду *до 5* рецептов,
        |в составе которых есть все эти ингредиенты.
        |
        |
        |`/всеиз` ингредиент1, ингредиент2, ...
        |или
        |`/all` ингредиент1, ингредиент2, ...
        |для поиска рецептов,
        |которые можно приготовить
        |из введенных ингредиентов.
        |Я найду *до 5* рецептов, все игредиенты которых
        |входят в приведенный список.
        |""".stripMargin
    val req: SendMessage = new SendMessage(chatId, menuText)
    req.parseMode(ParseMode.Markdown)
    val response = bot.execute(req)
  }

  private def searchIngred(text: String, chatId: Long): Unit = {
    val query = text.stripPrefix(INGRED).trim
    val tokenSet = query.split(" *, *").map(Util.tokenSet).toList
    val result = i.searchKeywordsListInAny(tokenSet)
    println(s"id: $chatId: ${result.size} recipes for '$query'")
    if result.isEmpty then
      val req = SendMessage(chatId, "Сочетание ингредиентов не найдено ни в одном из рецептов")
      val res = bot.execute(req)
    val resultShuffled = Random.shuffle(result)
    for {r: Recipe <- resultShuffled.take(5)} {
      val req: SendMessage = new SendMessage(chatId, r.toMarkdown)
      req.parseMode(ParseMode.Markdown)
      val res = bot.execute(req)
      if !res.isOk then
        println(res.errorCode())
      for {s <- r.steps.zipWithIndex} {
        val req: SendMessage = new SendMessage(chatId, s"${s._2 + 1}. ${s._1}\n")
        val res = bot.execute(req)
        if !res.isOk then
          println(res.errorCode())
      }
    }
  }

  private def searchExhaustive(text: String, chatId: Long): Unit = {
    val query = text.stripPrefix(SEARCH_ALL).trim
    val ingredients = query.split(" *, *").toList
    val result = i.searchExhaustive(ingredients.map(Util.tokenSet))
    println(s"id: $chatId: ${result.size} recipes for '$query'")
    if result.isEmpty then
      val req = SendMessage(chatId, "Ничего не найдено, попробуйте расширить список ингредиентов")
      val res = bot.execute(req)
    val resultShuffled = Random.shuffle(result)
    for {r: Recipe <- resultShuffled.take(5)} {
      val req: SendMessage = new SendMessage(chatId, r.toMarkdown)
      req.parseMode(ParseMode.Markdown)
      val res = bot.execute(req)
      if !res.isOk then
        println(res.errorCode())
      for {s <- r.steps.zipWithIndex} {
        val req: SendMessage = new SendMessage(chatId, s"${s._2+1}. ${s._1}\n")
        val res = bot.execute(req)
        if !res.isOk then
          println(res.errorCode())
      }
    }
  }

  private def sendIncorrectCommand(text: String, chatId: Long): Unit = {
    val errorText = "неизвестная команда:\n" + text + "\n"
    val req = new SendMessage(chatId, errorText)
    val response = bot.execute(req)
  }
}
