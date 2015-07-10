/*
 * Copyright 2015 Google, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.examples.abelanav2.grpc;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import com.google.identitytoolkit.GitkitClient;
import com.google.identitytoolkit.GitkitClientException;
import com.google.identitytoolkit.GitkitUser;

import com.examples.abelanav2.BackendConstants;
import net.oauth.jsontoken.Checker;
import net.oauth.jsontoken.JsonToken;
import net.oauth.jsontoken.JsonTokenParser;
import net.oauth.jsontoken.crypto.HmacSHA256Signer;
import net.oauth.jsontoken.crypto.HmacSHA256Verifier;
import net.oauth.jsontoken.crypto.SignatureAlgorithm;
import net.oauth.jsontoken.crypto.Verifier;
import net.oauth.jsontoken.discovery.VerifierProvider;
import net.oauth.jsontoken.discovery.VerifierProviders;

import java.io.FileNotFoundException;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Provides different useful method both to check the validity of Gitkit tokens
 * and to generate and verify the application own's tokens.
 */
public final class AuthUtils {

  /**
   * The verifier providers for the token signing.
   */
  private static VerifierProviders verifierProviders = null;

  static {
    try {
      final Verifier hmacVerifier =
          new HmacSHA256Verifier(BackendConstants.SIGNING_KEY.getBytes());
      verifierProviders = new VerifierProviders();
      verifierProviders.setVerifierProvider(SignatureAlgorithm.HS256,
          new VerifierProvider() {
            @Override
            public List<Verifier> findVerifier(final String signerId,
                                               final String keyId) {
              List<Verifier> list = new ArrayList<>();
              list.add(hmacVerifier);
              return list;
            }
          });

    } catch (InvalidKeyException e) {
      e.printStackTrace();
    }
  }

  /**
   * Constructor.
   */
  private AuthUtils() { }

  /**
   * Returns a signed JWT token containing the userId.
   * @param userId the userId to include in the token payload.
   * @return a signed JWT token.
   * @throws InvalidKeyException if the signing key cannot be retrieved.
   * @throws SignatureException if the signature failed.
   */
  public static String getJwt(final String userId) throws InvalidKeyException, SignatureException {
    JsonToken token;
    token = createToken(userId);
    return token.serializeAndSign();
  }

  /**
   * Creates and returns the token to be signed.
   * @param userId the userId to include in the token payload.
   * @return a token to be signed.
   * @throws InvalidKeyException if the signing key cannot be retrieved.
   */
  private static JsonToken createToken(final String userId) throws InvalidKeyException {
    // Current time and signing algorithm
    Calendar cal = Calendar.getInstance();
    HmacSHA256Signer signer = new HmacSHA256Signer(BackendConstants.TOKEN_ISSUER, null,
        BackendConstants.SIGNING_KEY.getBytes());

    // Configure JSON token with signer and SystemClock
    JsonToken token = new JsonToken(signer);
    token.setAudience(BackendConstants.TOKEN_ISSUER);
    token.setParam("typ", "abelana/auth/v1");
    token.setIssuedAt(new org.joda.time.Instant(cal.getTimeInMillis()));
    token.setExpiration(new org.joda.time.Instant(cal.getTimeInMillis()
        + BackendConstants.JWT_EXPIRATION_DURATION));

    //Configure user object, which contains information on the user
    JsonObject user = new JsonObject();
    user.addProperty("user_id", userId);

    JsonObject payload = token.getPayloadAsJsonObject();
    payload.add("user", user);
    return token;
  }

  /**
   * Deserializes the JWT signed token.
   * @param jwt the signed token.
   * @return the deserialized token.
   * @throws Exception if cannot deserialize
   */
  public static JsonToken deserialize(final String jwt) throws Exception {
    JsonTokenParser parser = new JsonTokenParser(verifierProviders,
        new AbelanaTokenAudienceChecker(BackendConstants.TOKEN_ISSUER));
    return parser.deserialize(jwt);
  }

  /**
   * Checks the signature and deserializes the JWT signed token.
   * @param jwt the signed token.
   * @return the deserialized token
   * @throws Exception if cannot verify signature or cannot deserialize
   */
  public static JsonToken verifyAndDeserialize(final String jwt) throws Exception {
    JsonTokenParser parser = new JsonTokenParser(verifierProviders,
        new AbelanaTokenAudienceChecker(BackendConstants.TOKEN_ISSUER));
    return parser.verifyAndDeserialize(jwt);
  }

  /**
   * Verifies the signature and audience of the GitkitToken.
   * @param gitkitToken the GitkitToken to check.
   * @return the GitkitUser associated to the GitkitToken.
   * @throws GitkitClientException if the GitkitClient cannot validate the
   *      token.
   * @throws FileNotFoundException if the service account key cannot be found.
   */
  public static GitkitUser verifyGitkitToken(final String gitkitToken)
      throws GitkitClientException, FileNotFoundException {

    // Initializes Gitkit client instance
    GitkitClient gitkitClient = GitkitClient.newBuilder()
        .setGoogleClientId(BackendConstants.GOOGLE_CLIENT_ID)
        .setServiceAccountEmail(BackendConstants.GOOGLE_SERVICE_ACCOUNT)
        .setKeyStream(Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(BackendConstants.GOOGLE_SERVICE_ACCOUNT_KEY_FILEPATH))
        .setWidgetUrl("/gitkit.jsp").setCookieName("gtoken").build();

    // Verifies a GitkitToken
    return gitkitClient.validateToken(gitkitToken);
  }

  /**
   * Checks if the current user is signed in.
   * @return boolean indicating if the user is signed in.
   */
  public static boolean isSignedIn() {
    String userIdToken = AuthToken.get();
    try {
      verifyAndDeserialize(userIdToken);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  /**
   * Returns the user id.
   * @return String the user id.
   */
  public static String getUserId() {
    String userIdToken = AuthToken.get();
    try {
      JsonToken token = deserialize(userIdToken);
      JsonObject payload = token.getPayloadAsJsonObject();
      JsonObject user = payload.get("user").getAsJsonObject();
      return user.get("user_id").getAsString();
    } catch (Exception e) {
      return null;
    }
  }


  /**
   * Audience checker for signed JWT tokens.
   */
  public static class AbelanaTokenAudienceChecker implements Checker {

    /**
     * URI that the client is accessing, as seen by the server.
     */
    private final String serverUri;

    /**
     * Public constructor.
     * @param uri the URI against which the signed JWT token was exercised.
     */
    public AbelanaTokenAudienceChecker(final String uri) {
      this.serverUri = uri;
    }

    /** Cheks the payload.
     * @see Checker#check(JsonObject)
     */
    @Override
    public final void check(final JsonObject payload) throws SignatureException {
      checkUri(serverUri, Preconditions.checkNotNull(payload.get(JsonToken.AUDIENCE).getAsString(),
          "Audience cannot be null!"));
    }

    /**
     * Checks the audience field.
     * @param ourUriString the authorized audience.
     * @param tokenUriString the audience in the token.
     * @throws SignatureException if the audience is not valid.
     */
    private void checkUri(final String ourUriString, final String tokenUriString)
        throws SignatureException {
      if (!tokenUriString.equalsIgnoreCase(ourUriString)) {
        throw new SignatureException("Wrong audience URI");
      }
    }
  }
}
