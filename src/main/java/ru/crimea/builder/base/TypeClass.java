package ru.crimea.builder.base;


import java.util.HashSet;
import java.util.Set;

/**
 * Created by vitaliy.orlov@gmail.com on 21.07.2015.
 */
public class TypeClass {
    private String packageName;
    private String className;
    private TypeClass[] parameters;


    public static TypeClass createTypeClass(String packageName, String className){
        return new TypeClass(packageName, className, null);
    }

    public static TypeClass createTypeClass(String packageName, String className, TypeClass... parameters){
        return new TypeClass(packageName, className, parameters);
    }

    public static TypeClass createVoid(){
        return new TypeClass("", "void");
    }

    public static TypeClass createString(){
        return new TypeClass("", "String");
    }

    public static TypeClass createLong(){
        return new TypeClass("", "Long");
    }

    public static TypeClass createBoolean(){
        return new TypeClass("", "Boolean");
    }

    public static TypeClass createDate(){
        return new TypeClass("java.util", "Date");
    }

    public static TypeClass createClassExtendedFrom(TypeClass extendedClassType){
        return new TypeClass("java.lang", "Class", extendedClassType){
            @Override
            public String getUsedClassName() {
                return new StringBuilder(getClassName()).append("< ? extends " ).append(getParameters()[0].getUsedClassName()).append(">").toString();
            }
        };
    }

    public static TypeClass createList(TypeClass parameterType){
        return new TypeClass("java.util", "List", parameterType);
    }

    public static TypeClass createMap(TypeClass keyType, TypeClass valueType){
        return new TypeClass("java.util", "Map", keyType, valueType);
    }

    public boolean isVoid(){
        return "void".equalsIgnoreCase(className);
    }

    private TypeClass(String packageName, String className, TypeClass... parameters) {
        this.packageName = packageName;
        this.className = className;
        this.parameters = parameters;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getPackagePath() {
        if(packageName != null && !packageName.isEmpty()){
            return packageName.replace(".", "/");
        }
        return "";
    }

    public String getFullClassName(){
        return getPackageName() + "." + getClassName();
    }

    public TypeClass[] getParameters(){
        return parameters;
    }

    public String getUsedClassName(){
        StringBuilder result = new StringBuilder(className);

        if(parameters != null && parameters.length > 0){
            result.append("<");
            boolean isFirstParam = true;
            for(TypeClass typeClass : parameters){
                if(!isFirstParam){
                    result.append(", ");
                }
                isFirstParam = false;
                result.append(typeClass.getUsedClassName());
            }
            result.append(">");
        }
        return  result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TypeClass typeClass = (TypeClass) o;

        if (packageName != null ? !packageName.equals(typeClass.packageName) : typeClass.packageName != null)
            return false;
        return !(className != null ? !className.equals(typeClass.className) : typeClass.className != null);

    }

    @Override
    public int hashCode() {
        int result = packageName != null ? packageName.hashCode() : 0;
        result = 31 * result + (className != null ? className.hashCode() : 0);
        return result;
    }

    public Set<TypeClass> getAllUsedTypes() {
        Set<TypeClass> result = new HashSet<TypeClass>();
        result.add(this);
        if(parameters != null && parameters.length > 0){
            for(TypeClass pType : parameters){
                if(!result.contains(pType)){
                    result.addAll(pType.getAllUsedTypes());
                }
            }
        }
        return result;
    }
}
