package com.tentixo;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.data.StringOrArray;
import se.curity.identityserver.sdk.errors.NoSingleValueException;

public final class StringOrArrayAdapter implements StringOrArray {

    private static final StringOrArrayAdapter EMPTY = new StringOrArrayAdapter(Collections.emptyList());
    private List<String> _values;

    @JsonCreator
    public StringOrArrayAdapter(@JsonProperty("values") @Nullable Object values) {
        if (values == null) {
            Collections.emptyList();
        } else if (values instanceof String) {
            this._values = values == null ? Collections.emptyList() : Collections.singletonList((String) values);
        } else if (values instanceof Collection)  {
            this._values = Collections.unmodifiableList(new ArrayList((Collection<String> )values));
        }
    }

    @JsonIgnore
    public @Nullable String valueOrError() {
        if (this._values.isEmpty()) {
            return null;
        } else if (this._values.size() == 1) {
            return (String)this._values.get(0);
        } else {
            return "error, no single value";
        }
    }

    @JsonIgnore
    @Override
    public @Nullable String getValueOrError() throws NoSingleValueException {
        return this.getValueOrError(NoSingleValueException::new);
    }

    @JsonIgnore
    @Override
    public @Nullable String getValueOrError(Supplier<? extends NoSingleValueException> exceptionSupplier) throws NoSingleValueException {
        if (this._values.isEmpty()) {
            return null;
        } else if (this._values.size() == 1) {
            return (String)this._values.get(0);
        } else {
            throw (NoSingleValueException)exceptionSupplier.get();
        }
    }

    @JsonProperty("values")
    public List<String> getValues() {
        return this._values;
    }

    public @Nullable Object toStringOrArray() {
        if (this._values.isEmpty()) {
            return null;
        } else {
            return this._values.size() == 1 ? this._values.get(0) : this._values;
        }
    }

    public boolean contains(String value) {
        return this._values.contains(value);
    }

    public String toString() {
        if (this._values.isEmpty()) {
            return "_null_";
        } else {
            return this._values.size() == 1 ? (String)this._values.get(0) : this._values.toString();
        }
    }

    public boolean equals(Object other) {
        HashSet values;
        HashSet others;
        if (other instanceof StringOrArray) {
            values = new HashSet(this._values);
            others = new HashSet(((StringOrArray)other).getValues());
            return values.equals(others);
        } else if (other instanceof String) {
            return this._values.size() == 1 && ((String)this._values.get(0)).equals(other);
        } else if (other instanceof Collection) {
            values = new HashSet(this._values);
            others = new HashSet((Collection)other);
            return values.equals(others);
        } else {
            return this._values.isEmpty() && other == null;
        }
    }

    public int compareTo(StringOrArray other) {
        Set<String> values = new LinkedHashSet(this._values);
        Set<String> others = new LinkedHashSet(other.getValues());
        if (values.size() == others.size() && values.containsAll(others)) {
            return 0;
        } else {
            Iterator<String> valuesIter = values.iterator();
            Iterator<String> othersIter = others.iterator();

            while(valuesIter.hasNext() && othersIter.hasNext()) {
                String v1 = (String)valuesIter.next();
                String v2 = (String)othersIter.next();
                int valueComparison = v1.compareTo(v2);
                if (valueComparison != 0) {
                    return valueComparison;
                }
            }

            if (valuesIter.hasNext()) {
                return 1;
            } else {
                return othersIter.hasNext() ? -1 : 0;
            }
        }
    }
}
