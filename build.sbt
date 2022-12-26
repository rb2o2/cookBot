ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "cookingScala",
    idePackagePrefix := Some("ru.pangaia")
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.14" % "test"
//  ,"com.bot4s" % "telegram-core_2.13" % "5.6.1"
  ,"com.github.pengrad" % "java-telegram-bot-api" % "6.3.0"
)
