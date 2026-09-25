ThisBuild / tlBaseVersion := "0.5" // current series x.y

ThisBuild / organization := "io.chrisdavenport"
ThisBuild / organizationName := "Christopher Davenport"
ThisBuild / startYear := Some(2022)
ThisBuild / licenses := Seq(License.MIT)
ThisBuild / developers := List(
  tlGitHubDev("christopherdavenport", "Christopher Davenport")
)

// sbt-davenverse published a snapshot from main on every push; preserve that.
ThisBuild / tlCiReleaseBranches := Seq()

val Scala213tl = "2.13.18"
ThisBuild / crossScalaVersions := Seq("2.12.20",  Scala213tl)

// 0.5.0 was published for 2.13 and 3 but not 2.12, whose last artifact is 0.4.0.
// Tell MiMa that 2.12 support resumes here rather than hunting a 2.12 0.5.0.
ThisBuild / tlVersionIntroduced := Map("2.12" -> "0.5.1")
ThisBuild / scalaVersion := Scala213tl

// Compiler settings DavenversePlugin injected globally. sbt-typelevel-ci-release
// does not supply these (only sbt-typelevel-settings would). Scoped to ThisBuild
// so every project picks them up without editing each one.
ThisBuild / libraryDependencies ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, _)) =>
    Seq(
      compilerPlugin("org.typelevel" % "kind-projector" % "0.13.4" cross CrossVersion.full),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )
  case _ => Nil
})
ThisBuild / scalacOptions ++= (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((3, _)) => Seq("-Ykind-projector")
  case Some((2, 12)) => Seq("-Ypartial-unification")
  case _ => Nil
})


lazy val `epimetheus-mules` = project.in(file("."))
    .enablePlugins(NoPublishPlugin)
  .aggregate(core, site)

lazy val core = project.in(file("core"))
  .settings(commonSettings)
  .settings(
    name := "epimetheus-mules"
  )

lazy val site = project.in(file("site"))
  .dependsOn(core)
    .enablePlugins(NoPublishPlugin)
  .enablePlugins(TypelevelSitePlugin)
  .settings(
    laikaTheme := tlSiteHelium.value.site
      .topNavigationBar(
        homeLink = laika.helium.config.IconLink.internal(laika.ast.Path.Root / "index.md", laika.helium.config.HeliumIcon.home)
      )
      .build
  )
  .settings(
  )

val catsV = "2.9.0"
val catsEffectV = "3.3.14"
val epimetheusV = "0.5.0"
val mulesV = "0.7.0"

val specs2V = "4.20.0"

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
