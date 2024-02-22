/*
 *  Copyright 2024 Curity AB
 *
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
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.AccountAttributes;
import se.curity.identityserver.sdk.attribute.Attribute;

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
class CouchbaseCredentialDataAccessProviderTest {

    private static final CouchbaseCredentialDataAccessProvider credentialDataAccessProvider =
            new CouchbaseCredentialDataAccessProvider(new CouchbaseExecutor(getConfiguration()));

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
