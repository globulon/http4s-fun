import com.typesafe.sbt.packager.docker.ExecCmd

import scala.language.postfixOps

name := "http4s-fun"
version := "0.0.1-SNAPSHOT"

lazy val commonSettings = Seq(
  organization := "com.omd",

  scalaVersion := "2.12.8",

  scalacOptions ++= Seq("-Xmax-classfile-name", "128"),

  scalacOptions ++= Seq(
    "-deprecation", // Emit warning and location for usages of deprecated APIs.
    "-encoding", "utf-8", // Specify character encoding used by source files.
    "-explaintypes", // Explain type errors in more detail.
    "-feature", // Emit warning and location for usages of features that should be imported explicitly.
    "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
    "-language:experimental.macros", // Allow macro definition (besides implementation and application)
    "-language:higherKinds", // Allow higher-kinded types
    "-language:implicitConversions", // Allow definition of implicit functions called views
    "-unchecked", // Enable additional warnings where generated code depends on assumptions.
    "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
//    "-Xfatal-warnings", // Fail the compilation if there are any warnings.
    "-Xfuture", // Turn on future language features.
    "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
    "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
    "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
    "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
    "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
    "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
    "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
    "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
    "-Xlint:option-implicit", // Option.apply used implicit view.
    "-Xlint:package-object-classes", // Class or object defined in package object.
    "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
    "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
    "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
    "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
    "-Xlint:unsound-match", // Pattern match may not be typesafe.
    "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
    "-Ypartial-unification", // Enable partial unification in type constructor inference
    "-Ywarn-dead-code", // Warn when dead code is identified.
    "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
    "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
    "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
    "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
    "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
    "-Ywarn-numeric-widen", // Warn when numerics are widened.
    "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
    "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
    "-Ywarn-unused:locals", // Warn if a local definition is unused.
    "-Ywarn-unused:params", // Warn if a value parameter is unused.
    "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
    "-Ywarn-unused:privates", // Warn if a private member is unused.
    "-Ywarn-value-discard" // Warn when non-Unit expression results are unused.
    //  "-Xlint:strict-unsealed-patmat"
  ),

  libraryDependencies ++= Seq(
    "org.scalactic" %% "scalactic" % "3.0.5",
    "org.scalatest" %% "scalatest" % "3.0.5" % Test
  ) map (_ withSources),

  fork in Test := true,

  addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.2.4"),


  resolvers ++= Seq(
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    // Only necessary for SNAPSHOT release
    Resolver.sonatypeRepo("snapshots"))
)

lazy val appSettings = Seq(
  libraryDependencies ++= Seq("com.typesafe" % "config" % "1.3.2")
)

lazy val coverageSettings = Seq(
  coverageEnabled in publishLocal := false,
  coverageEnabled in publish := false,
  coverageMinimum := 80
  //  coverageFailOnMinimum := true
)

lazy val loggerSettings = Seq(
  libraryDependencies ++= Seq(
    "ch.qos.logback" % "logback-classic" % "1.2.3" ,
    "ch.qos.logback" % "logback-core" % "1.2.3")  map {
    _ withSources() withJavadoc()
  }
)

val http4sVersion = "0.20.0-SNAPSHOT"
val CirceVersion = "0.10.0"

lazy val catsSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel" %% "cats-core" % "1.5.0",
    "org.typelevel" %% "cats-effect" % "1.1.0",
    "co.fs2" %% "fs2-core" % "1.0.1", // For cats 1.5.0 and cats-effect 1.1.0
    "co.fs2" %% "fs2-io" % "1.0.2",
    "org.typelevel" %% "cats-mtl-core" % "0.4.0",
    "com.olegpy" %% "meow-mtl" % "0.2.0",
    "com.ironcorelabs" %% "cats-scalatest" % "2.4.0" % Test,
    "com.typesafe" % "config" % "1.3.2",
    "org.scalactic" %% "scalactic" % "3.0.5" % Test,
    "org.typelevel" %% "cats-testkit" % "1.5.0" % Test,
    "org.scalatest" %% "scalatest" % "3.0.5" % Test,
    "org.typelevel" %% "cats-laws" % "1.1.0" % Test, //or `cats-testkit` if you are using ScalaTest
    "org.typelevel" %% "cats-effect-laws" % "1.1.0" % Test,
    "org.typelevel" %% "cats-mtl-laws" % "0.4.0" % Test,
    "com.github.alexarchambault" %% "scalacheck-shapeless_1.13" % "1.1.6" % Test,
    "org.scalaz" %% "scalaz-zio" % "1.0-RC4",
    "org.scalaz" %% "scalaz-zio-interop-cats" % "1.0-RC4"
   ) map {
    _ withSources() withJavadoc()
  }
)

lazy val http4sSettings = Seq(
  libraryDependencies ++= Seq(
    "org.http4s" %% "http4s-blaze-server" % http4sVersion,
    "org.http4s" %% "http4s-blaze-client" % http4sVersion,
    "org.http4s" %% "http4s-circe" % http4sVersion,
    // Optional for auto-derivation of JSON codecs
    "io.circe" %% "circe-generic" % "0.11.1",
    // Optional for string interpolation to JSON model
    "io.circe" %% "circe-literal" % "0.11.1",
    "org.http4s" %% "http4s-dsl" % http4sVersion
  )  map {
    _ withSources() withJavadoc()
  }
)

lazy val dockerSettings = Seq(
  dockerBaseImage := "registry.gitlab.com/graboids/alpha-project/jdk11:latest",
  version in Docker := "latest",
  maintainer in Docker := "marc-Daniel Ortega <globulon@gmail.com>",
  dockerRepository := Some("registry.gitlab.com"),
  // setting the run script executable
  dockerCommands ++= Seq(
    ExecCmd("RUN",
      "chmod", "u+x",
      s"${(defaultLinuxInstallLocation in Docker).value}/bin/users"),
  ),
  dockerCmd ++= Seq(s"${(defaultLinuxInstallLocation in Docker).value}/bin/users")
)

lazy val fp = (project in file("modules/fp"))
  .settings(commonSettings ++ catsSettings ++ coverageSettings)


lazy val core = (project in file("modules/core-service"))
  .settings(commonSettings  ++ appSettings ++ catsSettings ++ coverageSettings ++ http4sSettings ++ loggerSettings).dependsOn(fp)

lazy val users = (project in file("modules/users"))
  .settings(commonSettings ++ catsSettings ++ appSettings ++ http4sSettings ++ loggerSettings ++ 
    dockerSettings ++ coverageSettings ++ (packageName in Docker := "graboids/alpha-project/users-zio"))
  .dependsOn(core, fp)
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)

lazy val root = (project in file(".")).aggregate(core, fp, users)
