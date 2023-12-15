lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := """directbooks-coding-exercise""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.12",
    libraryDependencies ++= Seq(
      guice,
      evolutions,
      "com.typesafe.play" %% "play-guice" % "2.8.18",
      "ch.qos.logback" % "logback-classic" % "1.4.7",
      "com.beachape" %% "enumeratum" % "1.7.3",
      "com.beachape" %% "enumeratum-play-json" % "1.7.2",
      "com.h2database" % "h2" % "2.1.214",
      "com.typesafe.slick" %% "slick" % "3.4.1",
      "org.playframework" %% "play-slick" % "6.0.0",
      "org.playframework" %% "play-slick-evolutions" % "6.0.0",
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
