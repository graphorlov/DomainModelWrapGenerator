package ru.crimea.builder.config;

import ru.crimea.builder.base.AccessModifier;
import ru.crimea.builder.base.Field;
import ru.crimea.builder.base.TypeClass;
import ru.crimea.builder.builder.JavaClassBuilder;
import ru.crimea.builder.exception.ClassConfigurationException;
import ru.crimea.builder.method.Method;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vitaliy.orlov@gmail.com on 29.07.2015.
 */
public class ClassConfigurationContext {

    private Map<String, ClassConfig> configMap;

    public void addClassConfiguration(ClassConfig config){

        if(configMap == null){
            configMap= new HashMap<>();
        }
        if(configMap.containsKey(config.getDomainName())){
            throw new ClassConfigurationException("Class configuration already added to configuration context", config);
        }
        configMap.put(config.getDomainName(), config);
    }




    public void buildContext(String parentPackage, File rootFolder) throws Exception{

        Map<String, JavaClassBuilder> classBuilderMap = new HashMap<>();
        //resolve direct Links
        for(Map.Entry<String, ClassConfig> entry : configMap.entrySet()){
            classBuilderMap.put(entry.getKey(), buildClassBuilder(entry.getValue(), configMap, parentPackage));
        }

        // build back links
        for(Map.Entry<String, ClassConfig> entry : configMap.entrySet()){
            for(FieldConfiguration field : entry.getValue().getFieldsConfiguration()){
                if(field.isReference()){

                    ClassConfig reference = configMap.get(field.getPropertyType().getClassName());
                    if(reference == null){
                        throw new ClassConfigurationException("Cant find configuration for reference class with name {"+ field.getPropertyName()+"} in classConfig {"+entry.getValue().getDomainName()+"}", entry.getValue());
                    }

                    JavaClassBuilder builder = classBuilderMap.get(field.getPropertyType().getClassName());
                    builder.addMethod(createBackReferentceGetter(parentPackage, entry.getValue(), field ));

                }
            }
        }


        for(Map.Entry<String, JavaClassBuilder> entry : classBuilderMap.entrySet()){
            entry.getValue().saveClassToSourceFile(rootFolder);
        }

        JavaClassBuilder classFactory = buildClassFactory(classBuilderMap, parentPackage);
        classFactory.saveClassToSourceFile(rootFolder);

    }

    private JavaClassBuilder buildClassFactory(Map<String, JavaClassBuilder> classBuilderMap, String parentPackage) {
        JavaClassBuilder builder = new JavaClassBuilder("DomainClassFactory", parentPackage);
/*

        builder.addMethod(new Method(AccessModifier.PUBLIC_STATIC, TypeClass.createTypeClass("ru.intertrust", "DomainClass"), "getDomainClassByName") {
            @Override
            public String buildMethodBody() {
                return null;
            }
        });
        for(Map.Entry<String, JavaClassBuilder> entry : classBuilderMap.entrySet()){

        }
*/
        return builder;
    }


    private Method createBackReferentceGetter(String parentPackage, final ClassConfig referenceConfig, final FieldConfiguration field) {
        final TypeClass finalReturnTypeClass =  TypeClass.createList(field.getPropertyType());
        Method result = new Method(AccessModifier.PUBLIC, finalReturnTypeClass , Method.generateMethodName("get", field.getPropertyName()+"List")) {
            @Override
            public String buildMethodBody() {
                return new StringBuilder("\t\treturn (").append(finalReturnTypeClass.getUsedClassName()).append(") ")
                        .append("get$sourceDomain().getFieldValueByNameFromType(")
                        .append(" \"").append(field.getPropertyName()).append("\", \"")
                        .append(referenceConfig.getDomainName()).append("\");").toString();
            }
        };

        result.addExtraUsesClasses(TypeClass.createTypeClass(generatePackageName(parentPackage , referenceConfig.getModuleName()), referenceConfig.getDomainName()));

        return result;
    }


    private JavaClassBuilder buildClassBuilder(ClassConfig config, Map<String, ClassConfig> configMap, String parentPackage) {
        JavaClassBuilder classBuilder = new JavaClassBuilder(config.getDomainName(), generatePackageName(parentPackage , config.getModuleName()));
        if(config.getParentDomainName() != null && !config.getParentDomainName().isEmpty()){
            ClassConfig parentConfig = configMap.get(config.getParentDomainName());
            if(parentConfig == null){
                throw new ClassConfigurationException("Cant find configuration for parent class with name {"+ config.getParentDomainName()+"} in classConfig {"+config.getDomainName()+"}", config);
            }
            classBuilder.setParentClass(TypeClass.createTypeClass(generatePackageName(parentPackage, parentConfig.getModuleName()), config.getParentDomainName()));
        }else{
            // set default parent class
            classBuilder.setParentClass(TypeClass.createTypeClass("ru.intertrust", "DomainClass"));
        }



        for(final FieldConfiguration field : config.getFieldsConfiguration()){
            TypeClass returnTypeClass = null;
            if(field.isReference()){
                ClassConfig reference = configMap.get(field.getPropertyType().getClassName());
                if(reference == null){
                    throw new ClassConfigurationException("Cant find configuration for reference class with name {" + field.getPropertyType().getClassName()+"} in classConfig {"+config.getDomainName()+"}", config);
                }
                returnTypeClass =TypeClass.createTypeClass(generatePackageName(parentPackage, reference.getModuleName()), field.getPropertyType().getClassName());
            }else{
                returnTypeClass = field.getPropertyType();
            }

            final TypeClass finalReturnTypeClass = returnTypeClass;
            classBuilder.addMethod(new Method(AccessModifier.PUBLIC, finalReturnTypeClass, Method.generateMethodName("get", field.getPropertyName())) {
                @Override
                public String buildMethodBody() {
                    return new StringBuilder("\t\treturn (").append(finalReturnTypeClass.getUsedClassName()).append(") ")
                            .append("get$sourceDomain().getFieldValueByName(")
                            .append(" \"")
                            .append(field.getPropertyName()).append("\");").toString();
                }
            });
        }

        return classBuilder;
    }

    private TypeClass getDelegatTypeClass() {
        return TypeClass.createTypeClass("ru.intertrust", "DomainDataDelegat");
    }

    private String generatePackageName(String parentPackage, String moduleName){
        if(parentPackage != null && !parentPackage.isEmpty()){
            if(parentPackage.trim().endsWith("\\\\.")){
                return parentPackage.trim() + moduleName.toLowerCase();
            }else{
                return parentPackage.trim()+"."+moduleName.toLowerCase();
            }
        }
        return moduleName.toLowerCase();
    }



}
