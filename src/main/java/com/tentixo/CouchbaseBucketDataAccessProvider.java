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
import se.curity.identityserver.sdk.datasource.BucketDataAccessProvider;

import java.util.Map;

public final class CouchbaseBucketDataAccessProvider implements BucketDataAccessProvider {
    private static final Logger _logger = LoggerFactory.getLogger(CouchbaseBucketDataAccessProvider.class);

    private final CouchbaseBucketDataAccessProvider _configuration;

    @SuppressWarnings("unused") // used through DI
    public CouchbaseBucketDataAccessProvider(CouchbaseBucketDataAccessProvider configuration) {
        _configuration = configuration;
    }

    @Override
    public Map<String, Object> getAttributes(String subject, String purpose) {
        _logger.debug("Getting bucket attributes with subject: {} , purpose : {}", subject, purpose);
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, Object> storeAttributes(String subject, String purpose, Map<String, Object> dataMap) {
        dataMap.put("subject", subject);
        dataMap.put("purpose", purpose);

        _logger.debug("Storing bucket attributes with subject: {} , purpose : {} and data : {}", subject, purpose, dataMap);

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean clearBucket(String subject, String purpose) {
        throw new UnsupportedOperationException();
    }
}
