package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.exception.BackendResourcesException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Аннотация, указывающая, что этот класс будет обрабатывать исключения в контроллерах
@RestControllerAdvice
public class RestExceptionHandler {

    // Метод для обработки исключений типа BackendResourcesException
    @ExceptionHandler(BackendResourcesException.class)
    public ResponseEntity<String> handleException(BackendResourcesException backendResourcesException) {
        return new ResponseEntity<>(backendResourcesException.getMessage(), backendResourcesException.getHttpStatus());
    }

    // Устанавливаем статус ответа на BAD_REQUEST (400) для ошибок валидации
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    // Метод для обработки исключений типа MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        // Перебираем все ошибки полей и добавляем их в карту
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
        return errorMap;
    }

}
