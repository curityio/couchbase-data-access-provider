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
import se.curity.identityserver.sdk.data.authorization.Token;
import se.curity.identityserver.sdk.data.authorization.TokenStatus;
import se.curity.identityserver.sdk.datasource.TokenDataAccessProvider;

public final class CouchbaseTokenDataAccessProvider implements TokenDataAccessProvider {
    @Override
    public @Nullable Token getByHash(String tokenHash) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(Token token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public @Nullable String getStatus(String tokenHash) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long setStatusByTokenHash(String tokenHash, TokenStatus newStatus) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long setStatus(String tokenId, TokenStatus newStatus) {
        // TODO: This is a deprecated method and should not be used.
        throw new UnsupportedOperationException();
    }

}
