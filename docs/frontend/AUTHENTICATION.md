# Authentication Architecture & Contract Specification

**Platform:** Talnova Enterprise Survey Platform (TESP)  
**Document ID:** AUTH-001  
**Status:** Production Standard

---

## 1. Authentication Overview

TESP implements JWT-based authentication integrating with OAuth2 / OpenID Connect (OIDC) and direct authentication providers. Demo state flags like `isLoggedIn = true` or `localStorage.user = mockUser` are strictly prohibited.

---

## 2. JWT Claims Specification

The frontend auth layer parses JWT tokens containing identity and authorization scope claims:

```json
{
  "iss": "https://auth.tesp.talnova.com/auth/realms/tesp",
  "sub": "usr_9920184",
  "aud": "tesp-api-gateway",
  "exp": 1785952800,
  "iat": 1785949200,
  "projectId": "PRJ-99201",
  "email": "admin@aitkenspence.com",
  "name": "Platform Administrator",
  "roles": ["SUPER_ADMIN", "PROJECT_ADMIN"],
  "nodeScope": "N-001",
  "nodePath": ",N-001,"
}
```

---

## 3. Auth Service Layer Structure

The frontend auth module (`/core/auth`) provides:
- **`AuthService.login(credentials)`:** Sends authentication request to gateway auth ingress, extracts access and refresh tokens.
- **`AuthService.logout()`:** Clears memory tokens, local storage session references, and invalidates server-side session.
- **`AuthService.restoreSession()`:** Rehydrates identity state from stored refresh credentials upon application load.
- **`AuthService.getAccessToken()`:** Retrieves active bearer token for API interceptors.

---

## 4. Session Persistence & Token Handling

- **Access Token Storage:** In-memory state within `AuthContext` to prevent XSS exposure.
- **Refresh Token Storage:** Secure HTTP-only cookies or encrypted localStorage fallback for session continuation.
- **Expiration Handling:** Gateway Axios interceptor captures 401 responses and triggers silent token refresh before retrying failed requests.
