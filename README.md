This library is designed to be a Curity Couchbase datasource plugin

Library "execution" starts with the entrypoint
described [here](/src/main/resources/META-INF/services/se.curity.identityserver.sdk.plugin.descriptor.DataAccessProviderPluginDescriptor).  
You should keep this file location and structure, same for the other files (if possible).

Library can't "start" in a common sense, you can only build it and apply to your Curity instance as described below.

## Plugin deployment flow ##

You must build the library to get something in the target folder. This will be a plugin source.

- either execute `mvn clean package` being in the [root](.) directory, or by using Intellij Idea Maven toolbar, and
  ensure that all tests are passed
- get your Curity Docker container started, remember its name and obtain its id by
  executing `docker ps -aqf "name=containername"` (Windows)
- if you are not using Windows, look for the ways to get your container id for different OS
- execute `docker cp .\target\lib [container_id]:/opt/idsvr/usr/share/plugins/couchbase/`  
  being in the [root](.) directory. This will copy the library sources to the Curity container instance.
- use your Curity container's name to restart it with `docker restart [container_name]`

After that, go to the Curity [admin page](https://localhost:6749/), select Data sources on the right panel, and if you
see Coucbase here, that means everything is done successfully!

## Build docker image

- either execute `mvn clean package` being in the [root](.) directory, or by using Intellij Idea Maven toolbar
- run `docker build -f Dockerfile.idsvr . -t curity-couchbase_datasource` command
