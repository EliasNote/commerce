package com.esand.customers.web.dto;

import com.esand.customers.entity.Customer;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseDto implements Serializable {
    private String name;
    private String cpf;
    private String phone;
    private String email;
    private String address;
    private LocalDate birthDate;
    private Customer.Gender gender;
    private LocalDateTime createDate;
}
