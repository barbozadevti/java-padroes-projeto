#!/usr/bin/env bash
# Compila o projeto e roda os testes (Java puro, sem dependências).
set -euo pipefail
cd "$(dirname "$0")/.."
rm -rf out && mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java") tests/Testes.java
java -Dstdout.encoding=UTF-8 -cp out Testes
