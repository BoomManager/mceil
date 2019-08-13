package com.mceil.common.advice;


import com.mceil.common.exception.McException;
import com.mceil.common.vo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CommonExceptionHandler {
    @ExceptionHandler(McException.class)
    public ResponseEntity<ExceptionResult> handleException(McException e){
      return ResponseEntity.status(e.getExceptionEnum().getCode())
              .body(new ExceptionResult(e.getExceptionEnum()));
    }
}
