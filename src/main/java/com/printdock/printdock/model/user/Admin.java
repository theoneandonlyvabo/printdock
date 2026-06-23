package com.printdock.printdock.model.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("ADMIN")
@Getter
@Setter
@NoArgsConstructor
public class Admin extends User {
    public Admin(Long id, String username, String password) {
        super(id, username, password, Role.ADMIN);
    }
}
