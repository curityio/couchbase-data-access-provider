package com.tentixo;

import lombok.Getter;

/**
 * Enum representing parameters used in a database query.
 */
@Getter
public enum Parameters
{
    USERNAME("userName", "username"), EMAIL("emails", "email"), PHONE("phoneNumbers", "phone");

    private final String scimName;
    private final String name;

    Parameters(String scimName, String name)
    {
        this.scimName = scimName;
        this.name = name;
    }

}
