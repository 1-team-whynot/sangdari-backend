package com.sangdari.global.exception.custom;

public class ChecklistDateTooSoonException extends RuntimeException{
    public ChecklistDateTooSoonException() {
        super("행사일은 오늘 기준 14일 이후만 선택할 수 있습니다.");
    }
}
