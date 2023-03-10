ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.1"

lazy val root = (project in file("."))
  .settings(
    name := "cookingScala",
    version := "alpha-2"
//    idePackagePrefix := Some("ru.pangaia")
  )

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.15" % "test"
  ,"com.github.pengrad" % "java-telegram-bot-api" % "6.3.0"
)

assembly / assemblyMergeStrategy := {
  case PathList("module-info.class") => MergeStrategy.last
  case path if path.endsWith("/module-info.class") => MergeStrategy.last
  case x =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(x)
}
