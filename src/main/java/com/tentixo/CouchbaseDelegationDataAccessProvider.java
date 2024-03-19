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
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.data.authorization.DelegationStatus;
import se.curity.identityserver.sdk.data.query.ResourceQuery;
import se.curity.identityserver.sdk.datasource.DelegationDataAccessProvider;

import java.util.Collection;

/**
 * https://curity.io/docs/idsvr-java-plugin-sdk/latest/se/curity/identityserver/sdk/datasource/DelegationDataAccessProvider.html
 */
public final class CouchbaseDelegationDataAccessProvider implements DelegationDataAccessProvider {
    @Override
    public @Nullable Delegation getById(String id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void create(Delegation delegation) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long setStatus(String id, DelegationStatus status) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends Delegation> getByOwner(String owner, long startIndex, long count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCountByOwner(String owner) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends Delegation> getAllActive(long startIndex, long count) {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getCountAllActive() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends Delegation> getAll(ResourceQuery query) {
        throw new UnsupportedOperationException();
    }
}
