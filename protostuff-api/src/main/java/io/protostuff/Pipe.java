//========================================================================
//Copyright 2007-2010 David Yu dyuproject@gmail.com
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
 * Transfers data from an {@link Input} to an {@link Output}.
 * <p>
 * It is recommended to use pipe only to stream data coming from server-side services (e.g from your datastore/etc).
 * <p>
 * Incoming data from the interwebs should not be piped due to validation/security purposes.
 *
 * @author David Yu
 * @created Oct 6, 2010
 */
public abstract class Pipe {

    protected Input input;
    protected Output output;

    /**
     * 重置管道以便重用
     */
    protected Pipe reset() {
        output = null;
        input = null;
        return this;
    }

    /**
     * 开始准备处理input
     */
    protected abstract Input begin(Pipe.Schema<?> pipeSchema) throws IOException;

    /**
     * 结束处理input
     * 如果cleanupOnly为true，仅仅需要清理或者关闭资源（意外结束）
     */
    protected abstract void end(Pipe.Schema<?> pipeSchema, Input input,
                                boolean cleanupOnly) throws IOException;

    /**
     * 从Input传输数据到Output的Schema
     */
    public static abstract class Schema<T> implements io.protostuff.Schema<Pipe> {

        public final io.protostuff.Schema<T> wrappedSchema;

        public Schema(io.protostuff.Schema<T> wrappedSchema) {
            this.wrappedSchema = wrappedSchema;
        }

        @Override
        public String getFieldName(int number) {
            return wrappedSchema.getFieldName(number);
        }

        @Override
        public int getFieldNumber(String name) {
            return wrappedSchema.getFieldNumber(name);
        }

        /**
         * 总是返回true，因为我们只是传输数据。
         */
        @Override
        public boolean isInitialized(Pipe message) {
            return true;
        }

        @Override
        public String messageFullName() {
            return wrappedSchema.messageFullName();
        }

        @Override
        public String messageName() {
            return wrappedSchema.messageName();
        }

        @Override
        public Pipe newMessage() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Class<Pipe> typeClass() {
            throw new UnsupportedOperationException();
        }

        @Override
        public final void writeTo(final Output output, final Pipe pipe) throws IOException {
            if (pipe.output == null) {
                pipe.output = output;

                // begin message pipe
                final Input input = pipe.begin(this);

                if (input == null) {
                    // empty message pipe.
                    pipe.output = null;
                    pipe.end(this, input, true);
                    return;
                }

                pipe.input = input;

                boolean transferComplete = false;
                try {
                    transfer(pipe, input, output);
                    transferComplete = true;
                } finally {
                    pipe.end(this, input, !transferComplete);
                    // pipe.input = null;
                    // pipe.output = null;
                }

                return;
            }

            // nested message.
            pipe.input.mergeObject(pipe, this);
        }

        @Override
        public final void mergeFrom(final Input input, final Pipe pipe) throws IOException {
            transfer(pipe, input, pipe.output);
        }

        /**
         * 从Input向Output传输数据
         */
        protected abstract void transfer(Pipe pipe, Input input, Output output)
                throws IOException;

    }

    /**
     * 这不应该由应用程序直接调用。
     */
    public static <T> void transferDirect(Pipe.Schema<T> pipeSchema, Pipe pipe,
                                          Input input, Output output) throws IOException {
        pipeSchema.transfer(pipe, input, output);
    }

}
