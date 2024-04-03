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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.curity.identityserver.sdk.Nullable;
import se.curity.identityserver.sdk.attribute.*;
import se.curity.identityserver.sdk.datasource.CredentialDataAccessProvider;

import static java.util.Optional.ofNullable;

/**
 * The CouchbaseCredentialDataAccessProvider class is responsible for accessing and updating user credentials in Couchbase.
 */
// TODO: This uses the old style of credential storage. New implementations should use #{{@link se.curity.identityserver.sdk.datasource.CredentialStoringDataAccessProvider} and #{{@link se.curity.identityserver.sdk.datasource.CredentialVerifyingDataAccessProvider}}}
public class CouchbaseCredentialDataAccessProvider implements CredentialDataAccessProvider {

    private static final Logger _logger = LoggerFactory.getLogger(CouchbaseCredentialDataAccessProvider.class);
    private final CouchbaseExecutor _couchbaseExecutor;

    public CouchbaseCredentialDataAccessProvider(CouchbaseExecutor couchbaseExecutor) {
        _couchbaseExecutor = couchbaseExecutor;
    }

    /**
     * Updates the password for a given account.
     *
     * @param accountAttributes The account attributes containing the username and the new password.
     */
    @Override
    public void updatePassword(AccountAttributes accountAttributes) {
        var username = accountAttributes.getUserName();
        _logger.debug("Received update password request for username : {}", username);
        var newPassword = ofNullable(accountAttributes.getPassword());
        if (newPassword.isEmpty()) {
            _logger.warn("Cannot update account password, missing password value");
            return;
        }
        _couchbaseExecutor.updatePassword(username, newPassword.get());
        _logger.debug("Updated password for username : {}", username);
    }

    /**
     * Verifies the password for a given user.
     *
     * @param userName The username of the user.
     * @param password The password to be verified.
     * @return An AuthenticationAttributes object containing the subject attributes (username and account attributes)
     *         and an empty ContextAttributes object.
     */
    @Override
    public @Nullable AuthenticationAttributes verifyPassword(String userName, String password) {
        _logger.debug("Received request to verify password for username : {}", userName);
        Attributes accountAttributes = _couchbaseExecutor.getByParameter(Parameters.USERNAME, userName, null);
        if (accountAttributes == null) {
            accountAttributes = Attributes.of(Attribute.of(AccountAttributes.PASSWORD, ""));
        }
        return AuthenticationAttributes.of(SubjectAttributes.of(userName, accountAttributes),
                ContextAttributes.empty());
    }

    /**
     * This method is used to verify if a custom query verifies the password.
     *
     * @return true if the custom query verifies the password, false otherwise.
     */
    @Override
    public boolean customQueryVerifiesPassword() {
        return false;
    }
}
