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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.Attribute;

import java.time.Duration;

import static com.tentixo.testcontainers.CouchbaseContainerMetadata.BUCKET_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * You should have a document like below in the database:
 * {
 * "id": "node::user::personal_info::ilya",
 * "username": "ilya",
 * "password": "something"
 * }
 */
class CouchbaseCredentialDataAccessProviderTest extends AbstractCouchbaseRunner{

    private static CouchbaseCredentialDataAccessProvider credentialDataAccessProvider;

    @BeforeAll
    public static void setup() throws InterruptedException {
        CouchbaseExecutor ce = new CouchbaseExecutor(getConfiguration(null));
        credentialDataAccessProvider =
            new CouchbaseCredentialDataAccessProvider(ce);
        Cluster c = Cluster.connect(couchbaseContainer.getConnectionString(), couchbaseContainer.getUsername(), couchbaseContainer.getPassword());
        c.waitUntilReady(Duration.ofSeconds(2));

        Bucket b = c.bucket(BUCKET_NAME);
        Scope scope = b.scope(getConfiguration(null).getScope());
        Collection collection = scope.collection(CouchbaseUserAccountDataAccessProvider.ACCOUNT_COLLECTION_NAME);
        String json = """
              {
              "id": "node::user::personal_info::ilya",
              "username": "ilya",
              "password": "something"
              }
            """;
        JsonObject jo = JsonObject.fromJson(json);
        collection.upsert("node::user::personal_info::ilya", jo , UpsertOptions.upsertOptions().durability(DurabilityLevel.MAJORITY_AND_PERSIST_TO_ACTIVE));
        c.close();
     }

    @Test
    void updatePasswordTest() {
        credentialDataAccessProvider.updatePassword(AccountAttributes.of(
                Attribute.of(AccountAttributes.USER_NAME, "ilya"),
                Attribute.of(AccountAttributes.PASSWORD, "ilya1")
        ));
        var result = credentialDataAccessProvider.verifyPassword("ilya", "ilya");
        assertEquals("ilya1", result.getSubjectAttributes().get("password").getValue());
        credentialDataAccessProvider.updatePassword(AccountAttributes.of(
                Attribute.of(AccountAttributes.USER_NAME, "ilya"),
                Attribute.of(AccountAttributes.PASSWORD, "something")
        ));
        result = credentialDataAccessProvider.verifyPassword("ilya", "ilya");
        assertEquals("something", result.getSubjectAttributes().get("password").getValue());
    }

    @Test
    void verifyPasswordTest() {
        var result = credentialDataAccessProvider.verifyPassword("ilya", "ilya");
        assertNotNull(result.getSubjectAttributes().get("username"));
        assertNotNull(result.getSubjectAttributes().get("password"));
        assertEquals("ilya", result.getSubjectAttributes().get("username").getValue());
        assertEquals("something", result.getSubjectAttributes().get("password").getValue());
    }
}
