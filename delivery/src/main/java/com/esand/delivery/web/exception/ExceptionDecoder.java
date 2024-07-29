package com.esand.delivery.web.exception;

import com.esand.delivery.exception.ConnectionException;
import com.esand.delivery.exception.EntityNotFoundException;
import com.esand.delivery.exception.UnknownErrorException;
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

        if (method.contains("checkStatus()") && response.status() == 503) {
            return new ConnectionException("Products API not available");
        }

        if (method.contains("addProductQuantityBySku") && response.status() == 404) {
            return new EntityNotFoundException("Product not found by sku, but status has been updated");
        }

        return new UnknownErrorException();
    }
}
