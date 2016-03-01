package com.intel.databus.spike.flume;

import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

/**
 * Created by hugo on 12/14/15.
 */
public class MySink extends AbstractSink implements Configurable {
    private String myProp;

    Properties props;
    Producer<String, String> producer;


    public void configure(Context context) {
        String myProp = context.getString("myProp", "defaultValue");

        // Process the myProp value (e.g. validation)

        // Store myProp for later retrieval by process() method
        this.myProp = myProp;

        props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    }

    @Override public void start() {
        // Initialize the connection to the external repository (e.g. HDFS) that
        // this Sink will forward Events to ..
        producer = new KafkaProducer(props);
    }

    @Override public void stop() {
        // Disconnect from the external respository and do any
        // additional cleanup (e.g. releasing resources or nulling-out
        // field values) ..
        producer.close();
    }

    public Status process() throws EventDeliveryException {
        Status status = null;

        // Start transaction
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        txn.begin();
        try {

            Event event = ch.take();
            if (event != null) {

                String message = new String(event.getBody());

                ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>("test", "", "HEADER:" + event.getHeaders().toString() + " MESSAGE:" + message);

                producer.send(producerRecord);

                System.out.println("[SINK EVENT] HEADERS: " + event.getHeaders().toString() + "  MESSAGE: " + message);
                status = Status.READY;
            }
            else {
                status = Status.BACKOFF;
            }

            txn.commit();

        } catch (Throwable t) {
            txn.rollback();
            // Log exception, handle individual exceptions as needed
            System.out.println("[SINK EVENT ERROR] !!!!!!!!" + t.getMessage());
            t.printStackTrace();

            status = Status.BACKOFF;

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
