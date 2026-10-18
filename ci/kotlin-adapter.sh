#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
exec java -Djna.library.path="$LICENSEBOX_LIB_DIR" -cp "target/kotlin:target/scala:target/licensebox-0.2.0.jar:$(cat target/classpath.txt):$(cat target/jvm-classpath.txt)" KotlinAdapterKt
