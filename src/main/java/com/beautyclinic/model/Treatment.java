package com.beautyclinic.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Treatment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank
    private String name;

    @Column(nullable = false)
    @Positive
    private Integer durationMinutes;

    @Column(nullable = false)
    @PositiveOrZero
    private Double price;

    @Column(nullable = false)
    private Boolean active;

    @OneToMany(mappedBy = "treatment")
    private List<Appointment> appointments = new ArrayList<>();

    public LocalTime calculateEndTime(LocalTime startTime) {
        return startTime.plusMinutes(durationMinutes);
    }
}
