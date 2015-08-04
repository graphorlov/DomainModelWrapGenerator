package ru.crimea.builder.base;

import java.util.Set;

/**
 * Created by vitaliy.orlov@gmail.com on 21.07.2015.
 */
public interface PrintElement  {
    public String buildStringView();
    public Set<TypeClass> getImports();

}
