package ru.pangaia
package cookingallbot

import com.pengrad.telegrambot.model.*
import com.pengrad.telegrambot.model.request.{ParseMode, ReplyKeyboardMarkup}
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
  private val MORE = "more"
  private val MORE2 = "еще"
  private val NOT_FOUND_ANY = "Сочетание ингредиентов не найдено ни в одном из рецептов"
  private val NOT_FOUND_ALL = "Ничего не найдено, попробуйте расширить список ингредиентов"
  private val bot = new TelegramBot.Builder(token).updateListenerSleep(15000).build()
  private val searchResults: mutable.Map[Long, List[String]] = mutable.Map()
  private val userChoices: mutable.Map[Long, Map[Int, String]] = mutable.Map()


  bot.setUpdatesListener((updates: java.util.List[Update]) => {
    updates.asScala.foreach(processSingle)
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
        search(i.searchKeywordsListInAny,
          text.replaceAll(INGRED+"|"+INGRED2, ""),
          chatId,
          NOT_FOUND_ANY)
      else if text.startsWith(SEARCH_ALL) || text.startsWith(SEARCH_ALL2) then
        search(i.searchExhaustive,
          text.replaceAll(SEARCH_ALL+"|"+SEARCH_ALL2, ""),
          chatId,
          NOT_FOUND_ALL)
      else if text.matches("\\d") then
        chooseRecipe(text, chatId)
      else if text.equalsIgnoreCase(MORE) || text.equalsIgnoreCase(MORE2) then
        sendMore(chatId)
      else
        sendIncorrectCommand(text, chatId)
        displayMenu(chatId)
    }
  }

  private def displayMenu(chatId: Long): Unit = {
    val menuText =
      """
        |CookingBot версия 0.3-α
        |
        |Я ищу для вас кулинарные рецепты по составу.
        |Команды:
        |
        |1. `/старт` или `/start`
        |выводит это справочное сообщение.
        |
        |2. `/рецепт` ингредиент1, ингредиент2, ...
        |или
        |`/ingred` ингредиент1, ингредиент2, ...
        |для поиска рецептов
        |с конкретными ингредиентами.
        |Я найду рецепты,
        |в составе которых есть все эти ингредиенты.
        |Пример: `/рецепт яйцо, морковь, картофель, лук`
        |
        |3. `/всеиз` ингредиент1, ингредиент2, ...
        |или
        |`/all` ингредиент1, ингредиент2, ...
        |для поиска рецептов,
        |которые можно приготовить
        |из введенных ингредиентов.
        |Я найду рецепты, все игредиенты которых
        |входят в приведенный список.
        |Пример: `/всеиз говядина, морковь, лук, картофель, рис`
        |""".stripMargin
    val req: SendMessage = new SendMessage(chatId, menuText)
    req.parseMode(ParseMode.Markdown)
    val response = bot.execute(req)
  }
  def search(searchFunction: List[Set[String]] => List[Recipe],
             text: String,
             chatId: Long,
             notFound: String): Unit = {
    searchResults(chatId) = List()
    val query = text.trim
    val tokenSet = query.split(" *, *").map(Util.tokenSet).toList
    val result = searchFunction(tokenSet)
    println(s"id: $chatId: ${result.size} recipes for '$query'")
    if result.isEmpty then
      val req = SendMessage(chatId, notFound)
      val res = bot.execute(req)
    else
      val resultShuffled = Random.shuffle(result)
      searchResults(chatId) = resultShuffled.map(_.mealName)
      val portion = resultShuffled.take(5)
      val messTextBuffer = new StringBuilder("")
      val choices = mutable.Map[Int, String]()
      val buttons = new mutable.ListBuffer[String]()
      for ((r: Recipe, i: Int) <- portion.zipWithIndex) {
        messTextBuffer.append("" + (i + 1)).append(". ").append(r.mealName).append("\n")
        choices(i + 1) = r.mealName
        buttons += (i + 1).toString
      }
      userChoices(chatId) = choices.toMap
      searchResults(chatId) = searchResults(chatId).drop(choices.size)
      val req: SendMessage = new SendMessage(chatId, messTextBuffer.toString)
      val keyboard: ReplyKeyboardMarkup = if searchResults(chatId).nonEmpty then
        new ReplyKeyboardMarkup(
          Array(Array.from(buttons), Array("Еще")), false, true, true)
      else
        new ReplyKeyboardMarkup(
          Array(Array.from(buttons)), false, true, true)
      req.replyMarkup(keyboard)
      bot.execute(req)
  }

  private def chooseRecipe(str: String, chatId: Long): Unit = {
    val choices = userChoices(chatId)
    val choice = str.toInt
    if choice <= choices.size then
      val req = new SendMessage(chatId, i.data(userChoices(chatId)(choice)).toMarkdown)
      req.parseMode(ParseMode.Markdown)
      searchResults(chatId) = searchResults(chatId).drop(userChoices(chatId).size)
      bot.execute(req)
  }

  private def sendMore(chatId: Long): Unit = {
    val portion = searchResults(chatId).take(5)
    val messTextBuffer = new StringBuilder("")
    val choices = mutable.Map[Int, String]()
    val buttons = new mutable.ListBuffer[String]()
    for ((name: String, index: Int) <- portion.zipWithIndex) {
      messTextBuffer.append("" + (index + 1)).append(". ").append(name).append("\n")
      choices(index + 1) = i.data(name).mealName
      buttons += (index + 1).toString
    }
    searchResults(chatId) = searchResults(chatId).drop(choices.size)
    userChoices(chatId) = choices.toMap
    val req: SendMessage = new SendMessage(chatId, messTextBuffer.toString)
    val keyboard: ReplyKeyboardMarkup = if searchResults.nonEmpty then
      new ReplyKeyboardMarkup(
        Array(Array.from(buttons), Array("Еще")), false, true, true)
    else
      new ReplyKeyboardMarkup(
        Array(Array.from(buttons)), false, true, true)
    req.replyMarkup(keyboard)
    bot.execute(req)
  }

  private def sendIncorrectCommand(text: String, chatId: Long): Unit = {
    val errorText = "неизвестная команда:\n" + text + "\n"
    val req = new SendMessage(chatId, errorText)
    val response = bot.execute(req)
  }
}
