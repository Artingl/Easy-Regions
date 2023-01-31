package com.artingl.easyrg.misc.Database.Field;

import com.artingl.easyrg.misc.Database.Exception.ModelNotExist;

import java.util.List;

public interface DatabaseModel {

    List<ModelField> fields();
    List<String> names();

    String name();

    Object load(ModelCondition... keys) throws ModelNotExist;

}
