#!/bin/bash
rm -rf $CATALINA_HOME/webapps/ROOT.war
rm -rf $CATALINA_HOME/webapps/ROOT
$GRADLE_HOME/bin/gradle clean war
cp mbaas-server/build/libs/mbaas-server-1.0.war $CATALINA_HOME/webapps/ROOT.war
$CATALINA_HOME/bin/catalina.sh jpda run
