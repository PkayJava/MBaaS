#!/bin/bash
rm -rf mbaas-jooq/buildSrc
$GRADLE_HOME/bin/gradle flywayClean flywayMigrate jooq war
