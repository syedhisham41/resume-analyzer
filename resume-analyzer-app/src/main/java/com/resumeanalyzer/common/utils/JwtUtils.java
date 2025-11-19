package com.resumeanalyzer.common.utils;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.resumeanalyzer.auth.dto.Jwt;
import com.resumeanalyzer.auth.entity.User;
import com.resumeanalyzer.auth.exceptions.UnauthorizedException;
import com.resumeanalyzer.common.dto.GuestUserDetails;
import com.resumeanalyzer.common.dto.JwtUserDetails;

@Component
public class JwtUtils {

	private final String secret;
	private final long expiration;
	private final String guestSecret;
	private final long guestExpiration;

	public JwtUtils(@Value("${spring.jwt.secret.key}") String secret,
			@Value("${spring.jwt.expiration}") long expiration, @Value("${spring.guest.secret.key}") String guestSecret,
			@Value("${spring.guest.expiration}") long guestExpiration) {
		super();
		this.secret = secret;
		this.expiration = expiration;
		this.guestSecret = guestSecret;
		this.guestExpiration = guestExpiration;
	}

	public Jwt generateJwtToken(User user) {

		Algorithm algorithm = Algorithm.HMAC256(secret);

		long nowMillis = System.currentTimeMillis();
		long expiryMillis = nowMillis + expiration;

		Date issuedAt = new Date(nowMillis);
		Date expiresAt = new Date(expiryMillis);

		String token = JWT.create().withClaim("userId", user.getUserId()).withClaim("email", user.getEmail())
				.withClaim("userName", user.getUserName()).withIssuedAt(issuedAt).withExpiresAt(expiresAt)
				.sign(algorithm);

		return new Jwt(token, new Date(nowMillis), new Date(expiryMillis));
	}

	public Jwt generateGuestToken() {
		Algorithm algorithm = Algorithm.HMAC256(guestSecret);
		long nowMillis = System.currentTimeMillis();
		long expiryMillis = nowMillis + guestExpiration;
		Date issuedAt = new Date(nowMillis);
		Date expiresAt = new Date(expiryMillis);
		String token = JWT.create().withClaim("guestId", "guest_" + UUID.randomUUID()).withClaim("role", "ROLE_GUEST")
				.withIssuedAt(issuedAt).withExpiresAt(expiresAt).sign(algorithm);

		return new Jwt(token, new Date(nowMillis), new Date(expiryMillis));
	}

	public Object validateTokenAndExtractClaims(String token) {

		DecodedJWT jwt = null;

		try {
			DecodedJWT unverified = JWT.decode(token);
			String role = unverified.getClaim("role").asString();
			System.out.println("role" + role);
			Algorithm algo = "ROLE_GUEST".equals(role) ? Algorithm.HMAC256(guestSecret) : Algorithm.HMAC256(secret);

			jwt = JWT.require(algo).build().verify(token);
		} catch (Exception e) {
			throw new UnauthorizedException("Unauthorized User " + e.getMessage(), null);
		}

		if (jwt.getClaim("role").isMissing() && !jwt.getClaim("userId").isMissing()) {
			return new JwtUserDetails(jwt.getClaim("userName").asString(), jwt.getClaim("email").asString(),
					jwt.getClaim("userId").asInt());

		} else if (jwt.getClaim("role").asString().equals("ROLE_GUEST")) {
			return new GuestUserDetails(jwt.getClaim("guestId").asString(), jwt.getClaim("role").asString());
		}
		throw new UnauthorizedException("Unauthorized User", null);
	}
}
