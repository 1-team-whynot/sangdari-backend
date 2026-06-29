package com.sangdari.global.exception.custom;

public class ChecklistInvalidFormatException extends RuntimeException{
    public ChecklistInvalidFormatException() {
        super("입력 형식이 올바르지 않습니다.");
    }
}
