package com.esand.customers.repository.pagination;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.time.LocalDate;

public interface CustomerDtoPagination {
    String getName();
    String getCpf();
    String getPhone();
    String getEmail();
    String getAddress();
    LocalDate getBirthDate();
    String getGender();
}
