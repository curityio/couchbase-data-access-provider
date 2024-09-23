/*
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.tentixo.configuration;

import com.couchbase.client.core.error.CouchbaseException;
import com.couchbase.client.java.*;
import com.tentixo.CurityJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.plugin.ManagedObject;

import java.util.concurrent.atomic.AtomicReference;

import static com.tentixo.CouchbaseUserAccountDataAccessProvider.ACCOUNT_COLLECTION_NAME;

public final class CouchbaseConnectionManagedObject extends ManagedObject<CouchbaseDataAccessProviderConfiguration> {
    private static final Logger _logger = LoggerFactory.getLogger(CouchbaseConnectionManagedObject.class);
    private final CouchbaseDataAccessProviderConfiguration _configuration;
    private final AtomicReference<Cluster> _cluster;


    private @Nullable Scope _scope;

    public CouchbaseConnectionManagedObject(CouchbaseDataAccessProviderConfiguration configuration) {
        super(configuration);
        _configuration = configuration;
        _cluster = new AtomicReference<>();
    }

    /**
     * Get an initialized cluster instance
     *
     * @return initialized cluster
     */
    public Cluster getCluster() {
        if (_cluster.get() == null) {
            synchronized (_cluster) {
                if (_cluster.get() == null) {
                   _cluster.set(initCluster(_configuration));
                }
            }
        }
        return _cluster.get();
    }

    public Collection getAccountCollection() {
        return getCluster().bucket(_configuration.getBucket()).collection(ACCOUNT_COLLECTION_NAME);
    }

    public Scope getScope() {
        return getCluster().bucket(_configuration.getBucket()).scope(_configuration.getScope());
    }

    /**
     * Initializes the couchbase cluster instance with the provided configuration.
     *
     * @param configuration The configuration object containing the necessary information for initialization.
     */
    private Cluster initCluster(CouchbaseDataAccessProviderConfiguration configuration) {
        try {
            var connectionString = configuration.getConnectionString();
            var username = configuration.getUserName();
            var password = configuration.getPassword();

            ClusterOptions options = ClusterOptions.clusterOptions(username, password)
                    .environment(env -> env
                            .jsonSerializer(CurityJsonSerializer.create())

                    );
            var cluster = Cluster.connect(connectionString, options);

            Bucket bucket = cluster.bucket(configuration.getBucket());
            if (bucket == null) {
                throw _configuration.getExceptionFactory()
                        .configurationException("Given bucket does not exist: " + configuration.getBucket());
            }
            var scope = bucket.scope(configuration.getScope());
            if (scope == null) {
                bucket.collections().createScope(configuration.getScope());
                scope = bucket.scope(configuration.getScope());
            }
            DBSetupRunners setupRunners = new DBSetupRunners();
            setupRunners.run(cluster, bucket, scope);

            return cluster;
        } catch (CouchbaseException e) {
            _logger.error("Init error! {}", e.getMessage());
            _cluster.getAndSet(null).close();
            throw _configuration.getExceptionFactory().serviceUnavailable();
        }
    }
}
