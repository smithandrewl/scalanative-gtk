organization in ThisBuild := "de.surfice"

version in ThisBuild := "0.0.1-SNAPSHOT"

scalaVersion in ThisBuild := "2.11.12"

val Version = new {
  val smacrotools = "0.0.7-SNAPSHOT"
  val slogging    = "0.5.3"
  val utest       = "0.6.3"
}


lazy val commonSettings = Seq(
  scalacOptions ++= Seq("-deprecation","-unchecked","-feature","-language:implicitConversions","-Xlint"),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "utest" % Version.utest % "test"
    ),
  testFrameworks += new TestFramework("utest.runner.Framework")
  )

lazy val nativeSettings = Seq(
  nativeLinkingOptions ++= Seq("-lglib-2.0","-lgtk-3.0"),
  nativeLinkStubs := true
)

lazy val scalanativeGtk = project.in(file("."))
  .aggregate(macros,gobj,glib,gtk3)
  .settings(commonSettings ++ dontPublish:_*)
  .settings(
    name := "scalanative-gtk"
    )


lazy val macros = project
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings ++ nativeSettings:_*)
  .settings(
    name := "scalanative-gobj-macros",
    libraryDependencies ++= Seq(
      "de.surfice" %% "smacrotools" % Version.smacrotools
    )
  )


lazy val gobj = project
  .dependsOn(macros)
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings:_*)
  .settings(
    name := "scalanative-gobj"
  )


lazy val glib = project
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings ++ nativeSettings:_*)
  .settings(
    name := "scalanative-glib"
  )


lazy val gtk3 = project
  .dependsOn(gobj)
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings:_*)
  .settings(
    name := "scalanative-gtk-gtk3"
  )


lazy val gtkTest = project
  .dependsOn(gtk3)
  .enablePlugins(ScalaNativePlugin)
  .settings(commonSettings ++ nativeSettings ++ dontPublish:_*)
  

lazy val dontPublish = Seq(
  publish := {},
  publishLocal := {},
  com.typesafe.sbt.pgp.PgpKeys.publishSigned := {},
  com.typesafe.sbt.pgp.PgpKeys.publishLocalSigned := {},
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository",file("target/unusedrepo")))
)