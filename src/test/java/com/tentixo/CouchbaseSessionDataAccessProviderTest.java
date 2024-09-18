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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.data.Session;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

public final class CouchbaseSessionDataAccessProviderTest extends AbstractCouchbaseRunner
{
    @Test
    public void insertSession()
    {
        var executor = new CouchbaseExecutor(getConfiguration(null));
        var dap = new CouchbaseSessionDataAccessProvider(executor);
        var sessionId = UUID.randomUUID().toString();
        var session = new Session(sessionId, Instant.now().plus(Duration.ofSeconds(10L)), "{\"foo\": \"bar\")");
        dap.insertSession(session);
    }

    @Test
    public void readSession()
    {
        var executor = new CouchbaseExecutor(getConfiguration(null));
        var dap = new CouchbaseSessionDataAccessProvider(executor);
        var sessionId = UUID.randomUUID().toString();
        var session = new Session(sessionId, Instant.now().plus(Duration.ofSeconds(10L)), "{\"foo\": \"bar\")");
        dap.insertSession(session);
        var retrievedSession = dap.getSessionById(sessionId);
        Assertions.assertEquals(session.getExpiresAt(), retrievedSession.getExpiresAt());
        Assertions.assertEquals(session.getData(), retrievedSession.getData());
        Assertions.assertEquals(session.getId(), retrievedSession.getId());
    }
}
