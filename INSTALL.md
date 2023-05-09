## Installation Steps

Helm charts are provided inside https://github.com/eclipse-tractusx/sd-factory

1.) Using helm commands: <br />

How to install application using helm:  <br />
    helm install ReleaseName ChartName
    
    a.) Add helm repository in tractusx:
           helm repo add sd-factory https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo sd-factory/sdfactory
    c.) To install using helm command:
           helm install sdf sd-factory/sdfactory


2.) Local installation:

    a.) git clone https://github.com/eclipse-tractusx/sd-factory.git
    b.) Modify values file according to your requirement
    c.) You need to define the secrets as well in values.yaml
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

    d.) These secrets should be defined in Hashicorp vault
    e.) Deploy in a kubernetes cluster
        helm install sdfactory charts/SDFactory/ -n NameSpace
        
