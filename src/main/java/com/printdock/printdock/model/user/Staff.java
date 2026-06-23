package com.printdock.printdock.model.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@DiscriminatorValue("STAFF")
@Getter
@Setter
@NoArgsConstructor
public class Staff extends User {
    public Staff(Long id, String username, String password) {
        super(id, username, password, Role.STAFF);
    }
}
