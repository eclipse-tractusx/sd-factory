# <a id="introduction"></a>Self-Description Factory

In Catena-X we provide self-descriptions for any participant of this data space.
The Self Descriptions are stored inside the Self Description Hub, but need to be
created first. Self-Description Factory component is responsible for the creation of 
Self Descriptions. This component gets all necessary parameters and
information from the Onboarding Tool, which prepares the data for the SD-Factory,
and uses the
[Managed Identity Wallet](https://github.com/eclipse-tractusx/managed-identity-wallets)
based on custodian to sign the Self Descriptions.

# Solution Strategy 

Here the flow of Self-Description creation is shown:

![Process Flow](docs/images/process-flow.png)

1. A user is authenticated in Identity Provider service on behalf of a company
   and receives the authentication ticket.
2. User calls On-boarding Service with request for creating and publishing
   SD-document. The service authenticates the user and prepare the data
   SD-Factory needs for creating SD-document. The documents SD-Factory can 
   work with are defined in [Trust Framework V.22.04].
   Currently, these documents are supported by SD-Factory:
    - LegalPerson
    - ServiceOffering
    - PhysicalResource
    - VirtualResource
    - InstantiatedVirtualResource

   **Organization wallet of the company which runs the service shall
   be available at this point of time as it signs the Verifiable Credential
   with SD document. The wallet associated with the service shall be available
   as well.**
4. On-boarding service (OS) calls SD-Factory for creating SD-document passing this
   data as a parameter. OS uses a credential with a role allowing for this request
   (e.g. `add_self_descriptions`, the default role for SD-document creation). The
   credential for this operation is taken from ID Provider (keyclock).
5. SD-Factory creates a Verifiable Credential based on the information taken from
   OS and signs it with organization key. The organization is acting as an Issuer.
   The wallet ID of the service is used as Holder Id. The Custodian Wallet is used
   for this operation.
6. SD-Factory returns the Verifiable Credential to the requester.

For the VC we have to provide valid JSON context where we have a reference to an object
from known ontology. This object carries the claims the SD-Factory signs. The document
is published on the [github repository of the project](src/main/resources/verifiablecredentials.jsonld/sd-document-v0.3.jsonld).
The vocabulary URL can be changed when will be provided by Trusted Framework. 
Currently, the vocabulary is defined here:

```json
{
  "@context": {
    "id": "@id",
    "type": "@type",
    "ctxsd": "https://catena-x.net/selfdescription#",
    "spdx": "http://spdx.org/rdf/terms#",
    "schema": "https://schema.org/",
    "xsd": "http://www.w3.org/2001/XMLSchema#",
    "LegalPerson": {
      "@id": "ctxsd:LegalPerson",
      "@context": {
        "registrationNumber": {
          "@id": "ctxsd:registrationNumber",
          "@type": "schema:name"
        },
        "headquarterAddress": {
          "@id": "ctxsd:headquarterAddress",
          "@context": {
            "country": {
              "@id": "ctxsd:country",
              "@type": "schema:addressCountry"
            }
          }
        },
        "legalAddress": {
          "@id": "ctxsd:legalAddress",
          "@context": {
            "country": {
              "@id": "ctxsd:country",
              "@type": "schema:addressCountry"
            }
          }
        },
        "parentOrganisation": {
          "@id": "ctxsd:parentOrganisation",
          "@container": "@set",
          "@type": "@id"
        },
        "subOrganisation": {
          "@id": "ctxsd:subOrganisation",
          "@container": "@set",
          "@type": "@id"
        },
        "leiCode": {
          "@id": "ctxsd:leiCode",
          "@type": "schema:leiCode"
        },
        "bpn": {
          "@id": "ctxsd:bpn",
          "@type": "schema:name"
        }
      }
    },
    "ServiceOffering": {
      "@id": "ctxsd:ServiceOffering",
      "@type": "rdfs:Class",
      "@context": {
        "providedBy": {
          "@id": "ctxsd:providedBy",
          "@type": "@id"
        },
        "aggregationOf": {
          "@id": "ctxsd:aggregationOf",
          "@container": "@set",
          "@type": "@id"
        },
        "termsAndConditions": {
          "@id": "ctxsd:termsAndConditions",
          "@container": "@set",
          "@context": {
            "URL": {
              "@id": "ctxsd:URL",
              "@type": "schema:url"
            },
            "hash": {
              "@id": "ctxsd:hash",
              "@type": "schema:sha256"
            }
          }
        },
        "policies": {
          "@id": "ctxsd:policies",
          "@container": "@set"
        }
      }
    },
    "PhysicalResource": {
      "@id": "ctxsd:PhysicalResource",
      "@context": {
        "aggregationOf": {
          "@id": "ctxsd:aggregationOf",
          "@container": "@set",
          "@type": "@id"
        },
        "maintainedBy": {
          "@id": "ctxsd:maintainedBy",
          "@container": "@set",
          "@type": "@id"
        },
        "ownedBy": {
          "@id": "ctxsd:ownedBy",
          "@container": "@set",
          "@type": "@id"
        },
        "manufacturedBy": {
          "@id": "ctxsd:manufacturedBy",
          "@container": "@set",
          "@type": "@id"
        },
        "locationAddress": {
          "@id": "ctxsd:locationAddress",
          "@container": "@set",
          "@context": {
            "country": {
              "@id": "ctxsd:country",
              "@type": "schema:addressCountry"
            }
          }
        },
        "location": {
          "@id": "ctxsd:location",
          "@container": "@set",
          "@context": {
            "gps": {
              "@id": "ctxsd:gps",
              "@type": "xsd:string"
            }
          }
        }
      }
    },
    "VirtualResource": {
      "@id": "ctxsd:VirtualResource",
      "@context": {
        "aggregationOf": {
          "@id": "ctxsd:aggregationOf",
          "@container": "@set",
          "@type": "@id"
        },
        "copyrightOwnedBy": {
          "@id": "ctxsd:copyrightOwnedBy",
          "@container": "@set",
          "@type": "xsd:string"
        },
        "license": {
          "@id": "ctxsd:license",
          "@container": "@set",
          "@type": "spdx:ListedLicense"
        }
      }
    },
    "InstantiatedVirtualResource": {
      "@id": "ctxsd:InstantiatedVirtualResource",
      "@context": {
        "aggregationOf": {
          "@id": "ctxsd:aggregationOf",
          "@container": "@set",
          "@type": "@id"
        },
        "copyrightOwnedBy": {
          "@id": "ctxsd:copyrightOwnedBy",
          "@container": "@set",
          "@type": "xsd:string"
        },
        "license": {
          "@id": "ctxsd:license",
          "@container": "@set",
          "@type": "spdx:licenseId"
        },
        "maintainedBy": {
          "@id": "ctxsd:maintainedBy",
          "@container": "@set",
          "@type": "@id"
        },
        "hostedOn": {
          "@id": "ctxsd:hostedOn",
          "@type": "@id"
        },
        "tenantOwnedBy": {
          "@id": "ctxsd:tenantOwnedBy",
          "@container": "@set",
          "@type": "@id"
        },
        "endpoint": {
          "@id": "ctxsd:endpoint",
          "@container": "@set",
          "@type": "xsd:string"
        }
      }
    }
  }
}
```

# REST Interface

## The SD-Factory

The SD-Factory provides an interface to creating Verifiable Credential for one of mentioned documents.
Only the authorized user can call this interface to create it. It is protected 
with keycloak. The configuration parameters are given in `application.yml`.
The user role for creating Self-Descriptions is specified in `application.yml` as well.

```http request
POST /selfdescription
```
where body is
```json
{
  "type": "LegalPerson",
  "issuer": "CAXSDUMMYCATENAZZ",
  "registrationNumber": "o12345678",
  "headquarterAddress": {
    "country": "DE"
  },
  "legalAddress": {
    "country": "DE"
  },
  "leiCode": "20_digit_code",
  "parentOrganisation": [
    "https://parent.organisation1.org",
    "https://parent.organisation2.org"
  ],
  "subOrganisation": [
    "https://sub.organisation1.org",
    "https://sub.organisation2.org"
  ],
  "bpn": "BPNL000000000000"
}
```
for LegalPerson Self-Description and
```json
{
  "type": "ServiceOffering",
  "holder": "BPNL000000000000",
  "issuer": "CAXSDUMMYCATENAZZ",
  "providedBy": "https://participant.link.example.com",
  "aggregationOf": [
    "https://resource.sd.example1.com",
    "https://resource.sd.example2.com"
  ],
  "termsAndConditions": [
    {
      "URL": "https://terms.and.conditions.example1.com",
      "hash": "<sha256 hash of the document>"
    },
    {
      "URL": "https://terms.and.conditions.example2.com",
      "hash": "<sha256 hash of the document>"
    }
  ],
  "policies": [
    "Policy example1",
    "Policy example2"
  ]
}
```
for ServiceOffering.

This call creates a Self-Description. The full OpenAPI specification defined in 
[SDFactoryApi.yml](src/main/resources/static/SDFactoryApi.yml). Note, not all parameters are
mandatory. The model is given in [Trust Framework V.22.04].

The Self-Description in the format of Verifiable Credential is returned. Here is an example of
Verifiable Credentials for LegalPerson:

```json
{
  "@context": [
    "https://www.w3.org/2018/credentials/v1",
    "https://github.com/catenax-ng/tx-sd-factory/raw/main/src/main/resources/verifiablecredentials.jsonld/sd-document-v0.3.jsonld",
    "https://w3id.org/vc/status-list/2021/v1"
  ],
  "type": [
    "VerifiableCredential",
    "LegalPerson"
  ],
  "issuer": "did:sov:BEumURwPdXCobgbPYQZXge",
  "issuanceDate": "2022-11-23T12:02:41Z",
  "expirationDate": "2023-02-21T12:02:41Z",
  "credentialSubject": {
    "type": "LegalPerson",
    "registrationNumber": "o12345678",
    "headquarterAddress": {
      "country": "DE"
    },
    "legalAddress": {
      "country": "DE"
    },
    "leiCode": "20_digit_code",
    "parentOrganisation": [
      "https://parent.organisation1.org",
      "https://parent.organisation2.org"
    ],
    "subOrganisation": [
      "https://sub.organisation1.org",
      "https://sub.organisation2.org"
    ],
    "bpn": "BPNL000000000000",
    "id": "did:indy:idunion:test:P5TFvs9PQ6e6nMB18XVTJw"
  },
  "credentialStatus": {
    "id": "https://managed-identity-wallets.int.demo.catena-x.net/api/credentials/status/fe5da20d-35c1-4154-b764-1e7dc875ca1d#452",
    "type": "StatusList2021Entry",
    "statusPurpose": "revocation",
    "statusListIndex": "452",
    "statusListCredential": "https://managed-identity-wallets.int.demo.catena-x.net/api/credentials/status/fe5da20d-35c1-4154-b764-1e7dc875ca1d"
  },
  "proof": {
    "type": "Ed25519Signature2018",
    "created": "2022-11-23T12:02:43Z",
    "proofPurpose": "assertionMethod",
    "verificationMethod": "did:sov:BEumURwPdXCobgbPYQZXge#key-1",
    "jws": "eyJhbGciOiAiRWREU0EiLCAiYjY0IjogZmFsc2UsICJjcml0IjogWyJiNjQiXX0..KALYtsHMI62J0x3ILqkuOc8hu30YzBevanddWesaEd2j776fKZN5dvJBfUH_Lo7Q97jXhmZMiYt7HW7k-8duBA"
  }
}
```

# Configuration
The configuration property file is located under `resources` folder and is incorporated 
into the fat jar during build process. It can be customized before building if needed.
Or,the another one can be used as its location can be overridden:
```shell
java -jar myproject.jar --spring.config.location=file:./custom-config/
```
Here application.yml will be searched in custom-config dir.

## Self-Description Factory Property file
An example of [application.yml](src/main/resources/application.yml) for SD-Factory is given bellow:
```yaml
server:
  port: 8080
  error:
    include-message: always
keycloak:
  #auth-server-url: https://centralidp.int.demo.catena-x.net/auth
  #realm: CX-Central
  #resource: Cl2-CX-Portal
  bearer-only: true
  use-resource-role-mappings: true
  principal-attribute: preferred_username
spring:
  jackson:
    default-property-inclusion: non_null
springdoc:
  api-docs:
    enabled: false
  swagger-ui:
    url: /SDFactoryApi.yml
app:
  build:
    version: ^project.version^
  verifiableCredentials:
    durationDays: 90
    schemaUrl: https://github.com/catenax-ng/tx-sd-factory/raw/main/src/main/resources/verifiablecredentials.jsonld/sd-document-v0.3.jsonld
  custodianWallet:
    uri: https://managed-identity-wallets.int.demo.catena-x.net/api
    #auth-server-url: https://centralidp.int.demo.catena-x.net/auth
    realm: CX-Central
    #clientId: ${CLIENTID}
    #clientSecret: ${CLIENTSECRET}
  security:
    createRole: add_self_descriptions
```

Here `keycloak` section defines keycloak's parameters for authentication client requests.

`app.verifiableCredentials.durationDays` defines for how many days the VC is issued.

`app.custodianWallet` contains parameters for accessing Custodian Wallet:
- `uri` is custodian Wallet url
- `auth-server-url`, `realm`, `clientId`, `clientSecret` are keycloak parameters for 
   a user which calls the Custodian Wallet. This user shall have enough rights to create 
   Verifiable Credentials and Verifiable Presentations.
- `app.security` sets a role a user must have for creating Self-Description.

# Building
SD-Factory use Maven for building process. To build a service from sources one
need to go to corresponding directory and trigger building process:
```shell
cd SDFactory
./mvnw clean install
```
Then fat jar file can be found in `target` folder as well as in local Maven repository.
it can be run with this command:
```shell
java -jar target/sd-factory-1.1.0.jar
```
Please note the name of jar-file as it may differ if version is changed.

<a name="docker"></a>To build a Docker image one can use this command:
```shell
docker build .
```
A Docker image will be built and installed to the local repository.

# Testing
SD-Factory can be fired up locally in Docker environment. Before that
the images need to be created as it is [described here](#docker). Do not forget
to provide necessary configuration parameters in application.yml for keycloak 
and the Custodian Wallet.

## Installation Steps:-

Helm charts are provided inside https://github.com/catenax-ng/tx-sd-factory

There are diffrent ways to do the installation

1. Using helm commands:-  

    a.) git clone https://github.com/catenax-ng/product-sd-hub.git  <br />
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

2. Using ArgoCD. 

To see how to deploy an application on 'Hotel Budapest': 
[How to deploy](https://catenax-ng.github.io/docs/guides/ArgoCD/how-to-deploy-an-application)

[Trust Framework V.22.04]: https://gitlab.com/gaia-x/policy-rules-committee/trust-framework/-/tree/22.04
