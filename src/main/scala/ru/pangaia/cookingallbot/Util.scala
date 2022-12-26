package ru.pangaia.cookingallbot

import scala.io.Source
import scala.annotation.tailrec
import java.util.Properties
import java.io.InputStreamReader

object Util {
  def readToken(filename: String): String = {
    val src = Source.fromFile(filename)
    val token = src.getLines().next()
    src.close()
    token
  }

  def tokenSet(str: String): Set[String] = {
    str.split(" +").map(_.toLowerCase).toSet
  }

  def intersects[A](setA: Set[A], setB: Set[A]): Boolean = {
    @tailrec
    def intersects(seqA: Seq[A], seqB: Seq[A]): Boolean = {
      seqA match
        case empty if empty.isEmpty => false
        case Seq(h, t:_*) => seqB.contains(h) || intersects(t, seqB)
    }
    intersects(setA.toSeq, setB.toSeq)
  }

  def recode(string: String, cp: String): String = {
    if cp != "UTF-8" then
      val inputBytes = string.getBytes("UTF-8")
      new String(inputBytes, cp)
    else string
  }

  def parseCodepage(args: Seq[String]): String = {
    val profile = if args.isEmpty then "release" else "dev"
    val resourceName = s"$profile.properties"
    val stream = this.getClass.getClassLoader.getResourceAsStream(resourceName)
    val cp = new Properties()
    cp.load(new InputStreamReader(stream))
    stream.close()
    cp.getProperty("cp")

  }
}
