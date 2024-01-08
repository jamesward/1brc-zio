name := "1brc-zio"

scalaVersion := "3.3.1"

libraryDependencies ++= Seq(
  "dev.zio" %% "zio"          % "2.1-RC1",
  "dev.zio" %% "zio-streams"  % "2.1-RC1",
  "dev.zio" %% "zio-direct"   % "1.0.0-RC7",
)

