package ru.pangaia.cookingallbot

import java.io.*
import java.nio.charset.Charset
import java.util.Properties
import java.util.zip.ZipInputStream
import scala.annotation.tailrec
import scala.io.Source

object Util {
  def readToken(filename: String): String = {
    val is = this.getClass.getClassLoader.getResourceAsStream(filename)
    val tok = new BufferedReader(new InputStreamReader(is)).readLine
    is.close()
    tok
  }

  def tokenSet(str: String): Set[String] = {
    str.split(" +").map(_.toLowerCase).toSet
  }

  def superset(a: List[Set[String]], b: List[Set[String]]): Boolean = {
    b.toSet.subsetOf(a.toSet)
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
      val inputBytes = string.getBytes(cp)
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

  def uncompress(zipFile: String): Map[String, Recipe] =
    val datafileIS = new ZipInputStream(getClass
      .getClassLoader
      .getResourceAsStream(zipFile))
    if datafileIS.getNextEntry != null then
      val reader = new BufferedReader(new InputStreamReader(datafileIS,Charset.forName("UTF-8")))
      var line: String = null
      val result = collection.mutable.Map[String, Recipe]()
      while { line = reader.readLine(); line != null} do
        if line.nonEmpty then result += {val r = Recipe(line); (r.mealName, r)}
      result.toMap
    else throw new IOException("data not found in archive")

}
