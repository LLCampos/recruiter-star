# Changelog

## [1.1.1]

### Fixed
* Fix bugs in which experience sections can be cut

## [1.1.0]

### Added
* Possible to activate and deactivate extension
* Possible to select which technologies to see
* Highlight selected technologies
* Add new technologies

### Changed
* Increase times it tries to create the tech box

### Removed
* Remove minor technologies

### Fixed
* Fix tech names
* Don't use previous profile information when navigating using the browser history ("go back", "go forward")

(and other misc fixes)

## [1.0.0]

### Fixed
* On error, retry to add experience summary box

## [0.0.2]

### Added
* Errors are now logged to console
* More aliases for Node.js

### Changed
* .NET and Node.js are now categorized as platforms
* Change how total experience per technology is calculated, to consider cases when experiences overlap in time

### Fixed
* Premium: deal with cases in which experience section has no duration
* Correctly sum technology experience even when they are represented with different aliases

### Removed
* Remove some minor technologies
* Remove "Node" as a Node.js alias
