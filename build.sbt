lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """directbooks-coding-exercise""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.12.9",
    libraryDependencies ++= Seq(
      guice,
      "com.beachape" %% "enumeratum" % "1.7.3",
      "com.beachape" %% "enumeratum-play-json" % "1.5.13",
      "com.h2database" % "h2" % "2.1.214",
      "com.github.tototoshi" %% "scala-csv" % "1.3.10",
      "org.scalamock" %% "scalamock" % "5.2.0" % Test,
      "org.scalatestplus" %% "mockito-4-11" % "3.2.17.0" % "test",
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings",
      "-language:higherKinds"
    )
  )
