# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),

## [Unreleased]


## [2.0.2] - 2023-03-23

### Changed
- Upgrade Spring Boot to 3.0.3
- Upgrade Snakeyaml to 2.0 as 1.33 has security issue
- Readme correction
- Authors.md modification
- Dockerfile modification

## [2.0.1] - 2023-02-23

### Added
- call compliance service after getting the signature for asynchronous processing
- add async support.

### Removed
- Remove 22.10 API from endpoint

### Changed
- schema was changed so registrationNumber is an array of documents
  with type and value fields  
- removed support for 22.10 as OpenAPI yaml is buggy and there ara 
  complains from KICS

## [1.2.1] - 2023-02-17

### Added
- Added AUTHORS.md, INSTALL.md file
- Added .helmignore file
- Added service port to values.yaml

### Changed
- Changed versions & installations steps in Readme
- Changed secrets manifest in secret.yaml

## [1.2.0] - 2023-01-16

### Added
- added a converter to support Trust Framework 22.10 keeping old API v1.0.6
- introduce vavr.io library for neater code
- add support for all versions of Trust Framework in a single project

### Changed
- rename endpoint path to reflect API version (/api/22.04/selfdescription)
- better error propagation from the Custodian to get more details on an error
- update Spring Boot from 2.7.5 -> 2.7.6
- update springdoc-openapi-ui 1.6.12 -> 1.6.13
- update keycloak-admin-client 19.0.3 -> 20.0.2
- update com.google.protobuf 3.21.9 -> 3.21.11
- update openapi-generator-maven-plugin 6.2.0 -> 6.2.1

## [1.1.0] - 2022-11-23

### Added
- compatibility with Trust Framework V.22.04. 
- Better Exception handling
- parameters validation
- new schema of sd documents for TermsOfConditions, PhysicalResource, VirtualResource
  and InstantiatedVirtualResource


## [1.0.6] - 2022-10-22
Added helm release, versioning & tagging

### Added
- Generate self description for LegalPerson 
- Generate self description for ServiceOffering
- Keycloak protection is added

### Changed
- Generate the controller from the openAPI description
- Update all the used libraries to the latest version
- Change the representation of the consumed content type to application/json
- All smells from SonarQube were fixed
- Moved helm charts from `helm/` to `charts`

### Removed
- Controller has been removed
