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
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.AttributeTableView;
import se.curity.identityserver.sdk.attribute.Attributes;

import java.util.List;

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
class CouchbaseExecutorTest {

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
                                            configuration.getCollection()))))
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
                                "org_permissions from `:bucket`.`:scope`.`:collection` data " +
                                "WHERE META().id = \"edge::user_org:::subject\"");
        CouchbaseAttributeDataAccessProvider dataAccessProvider
                = new CouchbaseAttributeDataAccessProvider(configuration, new CouchbaseExecutor(configuration));
        var result = dataAccessProvider.getAttributes("morre");
        List<@Nullable ?> resultColumns = result.getColumnValues("org_permissions");
        resultColumns.removeAll(singleton(null));
        assertEquals(1, resultColumns.size());
    }

    private CouchbaseDataAccessProviderConfiguration getConfiguration(String query) {
        return new CouchbaseDataAccessProviderConfiguration() {
            @Override
            public String getHost() {
                return "localhost"; //your host
            }

            @Override
            public boolean useTls() {
                return false;
            }

            @Override
            public String getUserName() {
                return "cms-admin"; //your username
            }

            @Override
            public String getPassword() {
                return "cms-admin"; //your password
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
            public String getCollection() {
                return "curity";
            }

            @Override
            public String getClaimQuery() {
                return ofNullable(query).orElse(query);
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
