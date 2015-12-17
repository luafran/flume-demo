package com.intel.databus.spike.flume;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by hugo on 12/15/15.
 */
public class MySkinKafka extends AbstractSink implements Configurable {

    private String myProp;

    public void configure(Context context) {
        String myProp = context.getString("myProp", "defaultValue");

        // Process the myProp value (e.g. validation)

        // Store myProp for later retrieval by process() method
        this.myProp = myProp;
    }

    @Override public void start() {
        // Initialize the connection to the external repository (e.g. HDFS) that
        // this Sink will forward Events to ..
    }

    @Override public void stop() {
        // Disconnect from the external respository and do any
        // additional cleanup (e.g. releasing resources or nulling-out
        // field values) ..
    }

    public Sink.Status process() throws EventDeliveryException {
        Sink.Status status = null;

        // Start transaction
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        txn.begin();
        try {
            // This try clause includes whatever Channel operations you want to do

            Event event = ch.take();

            // Send the Event to the external repository.
            // storeSomeData(e);

            String message = new String(event.getBody());
            System.out.println("[SINK KAFKA SOURCE] HEADER:" + event.getHeaders().toString() +" MESSAGE:"+message);

            txn.commit();
            status = Sink.Status.READY;
        } catch (Throwable t) {
            txn.rollback();

            // Log exception, handle individual exceptions as needed
            System.out.println("[SINK EVENT ERROR] !!!!!!!!" + t.getMessage());

            status = Sink.Status.BACKOFF;

            // re-throw all Errors
            if (t instanceof Error) {
                throw (Error) t;
            }
        }
        finally {
            txn.close();
        }
        return status;
    }

}
