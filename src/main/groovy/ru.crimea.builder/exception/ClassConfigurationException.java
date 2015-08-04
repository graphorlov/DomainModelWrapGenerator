package ru.crimea.builder.exception;

import ru.crimea.builder.config.ClassConfig;

/**
 * Created by vitaliy.orlov@gmail.com on 29.07.2015.
 */
public class ClassConfigurationException extends RuntimeException {
    private ClassConfig classConfig;

    public ClassConfigurationException(String message, ClassConfig classConfig) {
        super(message);
        this.classConfig = classConfig;
    }

    public ClassConfig getClassConfig() {
        return classConfig;
    }
}
