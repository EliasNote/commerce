package com.esand.orders.client;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakToken {
    private String access_token;
    private Integer expires_in;
}