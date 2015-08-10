package ru.crimea.builder.base;

import java.util.Set;

/**
 * Created by vitaliy.orlov@gmail.com on 03.08.2015.
 */
public abstract class StaticProperty extends Property {

    private Set<TypeClass> additionalClasses;

    public StaticProperty(TypeClass propertyType, String propertyName, Set<TypeClass> additionalClasses) {
        super(propertyType, propertyName);
        this.additionalClasses = additionalClasses;
    }

    public void setAdditionalClasses(Set<TypeClass> additionalClasses) {
        this.additionalClasses = additionalClasses;
    }


    @Override
    public Set<TypeClass> getImports() {
        Set<TypeClass> result = super.getImports();
        if(additionalClasses != null){
            result.addAll(additionalClasses);
        }
        return result;
    }

    @Override
    public String buildStringView() {
        String initContent = buildInitString();
        if(initContent != null && !initContent.isEmpty()){
            return new StringBuilder("static ").append(getPropertyType().getUsedClassName()).append(" ").append(getPropertyName())
                    .append(" = ").append(initContent).append(";").toString();
        }
        return super.buildStringView();
    }

    public abstract String buildInitString();
}








