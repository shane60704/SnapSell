package com.example.streamlive.filter;

//@Component
//@Slf4j
//@RequiredArgsConstructor
//public class JwtFilter extends OncePerRequestFilter {
//
//    private final JwtUtil jwtUtil;
//    private final AntPathMatcher pathMatcher = new AntPathMatcher();
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
//
//        // filter for the pages
//        String requestURI = request.getRequestURI();
//        String[] excludedPaths = {
//                "/",
//                "/api/1.0/user/signup",
//                "/api/1.0/user/signin",
//                "/api/1.0/user/update-authTime",
//                "/api/1.0/user/email/reset-password",
//                "/api/1.0/user/reset-password",
//                "/api/1.0/user/solve-jwt",
//                "/favicon.ico",
//                "/**/*.html",
//                "/assets/**"
//        };
//
//        for (String path : excludedPaths) {
//            if (pathMatcher.match(path, requestURI)) {
//                filterChain.doFilter(request, response);
//                return;
//            }
//        }
//
//        final String authHeader = request.getHeader("Authorization");
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            log.error("Token validation error 1");
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\":\"Invalid token\"}");
//            return;
//        }
//
//        final String token = authHeader.substring(7);
//        try {
//            if (token.isEmpty() || !jwtUtil.isTokenValid(token)) {
//                log.error("Token validation error 2");
//                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                response.setContentType("application/json");
//                response.getWriter().write("{\"error\":\"Invalid token\"}");
//                return;
//            }
//
//            Map<String,Object> claims = jwtUtil.getClaims(token);
//
//            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                    claims,
//                    null,
//                    List.of(new SimpleGrantedAuthority("ROLE_"+(String)claims.get("role")))
//            );
//            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//            SecurityContextHolder.getContext().setAuthentication(authToken);
//
//        } catch (Exception e) {
//            log.error("Token validation error", e);
//            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//            response.setContentType("application/json");
//            response.getWriter().write("{\"error\":\"Invalid token\"}");
//            return;
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
