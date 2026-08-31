package com.entity;

import com.enums.Permissions;
import com.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "roles")
public class Roles  extends AbstractEntity{

    @Column(length = 20, unique = true)
    private String roles;


    @Column(name = "name")
    private String name;


//    @ManyToMany(fetch = FetchType.EAGER)
//    @JoinTable(
//            name = "roles_permissions",
//            joinColumns = @JoinColumn(name = "role_id"),
//            inverseJoinColumns = @JoinColumn(name = "permission_id")
//    )
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "roles_permission",
            joinColumns = @JoinColumn(name = "role_id")
    )
    @Enumerated(EnumType.STRING)
    private Set<Permissions> permissions;
}
