#!/usr/bin/env bash

export JAVA_HOME=./build_tools/zulu17.54.21-ca-jdk17.0.13-macosx_x64
export ANT_HOME=./build_tools/apache-ant-1.10.15
export PATH=$PATH:$JAVA_HOME/bin:$ANT_HOME/bin

ant