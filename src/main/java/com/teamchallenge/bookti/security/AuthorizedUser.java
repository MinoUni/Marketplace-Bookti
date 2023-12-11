package com.teamchallenge.bookti.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

@Setter
@Getter
public class AuthorizedUser extends User {

    private UUID id;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private String email;
    private String firstName;
    private String lastName;
    private String avatarUrl;

    public AuthorizedUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AuthorizedUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public String getEmail() {
        return this.getUsername();
    }

    public static AuthorizedUserBuilder authorizedUserBuilder(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        return new AuthorizedUserBuilder(username, password, authorities);
    }

    public static AuthorizedUserBuilder authorizedUserBuilder(String username,
                                                              String password,
                                                              boolean enabled,
                                                              boolean accountNonExpired,
                                                              boolean credentialsNonExpired,
                                                              boolean accountNonLocked,
                                                              Collection<? extends GrantedAuthority> authorities) {
        return new AuthorizedUserBuilder(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public static final class AuthorizedUserBuilder {

        private final AuthorizedUser authorizedUser;

        public AuthorizedUserBuilder(String username, String password, Collection<? extends GrantedAuthority> authorities) {
            this.authorizedUser = new AuthorizedUser(username, password, authorities);
        }

        public AuthorizedUserBuilder(String username,
                                     String password,
                                     boolean enabled,
                                     boolean accountNonExpired,
                                     boolean credentialsNonExpired,
                                     boolean accountNonLocked,
                                     Collection<? extends GrantedAuthority> authorities) {
            this.authorizedUser = new AuthorizedUser(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        }

        public AuthorizedUserBuilder id(UUID id) {
            Assert.notNull(id, "id cannot be null");
            this.authorizedUser.setId(id);
            return this;
        }

        public AuthorizedUserBuilder firstName(String firstName) {
            this.authorizedUser.setFirstName(firstName);
            return this;
        }

        public AuthorizedUserBuilder lastName(String lastName) {
            this.authorizedUser.setLastName(lastName);
            return this;
        }

        public AuthorizedUserBuilder avatarUrl(String avatarUrl) {
            this.authorizedUser.setAvatarUrl(avatarUrl);
            return this;
        }

        public AuthorizedUser build() {
            return this.authorizedUser;
        }
    }
}
