package com.learning.userauthservice.services;

import com.learning.userauthservice.exceptions.AuthServiceException;
import com.learning.userauthservice.models.User;
import com.learning.userauthservice.repo.UserRepo;
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
        claims.put("exp",currentTimeStamp+10000);
        claims.put("roles",user.getRoles());

        MacAlgorithm algorithm = Jwts.SIG.HS256;
        SecretKey secretKey = algorithm.key().build();
        //return Jwts.builder().content(content).compact();
        //return Jwts.builder().content(content).signWith(secretKey).compact(); //Adding secret key in jwt payload
        return Jwts.builder().claims(claims).signWith(secretKey).compact();
    }

    @Override
    public Boolean validateToken(String token, Long userId) {
        return null;
    }

    @Override
    public void logout(String token, Long userId) {

    }

    @Override
    public User forgotPassword(String email, String password) {
        return null;
    }
}
