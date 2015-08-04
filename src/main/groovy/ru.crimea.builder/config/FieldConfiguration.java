package ru.crimea.builder.config;

import ru.crimea.builder.base.Property;
import ru.crimea.builder.base.TypeClass;

/**
 * Created by vitaliy.orlov@gmail.com on 29.07.2015.
 */
public class FieldConfiguration extends Property {
    private boolean reference;

    public FieldConfiguration(TypeClass propertyType, String propertyName, boolean reference) {
        super(propertyType, propertyName);
        this.reference = reference;
    }

    public boolean isReference() {
        return reference;
    }
}
