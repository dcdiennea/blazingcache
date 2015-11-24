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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * A message (from broker to worker or from worker to broker)
 *
 * @author enrico.olivelli
 */
public final class Message {

    public static Message ACK(String clientId) {
        return new Message(clientId, TYPE_ACK, new HashMap<>());
    }

    public static Message FETCH_ENTRY(String clientId, String key) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("key", key);
        return new Message(clientId, TYPE_FETCH_ENTRY, data);
    }

    public static Message PUT_ENTRY(String clientId, String key, byte[] serializedData, long expiretime) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("key", key);
        data.put("data", serializedData);
        data.put("expiretime", expiretime);
        return new Message(clientId, TYPE_PUT_ENTRY, data);
    }

    public static Message UNREGISTER_ENTRY(String clientId, String key) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("key", key);
        return new Message(clientId, TYPE_UNREGISTER_ENTRY, data);
    }

    public static Message ERROR(String clientId, Throwable error) {
        Map<String, Object> params = new HashMap<>();
        params.put("error", error + "");
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        params.put("stackTrace", writer.toString());
        return new Message(clientId, TYPE_ERROR, params);
    }

    public static Message CLIENT_CONNECTION_REQUEST(String clientId, String secret) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("secret", secret);
        return new Message(clientId, TYPE_CLIENT_CONNECTION_REQUEST, data);
    }

    public static Message CLIENT_SHUTDOWN(String clientId) {
        return new Message(clientId, TYPE_CLIENT_SHUTDOWN, new HashMap<>());
    }

    public static Message INVALIDATE(String clientId, String key) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("key", key);
        return new Message(clientId, TYPE_INVALIDATE, data);
    }

    public static Message INVALIDATE_BY_PREFIX(String clientId, String prefix) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("prefix", prefix);
        return new Message(clientId, TYPE_INVALIDATE_BY_PREFIX, data);
    }

    public final String clientId;
    public final int type;
    public final Map<String, Object> parameters;
    public String messageId;
    public String replyMessageId;

    @Override
    public String toString() {
        if (replyMessageId != null) {
            return typeToString(type) + ", parameters=" + parameters + ", id=" + messageId + ", replyMessageId=" + replyMessageId;
        } else {
            return typeToString(type) + ", parameters=" + parameters + ", id=" + messageId;
        }
    }

    public static final int TYPE_ACK = 1;
    public static final int TYPE_CLIENT_CONNECTION_REQUEST = 2;
    public static final int TYPE_CLIENT_SHUTDOWN = 3;
    public static final int TYPE_INVALIDATE = 4;
    public static final int TYPE_ERROR = 5;
    public static final int TYPE_PUT_ENTRY = 6;
    public static final int TYPE_INVALIDATE_BY_PREFIX = 7;
    public static final int TYPE_UNREGISTER_ENTRY = 8;
    public static final int TYPE_FETCH_ENTRY = 9;

    public static String typeToString(int type) {
        switch (type) {
            case TYPE_ACK:
                return "TYPE_ACK";
            case TYPE_ERROR:
                return "TYPE_ERROR";
            case TYPE_CLIENT_CONNECTION_REQUEST:
                return "TYPE_CLIENT_CONNECTION_REQUEST";
            case TYPE_CLIENT_SHUTDOWN:
                return "TYPE_CLIENT_SHUTDOWN";
            case TYPE_INVALIDATE:
                return "TYPE_INVALIDATE";
            case TYPE_INVALIDATE_BY_PREFIX:
                return "TYPE_INVALIDATE_BY_PREFIX";
            case TYPE_PUT_ENTRY:
                return "PUT_ENTRY";
            case TYPE_UNREGISTER_ENTRY:
                return "TYPE_UNREGISTER_ENTRY";
            case TYPE_FETCH_ENTRY:
                return "TYPE_FETCH_ENTRY";
            default:
                return "?" + type;
        }
    }

    public Message(String workerProcessId, int type, Map<String, Object> parameters) {
        this.clientId = workerProcessId;
        this.type = type;
        this.parameters = parameters;
    }

    public String getMessageId() {
        return messageId;
    }

    public Message setMessageId(String messageId) {
        this.messageId = messageId;
        return this;
    }

    public String getReplyMessageId() {
        return replyMessageId;
    }

    public Message setReplyMessageId(String replyMessageId) {
        this.replyMessageId = replyMessageId;
        return this;
    }

    public Message setParameter(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }
}
