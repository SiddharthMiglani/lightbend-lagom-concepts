organization in ThisBuild := "org.icx"
version in ThisBuild := "1.0.0"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.13.0"

// used for dependency injection. uses "thin cake pattern"
// more details here: https://di-in-scala.github.io/#modules
val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"

// required for testing
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test

// required for using ServerServiceCall - used for handling request headers and creating response headers
val lagomScaladslServer = "com.lightbend.lagom" %% "lagom-scaladsl-server" % "1.6.1"

lazy val `hello-lagom-using-sbt-commands` = (project in file("."))
  .aggregate(
    `micro-service-one-api`,
    `micro-service-one-impl`,

    `micro-service-two-api`,
    `micro-service-two-impl`,

    `micro-service-two-consumer-api`,
    `micro-service-two-consumer-impl`
  )

lazy val `micro-service-one-api` = (project in file("micro-service-one-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `micro-service-one-impl` = (project in file("micro-service-one-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`micro-service-one-api`)

lazy val `micro-service-two-api` = (project in file("micro-service-two-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `micro-service-two-impl` = (project in file("micro-service-two-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest,
      lagomScaladslServer
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`micro-service-two-api`)

lazy val `micro-service-two-consumer-api` = (project in file("micro-service-two-consumer-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `micro-service-two-consumer-impl` = (project in file("micro-service-two-consumer-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(
    `micro-service-two-consumer-api`,
    `micro-service-two-api`, // this service is a consumer of micro-service-two and hence added as dependency
  )

lazy val `async-comm-kafka-publisher-api` = (project in file("async-comm-kafka-publisher-api"))
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslApi
    )
  )

lazy val `async-comm-kafka-publisher-impl` = (project in file("async-comm-kafka-publisher-impl"))
  .enablePlugins(LagomScala)
  .settings(
    libraryDependencies ++= Seq(
      lagomScaladslPersistenceCassandra,
      lagomScaladslKafkaBroker,
      lagomScaladslTestKit,
      macwire,
      scalaTest
    )
  )
  .settings(lagomForkedTestSettings)
  .dependsOn(`async-comm-kafka-publisher-api`)