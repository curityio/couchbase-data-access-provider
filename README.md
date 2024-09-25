# Couchbase Data Source plugin

[![Quality](https://img.shields.io/badge/quality-experiment-red)](https://curity.io/resources/code-examples/status/)
[![Availability](https://img.shields.io/badge/availability-source-blue)](https://curity.io/resources/code-examples/status/)

This is an implementation of a Curity Couchbase datasource plugin

This plugin currently implements the following interfaces:

* `AttributeDataAccessProvider`
* `CredentialDataAccessProvider`
* `UserAccountDataAccessProvider`

This means that it will be usable for authentication and attribute collection use cases with the Curity Identity Server.

## Build from source
To build the project, use `./gradlew build`.
To create a folder containing both the plugin and the necessary dependencies, run `./gradlew createPluginDir`. 
`createPluginDir` creates `build/curity-couchbase-plugin`. This folder can then be copied into the Curity on the path: `usr/share/plugins`.

## Plugin deployment options ##
To use the plugin in Curity, there are some options.

- Copy the plugin directory to an existing docker container.
  `docker cp ./build/curity-couchbase-plugin [container_id]:/opt/idsvr/usr/share/plugins/`. Note that the container will have to be restarted for the plugin to be picked up.
- Copy the plugin directory to a local Curity installation, where `IDSVR_HOME` is the home folder for you installation.
  `cp -r ./build/curity-couchbase-plugin $IDSVR_HOME/usr/share/plugins/`, and restart your installation
- Build a docker image with the plugin folder copied into the image. There's a Dockerfile in the repo to help with that.


## Build docker image

- Run `./gradlew createPluginDir` to build the plugin.
- Run `docker build . -t curity-couchbase-datasource` to build the image
- Start the container using `docker run -ti --rm -p6749:6749 -e PASSWORD=mysecret curity-couchbase-datasource`

After that, go to the Curity [admin page](https://localhost:6749/admin), login using username `admin` and password `mysecret`(set in the run command above). Check that the plugin was loaded by clicking `Facilities -> Data Sources -> +New` in the top right corner. If you have the possibility to select Couchbase as a type, the plugin has been loaded.

Visit [Getting Started](https://curity.io/resources/getting-started/) to help with setting up your new Curity instance.