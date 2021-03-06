// import de.johoop.findbugs4sbt.FindBugs.findbugsSettings
// import de.johoop.findbugs4sbt.ReportType.FancyHtml

import sbtunidoc.Plugin.UnidocKeys.unidoc

// Main settings

version in ThisBuild := "2.0.0-SNAPSHOT"

organization in ThisBuild := "com.googlecode.kiama"

// Scala compiler settings

scalaVersion in ThisBuild := "2.11.6"

crossScalaVersions := Seq ("2.11.6", "2.10.5")

scalacOptions in ThisBuild :=
    Seq (
        "-deprecation",
        "-feature",
        "-sourcepath", baseDirectory.value.getAbsolutePath,
        "-unchecked",
        "-Xfatal-warnings",
        "-Xlint",
        "-Xcheckinit"
    )

// Dependency resolution

resolvers in ThisBuild ++= Seq (
    Resolver.sonatypeRepo ("releases"),
    Resolver.sonatypeRepo ("snapshots")
)

// Dependencies

libraryDependencies in ThisBuild ++= {
    val dsinfoVersion =
        if (scalaVersion.value.startsWith ("2.10"))
            "0.3.0"
        else
            "0.4.0"
    val dsprofileVersion =
        if (scalaVersion.value.startsWith ("2.10"))
            "0.3.0"
        else
            "0.4.0"
    val parserCombinatorLibrary =
        if (scalaVersion.value.startsWith ("2.10"))
            Seq ()
        else
            Seq ("org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.3")
    Seq (
        // Caching:
        "com.google.code.findbugs" % "jsr305" % "3.0.0",
        "com.google.guava" % "guava" % "18.0",
        // DSL support:
        "org.bitbucket.inkytonik.dsinfo" %% "dsinfo" % dsinfoVersion,
        // Profiling:
        "org.bitbucket.inkytonik.dsprofile" %% "dsprofile" % dsprofileVersion,
        // Command-line handling:
        "org.rogach" %% "scallop" % "0.9.5",
        // Reflection
        "org.scala-lang" % "scala-reflect" % scalaVersion.value,
        // REPLs:
        "jline" % "jline" % "2.12",
        // Testing:
        "org.scalacheck" %% "scalacheck" % "1.12.2" % "test",
        "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    ) ++
        // Parsing:
        parserCombinatorLibrary
}

// Turn on incremental compilation based on name hashing

incOptions := incOptions.value.withNameHashing (true)

// Interactive settings

logLevel in ThisBuild := Level.Info

shellPrompt in ThisBuild := {
    state =>
        Project.extract (state).currentRef.project + " " + version.value +
            " " + scalaVersion.value + "> "
}

// No main class since Kiama is a library

mainClass in ThisBuild := None

// unidoc

scalacOptions in (ScalaUnidoc, unidoc) ++= {
    val macroExpandOption =
        if (scalaVersion.value.startsWith ("2.10"))
            "-Ymacro-no-expand"
        else
            "-Ymacro-expand:none"
    Seq (
        macroExpandOption,
        "-doc-source-url",
            "https://code.google.com/p/kiama/source/browse€{FILE_PATH}.scala"
    )
}

scalacOptions in (TestScalaUnidoc, unidoc) := (scalacOptions in (ScalaUnidoc, unidoc)).value

// Publishing

publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (version.value.trim.endsWith ("SNAPSHOT"))
        Some ("snapshots" at nexus + "content/repositories/snapshots")
    else
        Some ("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := true

pomIncludeRepository := { x => false }

pomExtra := (
    <url>http://kiama.googlecode.com</url>
    <licenses>
        <license>
            <name>LGPL 3.0 license</name>
            <url>http://www.opensource.org/licenses/lgpl-3.0.html</url>
            <distribution>repo</distribution>
        </license>
    </licenses>
    <scm>
        <url>https://kiama.googlecode.com/hg</url>
        <connection>scm:hg:https://kiama.googlecode.com/hg</connection>
    </scm>
    <developers>
        <developer>
           <id>inkytonik</id>
           <name>Tony Sloane</name>
           <url>https://code.google.com/u/inkytonik</url>
        </developer>
    </developers>
)

// scalastyle

// org.scalastyle.sbt.ScalastylePlugin.Settings

// org.scalastyle.sbt.PluginKeys.config := baseDirectory.value / "etc" / "scalastyle-config.xml"

// findbugs4sbt: commented out by default since it brings in many dependencies
// and is used rarely. See also imports at top of file.

// seq (findbugsSettings : _*)
//
// // The next two settings ensure that the test classes are included,
// // otherwise it just check the main library files.  There appears to
// // be no way to add more than one path to be analyzed.
// findbugsAnalyzedPath <<= target { identity[File] }
//
// // Normally it just depends on compile in Compile so changes in the
// // test files do not trigger a rebuild before the findbugs task runs
// findbugsPathSettings <<= findbugsPathSettings.dependsOn (compile in Test)
//
// findbugsReportType := FancyHtml
//
// findbugsReportName := "findbugsReport.html"
//
// findbugsExcludeFilters := Some (
//     <FindBugsFilter>
//         <!-- For a private final foo = exp construct where exp is constant, scalac
//              seems to inline the constant into the foo() accessor, but also leaves
//              the private foo field, which is now not used -->
//         <Match>
//             <Class name="org.kiama.example.obr.RISCEncoder$" />
//             <Bug pattern="UUF_UNUSED_FIELD" />
//             <Or>
//                 <Field name="firsttemp" />
//                 <Field name="lasttemp" />
//                 <Field name="memreg" />
//             </Or>
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.obr.SPARCEncoder" />
//             <Bug pattern="UUF_UNUSED_FIELD" />
//             <Or>
//                 <Field name="arg1reg" />
//                 <Field name="arg2reg" />
//                 <Field name="memreg" />
//                 <Field name="resreg" />
//             </Or>
//         </Match>
//         <!-- Places where a method is added to an anonymous class.  Findbugs thinks
//              it can't be called, but it can. -->
//         <Match>
//             <Class name="~org\.kiama\.example\.iswim\.tests\.SECDTests\$\$anonfun\$[1-8]\$\$anon\$[1-8]" />
//             <Method name="verify" />
//             <Bug pattern="UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.iswim.secd.HeapOps$RefValue$$anon$1" />
//             <Method name="toDoc" />
//             <Bug pattern="UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS" />
//         </Match>
//         <!-- Places where a pattern match ensures that a value is null or not null,
//              but then subsequent code checks it again. -->
//         <Match>
//             <Class name="org.kiama.example.iswim.driver.Main$" />
//             <Method name="processArgs$1" />
//             <Bug pattern="RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.iswim.compiler.Parser$class" />
//             <Method name="stripComments$1" />
//             <Bug pattern="RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.picojava.TypeAnalysis$$anonfun$5$$anonfun$apply$3" />
//             <Method name="apply" />
//             <Bug pattern="RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE" />
//         </Match>
//         <!-- Case classes are implemented using a static MODULE$ link to the actual
//              class.  Findbugs complains that this field is written. -->
//         <Match>
//             <Field name="MODULE$" />
//             <Bug pattern="ST_WRITE_TO_STATIC_FROM_INSTANCE_METHOD" />
//         </Match>
//         <!-- These seem to be due to a pattern matching code fragment which first tests
//              the value for null, and if that's true, jumps to a block to produce a match
//              error.  That block loads the null value.  Seems to be (mostly) for cases
//              where the pattern match is on the LHS off assignment or in comprehension.
//              FIXME: We assume that all of these bugs are due to this reason, but haven't
//              checked every case. -->
//         <Match>
//             <Method name="apply" />
//             <Bug pattern="NP_LOAD_OF_KNOWN_NULL_VALUE" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.obr.RISCTransformation$" />
//             <Method name="org$kiama$example$obr$RISCTransformation$$cblock" />
//             <Bug pattern="NP_LOAD_OF_KNOWN_NULL_VALUE" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.obr.tests.ObrExecTests" />
//             <Method name="org$kiama$example$obr$tests$ObrExecTests$$exectest" />
//             <Bug pattern="NP_LOAD_OF_KNOWN_NULL_VALUE" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.til.TIL2_3$class" />
//             <Method name="transform" />
//             <Bug pattern="NP_LOAD_OF_KNOWN_NULL_VALUE" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.util.PrettyPrinterBase$class" />
//             <Method name="string" />
//             <Bug pattern="NP_LOAD_OF_KNOWN_NULL_VALUE" />
//         </Match>
//         <!-- A possible null value can reach a dereference, but for now we are ok with
//              it since null checks are not present in much of the code anyway. -->
//         <Match>
//             <Class name="org.kiama.example.obr.SymbolTable$Variable" />
//             <Method name="&lt;init&gt;" />
//             <Bug pattern="NP_NULL_ON_SOME_PATH_MIGHT_BE_INFEASIBLE" />
//         </Match>
//         <!-- Findbugs doesn't like the classes that Scala produces whose names are
//              something starting with an upper case letter then $something,
//              complaining that it doesn't start with an upper case letter... -->
//         <Match>
//             <Or>
//                 <Class name="~.*\$class" />
//                 <Class name="org.kiama.example.lambda2.Evaluator$freshvar$" />
//                 <Class name="org.kiama.example.picojava.benchmark.PicoJavaBenchmark$delayedInit$body" />
//             </Or>
//             <Bug pattern="NM_CLASS_NAMING_CONVENTION" />
//         </Match>
//         <!-- These ones actually *do* start with a lower case letter, but that is
//              OK due to their role to implement the package object. -->
//         <Match>
//             <Or>
//                 <Class name="org.kiama.package" />
//                 <Class name="org.kiama.package$" />
//             </Or>
//             <Bug pattern="NM_CLASS_NAMING_CONVENTION" />
//         </Match>
//         <!-- These classes have "Exception" in their name for a good reason... -->
//         <Match>
//             <Or>
//                 <Class name="org.kiama.example.iswim.secd.SECDBase$MkUserException" />
//                 <Class name="org.kiama.example.iswim.secd.SECDBase$PushMachineException" />
//                 <Class name="org.kiama.example.iswim.secd.SECDBase$RaiseException" />
//             </Or>
//             <Bug pattern="NM_CLASS_NOT_EXCEPTION" />
//         </Match>
//         <!-- Allow assert as a method name since used by testing frameworks. -->
//         <Match>
//             <Method name="assert" />
//             <Bug pattern="NM_FUTURE_KEYWORD_USED_AS_MEMBER_IDENTIFIER" />
//         </Match>
//         <!-- These are methods derived from objects, whose names *should* start with
//              an upper case letter. -->
//         <Match>
//             <Or>
//                 <Method name="FocusTest" />
//                 <Method name="MaxDiscarded" />
//                 <Method name="MaxSize" />
//                 <Method name="MinSize" />
//                 <Method name="MinSuccessful" />
//                 <Method name="PropertyCheckConfig" />
//                 <Method name="Workers" />
//                 <Method name="Error" />
//                 <Method name="Failure" />
//                 <Method name="NoSuccess" />
//                 <Method name="OnceParser" />
//                 <Method name="Parser" />
//                 <Method name="Success" />
//                 <Method name="Pos" />
//             </Or>
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Or>
//                 <Class name="org.kiama.attribution.AttributionTests" />
//                 <Class name="org.kiama.attribution.DynamicAttributionTests" />
//             </Or>
//             <Or>
//                 <Method name="Leaf" />
//                 <Method name="Pair" />
//                 <Method name="GenSeqTree" />
//                 <Method name="ListTree" />
//                 <Method name="MapTree" />
//                 <Method name="PairTree" />
//                 <Method name="SetTree" />
//                 <Method name="Unused" />
//             </Or>
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.attribution.DynamicAttributionTests$$anonfun$8" />
//             <Or>
//                 <Method name="ExtensionOne$1" />
//                 <Method name="ExtensionTwo$1" />
//             </Or>
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.attribution.DynamicAttributionTests$$anonfun$9" />
//             <Method name="Extension$1" />
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.example.RISC.RISC" />
//             <Method name="Mem" />
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Package name="~org\.kiama\.example\.iswim.*" />
//             <Or>
//                 <Method name="ExnContValue" />
//                 <Method name="FalseValue" />
//                 <Method name="IntValue" />
//                 <Method name="RecordValue" />
//                 <Method name="RefValue" />
//                 <Method name="StringValue" />
//                 <Method name="TrueValue" />
//                 <Method name="UserExceptionValue" />
//                 <Method name="ClosureValue" />
//                 <Method name="ContValue" />
//                 <Method name="EmptyValue" />
//                 <Method name="PrimValue" />
//                 <Method name="EmptyCont" />
//             </Or>
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Package name="org.kiama.example.picojava" />
//             <Or>
//                 <Method name="Value" />
//                 <Method name="Variable" />
//                 <Method name="BlockStmts" />
//                 <Method name="Body" />
//                 <Method name="Name" />
//                 <Method name="Superclass" />
//                 <Method name="IdnUse" />
//                 <Method name="ObjectReference" />
//                 <Method name="Block" />
//                 <Method name="Type" />
//                 <Method name="Condition" />
//                 <Method name="PrimValue" />
//             </Or>
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Package name="org.kiama.rewriting" />
//             <Or>
//                 <Method name="Term" />
//                 <Method name="Equal" />
//                 <Method name="NotSame" />
//                 <Method name="Same" />
//             </Or>
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.util.PrettyPrinterTests" />
//             <Method name="Val" />
//             <Bug pattern="NM_METHOD_NAMING_CONVENTION" />
//         </Match>
//         <!-- Scala generates classes that are serializable but contain non-transient
//              non-serializable instance fields, particularly $outer.  Not sure if that
//              has any implications, but it's none of our business... -->
//         <Match>
//             <Or>
//                 <Bug pattern="SE_BAD_FIELD" />
//                 <Bug pattern="SE_BAD_FIELD_STORE" />
//             </Or>
//         </Match>
//         <!-- Some cases where mutable object references are stored in other objects
//              or returned. Due to use of App, for comprehension, anon functions inside
//              defs  -->
//         <Match>
//             <Class name="org.kiama.example.picojava.benchmark.PicoJavaBenchmark$" />
//             <Field name="scala$App$$_args" />
//             <Or>
//                 <Bug pattern="EI_EXPOSE_REP" />
//                 <Bug pattern="EI_EXPOSE_REP2" />
//             </Or>
//         </Match>
//         <Match>
//             <Class name="org.kiama.util.CompilerBase$$anonfun$driver$1" />
//             <Field name="newargs$1" />
//             <Bug pattern="EI_EXPOSE_REP2" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.util.TestCompiler$$anonfun$filetest$1$1" />
//             <Field name="cmd$1" />
//             <Bug pattern="EI_EXPOSE_REP2" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.util.TestCompiler$$anonfun$filetests$1" />
//             <Field name="children$1" />
//             <Bug pattern="EI_EXPOSE_REP2" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.util.TestCompiler$$anonfun$filetests$1$$anonfun$apply$1" />
//             <Field name="args$2" />
//             <Bug pattern="EI_EXPOSE_REP2" />
//         </Match>
//         <Match>
//             <Class name="org.kiama.util.TestCompiler$$anonfun$infiletests$1$1" />
//             <Field name="args$1" />
//             <Bug pattern="EI_EXPOSE_REP2" />
//         </Match>
//         <!-- These synchronisation problems seem to be due to something the compiler
//              is doing, not sure what. Ignoring for now... -->
//         <Match>
//             <Or>
//                 <Class name="org.kiama.example.iswim.secd.SECDBase" />
//                 <Class name="org.kiama.example.obr.SyntaxAnalysis" />
//             </Or>
//             <Bug pattern="IS2_INCONSISTENT_SYNC" />
//         </Match>
//         </FindBugsFilter>)
