name := "capsule-corp"

description := "Lib de base pour scala, notamment pour la sérialisation JSON avec play-json et la configuration avec kxbmap.configs."

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % Version.playJson,
  "com.github.kxbmap" %% "configs" % Version.configs,

  "org.scalatest" %% "scalatest" % Version.scalaTest % "test"
)