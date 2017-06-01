// The simplest possible sbt build file is just one line:

scalaVersion := "2.12.1"
name := "algorithms1_5_7"
organization := "ch.epfl.scala"
version := "1.0"
libraryDependencies ++= Seq("org.scalatest" % "scalatest_2.12" % "3.0.1" % "test",
			"org.scalacheck" %% "scalacheck" % "1.13.4" % "test",
			"io.reactivex" % "rxscala_2.12" % "0.26.5")

			