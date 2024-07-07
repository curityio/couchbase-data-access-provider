package com.tentixo.testcontainers;

import org.testcontainers.couchbase.BucketDefinition;
import org.testcontainers.utility.DockerImageName;

public class CouchbaseContainerMetadata {
    public static final String BUCKET_NAME = "curity";
    public static final String USERNAME = "Administrator";
    public static final String PASSWORD = "password";
    public static final BucketDefinition bucketDefinition = new BucketDefinition(BUCKET_NAME);
    public static final DockerImageName COUCHBASE_IMAGE_ENTERPRISE = DockerImageName.parse("couchbase:enterprise")
        .asCompatibleSubstituteFor("couchbase/server")
        .withTag("7.6.1");
}
