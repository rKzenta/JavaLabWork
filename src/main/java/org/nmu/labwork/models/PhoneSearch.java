package org.nmu.labwork.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.collection.spi.PersistentBag;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "phone_search")
public class PhoneSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    String search;

    OffsetDateTime createdAt = OffsetDateTime.now();

    @OneToMany(cascade = { CascadeType.REMOVE }, orphanRemoval = true)
    List<Phone> phones = new ArrayList<>();
}
