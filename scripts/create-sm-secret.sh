#!/bin/bash

set -e
cd "$(dirname "$(dirname "$0")")"

npm install --no-save yaml

function value() {
    node ./scripts/value.js "$1"
}

NAME="$1"
if [ "$NAME" == "" ]; then
  if [ ! -f "chart/values.yaml" ]; then
    echo >&2 "[ERROR] Please either specify the name for the service manager secret or maintain it in the Helm chart"
    exit 1
  fi
  NAME="$(value .srv.bindings.db.fromSecret)"
  if [ "$NAME" == "" -o "$NAME" == "<nil>" ]; then
    echo >&2 "[ERROR] Please either specify the name for the service manager secret or maintain it in the Helm chart"
    exit 1
  fi
fi

SECRET_HEADER="$(cat <<EOF
apiVersion: v1
kind: Secret
metadata:
  name: $NAME
type: Opaque
stringData:
  .metadata: |
    {
      "credentialProperties": [
        {
          "name": "xsappname",
          "format": "text"
        },
        {
          "name": "clientid",
          "format": "text"
        },
        {
          "name": "clientsecret",
          "format": "text"
        },
        {
          "name": "sm_url",
          "format": "text"
        },
        {
          "name": "url",
          "format": "text"
        }
      ],
      "metaDataProperties": [
        {
          "name": "plan",
          "format": "text"
        },
        {
          "name": "label",
          "format": "text"
        },
        {
          "name": "type",
          "format": "text"
        }
      ]
    }
  type: service-manager
  label: service-manager
  plan: container
EOF
)"

cf 2>/dev/null >/dev/null service $NAME || cf create-service service-manager container $NAME
while true; do
    STATUS="$(cf 2>/dev/null service $NAME | grep status: | head -n 1)"
    echo $STATUS
    if [[ "$STATUS" = *succeeded* ]]; then
        break
    fi
    sleep 1
done

cf create-service-key $NAME $NAME-key

node "$(dirname "$0")/format-kyma-secret.js" -- "$(echo "$SECRET_HEADER")" "$(cf service-key $NAME $NAME-key)" | kubectl apply -f -
echo
echo "Service Manager container secret '$NAME' created."
echo
echo "You can view it using:"
echo
echo "kubectl get secret $NAME -o yaml"
exit 0