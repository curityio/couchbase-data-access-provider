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

package com.tentixo;

import com.tentixo.configuration.CouchbaseConnectionManagedObject;
import com.tentixo.configuration.CouchbaseDataAccessProviderConfiguration;
import com.tentixo.token.CouchbaseDelegationDataAccessProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.attribute.ContextAttributes;
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.data.authorization.DelegationConsentResult;
import se.curity.identityserver.sdk.data.authorization.DelegationStatus;
import se.curity.identityserver.sdk.data.authorization.ScopeClaim;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.tentixo.token.CouchbaseDelegationDataAccessProvider.DELEGATION_COLLECTION_NAME;

class CouchbaseDelegationDataAccessProviderTest extends AbstractCouchbaseRunner
{
    private final CouchbaseDataAccessProviderConfiguration configuration = getConfiguration(null);
    private final CouchbaseConnectionManagedObject clusterConnection = new CouchbaseConnectionManagedObject(configuration);
    private final CouchbaseDelegationDataAccessProvider dap = new CouchbaseDelegationDataAccessProvider(clusterConnection);

    {
    }

    @Test
    void create()
    {
        var delegation = new TestDelegation("johndoe", "my-client");
        dap.create(delegation);
    }

    @Test
    void getById()
    {
        var delegation = new TestDelegation("johndoe", "my-client");
        dap.create(delegation);
        var retrievedDelegation = dap.getById(delegation.getId());
        Assertions.assertEquals(delegation.getId(), retrievedDelegation.getId());
        Assertions.assertEquals(delegation.getClientId(), retrievedDelegation.getClientId());
        Assertions.assertEquals(delegation.getOwner(), retrievedDelegation.getOwner());
        Assertions.assertEquals(delegation.getClaimMap().keySet(), retrievedDelegation.getClaimMap().keySet());
        Assertions.assertEquals(delegation.getEnumActiveStatus(), retrievedDelegation.getEnumActiveStatus());
        Assertions.assertEquals(delegation.getAuthorizationCodeHash(), retrievedDelegation.getAuthorizationCodeHash());
        Assertions.assertEquals(delegation.getMtlsClientCertificate(), retrievedDelegation.getMtlsClientCertificate());
        Assertions.assertEquals(delegation.getMtlsClientCertificateDN(), retrievedDelegation.getMtlsClientCertificateDN());
        Assertions.assertEquals(delegation.getMtlsClientCertificateX5TS256(), retrievedDelegation.getMtlsClientCertificateX5TS256());
        Assertions.assertEquals(delegation.getAuthenticationAttributes().getSubject(), retrievedDelegation.getAuthenticationAttributes().getSubject());
    }

    @Test
    void setStatus() throws InterruptedException {
        var delegation = new TestDelegation("johndoe", "my-client");
        dap.create(delegation);
        dap.setStatus(delegation.getId(), DelegationStatus.revoked);
        Delegation d = clusterConnection.getScope().collection(DELEGATION_COLLECTION_NAME)
                .get(delegation.getId()).contentAs(Delegation.class);
        Assertions.assertEquals(DelegationStatus.revoked, d.getStatus());
        // Should return null because revoked
        var retrievedDelegation = dap.getById(delegation.getId());
        Assertions.assertNull(retrievedDelegation);
    }

    @Test
    void getByAuthorizationCodeHash()
    {

    }

    @Test
    void getByOwner()
    {
        var randomUser = UUID.randomUUID().toString();
        dap.create(new TestDelegation(randomUser, "my-client"));
        dap.create(new TestDelegation(randomUser, "my-client"));
        dap.create(new TestDelegation(randomUser, "my-client"));
        var listOfDelegations = dap.getByOwner(randomUser, 0, 50);
        Assertions.assertEquals(3, listOfDelegations.size());
    }

    @Test
    void getAllActive()
    {
    }

    @Test
    void getCountAllActive()
    {
    }

    @Test
    void getCountByOwner()
    {
    }

    @Test
    void getAll()
    {
    }

    static class TestDelegation implements Delegation
    {
        private final String _id;
        private final String _subject;
        private final String _clientId;
        private final long _created;

        TestDelegation(String subject, String clientId)
        {
            _subject = subject;
            _created = Instant.now().getEpochSecond();
            _id = UUID.randomUUID().toString();
            _clientId = clientId;
        }

        @Override
        public String getId()
        {
            return _id;
        }

        @Override
        public String getOwner()
        {
            return _subject;
        }

        @Override
        public long getCreated()
        {
            return _created;
        }

        @Override
        public String getScope()
        {
            return "";
        }

        @Override
        public Set<ScopeClaim> getScopeClaims()
        {
            return Set.of();
        }

        @Override
        public Map<String, Object> getClaimMap()
        {
            return getClaims();
        }

        @Override
        public String getClientId()
        {
            return _clientId;
        }

        @Override
        public @Nullable String getRedirectUri()
        {
            return null;
        }

        @Override
        public @Nullable String getAuthorizationCodeHash()
        {
            return null;
        }

        @Override
        public AuthenticationAttributes getAuthenticationAttributes()
        {

            return AuthenticationAttributes.of(_subject, ContextAttributes.empty());
        }

        @Override
        public Map<String, Object> getCustomClaimValues()
        {
            return Map.of("custom-foo", "bar");
        }

        @Override
        public long getExpires()
        {
            return _created + 300;
        }

        @Override
        public DelegationStatus getStatus()
        {
            return DelegationStatus.issued;
        }

        @Override
        public DelegationStatus getEnumActiveStatus()
        {
            return DelegationStatus.issued;
        }

        @Override
        public @Nullable String getMtlsClientCertificate()
        {
            return null;
        }

        @Override
        public @Nullable String getMtlsClientCertificateX5TS256()
        {
            return null;
        }

        @Override
        public @Nullable String getMtlsClientCertificateDN()
        {
            return null;
        }

        @Override
        public @Nullable DelegationConsentResult getConsentResult()
        {
            return null;
        }

        @Override
        public Map<String, Object> getClaims()
        {
            return Map.of("foo", "bar");
        }
    }
}
