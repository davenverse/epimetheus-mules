ThisBuild / crossScalaVersions := Seq("2.12.15", "2.13.6")

lazy val `epimetheus-mules` = project.in(file("."))
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .aggregate(core, site)

lazy val core = project.in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "epimetheus-mules"
  )

lazy val site = project.in(file("site"))
  .dependsOn(core)
  .disablePlugins(MimaPlugin)
  .enablePlugins(NoPublishPlugin)
  .enablePlugins(DavenverseMicrositePlugin)
  .settings(
    micrositeDescription := "Metrics for Mules Caches",
  )

val catsV = "2.6.1"
val catsEffectV = "3.2.9"
val epimetheusV = "0.5.0-M2"
val mulesV = "0.5.0-M2"

val specs2V = "4.14.0"

// General Settings
lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    "org.typelevel"               %% "cats-core"                  % catsV,
    "org.typelevel"               %% "cats-effect"                % catsEffectV,
    "io.chrisdavenport"           %% "epimetheus"                 % epimetheusV,
    "io.chrisdavenport"           %% "mules"                      % mulesV,

    "org.specs2"                  %% "specs2-core"                % specs2V       % Test,
    "org.specs2"                  %% "specs2-scalacheck"          % specs2V       % Test
  )
)
