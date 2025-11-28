#!/bin/bash

GOOGLE_JAVA_FORMAT="/home/${USER}/bin/google-java-format.jar" # https://github.com/google/google-java-format

java -jar "$GOOGLE_JAVA_FORMAT" -a -i $(find src/ -type f -name "*.java")
