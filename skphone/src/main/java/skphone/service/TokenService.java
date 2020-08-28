package skphone.service;

import com.auth0.jwt.interfaces.DecodedJWT;

import skphone.domain.Token;
import skphone.domain.User;

public interface TokenService {

    Token createToken(User user);

    DecodedJWT verifyToken(String token);

}
