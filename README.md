# POAO GCP Proxy

Proxy som gjør det mulig å kontakte tjenester i FSS fra GCP.

## Routes

* /proxy/veilarboppfolging/**
* /proxy/norg2/**
* /proxy/veilarbarena/**
* /proxy/amt-arena-ords-proxy/**
* /proxy/veilarbaktivitet/**

## Hvordan ta i bruk

1. Legg til applikasjonen som skal bruke proxyen i whitelisten
```yaml
accessPolicy:
    inbound:
      rules:
        - application: my-application
          namespace: ny-namespace
          cluster: dev-gcp | prod-gcp
```

2. Gjør et request til proxyen. I `Authorization`-headeren så må det ligge et Azure AD token scopet til poao-gcp-proxy. 
    Hvis downstream applikasjonen krever autentisering så må det også legges med en ekstra header `Downstream-Authorization` 
    som vil bli sendt videre som `Authorization` til downstream applikasjonen.

For eksempel så vil et request til veilarboppfolging gjennom proxyen se slik ut:

```
# Dette blir sendt til proxyen
GET /proxy/veilarboppfolging/api/v2/oppfolging
Authorization: Bearer <token til poao-gcp-proxy>
Downstream-Authorization: Bearer <token til veilarboppfolging>
```
--->
```
# Dette blir sendt fra proxyen til veilarboppfolging
GET /veilarboppfolging/api/v2/oppfolging
Authorization: Bearer <token til veilarboppfolging>
```

## Hvordan legge til et nytt proxy endepunkt

Legg til følgende i `application.properties`:

```properties
zuul.routes.<app-name>.path=/proxy/<app-name>/**
zuul.routes.<app-name>.url=${<app-name>_URL:null}
zuul.routes.<app-name>.sensitiveHeaders=Cookie,Set-Cookie,Downstream-Authorization
# Hvis downstream applikasjonen ikke trenger autentisering så kan man også legge til "Authorization" i sensitiveHeaders
```

Legg til følgende i `nais-(dev|prod).yaml`:
```yaml
env:
    - name: <app-name>_URL
      value: <url to application>
```
