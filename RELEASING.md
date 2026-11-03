# Releasing Palladium

Releases are published to [Maven Central](https://central.sonatype.com/artifact/io.github.kistler-group/palladium)
by the [Publish release](.github/workflows/publish-release.yml) workflow.

## Publishing a release
1. Make sure `main` is green.
2. Set the release version and the scm tag in `pom.xml` on `main`, then commit and push:

   ```bash
   mvn versions:set versions:set-scm-tag -DnewVersion=2.0.0 -DnewTag=v2.0.0 -DgenerateBackupPoms=false
   ```

3. Create a GitHub release with a new tag `vX.Y.Z` (e.g. `v2.0.0`) on that commit and publish it.
   The workflow fails for tags that don't match the version in `pom.xml` and for commits that are not on `main`,
   and is skipped for pre-releases.
   Turning a pre-release into a release later does not publish it; create a new release instead.
4. If the `maven-central` environment requires a review, approve the deployment in the workflow run.
5. The workflow builds, signs and uploads the artifacts to Maven Central.
   It waits until Central has published them, which usually takes 10–30 minutes.
6. Set `pom.xml` on `main` to the next `-SNAPSHOT` version and the scm tag back to `HEAD`:

   ```bash
   mvn versions:set versions:set-scm-tag -DnewVersion=2.0.1-SNAPSHOT -DnewTag=HEAD -DgenerateBackupPoms=false
   ```

## Building a release locally
Signing and uploading only happen with the `release` profile. To check a release build without either:

```bash
mvn -P release verify -Dgpg.skip
```
