#!/bin/bash

set -e
cd "$(dirname "$(dirname "$0")")"
. ./scripts/values.sh

rm -rf gen/ui
mkdir -p gen/ui/resources

CLOUD_SERVICE="$(value .html5_apps_deployer.cloudService)"
DESTINATIONS=`helm inspect values "chart/" --jsonpath="{.html5_apps_deployer.backendDestinations}"`

IMAGE="$(image .html5_apps_deployer)"

for APP in app/*; do
    if [ -f "$APP/webapp/manifest.json" ]; then
        echo "Build $APP..."
        echo

        rm -rf "gen/$APP"
        mkdir -p "gen/app"
        cp -r "$APP" gen/app
        pushd >/dev/null "gen/$APP"

        node ../../../scripts/prepareUiFiles.js $CLOUD_SERVICE $DESTINATIONS
        npm install
        npx ui5 build preload --clean-dest --config ui5-deploy.yaml --include-task=generateManifestBundle generateCachebusterInfo
        cd dist
        rm manifest-bundle.zip
        mv *.zip ../../../ui/resources

        popd >/dev/null
    fi
done

cd gen/ui

echo
echo "HTML5 Apps:"
ls -l resources
echo

cat >package.json <<EOF
{
    "name": "ui-deployer",
    "scripts": { "start": "node node_modules/@sap/html5-app-deployer/index.js" }
}
EOF

npm install @sap/html5-app-deployer
pack build $IMAGE --path . --buildpack gcr.io/paketo-buildpacks/nodejs --builder paketobuildpacks/builder:base