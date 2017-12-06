name := "capsule-corp-project"

organization in ThisBuild := "com.kreactive"

version in ThisBuild := "1.0.7"

scalaVersion in ThisBuild := "2.11.11"

scalacOptions in ThisBuild ++= Seq("-deprecation")

crossScalaVersions in ThisBuild := Seq("2.11.11", "2.12.4")

lazy val lib = project in file("Lib")

lazy val play = project in file("Play") dependsOn lib aggregate lib

lazy val testkit = project in file("Testkit") dependsOn lib aggregate lib


bintrayReleaseOnPublish in ThisBuild := false

bintrayOrganization in ThisBuild := Some("kreactive")

licenses in ThisBuild := List(
  ("Apache-2.0",
    url("https://www.apache.org/licenses/LICENSE-2.0"))
)

homepage in ThisBuild := Some(url("https://github.com/kreactive"))

publishTo := Some("kreactive bintray" at "https://api.bintray.com/maven/kreactive/maven/capsulecorp")

credentials += Credentials(Path.userHome / ".sbt" / ".credentials")

publishMavenStyle := true

publishArtifact := false