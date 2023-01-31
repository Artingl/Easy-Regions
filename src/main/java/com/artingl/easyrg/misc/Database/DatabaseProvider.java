package com.artingl.easyrg.misc.Database;

import com.artingl.easyrg.misc.Database.Field.DatabaseModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;
import com.artingl.easyrg.misc.Database.Field.ModelField;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public interface DatabaseProvider {


    boolean connect();
    boolean shutdown();

    Map.Entry<DatabaseResult, ModelField[]> getValue(DatabaseModel model, ModelCondition... keys);
    DatabaseResult setValue(DatabaseModel model) throws NullPointerException, SQLException;

    Statement createStatement() throws SQLException;
    PreparedStatement prepareStatement(String sql) throws SQLException;

    void freeStatement(Statement statement) throws SQLException;

}
