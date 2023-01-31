package com.artingl.easyrg.misc.Database.Field;

public class ModelCondition {

    public static ModelCondition make(Object ...values) {
        return new ModelCondition(values);
    }

    private Object[] values;

    private ModelCondition(Object ...values) {
        this.values = values;
    }

    public String getAsString() {
        StringBuilder result = new StringBuilder();

        for (Object value: values) {
            if (value instanceof ModelField) {
                result.append(((ModelField<?>) value).name());
            }
            else if (value instanceof Conditions) {
                switch ((Conditions) value) {
                    case EQUALS: {
                        result.append("=");
                        break;
                    }
                }
            }
            else {
                result.append(value);
            }
        }

        return result.toString();
    }

    public enum Conditions {
        EQUALS
    }
}
