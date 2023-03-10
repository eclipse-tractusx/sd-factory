## Installation Steps

Helm charts are provided inside https://github.com/eclipse-tractusx/sd-factory

1.) Using helm commands:- <br />

How to install application using helm:-  <br />
    helm install ReleaseName ChartName
    
    a.) Add helm repository in tractusx:-
           helm repo add sd-factory https://eclipse-tractusx.github.io/charts/dev
    b.) To search the specific repo in helm repositories 
           helm search repo tractusx-dev
    c.) To install using helm command:-   
           helm install sd-factory tractusx-dev/sd-factory


2.) Local installation:

    a.) git clone https://github.com/eclipse-tractusx/sd-factory.git  <br />
    b.) Modify values file according to your requirement.  <br />
    c.) You need to define the secrets as well in values.yaml
        secret:  <br />
          clientId: ""  -> Custodian wallet client id  <br />
          clientSecret: ""  -> Custodian wallet client secret  <br />
          authServerUrl: ""  ->  Keycloak URL   <br />
          realm: ""   -> Keycloak Realm  <br />
          resource: ""  ->  Keycloak Resource   <br />
          custodianWalletUri: "" -> Custodian wallet URI  <br /> 
    d.) These secrets should be defined in Hashicorp vault
    e.) Deploy in a kubernetes cluster  <br />
        helm install sdfactory charts/SDFactory/ -n NameSpace  <br />
        
