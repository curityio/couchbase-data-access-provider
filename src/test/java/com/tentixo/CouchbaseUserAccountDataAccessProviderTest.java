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
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.Attribute;
import se.curity.identityserver.sdk.attribute.scim.v2.ResourceAttributes;
import se.curity.identityserver.sdk.data.query.ResourceQuery.Inclusions;
import se.curity.identityserver.sdk.data.query.ResourceQueryResult;
import se.curity.identityserver.sdk.data.update.AttributeReplacements;
import se.curity.identityserver.sdk.data.update.AttributeUpdate;

import java.time.Duration;
import java.util.Map;
import java.util.Set;

import static com.tentixo.testcontainers.CouchbaseContainerMetadata.BUCKET_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * You should have a document like below in the database:
 * {
 * "id": "node::user::personal_info::morre",
 * "username": "morre",
 * "email": "morre@tentixo.com",
 * "phone": "+375295672678"
 * }
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CouchbaseUserAccountDataAccessProviderTest  extends AbstractCouchbaseRunner{

    private static CouchbaseUserAccountDataAccessProvider dataAccessProvider;

    @BeforeAll
    public static void setup() throws InterruptedException {
        CouchbaseExecutor ce = new CouchbaseExecutor(getConfiguration(null));
        dataAccessProvider =
            new CouchbaseUserAccountDataAccessProvider(ce);
        Cluster c = Cluster.connect(couchbaseContainer.getConnectionString(), couchbaseContainer.getUsername(), couchbaseContainer.getPassword());
        c.waitUntilReady(Duration.ofSeconds(2));

        // Clear any created accounts
        var resources = ce.findAllPageable(0, 100).getResources();
        resources.forEach(resource -> ce.delete(resource.getId()));

        Bucket b = c.bucket(BUCKET_NAME);
        Scope scope = b.scope(getConfiguration(null).getScope());
        Collection collection = scope.collection(CouchbaseUserAccountDataAccessProvider.ACCOUNT_COLLECTION_NAME);
        String json = """
             {
              "id": "node::user::personal_info::morre",
              "username": "morre",
              "email": "morre@tentixo.com",
              "phone": "+375295672678"
             }
            """;
        JsonObject jo = JsonObject.fromJson(json);
        collection.upsert("node::user::personal_info::morre", jo , UpsertOptions.upsertOptions().durability(DurabilityLevel.MAJORITY_AND_PERSIST_TO_ACTIVE));
        c.close();
    }

    @Test
    void getByUserNameTest() {
        ResourceAttributes<?> result =
                dataAccessProvider
                        .getByUserName("morre",
                                Inclusions.of(Set.of("email")));
        assertNotNull(result.get("email"));
    }

    @Test
    void getByEmailTest() {
        ResourceAttributes<?> result =
                dataAccessProvider
                        .getByEmail("morre@tentixo.com",
                                Inclusions.of(Set.of("email")));
        assertNotNull(result.get("email"));
    }

    @Test
    void getByPhoneTest() {
        ResourceAttributes<?> result =
                dataAccessProvider
                        .getByPhone("+375295672678",
                                Inclusions.of(Set.of("email")));
        assertNotNull(result.get("email"));
    }

    @Test
    @Order(1)
    void createTest() {
        AccountAttributes result = dataAccessProvider.create(
                AccountAttributes.of(Attribute.of("userName", "newGuy"),
                        Attribute.of("phone", "123")));
        assertEquals("123", result.toMap().get("phone"));
    }

    @Test
    @Order(3)
    void deleteTest() {
        dataAccessProvider.delete("newGuy");
        var result = dataAccessProvider.getByUserName("newGuy",
                Inclusions.of(Set.of("email")));
        assertNull(result);
    }

    @Test
    @Order(2)
    void getAllTest() {
        ResourceQueryResult result = dataAccessProvider.getAll(0, 2);
        assertEquals(2, result.getTotalResults());
        result = dataAccessProvider.getAll(0, 1);
        assertEquals(1, result.getTotalResults());
        result = dataAccessProvider.getAll(1, 2);
        assertEquals(1, result.getTotalResults());
    }

    @Test
    void updateByUsernameTest() {
        ResourceAttributes<?> result = dataAccessProvider.update(
                AccountAttributes.of(Attribute.of("userName", "morre"),
                        Attribute.of("phone", "+++")),
                Inclusions.of(Set.of("email", "phone")));
        assertEquals("+++", result.get("phone").getValue());
        dataAccessProvider.update(
                AccountAttributes.of(Attribute.of("userName", "morre"),
                        Attribute.of("phone", "+375295672678")),
                Inclusions.of(Set.of("email", "phone")));
    }

    @Test
    void updateByAccountIdTest() {
        ResourceAttributes<?> result = dataAccessProvider.update(
                "morre", Map.of("username", "morre", "phone", "---"),
                Inclusions.of(Set.of("email", "phone")));
        assertEquals("---", result.get("phone").getValue());
        dataAccessProvider.update(
                AccountAttributes.of(Attribute.of("userName", "morre"),
                        Attribute.of("phone", "+375295672678")),
                Inclusions.of(Set.of("email", "phone")));
    }

    @Test
    void patchTest() {
        ResourceAttributes<?> result = dataAccessProvider.patch(
                "morre",
                new AttributeUpdate(AttributeReplacements.of(Set.of(Attribute.of("phone", "+++")))),
                Inclusions.of(Set.of("email", "phone")));
        assertEquals("+++", result.get("phone").getValue());
        dataAccessProvider.update(
                AccountAttributes.of(Attribute.of("userName", "morre"),
                        Attribute.of("phone", "+375295672678")),
                Inclusions.of(Set.of("email", "phone")));
    }

}
