package io.protostuff.runtime;

import java.io.IOException;

import io.protostuff.Input;
import io.protostuff.Output;
import io.protostuff.Pipe;
import io.protostuff.Tag;
import io.protostuff.WireFormat;

/**
 * 用来代表序列化对象的字段
 */
public abstract class Field<T> {
    public final WireFormat.FieldType type;
    public final int number;
    public final String name;
    public final boolean repeated;
    public final int groupFilter;

    // public final Tag tag;

    public Field(WireFormat.FieldType type, int number, String name, boolean repeated,
                 Tag tag) {
        this.type = type;
        this.number = number;
        this.name = name;
        this.repeated = repeated;
        this.groupFilter = tag == null ? 0 : tag.groupFilter();
        // this.tag = tag;
    }

    public Field(WireFormat.FieldType type, int number, String name, Tag tag) {
        this(type, number, name, false, tag);
    }

    /**
     * Writes the value of a field to the {@code output}.
     */
    protected abstract void writeTo(Output output, T message)
            throws IOException;

    /**
     * Reads the field value into the {@code message}.
     */
    protected abstract void mergeFrom(Input input, T message)
            throws IOException;

    /**
     * 从input中传输字段到output
     */
    protected abstract void transfer(Pipe pipe, Input input, Output output,
                                     boolean repeated) throws IOException;
}
