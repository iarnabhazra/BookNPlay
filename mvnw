#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper Startup Script (custom added)
# ----------------------------------------------------------------------------
MAVEN_WRAPPER_DIR="$(dirname "$0")/.mvn/wrapper"
WRAPPER_JAR="$MAVEN_WRAPPER_DIR/maven-wrapper.jar"
WRAPPER_PROPERTIES="$MAVEN_WRAPPER_DIR/maven-wrapper.properties"
DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
  mkdir -p "$MAVEN_WRAPPER_DIR"
  echo "Downloading Maven Wrapper JAR..." >&2
  curl -fsSL -o "$WRAPPER_JAR" "$DOWNLOAD_URL" || {
    echo "Failed to download Maven Wrapper" >&2
    exit 1
  }
fi

JAVA_EXEC="java"

# Derive project base directory (parent of this script) for multi-module builds
MAVEN_PROJECTBASEDIR="$(cd "$(dirname "$0")"; pwd -P)"

exec "$JAVA_EXEC" \
  -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
  -classpath "$WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain "$@"
