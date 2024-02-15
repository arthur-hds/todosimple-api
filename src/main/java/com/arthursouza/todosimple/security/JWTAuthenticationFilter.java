package com.arthursouza.todosimple.security;

import java.util.ArrayList;

import org.apache.catalina.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.arthursouza.todosimple.exceptions.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


//This class gets everything from a login prompt attempt
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
    
    
    private AuthenticationManager authenticationManager;

    private JWTUtil jwtUtil;


    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

        setAuthenticationFailureHandler(new GlobalExceptionHandler());


        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }



    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, 
    HttpServletResponse response) throws AuthenticationException{

        try {
            
            User userCredentials = new ObjectMapper().readValue(request.getInputStream(), User.class);

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userCredentials.getUsername(), 
            userCredentials.getPassword(), new ArrayList<>());
            
            Authentication authentication = this.authenticationManager.authenticate(authToken);

            return authentication;

        } catch (Exception e) {
            
            throw new RuntimeException();
            
        }



    }



    public void successfullAuthentication(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain, Authentication authentication) throws IOException, ServletException{

        UserSpringSecurity usersSpringSecurity = (UserSpringSecurity) authentication.getPrincipal();

        String username = usersSpringSecurity.getUsername();
        String token = this.jwtUtil.generateToken(username);

        response.addHeader("Authorization", "Bearer "+ token);
        response.addHeader("access-control-expose-headers", "Authorization");


    }
         




        



}
