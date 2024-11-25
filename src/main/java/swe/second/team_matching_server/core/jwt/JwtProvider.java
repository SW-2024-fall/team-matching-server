package swe.second.team_matching_server.core.jwt;

import java.util.Date;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import swe.second.team_matching_server.domain.auth.model.dto.TokenResponse;
import swe.second.team_matching_server.domain.auth.model.exception.ExpiredTokenException;
import swe.second.team_matching_server.domain.auth.model.exception.IllegalArgumentToken;
import swe.second.team_matching_server.domain.auth.model.exception.InvalidSignatureException;
import swe.second.team_matching_server.domain.auth.model.exception.InvalidTokenException;
import swe.second.team_matching_server.domain.auth.model.exception.UnsupportedTokenException;
import swe.second.team_matching_server.domain.auth.model.enums.UserRole;

@Component
public class JwtProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    public TokenResponse createToken(String userId) {
        return new TokenResponse(createAccessToken(userId), createRefreshToken(userId));
    }

    public String createAccessToken(String userId) {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public String createRefreshToken(String userId) {
        return Jwts.builder()
            .setSubject(userId)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }

    public String getUserId(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }

    public Authentication getAuthentication(String token) {
        String userId = getUserId(token);
        UserDetails userDetails = User.withUsername(userId)
                .password("")
                .authorities(UserRole.USER.getRole())
                .build();

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);

            return true;
        } catch (ExpiredJwtException e) {
            throw new ExpiredTokenException();
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            throw new IllegalArgumentToken();
        } catch (io.jsonwebtoken.SignatureException e) {
            throw new InvalidSignatureException();
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            throw new UnsupportedTokenException();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentToken();
        } catch (Exception e) {
            throw new InvalidTokenException();
        }
    }
}
