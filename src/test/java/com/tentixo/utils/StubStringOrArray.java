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

import com.tentixo.CouchbaseTokenDataAccessProviderTest;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.shaded.org.apache.commons.lang3.NotImplementedException;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.data.StringOrArray;
import se.curity.identityserver.sdk.errors.NoSingleValueException;

import java.util.List;
import java.util.function.Supplier;

public class StubStringOrArray implements StringOrArray
{
    private final String _value;

    public StubStringOrArray(String value)
    {
        _value = value;
    }

    @Override
    public @Nullable String getValueOrError() throws NoSingleValueException
    {
        return _value;
    }

    @Override
    public @Nullable String getValueOrError(Supplier<? extends NoSingleValueException> exceptionSupplier) throws NoSingleValueException
    {
        return _value;
    }

    @Override
    public List<String> getValues()
    {
        return List.of(_value);
    }

    @Override
    public @Nullable Object toStringOrArray()
    {
        return new StubStringOrArray(_value);
    }

    @Override
    public boolean contains(String value)
    {
        return value.equals(_value);
    }

    @Override
    public int compareTo(@NotNull StringOrArray o)
    {
        throw new NotImplementedException("compareTo");
    }
}
