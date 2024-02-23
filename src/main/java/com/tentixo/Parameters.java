/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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
