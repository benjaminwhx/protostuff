//========================================================================
//Copyright 2007-2009 David Yu dyuproject@gmail.com
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

package io.protostuff;

import java.io.IOException;

/**
 * 处理与自己绑定消息的序列化和反序列化
 *
 * Basically, any object can be serialized via protobuf. As long as its schema is provided, it does not need to
 * implement {@link Message}. This was designed with "unobtrusive" in mind. The goal was to be able to
 * serialize/deserialize any existing object without having to touch its source. This will enable you to customize the
 * serialization of objects from 3rd party libraries.
 *
 * @author David Yu
 * @created Nov 9, 2009
 */
public interface Schema<T> {

    /**
     *  返回number关联的字段名，当序列化不同格式（如JSON）时特别有用
     *  当使用数字作为字段名时，返回String.valueOf(number)
     */
    public String getFieldName(int number);

    /**
     * 返回字段名关联的字段number，当序列化不同格式（如JSON）时特别有用
     *  当使用数字作为字段名时，返回Integer.parseInt(name);
     */
    public int getFieldNumber(String name);

    /**
     * 如果没有必填字段或设置了所有必填字段，则返回true。
     */
    public boolean isInitialized(T message);

    /**
     * 创建与此模式绑定的消息/对象。
     */
    public T newMessage();

    /**
     * 返回与此schema绑定的简单消息名，允许自定义schema来提供不同于typeClass().getSimpleName()的名称
     */
    public String messageName();

    /**
     * 返回与此schema绑定的消息全名，允许自定义schema来提供不同于typeClass().getName()的名称
     */
    public String messageFullName();

    /**
     * 返回消息类型
     */
    public Class<? super T> typeClass();

    /**
     * 从input反序列化message/object
     */
    public void mergeFrom(Input input, T message) throws IOException;

    /**
     * 序列化message/object到output
     */
    public void writeTo(Output output, T message) throws IOException;

}
