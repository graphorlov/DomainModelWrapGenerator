package ru.crimea.builder.config;

import ru.crimea.builder.base.AccessModifier;
import ru.crimea.builder.base.Field;
import ru.crimea.builder.base.StaticProperty;
import ru.crimea.builder.base.TypeClass;
import ru.crimea.builder.builder.JavaClassBuilder;
import ru.crimea.builder.exception.ClassConfigurationException;
import ru.crimea.builder.method.Method;

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;

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
            JavaClassBuilder sourceBuilder = classBuilderMap.get(entry.getKey());
            for(FieldConfiguration field : entry.getValue().getFieldsConfiguration()){
                if(field.isReference()){

                    ClassConfig reference = configMap.get(field.getPropertyType().getClassName());
                    if(reference == null){
                        throw new ClassConfigurationException("Cant find configuration for reference class with name {"+ field.getPropertyName()+"} in classConfig {"+entry.getValue().getDomainName()+"}", entry.getValue());
                    }


                    JavaClassBuilder builder = classBuilderMap.get(field.getPropertyType().getClassName());
                    builder.addMethod(createBackReferentceGetter(parentPackage, sourceBuilder , field ));

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
        String fieldName = "domainClassesMap";
        TypeClass mainType = TypeClass.createClassExtendedFrom(TypeClass.createTypeClass("ru.intertrust", "DomainClass"));
        Set<TypeClass> classesSet = new HashSet<>();
        classesSet.add(TypeClass.createTypeClass("java.util", "HashMap"));

        final StringBuilder initStringResult = new StringBuilder();
        for(Map.Entry<String, JavaClassBuilder> classEntry :classBuilderMap.entrySet()){
            classesSet.add(classEntry.getValue().getCurrentType());
            initStringResult.append("put(\"").append(classEntry.getKey()).append("\", ").append(classEntry.getValue().getCurrentType().getFullClassName()).append(".class);");
        }

        builder.addField(new Field( new StaticProperty(TypeClass.createMap(TypeClass.createString(), mainType), fieldName,  classesSet) {
            @Override
            public String buildInitString() {
                return new StringBuilder(" new HashMap<String, Class<? extends DomainClass>>(){{\n\t").append(initStringResult).append("}};").toString();
            }
        }, AccessModifier.PROTECTED));




        return builder;
    }


    private Method createBackReferentceGetter(String parentPackage, final JavaClassBuilder sourceBuilder, final FieldConfiguration field) {

        final TypeClass finalReturnTypeClass =  TypeClass.createList(sourceBuilder.getCurrentType());
        Method result = new Method(AccessModifier.PUBLIC, finalReturnTypeClass , Method.generateMethodName("get", sourceBuilder.getCurrentType().getClassName() +"List")) {
            @Override
            public String buildMethodBody() {
                return new StringBuilder("\t\treturn (").append(finalReturnTypeClass.getUsedClassName()).append(") ")
                        .append("get$sourceDomain().getFieldValueByNameFromType(")
                        .append(" \"").append(field.getPropertyName()).append("\", \"")
                        .append(sourceBuilder.getCurrentType().getClassName()).append("\");").toString();
            }
        };

        result.addExtraUsesClasses(sourceBuilder.getCurrentType());

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
