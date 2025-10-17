package com.backend.util;


import com.backend.enums.UserRole;
import com.backend.exception.NoAuthenticatedUserException;
import com.backend.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    /**
     * Récupère l'utilisateur actuellement authentifié.
     * @return l'entité User de l'utilisateur connecté.
     * @throws NoAuthenticatedUserException si aucun utilisateur n'est connecté.
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())
                && authentication.getPrincipal() instanceof User user) {
            return user;
        }

        throw new NoAuthenticatedUserException();
    }

    /**
     * Vérifie si l'utilisateur connecté a un rôle spécifique.
     * @param role nom du rôle (ex: "ADMIN", "USER").
     * @return true si l'utilisateur possède ce rôle.
     */
    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null &&
                authentication.isAuthenticated() &&
                authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .anyMatch(r -> r.equals("ROLE_" + role));
    }

    /**
     * Vérifie si l'utilisateur est admin.
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Variante souple — renvoie null si aucun utilisateur connecté ou si c'est un admin.
     * Utile pour les contextes facultatifs (progression, logs, etc.)
     */
    public static User getCurrentUserOrNull() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())
                && authentication.getPrincipal() instanceof User user) {

            // ✅ Ignorer les administrateurs
            if (user.getRole() == UserRole.ADMIN) {
                return null;
            }

            return user;
        }

        return null;
    }

}
