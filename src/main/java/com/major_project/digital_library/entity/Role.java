package com.major_project.digital_library.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Component
public class Role implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator
    private UUID id;

    @Column(unique = true)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private Set<User> users;
}
