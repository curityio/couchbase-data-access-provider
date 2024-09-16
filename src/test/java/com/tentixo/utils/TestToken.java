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

package com.tentixo.utils;

import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.data.StringOrArray;
import se.curity.identityserver.sdk.data.authorization.Token;
import se.curity.identityserver.sdk.data.authorization.TokenStatus;

import java.util.Map;

public class TestToken implements Token
{
    private final String _id;
    private final String _hash;
    private final String _delegationId;
    private final String _scope;
    private final long _created;
    private final long _expires;
    private final boolean _active;
    private final TokenStatus _status;
    private final String _issuer;
    private final String _subject;
    private final StringOrArray _audience;
    private final long _notBefore;
    private final Map<String, Object> _data;

    public TestToken(String id, String hash, String delegationId, String scope, long created, long expires, boolean active,
                     TokenStatus status, String issuer, String subject, StringOrArray audience, long notBefore, Map<String, Object> data)
    {

        _id = id;
        _hash = hash;
        _delegationId = delegationId;
        _scope = scope;
        _created = created;
        _expires = expires;
        _active = active;
        _status = status;
        _issuer = issuer;
        _subject = subject;
        _audience = audience;
        _notBefore = notBefore;
        _data = data;
    }

    @Override
    public String getTokenHash()
    {
        return _hash;
    }

    @Override
    public @Nullable String getId()
    {
        return _id;
    }

    @Override
    public String getDelegationsId()
    {
        return _delegationId;
    }

    @Override
    public String getPurpose()
    {
        return "test-token";
    }

    @Override
    public String getUsage()
    {
        return "testing";
    }

    @Override
    public String getFormat()
    {
        return "mine";
    }

    @Override
    public @Nullable String getScope()
    {
        return _scope;
    }

    @Override
    public long getCreated()
    {
        return _created;
    }

    @Override
    public long getExpires()
    {
        return _expires;
    }

    @Override
    public boolean isActive()
    {
        return _active;
    }

    @Override
    public TokenStatus getEnumActiveStatus()
    {
        return TokenStatus.issued;
    }

    @Override
    public TokenStatus getStatus()
    {
        return _status;
    }

    @Override
    public String getIssuer()
    {
        return _issuer;
    }

    @Override
    public String getSubject()
    {
        return _subject;
    }

    @Override
    public StringOrArray getAudience()
    {
        return _audience;
    }

    @Override
    public long getNotBefore()
    {
        return _notBefore;
    }

    @Override
    public Map<String, Object> getData()
    {
        return _data;
    }
}
