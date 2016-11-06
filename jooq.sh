#!/bin/bash
rm -rf mbaas-jooq/buildSrc
$GRADLE_HOME/bin/gradle clean flywayClean flywayMigrate jooq war
