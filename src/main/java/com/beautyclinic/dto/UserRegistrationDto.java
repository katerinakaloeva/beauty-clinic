package com.beautyclinic.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDto {

    @NotBlank(message = "Το ονοματεπώνυμο είναι υποχρεωτικό")
    @Size(max = 100, message = "Το ονοματεπώνυμο δεν μπορεί να ξεπερνά τους 100 χαρακτήρες")
    private String fullName;

    @NotBlank(message = "Το email είναι υποχρεωτικό")
    @Email(message = "Το email δεν έχει έγκυρη μορφή")
    @Size(max = 150, message = "Το email δεν μπορεί να ξεπερνά τους 150 χαρακτήρες")
    private String email;

    @NotBlank(message = "Ο κωδικός είναι υποχρεωτικός")
    @Size(min = 8, max = 64, message = "Ο κωδικός πρέπει να έχει 8 έως 64 χαρακτήρες")
    private String password;

    @NotBlank(message = "Η επιβεβαίωση κωδικού είναι υποχρεωτική")
    private String confirmPassword;
}