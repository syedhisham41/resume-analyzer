package com.resumeanalyzer.common.security;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.resumeanalyzer.common.dto.GuestUserDetails;
import com.resumeanalyzer.common.dto.JwtUserDetails;
import com.resumeanalyzer.common.utils.JwtUtils;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

	private final JwtUtils jwtUtils;

	private final JwtAuthEntryPoint entryPoint;

	@Value("${app.admin.userId}")
	private int adminUserId;

	public JwtAuthFilter(JwtUtils jwtUtils, JwtAuthEntryPoint entryPoint) {
		this.jwtUtils = jwtUtils;
		this.entryPoint = entryPoint;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		System.out.println("JWT header: " + authHeader);

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			entryPoint.commence(request, response,
					new AuthenticationException("Missing or invalid Authorization header") {
						private static final long serialVersionUID = 1L;
					});
			return;
		}

		String token = authHeader.substring(7);

		try {

			// JwtUserDetails userDetails = jwtUtils.validateTokenAndExtractClaims(token);
			Object userDetailsObject = jwtUtils.validateTokenAndExtractClaims(token);

			Collection<SimpleGrantedAuthority> authorities = null;
			if (userDetailsObject instanceof GuestUserDetails) {
				authorities = List.of(new SimpleGrantedAuthority(((GuestUserDetails) userDetailsObject).getRole()));
			} else if (userDetailsObject instanceof JwtUserDetails) {

				authorities = ((JwtUserDetails) userDetailsObject).getUserId() == adminUserId
						? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
						: List.of(new SimpleGrantedAuthority("ROLE_USER"));
			}

			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
					userDetailsObject, null, authorities);

			SecurityContextHolder.getContext().setAuthentication(authentication);

		} catch (Exception ex) {
			System.out.println("JWT validation failed: " + ex.getMessage());
			entryPoint.commence(request, response, new AuthenticationException(ex.getMessage()) {
				private static final long serialVersionUID = 1L;
			});

			return;
		}

		filterChain.doFilter(request, response);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		// Skip static resources
		if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
				|| path.equals("/favicon.ico")) {
			return true;
		}
		// Skip frontend pages (HTML)
		if (path.equals("/login") || path.equals("/signup") || path.equals("/welcome") || path.equals("/dashboard")
				|| path.equals("/analyzedashboard") || path.equals("/settings") || path.equals("/jdview")
				|| path.equals("/resumeview") || path.equals("/jdupload") || path.equals("/resumeupload")
				|| path.equals("/reports") || path.equals("/guest")) {
			return true;
		}
		// Skip public APIs
		if (path.startsWith("/api/user/login") || path.startsWith("/api/user/signup") || path.startsWith("/api/guest/login")) {
			return true;
		}
		return false;
	}
}
