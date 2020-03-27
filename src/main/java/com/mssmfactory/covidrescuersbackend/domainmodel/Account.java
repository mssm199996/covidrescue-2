package com.mssmfactory.covidrescuersbackend.domainmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mssmfactory.covidrescuersbackend.security.WebSecurityConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Document
@ToString
public class Account implements UserDetails {

    public static String SEQUENCE_ID = "accounts_sequence";

    @Id
    @Indexed
    private Long id;

    @NotNull
    private String firstName, famillyName, username;

    @NotNull
    @JsonIgnore
    private String password;

    @NotNull
    @Email
    @Indexed(unique = true)
    private String email;

    @NotNull
    @JsonIgnore
    private AccountRole accountRole;

    @NotNull
    @Indexed
    private Integer cityId, townId;

    @NotNull
    private Integer numberOfMeetings;

    @NotNull
    private AccountState accountState;

    @NotNull
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint position;

    @Transient
    @JsonIgnore
    private Set<SimpleGrantedAuthority> authorities = new HashSet<>();

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o != null && o instanceof Account)
            return this.getId() == ((Account) o).getId();

        return false;
    }

    @Override
    public Collection<SimpleGrantedAuthority> getAuthorities() {
        if (this.getAccountRole() != null)
            this.authorities.add(new SimpleGrantedAuthority(this.getAccountRole().getName()));

        return this.authorities;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public enum AccountState {
        HEALTHY, SUSPECTED, CONTAMINATED, CURED, DEAD
    }

    @Getter
    public enum AccountRole {
        ADMIN(WebSecurityConfig.ADMIN_ROLE),
        USER(WebSecurityConfig.USER_ROLE),
        API(WebSecurityConfig.API_ROLE),
        DEV(WebSecurityConfig.DEV_ROLE);

        private String name;

        AccountRole(String name) {
            this.name = name;
        }
    }
}
