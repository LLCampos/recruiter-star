# RecruiterStar

## Development
- Running `sbt "~chromeUnpackedFast"` will build the app each time it detects changes on the code, it also disables js optimizations which result in faster builds (placing the build at `target/chrome/unpacked-fast`).

## Release
Run: `PROD=true sbt chromePackage` which generates:
- A zip file that can be uploaded to the chrome store: `target/chrome/chrome-scalajs-template.zip`
- A folder with all resources which can be packaged for firefox: `target/chrome/unpacked-opt/`
