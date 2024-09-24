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

import com.tentixo.token.CouchbaseNonceDataAccessProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

public final class CouchbaseNonceDataAccessProviderTest extends AbstractCouchbaseRunner
{
    private final CouchbaseNonceDataAccessProvider dap
            = new CouchbaseNonceDataAccessProvider(getConfiguration(null),
            new CouchbaseExecutor(getConfiguration(null)));

    @Test
    public void createNonce()
    {
        dap.save(UUID.randomUUID().toString(), "{\"foo\": \"bar\"}", Instant.now().getEpochSecond(), 200L);
    }

    @Test
    public void readNonce()
    {
        var nonce = UUID.randomUUID().toString();
        String value = "{\"foo\": \"bar\"}";
        dap.save(nonce, value, Instant.now().getEpochSecond(), 200L);
        var retrievedNonceValue = dap.get(nonce);
        Assertions.assertEquals(value, retrievedNonceValue);
    }

    @Test
    public void consumeNonce()
    {
        var nonce = UUID.randomUUID().toString();
        dap.save(nonce, "{\"foo\": \"bar\"}", Instant.now().getEpochSecond(), 200L);
        dap.consume(nonce, Instant.now().getEpochSecond());
        var retrievedNonce = dap.get(nonce);
        Assertions.assertNull(retrievedNonce);
    }

    @Test
    public void nonceExpires() throws InterruptedException
    {
        var nonce = UUID.randomUUID().toString();
        dap.save(nonce, "{\"foo\": \"bar\"}", Instant.now().getEpochSecond(), 1L);
        Thread.sleep(1500);
        var retrievedNonce = dap.get(nonce);
        Assertions.assertNull(retrievedNonce);
    }
}
