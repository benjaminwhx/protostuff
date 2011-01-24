//========================================================================
//Copyright 2007-2011 David Yu dyuproject@gmail.com
//------------------------------------------------------------------------
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at 
//http://www.apache.org/licenses/LICENSE-2.0
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
//========================================================================

package com.dyuproject.protostuff.runtime;

import static com.dyuproject.protostuff.runtime.RuntimeFieldFactory.getInline;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import com.dyuproject.protostuff.GraphInput;
import com.dyuproject.protostuff.Input;
import com.dyuproject.protostuff.Output;
import com.dyuproject.protostuff.Pipe;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.WireFormat.FieldType;
import com.dyuproject.protostuff.runtime.MappedSchema.Field;

/**
 * Static utility for creating runtime {@link List} fields.
 *
 * @author David Yu
 * @created Jan 23, 2011
 */
final class RuntimeListFieldFactory
{
    
    private RuntimeListFieldFactory() {}
    
    private static <T> Field<T> createListInlineV(int number, String name, 
            final java.lang.reflect.Field f, final Class<Object> genericType)
    {
        final RuntimeFieldFactory<Object> inline = getInline(genericType);
        return new Field<T>(inline.getFieldType(), number, name, true)
        {
            {
                f.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            protected void mergeFrom(Input input, T message) throws IOException
            {
                final Object value = inline.readFrom(input);
                try
                {
                    final List<Object> existing = (List<Object>)f.get(message);
                    if(existing == null)
                    {
                        final ArrayList<Object> list = new ArrayList<Object>();
                        list.add(value);
                        f.set(message, list);
                    }
                    else
                        existing.add(value);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
            @SuppressWarnings("unchecked")
            protected void writeTo(Output output, T message) throws IOException
            {
                final List<Object> list;
                try
                {
                    list = (List<Object>)f.get(message);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                
                if(list != null && !list.isEmpty())
                {
                    for(Object o : list)
                        inline.writeTo(output, number, o, true);
                }
            }
            protected void transfer(Pipe pipe, Input input, Output output, 
                    boolean repeated) throws IOException
            {
                inline.transfer(pipe, input, output, number, repeated);
            }
        };
    }
    
    private static <T> Field<T> createListEnumV(int number, String name, 
            final java.lang.reflect.Field f, final Class<Object> genericType)
    {
        final EnumIO<?> eio = EnumIO.create(genericType, null);
        return new Field<T>(FieldType.ENUM, number, name, true)
        {
            {
                f.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            protected void mergeFrom(Input input, T message) throws IOException
            {
                final Enum<?> value = eio.readFrom(input);
                try
                {
                    final List<Enum<?>> existing = (List<Enum<?>>)f.get(message);
                    if(existing == null)
                    {
                        final ArrayList<Enum<?>> list = new ArrayList<Enum<?>>();
                        list.add(value);
                        f.set(message, list);
                    }
                    else
                        existing.add(value);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
            @SuppressWarnings("unchecked")
            protected void writeTo(Output output, T message) throws IOException
            {
                final List<Enum<?>> list;
                try
                {
                    list = (List<Enum<?>>)f.get(message);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                
                if(list != null && !list.isEmpty())
                {
                    for(Enum<?> en : list)
                        eio.writeTo(output, number, true, en);
                }
            }
            protected void transfer(Pipe pipe, Input input, Output output, 
                    boolean repeated) throws IOException
            {
                eio.transfer(pipe, input, output, number, repeated);
            }
        };
    }
    
    private static <T> Field<T> createListPojoV(int number, String name, 
            final java.lang.reflect.Field f, final Class<Object> genericType)
    {
        return new RuntimeMessageField<T,Object>(
                genericType, RuntimeSchema.getSchemaWrapper(genericType), 
                FieldType.MESSAGE, number, name, true)
        {
            
            {
                f.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            protected void mergeFrom(Input input, T message) throws IOException
            {
                final Object value = input.mergeObject(null, getSchema());
                try
                {
                    final List<Object> existing = (List<Object>)f.get(message);
                    if(existing == null)
                    {
                        final ArrayList<Object> list = new ArrayList<Object>();
                        list.add(value);
                        f.set(message, list);
                    }
                    else
                        existing.add(value);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
            @SuppressWarnings("unchecked")
            protected void writeTo(Output output, T message) throws IOException
            {
                final List<Object> list;
                try
                {
                    list = (List<Object>)f.get(message);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                
                if(list != null && !list.isEmpty())
                {
                    final Schema<Object> schema = getSchema();
                    for(Object o : list)
                        output.writeObject(number, o, schema, true);
                }
            }
            protected void transfer(Pipe pipe, Input input, Output output, 
                    boolean repeated) throws IOException
            {
                output.writeObject(number, pipe, getPipeSchema(), repeated);
            }
        };
    }
    
    private static <T> Field<T> createListPolymorphicV(int number, String name, 
            final java.lang.reflect.Field f, final Class<Object> genericType)
    {
        return new PolymorphicRuntimeField<T>(
                genericType, FieldType.MESSAGE, number, name, true)
        {
            {
                f.setAccessible(true);
            }
            @SuppressWarnings("unchecked")
            protected void mergeFrom(Input input, T message) throws IOException
            {
                final Object value = input.mergeObject(message, schema);
                if(input instanceof GraphInput && 
                        ((GraphInput)input).isCurrentMessageReference())
                {
                    // a reference from polymorphic+cyclic graph deser
                    try
                    {
                        final List<Object> existing = (List<Object>)f.get(message);
                        if(existing == null)
                        {
                            final ArrayList<Object> list = new ArrayList<Object>();
                            list.add(value);
                            f.set(message, list);
                        }
                        else
                            existing.add(value);
                    }
                    catch (IllegalArgumentException e)
                    {
                        throw new RuntimeException(e);
                    }
                    catch (IllegalAccessException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
            @SuppressWarnings("unchecked")
            protected void writeTo(Output output, T message) throws IOException
            {
                final List<Object> existing;
                try
                {
                    existing = (List<Object>)f.get(message);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
                
                if(existing != null && !existing.isEmpty())
                {
                    for(Object o : existing)
                        output.writeObject(number, o, schema, true);
                }
            }
            protected void transfer(Pipe pipe, Input input, Output output, 
                    boolean repeated) throws IOException
            {
                output.writeObject(number, pipe, schema.pipeSchema, repeated);
            }
            @SuppressWarnings("unchecked")
            protected void doMergeFrom(Input input, Schema<Object> schema, 
                    Object message) throws IOException
            {
                final Object value = schema.newMessage();
                if(input instanceof GraphInput)
                {
                    // update the actual reference.
                    ((GraphInput)input).updateLast(value, message);
                }
                
                schema.mergeFrom(input, value);
                try
                {
                    final List<Object> existing = (List<Object>)f.get(message);
                    if(existing == null)
                    {
                        final ArrayList<Object> list = new ArrayList<Object>();
                        list.add(value);
                        f.set(message, list);
                    }
                    else
                        existing.add(value);
                }
                catch (IllegalArgumentException e)
                {
                    throw new RuntimeException(e);
                }
                catch (IllegalAccessException e)
                {
                    throw new RuntimeException(e);
                }
            }
        };
    }
    
    /**
     * Mapped to {@link ArrayList} if the field does not have an existing value.
     */
    static final RuntimeFieldFactory<List<?>> LIST = new RuntimeFieldFactory<List<?>>()
    {
        @SuppressWarnings("unchecked")
        public <T> Field<T> create(int number, String name, final java.lang.reflect.Field f)
        {
            final Class<Object> genericType;
            try
            {
                genericType = (Class<Object>)((ParameterizedType)f.getGenericType()).getActualTypeArguments()[0];
            }
            catch(Exception e)
            {
                return null;
            }
            
            if(genericType.isEnum())
                return createListEnumV(number, name, f, genericType);
            
            if(getInline(genericType) != null)
                return createListInlineV(number, name, f, genericType);
            
            if(isInvalidChildType(genericType))
                return null;
            
            if(POJO == pojo(genericType) || RuntimeSchema.isRegistered(genericType))
                return createListPojoV(number, name, f, genericType);
            
            return createListPolymorphicV(number, name, f, genericType);
        }
        protected void transfer(Pipe pipe, Input input, Output output, int number, 
                boolean repeated) throws IOException
        {
            throw new UnsupportedOperationException();
        }
        protected List<?> readFrom(Input input) throws IOException
        {
            throw new UnsupportedOperationException();
        }
        protected void writeTo(Output output, int number, List<?> value, 
                boolean repeated) throws IOException
        {
            throw new UnsupportedOperationException();
        }
        protected FieldType getFieldType()
        {
            throw new UnsupportedOperationException();
        }
    };

}