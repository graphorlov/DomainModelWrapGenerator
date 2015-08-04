package ru.crimea.builder;

import ru.crimea.builder.base.AccessModifier;
import ru.crimea.builder.base.Field;
import ru.crimea.builder.base.Property;
import ru.crimea.builder.base.TypeClass;
import ru.crimea.builder.builder.JavaClassBuilder;
import ru.crimea.builder.config.ClassConfig;
import ru.crimea.builder.config.ClassConfigurationContext;
import ru.crimea.builder.method.Method;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vitaliy.orlov@gmail.com on 27.07.2015.
 */
public class testMain {

    public static void main(String[] params) throws Exception {

        ClassConfigurationContext context = new ClassConfigurationContext();

        context.addClassConfiguration(new ClassConfig("contact", "Organization"){{
            addSimpleFieldConfiguration("name", FieldType.String);
            addSimpleFieldConfiguration("active", FieldType.Boolean);
            addReferenceFieldConfiguration("address", "Address");
        }});

        context.addClassConfiguration(new ClassConfig("contact", "ContactPerson"){{
            addSimpleFieldConfiguration("firstName", FieldType.String);
            addSimpleFieldConfiguration("lastName", FieldType.String);
            addReferenceFieldConfiguration("mailAddress", "MailAddress");
        }});

        context.addClassConfiguration(new ClassConfig("addresses", "Address" ){{
            addSimpleFieldConfiguration("address", FieldType.String);
        }});

        context.addClassConfiguration(new ClassConfig("addresses", "MailAddress", "Address"){{
            addSimpleFieldConfiguration("emailAddress", FieldType.String);
        }});


        context.addClassConfiguration(new ClassConfig("contact", "OrganozationContactPerson") {{
            addReferenceFieldConfiguration("organization", "Organization");
            addReferenceFieldConfiguration("contactPerson", "ContactPerson");
        }});


        context.buildContext("ru.intertrust.generated", new File("D:\\projectS\\JavaClassGeneration\\generated"));
    }

    private static void test01() throws Exception{
        JavaClassBuilder builder = new JavaClassBuilder("BaseClass", "ru.intertrust.domain");
        List<Field> fields = new ArrayList<Field>(){{
            add(Field.createPublicField("stringField", TypeClass.createString()));
            add(Field.createPublicField("longField", TypeClass.createLong()));
            add(Field.createPublicField("booleanField", TypeClass.createBoolean()));
            add(Field.createPublicField("dateField", TypeClass.createDate()));
        }};

        for(Field field : fields){
            builder.addField(field).addMethod(field.createGetter()).addMethod(field.createSetter());
        }

        builder.saveClassToSourceFile(new File("D:\\projectS\\JavaClassGeneration\\generated"));



        builder = new JavaClassBuilder("TestClass", "ru.intertrust.domain");
        Field baseField = Field.createPrivateField("baseField", TypeClass.createTypeClass("ru.intertrust.domain", "BaseClass"));
        builder.addField(baseField)
                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createVoid(), "setStringValue", "setStringField", new Property(TypeClass.createString(), "p1")))
                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createVoid(), "setLongValue", "setLongField", new Property(TypeClass.createLong(), "p1")))
                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createVoid(), "setBooleanValue", "setBooleanField", new Property(TypeClass.createBoolean(), "p1")))
                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createVoid(), "setDateValue", "setDateField", new Property(TypeClass.createDate(), "p1")))

                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createString(), "getStringValue", "getStringField"))
                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createLong(), "getLongValue", "getLongField"))
                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createBoolean(), "getBooleanValue", "getBooleanField"))
                .addMethod(generateDelegatMethod(baseField.getProperty().getPropertyName(), TypeClass.createDate(), "getDateValue", "getDateField"))

                .addMethod(baseField.createGetter())
                .addMethod(baseField.createSetter());

        builder.saveClassToSourceFile(new File("D:\\projectS\\JavaClassGeneration\\generated"));
    }


    private static Method generateDelegatMethod( final String targetDelegatName, final TypeClass returnResult, String inputMethodName, final String delegatMethodName, final Property... inputParams){
        return new Method(AccessModifier.PUBLIC, returnResult, inputMethodName, inputParams) {
            @Override
            public String buildMethodBody() {
                StringBuilder result = new StringBuilder("\t\t");
                if(!returnResult.isVoid()){
                    result.append("return ");
                }
                result.append(targetDelegatName).append(".").append(delegatMethodName).append("(");
                if(inputParams != null){
                    boolean isFirstProperty = true;
                    for(Property prop : inputParams){
                        if(!isFirstProperty){
                            result.append(", ");
                        }

                        result.append(prop.getPropertyName());
                        isFirstProperty = false;
                    }
                }
                result.append(");");
                return  result.toString();
            }
        };
    }
}
