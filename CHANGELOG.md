# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),


### Changed
- Moved helm charts from `helm/` to `charts`

## [2.0.0] - 2022-11-09
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

### Removed
- Controller has been removed

### Known knowns
- Cross side scripting (XSS) shall be mitigated (low risk)
- Improving the validation of the input parameters (low risk)
