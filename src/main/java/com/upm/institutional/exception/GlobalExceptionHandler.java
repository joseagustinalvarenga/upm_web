package com.upm.institutional.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IOException.class)
    public String handleIOException(IOException e, Model model) {
        String message = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        if (message.contains("connection reset") || message.contains("broken pipe") || e.getClass().getName().contains("ClientAbortException")) {
            // Client closed the connection (browser tab closed, refreshed, or network dropped).
            // Return null so Spring does not try to render an error page over a closed socket.
            log.debug("Cliente desconectó la conexión antes de finalizar la respuesta: {}", e.getMessage());
            return null;
        }
        log.error("Error de entrada/salida: ", e);
        model.addAttribute("error", "Error de conexión.");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException e, Model model) {
        log.error("Error de tiempo de ejecución: ", e);
        model.addAttribute("error", "Error en la operación.");
        model.addAttribute("message", e.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        log.error("Error no controlado: ", e);
        model.addAttribute("error", "Ha ocurrido un error inesperado.");
        model.addAttribute("message", e.getMessage());
        return "error";
    }
}
