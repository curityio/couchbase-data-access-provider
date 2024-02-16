package com.tentixo.descriptor;

import com.tentixo.CouchbaseAttributeDataAccessProvider;
import com.tentixo.CouchbaseCredentialDataAccessProvider;
import com.tentixo.CouchbaseExecutor;
import com.tentixo.CouchbaseUserAccountDataAccessProvider;
import com.tentixo.configuration.CouchbaseDataAccessProviderConfiguration;
import se.curity.identityserver.sdk.plugin.ManagedObject;
import se.curity.identityserver.sdk.plugin.descriptor.DataAccessProviderPluginDescriptor;

import java.util.Optional;


/**
 * An entry point for a Couchbase data access provider
 */
public class CouchbaseDataAccessProviderDescriptor
        implements DataAccessProviderPluginDescriptor<CouchbaseDataAccessProviderConfiguration> {
    @Override
    public String getPluginImplementationType() {
        return "couchbase";
    }

    /**
     * Retrieves the data access provider class for attributes in Couchbase.
     *
     * @return The class representing the data access provider for attributes in Couchbase.
     */
    @Override
    public Class<CouchbaseAttributeDataAccessProvider> getAttributeDataAccessProvider() {
        return CouchbaseAttributeDataAccessProvider.class;
    }

    /**
     * Retrieves the configuration type for the Couchbase data access provider.
     *
     * @return The configuration type for the Couchbase data access provider.
     */
    @Override
    public Class<CouchbaseDataAccessProviderConfiguration> getConfigurationType() {
        return CouchbaseDataAccessProviderConfiguration.class;
    }

    /**
     * Retrieves the class representing the credential data access provider in Couchbase.
     *
     * @return The class representing the credential data access provider in Couchbase.
     */
    @Override
    public Class<CouchbaseCredentialDataAccessProvider> getCredentialDataAccessProvider() {
        return CouchbaseCredentialDataAccessProvider.class;
    }

    /**
     * Retrieves the data access provider class for user account data in Couchbase.
     *
     * @return The class representing the user account data access provider in Couchbase.
     */
    @Override
    public Class<CouchbaseUserAccountDataAccessProvider> getUserAccountDataAccessProvider() {
        return CouchbaseUserAccountDataAccessProvider.class;
    }

    /**
     * Creates a new managed object for the Couchbase executor based on the provided configuration.
     *
     * @param configuration The configuration object containing the settings for the Couchbase data access provider.
     * @return An optional managed object of type ManagedObject<CouchbaseDataAccessProviderConfiguration>.
     */
    @Override
    public Optional<? extends ManagedObject<CouchbaseDataAccessProviderConfiguration>> createManagedObject(
            CouchbaseDataAccessProviderConfiguration configuration) {
        return Optional.of(new CouchbaseExecutor(configuration));
    }
}