package com.artingl.easyrg.misc.Database.PostgreSQL;

import com.artingl.easyrg.misc.Database.DatabaseProvider;
import com.artingl.easyrg.misc.Database.Field.DatabaseModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;

public class PostgreSQLProvider implements DatabaseProvider {

    public PostgreSQLProvider(String username, String password, String prefix, String database, String host, int port) {

    }

    @Override
    public boolean connect() {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public Statement createStatement() {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return null;
    }

    @Override
    public void freeStatement(Statement statement) {

    }

    @Override
    public boolean getValue(Consumer<ResultSet> callback, DatabaseModel model, ModelCondition... keys) {
        return false;
    }

    @Override
    public boolean setValue(DatabaseModel model) throws NullPointerException, SQLException {
        return false;
    }
}
