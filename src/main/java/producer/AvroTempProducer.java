package producer;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.clients.producer.*;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.lang.Runnable;
import java.lang.Math;
import java.util.Calendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;

public class AvroTempProducer {
<<<<<<< HEAD
<<<<<<< HEAD
    private final static String TOPIC = "avro-temp-data";
    private final static String CONFLUENT_IP = "10.10.0.154";
    private final static String KAFKA_PORTS = "9092";
    private final static String SCHEMA_REGISTRY_PORTS = "8081";
    private final static boolean generate_data = true;
    private final static String avroSerializer = KafkaAvroSerializer.class.getName();
    private final static String stringSerializer = StringSerializer.class.getName();
    private final static int SECONDS = 5;
    private final static int PARTITIONS = 0; //zero indexed
    private static  KafkaProducer<String, TemperatureData> producer;
=======
    private final static String TOPIC; //  = "avro-temp-data";
    private final static String BOOTSTRAP_SERVERS; // = "10.10.0.154:9092";
    private final static String avroSerializer = KafkaAvroSerializer.class.getName();
    private final static String stringSerializer = StringSerializer.class.getName();
=======
    private final static String TOPIC; //  = "avro-temp-data";
    private final static String BOOTSTRAP_SERVERS; // = "10.10.0.154:9092";
    private final static String avroSerializer = KafkaAvroSerializer.class.getName();
    private final static String stringSerializer = StringSerializer.class.getName();
>>>>>>> 28a48eb62915f70186dacf48f3a7bdba6e0a175f
    private final static int SECONDS; // = 5;
    private final static int PARTITIONS; // = 0; //zero indexed
    
    //Possibly move generate data to class initializer. VALUE IS SET IN
    //config.properties file to true
    private final static boolean generate_data = true;
    static {
        Properties properties = new Properties();
        try {
            FileInputStream stream = new FileInputStream(new File("config.properties"));
            properties.load(stream);
        } catch (Exception e) {
            System.out.println("Could not open File");
        }
        TOPIC = properties.getProperty("topic");
        BOOTSTRAP_SERVERS = properties.getProperty("bootstrap_servers");
        SECONDS = Integer.parseInt(properties.getProperty("seconds"));
        PARTITIONS = Integer.parseInt(properties.getProperty("partitions"));
   }
   private static  KafkaProducer<String, TemperatureData> producer;


<<<<<<< HEAD
>>>>>>> 28a48eb62915f70186dacf48f3a7bdba6e0a175f
=======
>>>>>>> 28a48eb62915f70186dacf48f3a7bdba6e0a175f

   public static void main(String[] args) throws Exception {
        //runProducer(5);
	//The following four lines can be deleted
	//Currently exist for debugging purposes
        System.out.println(SECONDS);
        System.out.println(PARTITIONS);
        System.out.println(BOOTSTRAP_SERVERS);
        System.out.println(TOPIC);
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
        props.setProperty("bootstrap.servers", CONFLUENT_IP + ":" + KAFKA_PORTS);
        props.setProperty("key.serializer", stringSerializer);
        props.setProperty("value.serializer", avroSerializer);
        props.setProperty("schema.registry.url", "http://" + CONFLUENT_IP +
                ":" + SCHEMA_REGISTRY_PORTS);
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
        TemperatureData temperature;

    if (generate_data) { 
        Calendar cal = Calendar.getInstance();
        int d = cal.get(Calendar.DAY_OF_YEAR);
        int h = cal.get(Calendar.HOUR_OF_DAY);
        int temp = (int) (11*Math.cos((2*Math.PI/365)*(d-244)) +
                13*Math.cos((2*Math.PI/24)*(h-15)) + 45);
        int humidity = (int) (15*Math.cos((2*Math.PI/24)*(h-4)) + 60);
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
                .build();
    } else {
    	while (data == null) {
    		data = getTemp();
    		Thread.sleep(500);
    	}
    	String[] info = data.split("[\\s\\D]+");
    	//[year, month, day, hour, minute, second, millisecond, temp]
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
                .build();
    }

    final ProducerRecord<String, TemperatureData> record =
                new ProducerRecord<String, TemperatureData>(TOPIC, temperature);
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
