package com.tekmentor.authenticationservice.service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.*;
import com.tekmentor.authenticationservice.config.CognitoConfiguration;
import com.tekmentor.authenticationservice.dto.ConfirmSignup;
import com.tekmentor.authenticationservice.dto.LoginRequest;
import com.tekmentor.authenticationservice.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class CognitoService {
    @Autowired
    private AWSCognitoIdentityProvider cognitoProvider;

    @Autowired
    private CognitoConfiguration configuration;

    public SignUpResult signUp(SignupRequest signupRequest) {
        String secretHash = calculateSecretHash(configuration.getAwsClientId(),
                                    configuration.getAwsSecretHash(),
                                    signupRequest.getUsername());
        String group = null != signupRequest.getGroup() ? signupRequest.getGroup() : "User";
        List<AttributeType> userAttributes = List.of(
                new AttributeType().withName("custom:group").withValue(group),
                new AttributeType().withName("profile").withValue(signupRequest.getProfileImage()),
                new AttributeType().withName("name").withValue(signupRequest.getUsername())
        );

        SignUpRequest request = new SignUpRequest()
                                            .withClientId(configuration.getAwsClientId() )
                                            .withUsername(signupRequest.getUsername())
                                            .withPassword(signupRequest.getPassword())
                                            .withUserAttributes(userAttributes)
                                            .withSecretHash(secretHash);

        SignUpResult result = cognitoProvider.signUp(request);
        AdminAddUserToGroupRequest adminAddUserToGroupRequest = new AdminAddUserToGroupRequest()
                .withUserPoolId(configuration.getUserPool())
                .withUsername(signupRequest.getUsername())
                .withGroupName(group);
        cognitoProvider.adminAddUserToGroup(adminAddUserToGroupRequest);
        return result;
    }

    public ConfirmSignUpResult confirmSignUp(ConfirmSignup confirmSignUp) {
        String secretHash = calculateSecretHash(configuration.getAwsClientId(),
                configuration.getAwsSecretHash(),
                confirmSignUp.getUsername());

        ConfirmSignUpRequest confirmSignUpRequest = new ConfirmSignUpRequest()
                .withClientId(configuration.getAwsClientId())
                .withUsername(confirmSignUp.getUsername())
                .withConfirmationCode(confirmSignUp.getConfirmationCode())
                .withSecretHash(secretHash);
        return cognitoProvider.confirmSignUp(confirmSignUpRequest);
    }

    //String email, String password
    public Map<String, String> login(LoginRequest loginRequest) {
        String secretHash = calculateSecretHash(configuration.getAwsClientId(), configuration.getAwsSecretHash(), loginRequest.getUsername());
        Map<String, String> authParams = new LinkedHashMap<String, String>() {{
            put("USERNAME", loginRequest.getUsername());
            put("PASSWORD", loginRequest.getPassword());
            put("SECRET_HASH",secretHash);
        }};

        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withUserPoolId(configuration.getUserPool())
                .withClientId(configuration.getAwsClientId())
                .withAuthParameters(authParams);

        AdminInitiateAuthResult authResult = cognitoProvider.adminInitiateAuth(authRequest);
        AuthenticationResultType resultType = authResult.getAuthenticationResult();

        return new LinkedHashMap<String, String>() {{
            put("idToken", resultType.getIdToken());
            put("accessToken", resultType.getAccessToken());
            put("refreshToken", resultType.getRefreshToken());
            put("message", "Successfully login");
        }};
    }
    //https://cognito-idp.ap-south-1.amazonaws.com/ap-south-1_FdoTQYVkk/.well-known/jwks.json

    public static String calculateSecretHash(String userPoolClientId, String userPoolClientSecret, String userName) {
        final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

        SecretKeySpec signingKey = new SecretKeySpec(
                userPoolClientSecret.getBytes(StandardCharsets.UTF_8),
                HMAC_SHA256_ALGORITHM);
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);
            mac.update(userName.getBytes(StandardCharsets.UTF_8));
            byte[] rawHmac = mac.doFinal(userPoolClientId.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(rawHmac);
        } catch (Exception e) {
            throw new RuntimeException("Error while calculating ");
        }
    }
}
