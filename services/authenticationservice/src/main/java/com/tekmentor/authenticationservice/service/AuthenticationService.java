package com.tekmentor.authenticationservice.service;

//import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.tekmentor.authenticationservice.dto.ConfirmSignup;
import com.tekmentor.authenticationservice.dto.LoginRequest;
import com.tekmentor.authenticationservice.dto.LoginResponse;
import com.tekmentor.authenticationservice.dto.SignupRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class AuthenticationService {

    @Autowired
    private CognitoService cognitoService;

    public SignUpResult signup(SignupRequest signUpRequest) {
        SignUpResult signUpResult = cognitoService.signUp(signUpRequest);

        //Here make a call to User service to persist the user in database
        // @TODO
        return signUpResult;
    }

    public LoginResponse signIn(LoginRequest signInRequest) {
        Map<String, String> loginResult = cognitoService.login(signInRequest);
        log.info("Login Result : {}",loginResult);
        LoginResponse response = new LoginResponse();
        response.setMessage("User Signed up successfully");
        response.setTokens(loginResult);
        return response;
    }

    public ConfirmSignUpResult confirmSignUp(ConfirmSignup confirmSignUp){
         return cognitoService.confirmSignUp(confirmSignUp);
    }
}
