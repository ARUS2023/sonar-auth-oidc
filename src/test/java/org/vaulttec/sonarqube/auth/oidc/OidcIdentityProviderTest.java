/*
 * OpenID Connect Authentication for SonarQube
 * Copyright (c) 2017 Torsten Juergeleit
 * mailto:torsten AT vaulttec DOT org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaulttec.sonarqube.auth.oidc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.net.URISyntaxException;

import com.nimbusds.openid.connect.sdk.AuthenticationRequest;

import org.junit.Test;
import org.sonar.api.server.authentication.Display;
import org.sonar.api.server.authentication.OAuth2IdentityProvider;

public class OidcIdentityProviderTest extends AbstractOidcTest {

  private UserIdentityFactory userIdentityFactory = mock(UserIdentityFactory.class);
  private OidcClient client = newMockClient();

  private OidcIdentityProvider underTest = new OidcIdentityProvider(config, client, userIdentityFactory);

  @Test
  public void init() throws Exception {
    setSettings(true);
    OAuth2IdentityProvider.InitContext context = mock(OAuth2IdentityProvider.InitContext.class);
    when(context.generateCsrfState()).thenReturn(STATE);
    when(context.getCallbackUrl()).thenReturn(CALLBACK_URL);
    settings.setProperty(OidcConfiguration.ISSUER_URI, ISSUER_URI);

    underTest.init(context);

    verify(context).redirectTo(ISSUER_URI + "/protocol/openid-connect/auth?response_type=code&client_id=id"
        + "&redirect_uri=http%3A%2F%2Flocalhost%2Fcallback%2Foidc&scope=openid+email+profile&state=state");
  }


  private OidcClient newMockClient() {
    OidcClient mockClient = mock(OidcClient.class);
    AuthenticationRequest request = mock(AuthenticationRequest.class);
    try {
      when(request.toURI())
          .thenReturn(new URI(ISSUER_URI + "/protocol/openid-connect/auth" + "?response_type=code&client_id=id"
              + "&redirect_uri=http%3A%2F%2Flocalhost%2Fcallback%2Foidc" + "&scope=openid+email+profile&state=state"));
    } catch (URISyntaxException e) {
      // ignore
    }
    when(mockClient.createAuthenticationRequest(CALLBACK_URL, STATE)).thenReturn(request);
    return mockClient;
  }

}
