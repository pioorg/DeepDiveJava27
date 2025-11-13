#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

java_version=$(java --version 2>/dev/null | awk 'NR==1 {split($2,a,"."); print a[1]}')
if [[ "$java_version" != "27" ]]; then
  echo "ERROR: Java 27 required (found: $java_version)" >&2
  exit 1
fi

echo "----"

java -Xms512m -Xmx512m -XX:+UnlockExperimentalVMOptions \
  -XX:+UseEpsilonGC -XX:-UseCompactObjectHeaders \
  -XX:StartFlightRecording=filename=coh-off.jfr,settings=profile,dumponexit=true \
  "$SCRIPT_DIR/CompactObjectHeadersDemo.java"

echo "===="

java -Xms512m -Xmx512m -XX:+UnlockExperimentalVMOptions \
  -XX:+UseEpsilonGC \
  -XX:StartFlightRecording=filename=coh-on.jfr,settings=profile,dumponexit=true \
  "$SCRIPT_DIR/CompactObjectHeadersDemo.java"

echo "----"
