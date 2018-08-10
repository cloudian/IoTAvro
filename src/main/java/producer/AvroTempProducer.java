package producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.Runnable;
import java.lang.Math;
import java.util.Calendar;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AvroTempProducer {

    private static String TOPIC;
    private static String KEY;
    private static String CONFLUENT_IP;
    private static String KAFKA_PORTS;
    private static String SCHEMA_REGISTRY_PORTS;
    private static boolean generate_data;
    private static String AVRO_SERIALIZER = KafkaAvroSerializer.class.getName();
    private static int SECONDS = 5;
    private static int NUM_PRODUCERS;

    //Possibly move generate data to class initializer. VALUE IS SET IN
    //config.properties file to true


    static {
        Properties properties = new Properties();
        try {
            FileInputStream stream = new FileInputStream(new File("config.properties"));
            properties.load(stream);
        } catch (Exception e) {
            System.out.println("Could not open File");
        }
        TOPIC = properties.getProperty("topic");
        CONFLUENT_IP = properties.getProperty("confluent_ip");
        KAFKA_PORTS = properties.getProperty("kafka_ports");
        SCHEMA_REGISTRY_PORTS = properties.getProperty("schema_registry_ports");
        SECONDS = Integer.parseInt(properties.getProperty("delay"));
        KEY = properties.getProperty("key");
        NUM_PRODUCERS = Integer.parseInt(properties.getProperty("num_producers"));
        generate_data=Boolean.parseBoolean(properties.getProperty("generate_data"));
    }


    private static class MyProducer {
        private KafkaProducer<String, TemperatureData> producer;
        private String producerID;
        private MyProducer(int id) {
            Properties props = new Properties();
            props.setProperty("bootstrap.servers", CONFLUENT_IP + ":" + KAFKA_PORTS);
            props.setProperty("key.serializer", AVRO_SERIALIZER);
            props.setProperty("value.serializer", AVRO_SERIALIZER);
            props.setProperty("schema.registry.url", "http://" + CONFLUENT_IP +
                    ":" + SCHEMA_REGISTRY_PORTS);
            producer = new KafkaProducer<>(props);
            producerID = "" + id;
        }
        public KafkaProducer<String, TemperatureData> getProducer() {
            return this.producer;
        }

        public String getID() {
            return this.producerID;
        }
    }

    public static void main(String[] args) throws Exception {
        List<MyProducer> producers = new ArrayList<>();
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            producers.add(new MyProducer(i));
        }
        int nProducer = 0;
        ScheduledExecutorService readData = Executors.newScheduledThreadPool(5);
        Runnable runnable = new Runnable() {
            public void run() {
                try {
                    runProducer(producers);
                } catch (Exception e) {
                    Thread t = Thread.currentThread();
                    t.getUncaughtExceptionHandler().uncaughtException(t, e);
                }

            }
        };
        readData.scheduleAtFixedRate(runnable, 0, 1000*SECONDS, TimeUnit.MILLISECONDS);
    }

    public static void runProducer(List<MyProducer> producers) throws Exception {
        //String key = "not real shit";
        String data = null;
        TemperatureData temperature;
        MyProducer current = producers.remove(0);
        KafkaProducer<String, TemperatureData> producer = current.getProducer();
        String producerID = current.getID();
        System.out.println(producerID);
        producers.add(current);

        if (generate_data) {
            Calendar cal = Calendar.getInstance();
            int d = cal.get(Calendar.DAY_OF_YEAR);
            int h = cal.get(Calendar.HOUR_OF_DAY);
            Random r = new Random();
            int temp_noise = r.nextInt(6) - 5;
            int hum_noise = r.nextInt(6) - 5;
            int temp = (int) (11*Math.cos((2*Math.PI/365)*(d-244)) +
                    13*Math.cos((2*Math.PI/24)*(h-15)) + 45) + temp_noise;
            int humidity = (int) (15*Math.cos((2*Math.PI/24)*(h-4)) + 60) + hum_noise;
            temperature = TemperatureData.newBuilder()
                    .setTimestamp(TimeRecord.newBuilder()
                            .setYear(cal.get(Calendar.YEAR))
                            .setMonth(cal.get(Calendar.MONTH))
                            .setDay(cal.get(Calendar.DAY_OF_MONTH))
                            .setHour(cal.get(Calendar.HOUR_OF_DAY))
                            .setMinute(cal.get(Calendar.MINUTE))
                            .setSecond(cal.get(Calendar.SECOND))
                            .build())
                    .setTemperature(temp)
                    .setHumidity(humidity)
                    .setProducerID(producerID)
                    .build();
        } else {
            while (data == null) {
                data = getTemp();
                Thread.sleep(500);
            }
            String[] info = data.split("[\\s\\D]+");
            //[year, month, day, hour, minute, second, millisecond, temp, humidity]
            temperature = TemperatureData.newBuilder()
                    .setTimestamp(TimeRecord.newBuilder()
                            .setYear(Integer.parseInt(info[0]))
                            .setMonth(Integer.parseInt(info[1]))
                            .setDay(Integer.parseInt(info[2]))
                            .setHour(Integer.parseInt(info[3]))
                            .setMinute(Integer.parseInt(info[4]))
                            .setSecond(Integer.parseInt(info[5]))
                            .build())
                    .setTemperature(Integer.parseInt(info[7]))
                    .setHumidity(Integer.parseInt(info[8]))
                    .setProducerID(KEY)
                    .build();
        }
        final ProducerRecord<String, TemperatureData> record =
                new ProducerRecord<>(TOPIC, temperature);
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

        //} finally {
        //  producer.flush();
        //  producer.close();
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