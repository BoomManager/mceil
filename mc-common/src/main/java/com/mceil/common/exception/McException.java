package com.mceil.common.exception;

import com.mceil.common.enums.ExceptionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class McException extends RuntimeException {
    private ExceptionEnum exceptionEnum;
}
