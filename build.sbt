name := """team10_project"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.38",
  javaJpa.exclude("org.hibernate.javax.persistence", "hibernate-jpa-2.0-api"),
  "org.hibernate" % "hibernate-entitymanager" % "4.3.8.Final" // replace by your jpa implementation
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator
