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

import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.data.Session;
import se.curity.identityserver.sdk.datasource.SessionDataAccessProvider;

import java.time.Instant;

public final class CouchbaseSessionDataAccessProvider implements SessionDataAccessProvider {
    @Override
    public @Nullable Session getSessionById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void insertSession(Session session) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSession(Session session) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSessionExpiration(String id, Instant expiresAt) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteSessionState(String id) {
        throw new UnsupportedOperationException();
    }
}
