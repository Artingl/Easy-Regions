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
        Cond lastCondition = null;

        for (Object value: values) {
            if (value instanceof ModelField) {
                ModelField<?> field = ((ModelField<?>) value);
                result.append(field.name());
            }
            else if (value instanceof Cond) {
                lastCondition = (Cond) value;

                switch ((Cond) value) {
                    case EQUALS: {
                        result.append(" = ");
                        break;
                    }

                    case LESS: {
                        result.append(" < ");
                        break;
                    }

                    case GREATER: {
                        result.append(" > ");
                        break;
                    }

                    case GREATER_OR_EQUAL: {
                        result.append(" >= ");
                        break;
                    }

                    case LESS_OR_EQUAL: {
                        result.append(" <= ");
                        break;
                    }

                    case AND: {
                        result.append(" AND ");
                        break;
                    }

                    case OR: {
                        result.append(" OR ");
                        break;
                    }

                    case CONTAINS: {
                        result.append(" LIKE ");
                        break;
                    }
                }
            }
            else {
                if (Cond.CONTAINS.equals(lastCondition))
                    result.append("'%");

                result.append(ModelField.serialize(value, !Cond.CONTAINS.equals(lastCondition)));

                if (Cond.CONTAINS.equals(lastCondition))
                    result.append("%'");
            }
        }

        return result.toString();
    }

    public enum Cond {
        EQUALS,
        GREATER_OR_EQUAL,
        LESS_OR_EQUAL,
        GREATER,
        LESS,
        AND,
        OR,
        CONTAINS,
    }
}
