 #
 #  Licensed under the Apache License, Version 2.0 (the "License");
 #  you may not use this file except in compliance with the License.
 #  You may obtain a copy of the License at
 #
 #      http://www.apache.org/licenses/LICENSE-2.0
 #
 #  Unless required by applicable law or agreed to in writing, software
 #  distributed under the License is distributed on an "AS IS" BASIS,
 #  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 #  See the License for the specific language governing permissions and
 #  limitations under the License.

FROM curity.azurecr.io/curity/idsvr:latest

#environment variables with default values
ARG AWS_ACCESS_KEY_ID=ID
ARG AWS_SECRET_ACCESS_KEY=KEY
ARG PASSWORD=admin
USER root
RUN apt-get update && apt-get upgrade -y
RUN apt-get install curl -y
RUN apt-get install unzip -y
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
RUN unzip awscliv2.zip
RUN ./aws/install
RUN aws configure list
RUN aws s3 cp s3://curity-couchbase-plugin/target/ /opt/idsvr/usr/share/plugins/couchbase/ --recursive
