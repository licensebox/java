#!/usr/bin/env bash
set -euo pipefail
mvn -B package dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -Djna.library.path="$LICENSEBOX_LIB_DIR" -cp "target/classes:target/test-classes:$(cat target/classpath.txt)" dev.licensebox.BindingTest
# Compile an independent consumer from the packaged jar, rather than source.
mkdir -p target/consumer
javac --release 11 -cp "target/licensebox-0.1.0.jar:$(cat target/classpath.txt)" -d target/consumer tests/consumer/Main.java
java -Djna.library.path="$LICENSEBOX_LIB_DIR" -cp "target/consumer:target/licensebox-0.1.0.jar:$(cat target/classpath.txt)" Main
