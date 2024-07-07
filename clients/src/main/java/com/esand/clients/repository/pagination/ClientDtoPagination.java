package com.esand.clients.repository.pagination;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public interface ClientDtoPagination {
    String getName();
    String getCpf();
    String getPhone();
    String getEmail();
    String getAddress();
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate getBirthDate();
    String getGender();
    Boolean getStatus();
}
