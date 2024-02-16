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