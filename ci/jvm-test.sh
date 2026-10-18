#!/usr/bin/env bash
set -euo pipefail
mvn -B -f tests/jvm/pom.xml dependency:build-classpath -Dmdep.outputFile="$PWD/target/jvm-classpath.txt"
cp="target/licensebox-0.1.1.jar:$(cat target/classpath.txt):$(cat target/jvm-classpath.txt)"
mkdir -p target/kotlin target/scala
java -cp "$cp" org.jetbrains.kotlin.cli.jvm.K2JVMCompiler -no-stdlib -no-reflect -jvm-target 11 -classpath "$cp" -d target/kotlin tests/jvm/KotlinAdapter.kt
java -cp "$cp" scala.tools.nsc.Main -usejavacp -d target/scala tests/jvm/ScalaAdapter.scala
for language in kotlin scala clojure; do
  python3 ../protocol/conformance/run.py --adapter bash "ci/$language-adapter.sh"
  python3 ../protocol/conformance/heartbeat.py --adapter bash "ci/$language-adapter.sh"
done
