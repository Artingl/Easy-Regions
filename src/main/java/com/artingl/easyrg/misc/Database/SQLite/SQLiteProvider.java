package com.artingl.easyrg.misc.Database.SQLite;

import com.artingl.easyrg.PluginMain;
import com.artingl.easyrg.misc.Database.DatabaseProvider;
import com.artingl.easyrg.misc.Database.Field.DatabaseModel;
import com.artingl.easyrg.misc.Database.Field.ModelCondition;
import com.artingl.easyrg.misc.Database.Field.ModelField;
import com.artingl.easyrg.misc.Database.Field.ModelsRegistry;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SQLiteProvider implements DatabaseProvider {
    static final String JDBC_DRIVER = "org.sqlite.JDBC";

    private List<Statement> statementList;
    private Connection connection = null;
    private String jdbcFile;
    private boolean connected;

    public SQLiteProvider(File dbFile) {
        try {
            boolean shouldInitialize = !dbFile.isFile();

            Class.forName(JDBC_DRIVER);

            this.jdbcFile = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            this.connection = DriverManager.getConnection(this.jdbcFile);
            this.statementList = new ArrayList<>();
            this.connected = true;

            if (shouldInitialize) {
                Statement statement = createStatement();

                for (Class<? extends DatabaseModel> modelClass : ModelsRegistry.modelsList.map().values()) {
                    DatabaseModel model = ModelsRegistry.modelsList.get(modelClass);
                    StringBuilder syntax = new StringBuilder();

                    syntax.append("id INTEGER PRIMARY KEY,");

                    for (ModelField field : model.fields()) {
                        syntax.append(field.name()).append(" ").append(field.fieldSettings()).append(",");
                    }

                    String sql = "CREATE TABLE " + model.name() + " (" + syntax.substring(0, syntax.length() - 1) + ")";

                    statement.execute(sql);
                    freeStatement(statement);
                }
            }
        } catch (Exception se) {
            PluginMain.logger.log(Level.SEVERE, "SQLite database connection error", se);
            this.connected = false;
        }
    }

    @Override
    public boolean connect() {
        return this.connected;
    }

    @Override
    public boolean shutdown() {
        try {
            this.connected = false;

            if (connection != null)
                connection.close();

            for (Statement statement: this.statementList) {
                statement.close();
            }

            this.statementList.clear();
            this.connection = null;

            return true;
        } catch (SQLException se) {
            PluginMain.logger.log(Level.SEVERE, "SQLite shutting down error", se);
        }

        return false;
    }

    @Override
    public boolean getValue(Consumer<ResultSet> callback, DatabaseModel model, ModelCondition... keys) throws SQLException {
        Statement statement = createStatement();

        try {
            StringBuilder where = new StringBuilder();

            for (ModelCondition condition : keys) {
                where.append(condition.getAsString()).append(",");
            }

            String sql = "select * from " + model.name() + " where " + where.substring(0, where.length() - 1);

            if (!statement.execute(sql)) {
                freeStatement(statement);
                callback.accept(null);
                return false;
            }

            callback.accept(statement.getResultSet());
            return true;
        } finally {
            freeStatement(statement);
        }
    }

    @Override
    public boolean setValue(DatabaseModel model) throws NullPointerException, SQLException {
        if (model == null)
            throw new NullPointerException("Model is null");

        List<ModelField<?>> fields = model.fields();
        List<String> names = model.names();

        PreparedStatement statement = prepareStatement(
                "insert into " + model.name() + " " +
                    "(" + String.join(",", model.names()) + ") values " +
                    "(" + String.join(",", Collections.nCopies(names.size(), "?")) + ")"
        );

        try {
            int i = 1;

            for (ModelField<?> field : fields) {
                String serialized = field.serialize();

                if (serialized != null) {
                    statement.setString(i, serialized);
                }

                ++i;
            }

            statement.executeUpdate();
        } finally {
            freeStatement(statement);
        }

        return true;
    }

    @Override
    public Statement createStatement() throws SQLException {
        if (!this.connected)
            throw new SQLException("Not connected to database!");

        Statement statement = this.connection.createStatement();
        this.statementList.add(statement);

        return statement;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (!this.connected)
            throw new SQLException("Not connected to database!");

        PreparedStatement statement = this.connection.prepareStatement(sql);
        this.statementList.add(statement);

        return statement;
    }

    @Override
    public void freeStatement(Statement statement) throws SQLException {
        statement.close();
        this.statementList.remove(statement);
    }
}
