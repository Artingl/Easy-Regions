package com.artingl.easyrg.misc.Database.Field;

import com.artingl.easyrg.misc.Database.Exception.ModelNotExist;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseModel {

    List<ModelField<?>> fields();
    List<String> names();

    String name();

    Object[] get(ModelCondition... keys) throws NullPointerException, ModelNotExist, SQLException;

    String identifierSQL();

    boolean update() throws SQLException;
}
