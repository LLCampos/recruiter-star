import com.alexitc.ChromeSbtPlugin

lazy val appName = "RecruiterStar"
lazy val isProductionBuild = sys.env.getOrElse("PROD", "false") == "true"

val circeVersion = "0.13.0"
val catsVersion = "2.3.0"

lazy val baseSettings: Project => Project = {
  _.enablePlugins(ScalaJSPlugin)
    .settings(
      name := appName,
      version := "1.1.2",
      scalaVersion := "2.13.3",
      scalacOptions ++= Seq(
        "-language:implicitConversions",
        "-language:existentials",
        "-Xlint",
        "-deprecation", // Emit warning and location for usages of deprecated APIs.
        "-encoding",
        "utf-8", // Specify character encoding used by source files.
        "-explaintypes", // Explain type errors in more detail.
        "-feature", // Emit warning and location for usages of features that should be imported explicitly.
        "-unchecked" // Enable additional warnings where generated code depends on assumptions.
      ),
      scalacOptions += "-Ymacro-annotations",
      scalacOptions in Test ++= Seq("-Yrangepos"),
      requireJsDomEnv in Test := true
    )
}

lazy val bundlerSettings: Project => Project = {
  _.enablePlugins(ScalaJSBundlerPlugin)
    .settings(
      // NOTE: source maps are disabled to avoid a file not found error which occurs when using the current
      // webpack settings.
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withSourceMap(false),
      version in webpack := "4.8.1",
      // Having this makes test fail for some unknown reason...See https://github.com/AlexITC/chrome-scalajs-template/issues/21
//      webpackConfigFile := {
//        val file = if (isProductionBuild) "production.webpack.config.js" else "dev.webpack.config.js"
//        Some(baseDirectory.value / file)
//      },
      // scala-js-chrome
      scalaJSLinkerConfig := scalaJSLinkerConfig.value.withRelativizeSourceMapBase(
        Some((Compile / fastOptJS / artifactPath).value.toURI)
      ),
      skip in packageJSDependencies := false,
      webpackBundlingMode := BundlingMode.Application,
      fastOptJsLib := (webpack in (Compile, fastOptJS)).value.head,
      fullOptJsLib := (webpack in (Compile, fullOptJS)).value.head,
      webpackBundlingMode := BundlingMode.LibraryAndApplication(),
      // you can customize and have a static output name for lib and dependencies
      // instead of having the default files names like extension-fastopt.js, ...
      artifactPath in (Compile, fastOptJS) := {
        (crossTarget in (Compile, fastOptJS)).value / "main.js"
      },
      artifactPath in (Compile, fullOptJS) := {
        (crossTarget in (Compile, fullOptJS)).value / "main.js"
      }
    )
}

lazy val buildInfoSettings: Project => Project = {
  _.enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoPackage := "com.lcampos",
      buildInfoKeys := Seq[BuildInfoKey](name),
      buildInfoKeys ++= Seq[BuildInfoKey](
        "production" -> isProductionBuild
      ),
      buildInfoUsePackageAsPath := true
    )
}

lazy val root = (project in file("."))
  .enablePlugins(ChromeSbtPlugin, ScalablyTypedConverterPlugin)
  .configure(baseSettings, bundlerSettings, buildInfoSettings)
  .settings(
    chromeManifest := AppManifest.generate(appName, Keys.version.value),
    // js dependencies, adding typescript type definitions gets them a Scala facade
    Compile / npmDependencies ++= Seq(),
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "1.1.0",
      "com.alexitc" %%% "scala-js-chrome" % "0.7.0",

      "io.circe" %%% "circe-core" % circeVersion,
      "io.circe" %%% "circe-generic" % circeVersion,
      "io.circe" %%% "circe-parser" % circeVersion,

      "org.typelevel" %%% "cats-core" % catsVersion,

      "io.github.cquiroz" %%% "scala-java-time" % "2.2.0",

      "com.softwaremill.retry" %%% "retry" % "0.3.3",

      "org.specs2" %%% "specs2-core" % "4.10.0" % "test"
    )
  )

Global / onChangedBuildSource := ReloadOnSourceChanges
