package com.divum.MeetingRoomBlocker.Service.Implementation;
import com.divum.MeetingRoomBlocker.Service.JwtServices;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JwtServicesImplementation implements JwtServices {
    private static final String SECRET_KEY = "5f787bf595208ce61a0105d4e8762643adaaeda1fac8202a7f6b410492ac02a8";
    @Override
    public String extractUserName(String token){
        return extractClaims(token,Claims::getSubject);
    }
    @Override
    public String extractSessionId(String token){
        return extractAllDetails(token).get("id").toString();
    }
    @Override
    public String getJwtToken(UserDetails userDetails,String sessionId){
        return getJwtToken(new HashMap<>(),userDetails,sessionId);
    }
    @Override
    public String getJwtToken(
            Map<String,Object> claims,
            UserDetails userDetails,
            String sessionId
    ){
        claims.put("id",sessionId);
        return Jwts
                .builder()
                .setId(sessionId)
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000*60*5)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    @Override
    public String getRefreshToken(UserDetails userDetails,String sessionId){
        HashMap<String,Object> claims = new HashMap<>();
        claims.put("id",sessionId);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000*60*30)))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public <T> T extractClaims(String token, Function<Claims,T> ClaimsResolver){
        final Claims claims = extractAllDetails(token);
        return  ClaimsResolver.apply(claims);
    }
    @Override
    public Claims extractAllDetails(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    @Override
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername()) && !isNotExpired(token));
    }
    private boolean isNotExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaims(token,Claims::getExpiration);
    }

}
