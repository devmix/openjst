#!/bin/sh

WD=`dirname $0`

xjc ${WD}/manifest.xsd -p org.openjst.server.commons.maven.manifest.jaxb -d ${WD}/../java -readOnly -no-header -npa