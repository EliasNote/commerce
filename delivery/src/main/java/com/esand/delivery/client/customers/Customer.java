package com.esand.delivery.client.customers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String name;
    private String cpf;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDate;
    private String gender;
}