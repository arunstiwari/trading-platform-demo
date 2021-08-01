package com.tekmentor.authenticationservice.controller;

import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.tekmentor.authenticationservice.dto.ConfirmSignup;
import com.tekmentor.authenticationservice.dto.LoginRequest;
import com.tekmentor.authenticationservice.dto.LoginResponse;
import com.tekmentor.authenticationservice.dto.SignupRequest;
import com.tekmentor.authenticationservice.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@CrossOrigin
public class RegistrationController {
    @Autowired
    private AuthenticationService authenticationservice;

    @PostMapping("/signup")
    public ResponseEntity registerUser(@RequestBody SignupRequest signupRequest){
        SignUpResult signUpResult = authenticationservice.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(signupRequest);
    }

    @PostMapping("/confirmsignup")
    public ResponseEntity confirmSignUp(@RequestBody ConfirmSignup confirmSignUp){
        ConfirmSignUpResult confirmSignUpResult = authenticationservice.confirmSignUp(confirmSignUp);
        return ResponseEntity.status(HttpStatus.OK).body(confirmSignUpResult);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginRequest loginRequest){
        LoginResponse loginResponse = authenticationservice.signIn(loginRequest);
        return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
    }
}
