package ru.crimea.builder.base;


import ru.crimea.builder.method.Method;

import java.util.Set;

/**
 * Created by vitaliy.orlov@gmail.com on 20.07.2015.
 */
public class Field implements PrintElement {
    private Property property;
    private AccessModifier accessModifier;

    private Field(Property property, AccessModifier accessModifier) {
        this.property = property;
        this.accessModifier = accessModifier;
    }

    public static Field createPrivateField(String fieldName, TypeClass typeClass){
        return new Field(new Property(typeClass, fieldName), AccessModifier.PRIVATE);
    }

    public static Field createPublicField(String fieldName, TypeClass typeClass){
        return new Field(new Property(typeClass, fieldName), AccessModifier.PUBLIC);
    }

    public static Field createProtectedField(String fieldName, TypeClass typeClass){
        return new Field(new Property(typeClass, fieldName), AccessModifier.PROTECTED);
    }

    public Method createGetter(){
        return Method.createGetter(this);
    }

    public Method createSetter(){
        return Method.createSetter(this);
    }

    public Property getProperty() {
        return property;
    }

    public AccessModifier getAccessModifier() {
        return accessModifier;
    }

    @Override
    public String buildStringView() {
        return new StringBuilder(accessModifier.getPrintValue()).append(" ").append(property.buildStringView()).toString();
    }

    @Override
    public Set<TypeClass> getImports() {
        return property.getImports();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Field field = (Field) o;

        if (property != null ? !property.equals(field.property) : field.property != null) return false;
        return accessModifier == field.accessModifier;

    }

    @Override
    public int hashCode() {
        int result = property != null ? property.hashCode() : 0;
        result = 31 * result + (accessModifier != null ? accessModifier.hashCode() : 0);
        return result;
    }
}
