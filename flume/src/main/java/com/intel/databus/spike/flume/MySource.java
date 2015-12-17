package com.intel.databus.spike.flume;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.PollableSource;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.SimpleEvent;
import org.apache.flume.source.AbstractSource;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


public class MySource  extends AbstractSource implements Configurable, PollableSource {


    public void configure(Context context) {

    }

    public Status process() throws EventDeliveryException {
        Event event = new SimpleEvent();
        Map<String,String> headers = new HashMap<String, String>();
        headers.put("Type","Message");
        event.setHeaders(headers);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String message = "Hello World !!!   " + timestamp.toString();
        event.setBody(message.getBytes());
        getChannelProcessor().processEvent(event);
        return null;
    }
}
