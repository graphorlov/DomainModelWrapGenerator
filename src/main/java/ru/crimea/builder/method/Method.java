package ru.crimea.builder.method;

import ru.crimea.builder.base.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by vitaliy.orlov@gmail.com on 21.07.2015.
 */
public abstract class Method implements PrintElement {

    private TypeClass returnTypeClass;
    private Set<TypeClass> usedClasses = new HashSet<>();
    private AccessModifier accessModifier;
    private Property[] inputParams;
    private String menthodName;


    public static Method createGetter(final Field field){
        return new Method(AccessModifier.PUBLIC,
                                    field.getProperty().getPropertyType(),
                                    generateMethodName("get", field.getProperty().getPropertyName())) {
            @Override
            public String buildMethodBody() {
                return new StringBuilder("\t\t return this.").append(field.getProperty().getPropertyName()).append(";").toString();
            }
        };
    }

    public static Method createSetter(final Field field){
        return new Method(AccessModifier.PUBLIC,
                TypeClass.createVoid(),
                generateMethodName("set", field.getProperty().getPropertyName()),
                field.getProperty()) {
            @Override
            public String buildMethodBody() {
                return new StringBuilder("\t\t this.").append(field.getProperty().getPropertyName()).append(" = ").append(field.getProperty().getPropertyName()).append(";").toString();
            }
        };
    }



    public Method( AccessModifier accessModifier, TypeClass returnTypeClass,  String menthodName, Property ... inputParams) {
        this.returnTypeClass = returnTypeClass;
        this.accessModifier = accessModifier;
        this.inputParams = inputParams;
        this.menthodName = menthodName;
    }

    public Method( AccessModifier accessModifier, TypeClass returnTypeClass,  String menthodName, Set<TypeClass> usedClasses, Property ... inputParams) {
        this.returnTypeClass = returnTypeClass;
        this.usedClasses = usedClasses;
        this.accessModifier = accessModifier;
        this.inputParams = inputParams;
        this.menthodName = menthodName;
    }

    public void addExtraUsesClasses(TypeClass ... types ){
        if(types != null){
            for(TypeClass cType : types ){
                if(cType != null){
                    usedClasses.add(cType);
                }
            }
        }
    }

    @Override
    public String buildStringView() {
        StringBuilder result = new StringBuilder(accessModifier.getPrintValue())
                                                .append(" ")
                                                .append(returnTypeClass.getUsedClassName())
                                                .append(" ")
                                                .append(menthodName)
                                                .append("(");
          if(inputParams != null){
              boolean isFirstProperty = true;
              for(Property in : inputParams){
                  if(!isFirstProperty){
                      result.append(", ");
                  }
                  result.append(in.getPropertyType().getUsedClassName()).append(" ").append(in.getPropertyName());
                  isFirstProperty = false;
              }
          }
        result.append("){\r\n\t")
                .append(buildMethodBody())
                .append("\n\t}");
        return result.toString();
    }

    @Override
    public Set<TypeClass> getImports() {
        Set<TypeClass> result = new HashSet<>();
        result.add(returnTypeClass);
        if(inputParams != null){
            for(Property in : inputParams){
                result.add(in.getPropertyType());
            }
        }

        if(usedClasses != null){
            result.addAll(usedClasses);
        }
        return result;
    }

    public static String generateMethodName(String prefix, String fieldName){
        return new StringBuilder(prefix).append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1)).toString();
    }

    public abstract String buildMethodBody();
}
