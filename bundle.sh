#!/bin/bash
JLINK_PATH=$(/usr/libexec/java_home -v 11)/bin
"${JLINK_PATH}"/jlink --no-header-files --no-man-pages \
   --add-modules java.datatransfer,java.desktop,java.logging,java.scripting,\
java.sql,java.xml,jdk.jsobject,jdk.unsupported,jdk.unsupported.desktop,jdk.xml.dom \
   --output java-runtime