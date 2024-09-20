package com.tentixo;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.tentixo.token.DelegationAdapter;
import org.junit.jupiter.api.Test;
import se.curity.identityserver.sdk.attribute.Attributes;
import se.curity.identityserver.sdk.attribute.AuthenticationAttributes;
import se.curity.identityserver.sdk.data.StringOrArray;
import se.curity.identityserver.sdk.data.authorization.Delegation;
import se.curity.identityserver.sdk.data.authorization.Token;
import se.curity.identityserver.sdk.errors.NoSingleValueException;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;


public class TokenSerializationTest {

    public String tokenSample = """
        {
          "id": null,
          "audience": {
            "values": [
              "oauth-tools"
            ],
            "valueOrError": "oauth-tools"
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
    public void testTokenSerialization() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final var simpleModule = new SimpleModule()
            .addSerializer(Supplier.class, new com.fasterxml.jackson.databind.JsonSerializer<Supplier>() {

                @Override
                public void serialize(Supplier value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    Object o = value.get();
                    if (o instanceof NoSingleValueException) {
                        gen.writeString("NoSingleValeError");
                    }
                }
            })
            .addDeserializer(Supplier.class, new JsonDeserializer<Supplier>() {
                @Override
                public Supplier deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                    return new Supplier() {
                        @Override
                        public Object get() {
                            try {
                                return p.getValueAsString();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
            })
            .addSerializer(AuthenticationAttributes.class, new com.fasterxml.jackson.databind.JsonSerializer<AuthenticationAttributes>() {
                @Override
                public void serialize(AuthenticationAttributes value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                    Map<String, Object> m = value.toMap();
                    gen.writeObject(m);
                }
            })
            .addDeserializer(AuthenticationAttributes.class, new JsonDeserializer<AuthenticationAttributes>(){

                @Override
                public AuthenticationAttributes deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
                    Map<String, Object> m = p.readValueAs(Map.class);
                    return AuthenticationAttributes.fromAttributes( Attributes.fromMap(m));
                }
            })
            .addAbstractTypeMapping(Token.class, TokenAdapter.class)
            .addAbstractTypeMapping(Delegation.class, DelegationAdapter.class)
            .addAbstractTypeMapping(StringOrArray.class, StringOrArrayAdapter.class);
        mapper.registerModule(simpleModule);
        Token t = mapper.readValue(tokenSample, Token.class);

    }
}
