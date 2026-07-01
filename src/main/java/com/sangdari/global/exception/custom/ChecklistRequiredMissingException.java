package com.sangdari.global.exception.custom;

public class ChecklistRequiredMissingException extends RuntimeException{
    public ChecklistRequiredMissingException() {
        super("행사 조건을 모두 입력해주세요.");
    }
}
