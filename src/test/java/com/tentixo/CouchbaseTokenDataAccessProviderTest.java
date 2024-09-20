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

import com.tentixo.token.CouchbaseTokenDataAccessProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.data.authorization.TokenStatus;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public final class CouchbaseTokenDataAccessProviderTest extends AbstractCouchbaseRunner
{

    private final CouchbaseTokenDataAccessProvider dap
            = new CouchbaseTokenDataAccessProvider(new CouchbaseExecutor(getConfiguration(null)));

    @Test
    public void insertToken()
    {
        var id = UUID.randomUUID();
        var token = new TokenAdapter(id.toString(), String.valueOf(id.hashCode()), "qwe-123", "purpose","usage","format", "openid",
                Instant.now().getEpochSecond(), Instant.now().plus(Duration.ofSeconds(10L)).getEpochSecond(),
                TokenStatus.issued, "secure-idp", "johndoe", new StringOrArrayAdapter("tests"),
                Instant.now().getEpochSecond(), Map.of("foo", "bar"));

        dap.create(token);
    }

    @Test
    public void readToken()
    {
        var id = UUID.randomUUID();
        var token = new TokenAdapter(String.valueOf(id.hashCode()), id.toString(), "qwe-123", "purpose","usage","format","openid",
                Instant.now().getEpochSecond(), Instant.now().plus(Duration.ofSeconds(10L)).getEpochSecond(),
                TokenStatus.issued, "secure-idp", "johndoe", new StringOrArrayAdapter("tests"),
                Instant.now().getEpochSecond(), Map.of("foo", "bar"));
        dap.create(token);
        var retrievedToken = dap.getByHash(token.getTokenHash());

        Assertions.assertEquals(token.getTokenHash(), retrievedToken.getTokenHash());
        Assertions.assertEquals(token.getData(), retrievedToken.getData());
        Assertions.assertEquals(token.getId(), retrievedToken.getId());
        Assertions.assertEquals(token.getDelegationsId(), retrievedToken.getDelegationsId());
        Assertions.assertEquals(token.getScope(), retrievedToken.getScope());
        Assertions.assertEquals(token.getFormat(), retrievedToken.getFormat());
        Assertions.assertEquals(token.getIssuer(), retrievedToken.getIssuer());
        Assertions.assertEquals(token.getAudience().getValues(), retrievedToken.getAudience().getValues());
    }

    @Test
    public void setStatus()
    {
        var id = UUID.randomUUID();
        var token = new TokenAdapter(id.toString(), String.valueOf(id.hashCode()), "qwe-123", "purpose","usage","format","openid",
                Instant.now().getEpochSecond(), Instant.now().plus(Duration.ofSeconds(10L)).getEpochSecond(),
                TokenStatus.issued, "secure-idp", "johndoe", new StringOrArrayAdapter("tests"),
                Instant.now().getEpochSecond(), Map.of("foo", "bar"));
        dap.create(token);
        dap.setStatusByTokenHash(token.getTokenHash(), TokenStatus.revoked);

        var retrievedToken = dap.getByHash(token.getTokenHash());
        Assertions.assertNotNull(retrievedToken);
        Assertions.assertEquals(token.getStatus(), retrievedToken.getStatus());
    }
}
