package com.esand.customers.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto {
    private String name;
    private String cpf;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDate;
    private String gender;
}
