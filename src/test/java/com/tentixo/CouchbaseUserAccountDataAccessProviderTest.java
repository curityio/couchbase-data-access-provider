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

import com.tentixo.configuration.CouchbaseDataAccessProviderConfiguration;
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

import java.util.Map;
import java.util.Set;

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
class CouchbaseUserAccountDataAccessProviderTest {

    private static final CouchbaseUserAccountDataAccessProvider dataAccessProvider =
            new CouchbaseUserAccountDataAccessProvider(new CouchbaseExecutor(getConfiguration()));

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
    @Order(2)
    void deleteTest() {
        dataAccessProvider.delete("newGuy");
        var result = dataAccessProvider.getByUserName("newGuy",
                Inclusions.of(Set.of("email")));
        assertNull(result.get("email"));
    }

    @Test
    @Order(3)
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

    private static CouchbaseDataAccessProviderConfiguration getConfiguration() {
        return new CouchbaseDataAccessProviderConfiguration() {
            @Override
            public String getHost() {
                return "52.28.76.107";
            }

            @Override
            public boolean useTls() {
                return false;
            }

            @Override
            public String getUserName() {
                return "demo";
            }

            @Override
            public String getPassword() {
                return "5672678i";
            }

            @Override
            public String getBucket() {
                return "demo";
            }

            @Override
            public String getScope() {
                return "_default";
            }

            @Override
            public String getCollection() {
                return "curity";
            }

            @Override
            public String getClaimQuery() {
                return null;
            }

            @Override
            public boolean getUseScimParameterNames() {
                return false;
            }

            @Override
            public String id() {
                return "couchbase";
            }
        };
    }
}
