package ru.pangaia
package cookingallbot

import scala.io.Source

object Util {
  def readToken(filename: String): String = {
    val src = Source.fromFile(filename)
    val token = src.getLines().next()
    src.close()
    token
  }
}
