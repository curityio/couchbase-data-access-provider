package com.tentixo;

import com.tentixo.configuration.CouchbaseDataAccessProviderConfiguration;
import se.curity.identityserver.sdk.attribute.AttributeTableView;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.datasource.AttributeDataAccessProvider;

import static java.util.Collections.singletonList;

/**
 * Provides data access for retrieving attributes from Couchbase.
 */
public class CouchbaseAttributeDataAccessProvider implements AttributeDataAccessProvider {

    private final CouchbaseExecutor _couchbaseExecutor;
    private final CouchbaseDataAccessProviderConfiguration _configuration;
    private static final String SUBJECT_MARK = ":subject";
    private static final String BUCKET_MARK = ":bucket";
    private static final String SCOPE_MARK = ":scope";
    private static final String COLLECTION_MARK = ":collection";

    public CouchbaseAttributeDataAccessProvider(CouchbaseDataAccessProviderConfiguration configuration,
                                                CouchbaseExecutor couchbaseExecutor) {
        _configuration = configuration;
        _couchbaseExecutor = couchbaseExecutor;
    }

    /**
     * Retrieves attributes for a given subject from Couchbase.
     *
     * @param subject The subject for which attributes are retrieved.
     * @return An AttributeTableView object containing the retrieved attributes.
     */
    @Override
    public AttributeTableView getAttributes(String subject) {
        var query = _configuration.getClaimQuery()
                .replace(BUCKET_MARK, _configuration.getBucket())
                .replace(SCOPE_MARK, _configuration.getScope())
                .replace(COLLECTION_MARK, _configuration.getCollection())
                .replace(SUBJECT_MARK, subject);
        return AttributeTableView.ofAttributes(
                singletonList(Attributes.fromMap(
                        _couchbaseExecutor.executeQueryForSingleResult(query)))
        );
    }
}