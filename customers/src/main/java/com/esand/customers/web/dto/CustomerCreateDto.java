package com.esand.customers.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCreateDto {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Size(min = 11, max = 11, message = "the CPF must have 11 characters")
    @CPF
    private String cpf;

    @Size(min = 7, max = 20)
    private String phone;

    @Size(min = 7, max = 100)
    private String email;

    @NotBlank
    @Size(min = 10, max = 200)
    private String address;

    private LocalDate birthDate;

    @NotBlank
    @Pattern(regexp = "M|F")
    private String gender;
}