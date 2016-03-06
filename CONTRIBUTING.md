# Prerequisites

1. __JDK__: Install from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
1. __Maven__: Install from [here](https://maven.apache.org/download.cgi).

# Building

```bash
mvn compile
```

# Testing

```bash
mvn test
```

# Generating documentation

```bash
mvn javadoc:javadoc
```

# Releasing

## Prerequisites

1. Set up your credentials for the Sonatype OSS repository (see [this page](https://maven.apache.org/guides/mini/guide-encryption.html) for instructions).
1. Place the following contents into the `~/.m2/settings.xml` file:

    ```xml
    <settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              https://maven.apache.org/xsd/settings-1.0.0.xsd">
      <servers>
        <server>
          <id>ossrh</id>
          <username>matej</username>
          <password>{my_encrypted_password}</password>
        </server>
      </servers>
    </settings>
    ```

## Releasing a snapshot

Say you are working towards releasing version `1.2.3` and you want to release some development snapshots for it:

1. Set the version:

    ```bash
    mvn versions:set -DnewVersion=1.2.3-SNAPSHOT
    ```

1.  Deploy:

    ```bash
    mvn clean deploy
    ```

## Releasing a proper release

Say want to release version `1.2.3`.

```bash
RELEASE_VERSION=1.2.3 && \
NEXT_DEV_VERSION=1.2.4-SNAPSHOT && \
  mvn versions:set -DnewVersion=$RELEASE_VERSION && \
  mvn clean deploy -P release && \
  git commit -am "Release $RELEASE_VERSION." && \
  git tag v$RELEASE_VERSION && \
  git push --tags && \
  mvn versions:set -DnewVersion=$NEXT_DEV_VERSION && \
  git commit -am "Sets the next dev version to $NEXT_DEV_VERSION."
```
