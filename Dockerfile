FROM ghcr.io/navikt/pus-nais-java-app/pus-nais-java-app:java11
COPY /target/poao-gcp-proxy.jar app.jar