package com.learning.userauthservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.userauthservice.clients.KafkaClient;
import com.learning.userauthservice.dtos.EmailDto;
import com.learning.userauthservice.exceptions.AuthServiceException;
import com.learning.userauthservice.models.Status;
import com.learning.userauthservice.models.User;
import com.learning.userauthservice.models.UserSession;
import com.learning.userauthservice.repo.UserRepo;
import com.learning.userauthservice.repo.UserSessionRepo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.MacAlgorithm;
import org.antlr.v4.runtime.misc.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService implements IAuthService{

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserSessionRepo userSessionRepo;

    @Autowired
    private SecretKey secretKey;

    @Autowired
    private KafkaClient kafkaClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public User signUp(String name, String email, String password, String phoneNumber) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isPresent()){
            throw new AuthServiceException("User already exists. Please login.");
        }

        //Encoding the password
        password = passwordEncoder.encode(password);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setPhoneNumber(phoneNumber);

        //send email via kafka
        try {
            EmailDto emailDto = new EmailDto();
            emailDto.setTo(email);
            emailDto.setFrom("shivamj1008@gmail.com");
            emailDto.setSubject("Welcome to Scaler");
            emailDto.setBody("Having good learning experience");
            kafkaClient.sendMessage("signup",objectMapper.writeValueAsString(emailDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return userRepo.save(user);
    }

    @Override
    public Pair<User, String> login(String email, String password) {
        Optional<User> userOptional = userRepo.findByEmail(email);
        if(userOptional.isEmpty()){
            throw new AuthServiceException("User doesn't exists. Please signup.");
        }
        User user = userOptional.get();
        //if(!user.getPassword().equals(password)){
        //matching the provided password with DB encoded password
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new AuthServiceException("Incorrect password.");
        }

        //Jwt Token Generation
        String jwtToken = getJwtToken(user);
        //Persist token in DB
        UserSession session = new UserSession();
        session.setUser(user);
        session.setToken(jwtToken);
        session.setStatus(Status.ACTIVE);
        userSessionRepo.save(session);
        return new Pair<User,String>(user,jwtToken);
    }

    private String getJwtToken(User user) {
        //Generate Token using string payload
        /*String payload = "{\n" +
                "    \"name\":\"shivam\",\n" +
                "    \"email\":\"shivam@gmail.com\",\n" +
                "    \"roles\":[\"student\",\"ta\"],\n" +
                "    \"expirationDate\":\"2Apr2025\"\n" +
                "}";

        byte[] content = payload.getBytes(StandardCharsets.UTF_8);*/

        //Generating jwt payload
        Map<String,Object> claims = new HashMap<>();
        claims.put("iss","scaler");
        claims.put("userId",user.getId());
        long currentTimeStamp = System.currentTimeMillis();
        claims.put("gen",currentTimeStamp);
        claims.put("exp",currentTimeStamp+30000);
        claims.put("roles",user.getRoles());

        //will create a bean of SecretKey so that we can use it to validate token
        //MacAlgorithm algorithm = Jwts.SIG.HS256;
        //SecretKey secretKey = algorithm.key().build();
        //return Jwts.builder().content(content).compact();
        //return Jwts.builder().content(content).signWith(secretKey).compact(); //Adding secret key in jwt payload
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        //check given token exists in DB
        Optional<UserSession> userSessionOptional = userSessionRepo.findByTokenAndUserId(token,userId);
        if(userSessionOptional.isEmpty()){
            return false;
        }

        UserSession session = userSessionOptional.get();
        if(session.getStatus().equals(Status.INACTIVE)){
            return false;
        }
        //parse token
        JwtParser parser = Jwts.parser().verifyWith(secretKey).build();
        Claims claims = parser.parseSignedClaims(token).getPayload();
        Long expiry = (Long)claims.get("exp");
        long currentTimeStamp = System.currentTimeMillis();
        if(currentTimeStamp>expiry){
            System.out.println("Token expired");
            //marking session as inactive in DB
            session.setStatus(Status.INACTIVE);
            userSessionRepo.save(session);
            return false;
        }
        return true;
    }

    @Override
    public void logout(String token, Long userId) {

    }

    @Override
    public User forgotPassword(String email, String password) {
        return null;
    }
}
