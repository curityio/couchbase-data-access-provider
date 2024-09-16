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
import com.tentixo.utils.StubStringOrArray;
import com.tentixo.utils.TestToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.data.authorization.TokenStatus;

import java.time.Duration;
import java.time.Instant;
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
        var token = new TestToken(id.toString(), String.valueOf(id.hashCode()), "qwe-123", "openid",
                Instant.now().getEpochSecond(), Instant.now().plus(Duration.ofSeconds(10L)).getEpochSecond(),
                true, TokenStatus.issued, "secure-idp", "johndoe", new StubStringOrArray("tests"),
                Instant.now().getEpochSecond(), Map.of("foo", "bar"));

        dap.create(token);
    }

    @Test
    public void readToken()
    {
        var id = UUID.randomUUID();
        var token = new TestToken(id.toString(), String.valueOf(id.hashCode()), "qwe-123", "openid",
                Instant.now().getEpochSecond(), Instant.now().plus(Duration.ofSeconds(10L)).getEpochSecond(),
                true, TokenStatus.issued, "secure-idp", "johndoe", new StubStringOrArray("tests"),
                Instant.now().getEpochSecond(), Map.of("foo", "bar"));
        dap.create(token);
        var retrievedToken = dap.getByHash(token.getTokenHash());
        Assertions.assertEquals(token, retrievedToken);
    }

    @Test
    public void setStatus()
    {
        var id = UUID.randomUUID();
        var token = new TestToken(id.toString(), String.valueOf(id.hashCode()), "qwe-123", "openid",
                Instant.now().getEpochSecond(), Instant.now().plus(Duration.ofSeconds(10L)).getEpochSecond(),
                true, TokenStatus.issued, "secure-idp", "johndoe", new StubStringOrArray("tests"),
                Instant.now().getEpochSecond(), Map.of("foo", "bar"));
        dap.create(token);
        dap.setStatusByTokenHash(token.getTokenHash(), TokenStatus.revoked);

        var retrievedToken = dap.getByHash(token.getTokenHash());
        Assertions.assertNotNull(retrievedToken);
        Assertions.assertEquals(token.getStatus(), retrievedToken.getStatus());
    }
}
