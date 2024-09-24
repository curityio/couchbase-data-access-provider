package com.tentixo;

import com.tentixo.configuration.CouchbaseDataAccessProviderConfiguration;
import com.tentixo.testcontainers.CouchbaseContainerMetadata;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.couchbase.CouchbaseContainer;
import org.testcontainers.couchbase.CouchbaseService;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;

import static com.tentixo.testcontainers.CouchbaseContainerMetadata.COUCHBASE_IMAGE_ENTERPRISE;
import static com.tentixo.testcontainers.CouchbaseContainerMetadata.bucketDefinition;

@Testcontainers
public class AbstractCouchbaseRunner {
    final static CouchbaseContainer couchbaseContainer;

    static {
         couchbaseContainer = new CouchbaseContainer(COUCHBASE_IMAGE_ENTERPRISE)
                .withCredentials(CouchbaseContainerMetadata.USERNAME, CouchbaseContainerMetadata.PASSWORD)
                .withEnabledServices(CouchbaseService.KV, CouchbaseService.QUERY, CouchbaseService.INDEX)
                .withBucket(bucketDefinition.withQuota(100))
                .withStartupAttempts(10)
                .withStartupTimeout(Duration.ofSeconds(90))
                .waitingFor(Wait.forHealthcheck());
        couchbaseContainer.start();
    }

    public static CouchbaseDataAccessProviderConfiguration getConfiguration(String claim) {
        return new CouchbaseDataAccessProviderConfiguration() {
            @Override
            public String getHost() {
                return String.format("%s:%s", couchbaseContainer.getHost(), couchbaseContainer.getBootstrapCarrierDirectPort());
            }

            @Override
            public boolean useTls() {
                return false;
            }

            @Override
            public String getUserName() {
                return couchbaseContainer.getUsername();
            }

            @Override
            public String getPassword() {
                return couchbaseContainer.getPassword();
            }

            @Override
            public String getBucket() {
                return "curity";
            }

            @Override
            public String getScope() {
                return "_default";
            }

            @Override
            public String getClaimQuery() {
                return claim;
            }

            @Override
            public boolean getUseScimParameterNames() {
                return false;
            }

            @Override
            public Long getSessionsTtlRetainDuration() {
                return 24l * 60 * 60;
            }

            @Override
            public Long getNoncesTtlRetainDuration() {
                return 24l * 60 * 60;
            }

            @Override
            public Long getDelegationsTtlRetainDuration() {
                return 365l * 24 * 60 * 60;
            }

            @Override
            public Long getTokensTtlRetainDuration() {
                return 24l * 60 * 60;
            }

            @Override
            public Long getDevicesTtlRetainDuration() {
                return 24l * 60 * 60;
            }

            @Override
            public String id() {
                return "couchbase";
            }

        };
    }

}

