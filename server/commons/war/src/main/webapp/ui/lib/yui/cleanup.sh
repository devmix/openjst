#!/bin/sh

find . -type f -name "*-coverage.js" -exec rm -f {} \;
find . -type f -name "*-debug.js" -exec rm -f {} \;

rm -rf ./build/simpleui
rm -rf ./build/datatable-*
rm -rf ./build/yui-nodejs
rm -rf ./build/yui-log-nodejs
