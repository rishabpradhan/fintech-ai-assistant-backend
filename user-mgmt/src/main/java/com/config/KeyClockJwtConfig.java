package com.config;

import org.jspecify.annotations.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class KeyClockJwtConfig implements Converter<Jwt, AbstractAuthenticationToken> {
    private static final String clientId = "muId"; //actual keyclock id

    @Override
    public AbstractAuthenticationToken convert(@NonNull Jwt jwt){
        Collection<GrantedAuthority> authorities = extractRealmRoles(jwt);
        authorities.addAll(extractClientRoles(jwt));

        return new JwtAuthenticationToken(jwt, authorities, Objects.requireNonNull(jwt.getClaimAsString("preferred_name")));

    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> extractRealmRoles(Jwt jwt){
        Map<String, Object> realAccess = jwt.getClaimAsMap("realm_access");
        if(realAccess == null || realAccess.get("roles") == null){
            return Collections.emptyList();
        }

        return ((List<String>) realAccess.get("roles")).stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private List<GrantedAuthority> extractClientRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess == null || resourceAccess.get(clientId) == null) {
            return List.of();
        }
        Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get(clientId);
        List<String> roles = (List<String>) clientAccess.getOrDefault("roles", List.of());
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                .collect(Collectors.toList());
    }

}
