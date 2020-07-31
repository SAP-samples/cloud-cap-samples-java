#!/bin/bash
set -e
cd "$(dirname "$0")"
rm -rf dist
mkdir dist
for APP in *; do
    if [ -f "$APP/webapp/manifest.json" ]; then
        echo "Build $APP"
        cd "$APP"
        if [ ! -f "package.json" ]; then
            npm init --yes
        fi
        npm install --save-dev @ui5/cli
        if [ ! -f "ui5.yaml" ]; then
            cat >ui5.yaml <<EOF
specVersion: "2.1"
type: application
metadata:
  name: $APP
EOF
    fi
        npx ui5 build --dest "../dist/$APP" -a --include-task=generateManifestBundle generateCachebusterInfo
        cd ..
    fi
done