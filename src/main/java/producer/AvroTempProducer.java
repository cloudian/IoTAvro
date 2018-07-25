package producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.*;
import java.util.Properties;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.Runnable;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class AvroTempProducer {
    private final static String TOPIC = "avro-temp-data";
    private final static String BOOTSTRAP_SERVERS = "10.10.0.154:9092";
    private final static String avroSerializer = KafkaAvroSerializer.class.getName();
    private final static String stringSerializer = StringSerializer.class.getName();
    private final static int SECONDS = 5;
    private final static int PARTITIONS = 0; //zero indexed
    private static  KafkaProducer<String, TemperatureData> producer;

    public static void main(String[] args) throws Exception {
        //runProducer(5);
        producer = createProducer();
        ScheduledExecutorService readData = Executors.newScheduledThreadPool(5);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    runProducer(1);
                } catch (Exception e) {
                    Thread t = Thread.currentThread();
                    t.getUncaughtExceptionHandler().uncaughtException(t, e);
                }

            }
        };
        readData.scheduleAtFixedRate(runnable, 0, 1000*SECONDS, TimeUnit.MILLISECONDS);
    }
    private static KafkaProducer< String, TemperatureData>createProducer() {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", BOOTSTRAP_SERVERS);
        props.setProperty("key.serializer", stringSerializer);
        props.setProperty("value.serializer", avroSerializer);
        props.setProperty("schema.registry.url", "http://10.10.0.154:8081");
        //props.put("key.serializer", stringSerializer);
        //props.put("value.serializer", jsonSerializer);
        KafkaProducer<String, TemperatureData> producer = new KafkaProducer<>(props);
        return producer;
    }

    public static void runProducer(int p) throws Exception {
        //final Producer<Integer, String> producer = createProducer();
        int partition = 0;
        String key = "not real shit";
        String data = null;
	while (data == null) {
		data = getTemp();
		Thread.sleep(500);
	}
	String[] info = data.split("[\\s\\D]+");
	//[year, month, day, hour, minute, second, millisecond, temp]
    final TemperatureData temp = TemperatureData.newBuilder()
            .setTimestamp(TimeRecord.newBuilder()
                    .setYear(Integer.parseInt(info[0]))
                    .setMonth(Integer.parseInt(info[1]))
                    .setDay(Integer.parseInt(info[2]))
                    .setHour(Integer.parseInt(info[3]))
                    .setMinute(Integer.parseInt(info[4]))
                    .setSecond(Integer.parseInt(info[5]))
                    .setMillisecond(Integer.parseInt(info[6]))
                    .build())
            .setTemperature(Integer.parseInt(info[7]))
            .build();

    final ProducerRecord<String, TemperatureData> record =
                new ProducerRecord<String, TemperatureData>(TOPIC, temp);
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata metadata, Exception e) {
                if (e == null) {
		    
                    System.out.println("Record Successfully Sent");
                } else {
                    System.out.println(e);

                }
            }
        });
        partition = increment(partition, PARTITIONS);

        //} finally {
        //  producer.flush();
        //  producer.close();
    }

    private static int increment(int k, int n) {
        if (k < n) {
            k++;
        } else {
            k = 0;
        }
        return k;
    }

    public static String getTemp() throws Exception {
        Runtime rt = Runtime.getRuntime();
        Process p = rt.exec("python trial.py");
        //Process p = rt.exec("echo /home/pi/DHT11_Python/trial.py");
        BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        if ((line = bri.readLine()) != null) {
            System.out.println(line);
        } else {
            System.out.println("This failed");
        }
        bri.close();
        return line;
    }
}
