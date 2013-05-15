import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "listman"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    resolvers ++= Seq(
      Resolver.file("Local repo", file("/home/ccollier/.ivy2/local"))(Resolver.ivyStylePatterns),
      "Sedis" at "http://pk11-scratch.googlecode.com/svn/trunk"
    ),
    libraryDependencies ++= Seq(
      "com.typesafe" %% "play-plugins-redis" % "2.1-2-RC2-SNAPSHOT" changing(),
      "org.reactivemongo" %% "play2-reactivemongo" % "0.9"
  ))
}
