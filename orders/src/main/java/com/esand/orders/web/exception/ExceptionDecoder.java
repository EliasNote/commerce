package com.esand.orders.web.exception;

import com.esand.orders.exception.ConnectionException;
import com.esand.orders.exception.EntityNotFoundException;
import com.esand.orders.exception.InvalidQuantityException;
import com.esand.orders.exception.UnknownErrorException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ExceptionDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String method, Response response) {
        log.info("Exception decoder: {}, {}", method, response);

        if (method.contains("getClientByCpf") && response.status() == 404) {
            return new EntityNotFoundException("Customer not found by CPF");
        } else if (method.contains("getClientByCpf") && response.status() == 503) {
            return new ConnectionException("Clients API not available");
        }

        if (method.contains("getProductBySku") && response.status() == 404) {
            return new EntityNotFoundException("Product not found by sku");
        } else if (method.contains("getProductBySku") && response.status() == 503) {
            return new ConnectionException("Products API not available");
        }

        return new UnknownErrorException();
    }
}
