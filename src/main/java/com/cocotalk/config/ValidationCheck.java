package com.cocotalk.config;

import java.util.Date;

public class ValidationCheck {
    public static boolean isValid(String value) {
        return (value != null && !value.isEmpty());
    }

    public static boolean isValidId(long id) {
        return (id > 0);
    }

    public static boolean isValidPage(int page) {
        return (page >= 0);
    }

    public static boolean isValidDate(Date date) {
        return (date != null);
    }
}
