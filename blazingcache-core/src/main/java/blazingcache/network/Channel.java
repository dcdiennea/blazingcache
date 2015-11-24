/*
 Licensed to Diennea S.r.l. under one
 or more contributor license agreements. See the NOTICE file
 distributed with this work for additional information
 regarding copyright ownership. Diennea S.r.l. licenses this file
 to you under the Apache License, Version 2.0 (the
 "License"); you may not use this file except in compliance
 with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing,
 software distributed under the License is distributed on an
 "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 KIND, either express or implied.  See the License for the
 specific language governing permissions and limitations
 under the License.

 */
package blazingcache.network;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Abstract for two-way async comunication channels
 *
 * @author enrico.olivelli
 */
public abstract class Channel implements AutoCloseable {

    protected ChannelEventListener messagesReceiver;

    public Channel() {
    }

    public ChannelEventListener getMessagesReceiver() {
        return messagesReceiver;
    }

    public void setMessagesReceiver(ChannelEventListener messagesReceiver) {
        this.messagesReceiver = messagesReceiver;
    }

    public abstract void sendOneWayMessage(Message message, SendResultCallback callback);

    public abstract void sendReplyMessage(Message inAnswerTo, Message message);

    public abstract void sendMessageWithAsyncReply(Message message, ReplyCallback callback);

    @Override
    public abstract void close();

    public Message sendMessageWithReply(Message message, long timeout) throws InterruptedException, TimeoutException {
        CompletableFuture<Message> resp = new CompletableFuture<>();
        sendMessageWithAsyncReply(message, (Message originalMessage, Message message1, Throwable error) -> {
            if (error != null) {
                resp.completeExceptionally(error);
            } else {
                resp.complete(message1);
            }
        });
        try {
            return resp.get(timeout, TimeUnit.MILLISECONDS);
        } catch (ExecutionException err) {
            if (err.getCause() instanceof IOException) {
                TimeoutException te = new TimeoutException("io-error while waiting for reply");
                te.initCause(err.getCause());
                throw te;
            }
            throw new RuntimeException(err.getCause());
        }
    }
    
    public abstract boolean isValid();

}
