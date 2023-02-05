package com.artingl.easyrg.misc.Database;

import com.artingl.easyrg.misc.Database.Field.DatabaseModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public interface DatabaseProvider {


    boolean connect();
    boolean shutdown();

    boolean getValue(Consumer<ResultSet> callback, DatabaseModel model, ModelCondition... keys) throws SQLException;
    boolean setValue(DatabaseModel model) throws NullPointerException, SQLException;

    Statement createStatement() throws SQLException;
    PreparedStatement prepareStatement(String sql) throws SQLException;

    void freeStatement(Statement statement) throws SQLException;

    boolean delete(DatabaseModel model) throws SQLException;

    boolean update(DatabaseModel model) throws SQLException;
}
