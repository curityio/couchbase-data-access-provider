/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.tentixo;

import com.couchbase.client.core.msg.kv.DurabilityLevel;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.Collection;
import com.couchbase.client.java.Scope;
import com.couchbase.client.java.json.JsonObject;
import com.couchbase.client.java.kv.UpsertOptions;
import com.tentixo.configuration.CouchbaseDataAccessProviderConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.AttributeTableView;
import se.curity.identityserver.sdk.attribute.Attributes;

import java.time.Duration;
import java.util.List;

import static com.tentixo.testcontainers.CouchbaseContainerMetadata.BUCKET_NAME;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Optional.ofNullable;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * You should have documents like below in the database:
 * {
 * "id": "edge::user_org::morre",
 * "to": "something"
 * },
 * <p>
 * {
 * "id": "edge::user_org::subject",
 * "to": "something"
 * }
 */
class CouchbaseExecutorTest  extends AbstractCouchbaseRunner{

    @BeforeAll
    public static void setup() throws InterruptedException {
        couchbaseContainer.start();
        CouchbaseExecutor ce = new CouchbaseExecutor(getConfiguration(null));
        new CouchbaseCredentialDataAccessProvider(ce);
        Cluster c = Cluster.connect(couchbaseContainer.getConnectionString(), couchbaseContainer.getUsername(), couchbaseContainer.getPassword());
        c.waitUntilReady(Duration.ofSeconds(2));

        Bucket b = c.bucket(BUCKET_NAME);
        Scope scope = b.scope(getConfiguration(null).getScope());
        Collection collection = scope.collection(CouchbaseUserAccountDataAccessProvider.ACCOUNT_COLLECTION_NAME);
        String json = """
              {
                  "id": "edge::user_org::morre",
                  "to": "something"
              }
            """;
        String json2 = """
              {
                  "id": "edge::user_org::subject",
                  "to": "something"
              }
            """;
        JsonObject jo = JsonObject.fromJson(json);
        JsonObject jo2 = JsonObject.fromJson(json2);
        b.waitUntilReady(Duration.ofSeconds(10));
        collection.upsert("edge::user_org::morre", jo , UpsertOptions.upsertOptions().durability(DurabilityLevel.MAJORITY_AND_PERSIST_TO_ACTIVE));
        collection.upsert("edge::user_org::subject", jo2 , UpsertOptions.upsertOptions().durability(DurabilityLevel.MAJORITY_AND_PERSIST_TO_ACTIVE));
        c.close();
    }

    @Test
    void executeQueryTest() {
        AttributeTableView result;
        var configuration = getConfiguration(null);
        try (CouchbaseExecutor couchbaseExecutor = new CouchbaseExecutor(configuration)) {
            result = AttributeTableView.ofAttributes(
                    singletonList(Attributes.fromMap(
                            couchbaseExecutor.executeQueryForSingleResult(
                                    String.format("SELECT ENCODE_JSON(data.`to`) as " +
                                                    "org_permissions from `%s`.`%s`.`%s` data " +
                                                    "WHERE META().id = \"edge::user_org::morre\"",
                                            configuration.getBucket(), configuration.getScope(),
                                        CouchbaseUserAccountDataAccessProvider.ACCOUNT_COLLECTION_NAME))))
            );
        }
        List<@Nullable ?> resultColumns = result.getColumnValues("org_permissions");
        resultColumns.removeAll(singleton(null));
        assertEquals(1, resultColumns.size());
    }

    @Test
    void getAttributeTest() {
        var configuration =
                getConfiguration("SELECT ENCODE_JSON(data.`to`) as " +
                                "org_permissions from `:bucket`.`:scope`.`curity-accounts` data " +
                                "WHERE META().id = \"edge::user_org:::subject\"");
        CouchbaseAttributeDataAccessProvider dataAccessProvider
                = new CouchbaseAttributeDataAccessProvider(configuration, new CouchbaseExecutor(configuration));
        var result = dataAccessProvider.getAttributes("morre");
        List<@Nullable ?> resultColumns = result.getColumnValues("org_permissions");
        resultColumns.removeAll(singleton(null));
        assertEquals(1, resultColumns.size());
    }

}
