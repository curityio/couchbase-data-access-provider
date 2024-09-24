package com.tentixo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.data.authorization.Token;
import se.curity.identityserver.sdk.data.authorization.TokenStatus;
import se.curity.identityserver.sdk.errors.NoSingleValueException;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class TokenSerializationTest {

    public String tokenSample = """
        {
          "id": null,
          "audience": {
            "values": [
              "oauth-tools"
            ]
          },
          "scope": "read",
          "tokenHash": "WENjVWTPb40BxqafX5x7XDA4l5FwmyA+fRN3eKGFpMZs5y4vASDlbT3JtRm2CH/mNKC4bGUwsVzG2rffDLYgIQ==",
          "delegationsId": "acf8e38c-fcf3-4cd1-a180-49c4f1fd34d0",
          "purpose": "access_token",
          "usage": "bearer",
          "format": "opaque",
          "created": 1726835990,
          "expires": 1726836290,
          "status": "issued",
          "issuer": "https://login.curity.local/~",
          "subject": "oauth-tools",
          "notBefore": 1726835990,
          "serializedTokenData": "{\\"__mandatory__\\":{\\"delegationId\\":\\"acf8e38c-fcf3-4cd1-a180-49c4f1fd34d0\\",\\"exp\\":1726836290,\\"nbf\\":1726835990,\\"scope\\":\\"read\\",\\"iss\\":\\"https://login.curity.local/~\\",\\"sub\\":\\"oauth-tools\\",\\"aud\\":\\"oauth-tools\\",\\"iat\\":1726835990,\\"purpose\\":\\"access_token\\"},\\"__token_class_name__\\":\\"se.curity.identityserver.tokens.data.OpaqueAccessTokenData\\",\\"__metadata__\\":{\\"_requestingClientIdentifier\\":{\\"id\\":\\"oauth-tools\\"},\\"_isAssistedToken\\":false,\\"_requestingSubject\\":\\"oauth-tools\\",\\"_claimMap\\":{\\"unmappedClaims\\":{\\"iss\\":{\\"required\\":true},\\"sub\\":{\\"required\\":true},\\"aud\\":{\\"required\\":true},\\"exp\\":{\\"required\\":true},\\"iat\\":{\\"required\\":true},\\"auth_time\\":{\\"required\\":true},\\"nonce\\":{\\"required\\":true},\\"acr\\":{\\"required\\":true},\\"amr\\":{\\"required\\":true},\\"azp\\":{\\"required\\":true},\\"nbf\\":{\\"required\\":true},\\"client_id\\":{\\"required\\":true},\\"delegation_id\\":{\\"required\\":true},\\"purpose\\":{\\"required\\":true},\\"scope\\":{\\"required\\":true},\\"jti\\":{\\"required\\":true},\\"sid\\":{\\"required\\":true},\\"authorization_details\\":{\\"required\\":true},\\"cnf\\":{\\"required\\":true}}},\\"_requestingClientAuthenticationMethod\\":\\"secret\\",\\"_clientId\\":\\"oauth-tools\\"}}",
          "serializedAudience": "[\\"oauth-tools\\"]",
          "data": {
            "__mandatory__": {
              "delegationId": "acf8e38c-fcf3-4cd1-a180-49c4f1fd34d0",
              "exp": 1726836290,
              "nbf": 1726835990,
              "scope": "read",
              "iss": "https://login.curity.local/~",
              "sub": "oauth-tools",
              "aud": "oauth-tools",
              "iat": 1726835990,
              "purpose": "access_token"
            },
            "__token_class_name__": "se.curity.identityserver.tokens.data.OpaqueAccessTokenData",
            "__metadata__": {
              "_requestingClientIdentifier": {
                "id": "oauth-tools"
              },
              "_isAssistedToken": false,
              "_requestingSubject": "oauth-tools",
              "_claimMap": {
                "unmappedClaims": {
                  "iss": {
                    "required": true
                  },
                  "sub": {
                    "required": true
                  },
                  "aud": {
                    "required": true
                  },
                  "exp": {
                    "required": true
                  },
                  "iat": {
                    "required": true
                  },
                  "auth_time": {
                    "required": true
                  },
                  "nonce": {
                    "required": true
                  },
                  "acr": {
                    "required": true
                  },
                  "amr": {
                    "required": true
                  },
                  "azp": {
                    "required": true
                  },
                  "nbf": {
                    "required": true
                  },
                  "client_id": {
                    "required": true
                  },
                  "delegation_id": {
                    "required": true
                  },
                  "purpose": {
                    "required": true
                  },
                  "scope": {
                    "required": true
                  },
                  "jti": {
                    "required": true
                  },
                  "sid": {
                    "required": true
                  },
                  "authorization_details": {
                    "required": true
                  },
                  "cnf": {
                    "required": true
                  }
                }
              },
              "_requestingClientAuthenticationMethod": "secret",
              "_clientId": "oauth-tools"
            }
          },
          "enumActiveStatus": "issued",
          "active": true
        }""";

    @Test
    public void testTokenSerialization() throws NoSingleValueException
    {
        var serializer = CurityJsonSerializer.create();
        var t = serializer.deserialize(Token.class, tokenSample.getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(t.getAudience().getValueOrError(), "oauth-tools");
    }

    @Test
    public void testSerializerRoundtrip()
    {
        var serializer = CurityJsonSerializer.create();
        var id = UUID.randomUUID();
        var token = new TokenAdapter(id.toString(), String.valueOf(id.hashCode()), "qwe-123", "purpose","usage","format","openid",
                Instant.now().getEpochSecond(), Instant.now().plus(Duration.ofSeconds(10L)).getEpochSecond(),
                TokenStatus.issued, "secure-idp", "johndoe", StringOrArrayAdapter.of("tests"),
                Instant.now().getEpochSecond(), Map.of("foo", "bar"));

        var serialized = serializer.serialize(token);
        var deserialized = serializer.deserialize(Token.class, serialized);

        Assertions.assertEquals(token.getId(), deserialized.getId());
        Assertions.assertEquals(token.getAudience(), deserialized.getAudience());
    }

    @Test
    public void testStringAdapterSerialization() throws NoSingleValueException
    {
        var serializer = CurityJsonSerializer.create();
        StringOrArrayAdapter stringOrArray = serializer.deserialize(StringOrArrayAdapter.class, "{ \"values\": [\"oauth-tools\", \"other-value\"] }".getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(stringOrArray.getValues(), List.of("oauth-tools", "other-value"));
        Assertions.assertThrows(NoSingleValueException.class, stringOrArray::getValueOrError);

        stringOrArray = serializer.deserialize(StringOrArrayAdapter.class, "\"oauth-tools\"".getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(stringOrArray.getValueOrError(), "oauth-tools");
        Assertions.assertEquals(stringOrArray.getValues(), List.of("oauth-tools"));

        stringOrArray = serializer.deserialize(StringOrArrayAdapter.class, "{ \"values\": [\"oauth-tools\"] }".getBytes(StandardCharsets.UTF_8));
        Assertions.assertEquals(stringOrArray.getValueOrError(), "oauth-tools");

    }
}
