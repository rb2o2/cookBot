package ru.pangaia
package cookingallbot

import scala.collection.mutable.ListBuffer

case class Recipe(url: String,
                  mealName: String,
                  category: String,
                  cuisine: String,
                  meals: String,
                  cookTime: String,
                  ingredients: List[Ingredient],
                  steps: List[String]) :

  override def toString: String = {
    val sb = new StringBuilder(mealName)
    sb.append(" / ")
    sb.append(category)
    sb.append(" (")
    sb.append(cuisine)
    sb.append("), ")
    sb.append(meals)
    sb.append(" порций\n===========\n")
    ingredients.foreach { case Ingredient(m, a) =>
      sb.append(m)
      sb.append(" -- ")
      sb.append(a)
      sb.append("\n")
    }
    sb.append("===== ")
    sb.append(cookTime)
    sb.append(" =====\n")
    steps.zipWithIndex.foreach { case (instr, i) =>
      sb.append(i + 1)
      sb.append(". ")
      sb.append(instr)
      sb.append("\n")
    }
    sb.append("***\n").toString
  }
end Recipe

object Recipe :
  def apply(line: String): Recipe = {
    val tokens: Array[String] = line
      .replace(" ", " ")
      .replace("½", "1/2")
      .replace("¼", "1/4")
      .replace("¾", "3/4")
      .replace("«", "'")
      .replace("»", "'")
      .replace("®", "")
      .replace("–","-")
      .replace("—","-")
      .split("\\$")
    val url_ = tokens(0)
    val category_ = tokens(1)
    val mealName_ = tokens(2)
    val cuisine_ = tokens(3)
    val meals_ = tokens(4)
    val cookTime_ = tokens(5)
    val ingredientsArr: Array[String] = tokens(6).substring(0, tokens(6).length - 1).split("\\),")
    val ingredients_ : ListBuffer[Ingredient] = ListBuffer[Ingredient]()
    for (ing <- ingredientsArr) {
      val lastP: Int = ing.lastIndexOf("(")
      if (lastP == -1) {
        ingredients_ += Ingredient(ing.trim, "-")
      } else ingredients_ += Ingredient(ing.substring(0, lastP).trim, ing.substring(lastP + 1))
    }
    val steps_ : ListBuffer[String] = ListBuffer()
    var i: Int = 7
    while (i < tokens.length && !tokens(i).isBlank) {
      steps_.+=(tokens(i).stripPrefix(";").stripSuffix(";"))
      i+=1
    }
    new Recipe(url_, mealName_, category_, cuisine_, meals_, cookTime_, ingredients_.toList, steps_.toList)
  }

end Recipe


case class Ingredient(name: String, amount: String)

case class IngredientName(name: String)

case object IngredientName {
  def apply(i: Ingredient): IngredientName = IngredientName(i.name)
}
