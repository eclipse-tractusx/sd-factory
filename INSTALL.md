## Installation Steps

Helm chart is provided inside https://github.com/eclipse-tractusx/sd-factory

How to install application using helm:  <br />
    helm install [ReleaseName] [ChartName]

1.) Installation from released chart: <br />
    
    a.) Add helm repository for tractusx-dev:
           helm repo add tractusx-dev https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo tractusx-dev/sdfactory
    c.) To set your own configuration and secret values, install the helm chart with your own values file in kubernetes cluster:

           helm install -f your-values.yaml [ReleaseName] tractusx-dev/sdfactory -n [NameSpace]


2.) Installation from repository:
        
    a.) git clone https://github.com/eclipse-tractusx/sd-factory.git
    b.) cd sd-factory
    c.) Modify values file according to your requirement
    d.) You need to define the secrets as well in your-values.yaml. These secrets should be defined in Hashicorp vault to keep them secure
        secret:
              jwkSetUri: -> JWK Set URL
              clientId: -> Custodian wallet client id
              clientSecret: -> Custodian wallet client secret
              authServerUrl: -> Keycloak URL
              realm:  -> Keycloak Realm
              resource:  ->  Keycloak Resource
              custodianWalletUri:  -> Custodian wallet URI
              clearingHouseUri:  -> Clearing House URI
              clearingHouseServerUrl: ->  Clearing House server URL
              clearingHouseRealm: ->  Realm for Clearing House
              clearingHouseClientId: -> Client id for Clearing House
              clearingHouseClientSecret: -> Clearing house for Client secret

    e.) To set your own configuration and secret values, install the helm chart with your own values file in a kubernetes cluster

           helm install -f your-values.yaml [ReleaseName] charts/sdfactory/ -n [NameSpace]