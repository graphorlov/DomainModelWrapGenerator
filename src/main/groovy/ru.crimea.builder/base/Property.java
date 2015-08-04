package ru.crimea.builder.base;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vitaliy.orlov@gmail.com on 21.07.2015.
 */
public class Property implements PrintElement{
    private TypeClass propertyType;
    private String propertyName;

    public Property(TypeClass propertyType, String propertyName) {
        this.propertyType = propertyType;
        this.propertyName = propertyName;
    }

    public TypeClass getPropertyType() {
        return propertyType;
    }

    public String getPropertyName() {
        return propertyName;
    }


    @Override
    public String buildStringView() {
        return new StringBuilder(propertyType.getUsedClassName()).append(" ").append(propertyName).append(";").toString();
    }

    @Override
    public Set<TypeClass> getImports() {
        return new HashSet<TypeClass>(){{
            addAll(propertyType.getAllUsedTypes());
        }};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (propertyType != null ? !propertyType.equals(property.propertyType) : property.propertyType != null)
            return false;
        return !(propertyName != null ? !propertyName.equals(property.propertyName) : property.propertyName != null);

    }

    @Override
    public int hashCode() {
        int result = propertyType != null ? propertyType.hashCode() : 0;
        result = 31 * result + (propertyName != null ? propertyName.hashCode() : 0);
        return result;
    }
}
