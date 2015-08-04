package ru.crimea.builder.builder;

import ru.crimea.builder.base.TypeClass;
import ru.crimea.builder.base.Field;
import ru.crimea.builder.method.Method;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * Created by Vitaliy.Orlov on 20.07.2015.
 */
public class JavaClassBuilder  {
    private Set<Field> fields = new HashSet<>();
    private Set<Method> methods = new HashSet<>();
    private Set<TypeClass> implementInterface = new HashSet<>();

    private TypeClass parentClass;
    private TypeClass currentType;

    public JavaClassBuilder(String className, String packageName) {
        this.currentType = TypeClass.createTypeClass(packageName, className);
    }

    public JavaClassBuilder addField(Field field){
        fields.add(field);
        return this;
    }

    public JavaClassBuilder addMethod(Method method){
        methods.add(method);
        return this;
    }

    public JavaClassBuilder setParentClass(TypeClass parentClass){
        this.parentClass = parentClass;
        return this;
    }

    public JavaClassBuilder addInterface(TypeClass typeClass){
        this.implementInterface.add(typeClass);
        return this;
    }

    public void saveClassToSourceFile(File rootFolder) throws Exception{
        File targetFolder = new File(rootFolder, currentType.getPackagePath());
        if (!targetFolder.exists()){
            targetFolder.mkdirs();
        }
        FileWriter writer = new FileWriter(new File(targetFolder, currentType.getClassName() + ".java"));
        writer.write(buildClassContent());
        writer.flush();
        writer.close();
    }

    public String buildClassContent(){
        StringBuilder fileContent = new StringBuilder();

        // package info
        fileContent.append("package ").append(currentType.getPackageName()).append(";\n");

        // import info
        for(TypeClass importClass : getAllUsedTypeClasses()){
            if(!importClass.getPackageName().isEmpty() && !currentType.getPackageName().equals(importClass.getPackageName())){
                fileContent.append("import ").append(importClass.getPackageName()).append(".").append(importClass.getClassName()).append(";\n");
            }
        }

        //add coment
        fileContent.append("/**\n*").append(getClassComment()).append("\n*/\n");

        //create class definition
        fileContent.append("public class ").append(currentType.getClassName());
        if(!implementInterface.isEmpty()){
            fileContent.append(" implements ");
            boolean isFirsImport = true;
            for(TypeClass interfaceType : implementInterface){
                if(!isFirsImport){
                    fileContent.append(", ");
                }
                fileContent.append(interfaceType.getClassName());
            }
        }
        if(parentClass != null){
            fileContent.append(" extends ").append(parentClass.getClassName());
        }

        fileContent.append("{\n\t");

        // add fields definition
        for(Field field : fields){
            fileContent.append(field.buildStringView()).append("\n\t");
        }

        // add methods definition
        for(Method method : methods){
            fileContent.append(method.buildStringView()).append("\n\t");
        }

        fileContent.append("}");
        return fileContent.toString();
    }

    private Set<TypeClass> getAllUsedTypeClasses(){
        Set<TypeClass>  result = new HashSet<>();
        for(Field field :fields){
            result.addAll(field.getImports());
        }

        for(Method method : methods){
            result.addAll(method.getImports());
        }

        result.addAll(implementInterface);
        if(parentClass != null){
            result.add(parentClass);
        }





        return result;
    }

    private String getClassComment(){
        return new StringBuilder("Generated special for InterTrust company :) at ").append(new Date().toString()).toString();
    }
}
