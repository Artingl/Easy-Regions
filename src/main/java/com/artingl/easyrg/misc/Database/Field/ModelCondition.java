package com.artingl.easyrg.misc.Database.Field;

import java.sql.SQLException;

public class ModelCondition {

    public static ModelCondition make(Object ...values) {
        return new ModelCondition(values);
    }

    private Object[] values;

    private ModelCondition(Object ...values) {
        this.values = values;
    }

    public String getAsString() throws SQLException {
        StringBuilder result = new StringBuilder();

        int state = 0;

        for (Object value: values) {
            if (value instanceof ModelField) {
                if (state != 0)
                    throw new SQLException("Invalid operand.");

                result.append(((ModelField<?>) value).name());
                state = 1;
            }
            else if (value instanceof Conditions) {
                if (state != 1)
                    throw new SQLException("Invalid operand.");

                switch ((Conditions) value) {
                    case EQUALS: {
                        result.append("=");
                        break;
                    }
                }

                state = 2;
            }
            else {
                if (state != 2)
                    throw new SQLException("Invalid operand.");

                result.append(value);

                state = 0;
            }
        }

        return result.toString();
    }

    public enum Conditions {
        EQUALS
    }
}
