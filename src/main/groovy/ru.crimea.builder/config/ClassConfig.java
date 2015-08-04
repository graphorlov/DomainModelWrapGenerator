package ru.crimea.builder.config;

import ru.crimea.builder.base.Field;
import ru.crimea.builder.base.Property;
import ru.crimea.builder.base.TypeClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vitaliy.orlov@gmail.com on 29.07.2015.
 */
public class ClassConfig {

    public enum FieldType{
        String(TypeClass.createString()), Long(TypeClass.createLong()), Date(TypeClass.createDate()), Boolean(TypeClass.createBoolean());

        private TypeClass typeClass;

        FieldType(TypeClass typeClass) {
            this.typeClass = typeClass;
        }

        public TypeClass getTypeClass() {
            return typeClass;
        }
    };
    private String moduleName;
    private String domainName;
    private String parentDomainName;
    private List<FieldConfiguration> fieldsConfiguration = new ArrayList<FieldConfiguration>();

    public ClassConfig(String moduleName, String domainName) {
        this.moduleName = moduleName;
        this.domainName = domainName;
    }

    public ClassConfig(String moduleName, String domainName, String parentDomainName) {
        this.moduleName = moduleName;
        this.domainName = domainName;
        this.parentDomainName = parentDomainName;
    }

    public void addSimpleFieldConfiguration(String fieldName, FieldType type){
        fieldsConfiguration.add(new FieldConfiguration(type.getTypeClass(), fieldName, false));
    }

    public void addReferenceFieldConfiguration(String fieldName, String domainType){
        fieldsConfiguration.add(new FieldConfiguration(TypeClass.createTypeClass("", domainType), fieldName, true));
    }

    public List<FieldConfiguration> getFieldsConfiguration(){
        return Collections.unmodifiableList(fieldsConfiguration);
    }

    public String getDomainName() {
        return domainName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getParentDomainName() {
        return parentDomainName;
    }
}
