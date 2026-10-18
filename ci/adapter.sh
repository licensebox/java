#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/.."
exec java -Djna.library.path="$LICENSEBOX_LIB_DIR" -cp "target/classes:target/test-classes:$(cat target/classpath.txt)" dev.licensebox.Adapter
