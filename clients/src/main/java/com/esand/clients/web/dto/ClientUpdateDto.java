package com.esand.clients.web.dto;

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
public class ClientUpdateDto {
    @Size(min = 5, max = 100)
    private String name;

    @Size(min = 11, max = 11, message = "the CPF must have 11 characters")
    @CPF
    private String cpf;

    @Size(min = 7, max = 20)
    private String phone;

    @Size(min = 7, max = 100)
    private String email;

    @Size(min = 10, max = 200)
    private String address;

    private LocalDate birthDate;

    @Pattern(regexp = "M|F")
    private String gender;
}
