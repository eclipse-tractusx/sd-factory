## Installation Steps

Helm charts are provided inside https://github.com/eclipse-tractusx/sd-factory

1.) Installation from released chart: <br />

How to install application using helm:  <br />
    helm install ReleaseName ChartName
    
    a.) Add helm repository for sdfactory:
           helm repo add [name] https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo [name]/sdfactory
    c.) To install using helm command:
           helm install [ReleaseName] [name]/sdfactory


2.) Installation from repository:

    a.) git clone https://github.com/eclipse-tractusx/sd-factory.git
    b.) cd sd-factory
    c.) Modify values file according to your requirement
    d.) You need to define the secrets as well in values.yaml
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

    e.) Deploy in a kubernetes cluster
        helm install [name] charts/sdfactory/ -n [NameSpace]
        
