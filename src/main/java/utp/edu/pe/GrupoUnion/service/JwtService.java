package utp.edu.pe.GrupoUnion.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtService {

    // Cambia esto a una clave segura de al menos 256 bits (32 caracteres)
    @Value("${jwt.secret:AseguraQueEstaClaveSeaSuficientementeLargaParaSerSeguraEnProduccion}")
    private String SECRET;

    // --- Métodos de Generación ---
    public String generateToken(String username, List<String> roles) {
        // Mapeamos los roles a claims (reclamaciones)
        String authorities = roles.stream().collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(username)
                .claim("roles", authorities) // Almacenamos los roles en el token
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 horas
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // --- Métodos de Extracción y Validación ---
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        String rolesString = (String) claims.get("roles");
        if (rolesString == null || rolesString.isEmpty()) {
            return List.of();
        }
        return List.of(rolesString.split(",")).stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public Boolean isTokenValid(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !extractAllClaims(token).getExpiration().before(new Date()));
    }
}