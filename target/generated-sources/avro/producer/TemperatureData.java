/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package producer;

import org.apache.avro.specific.SpecificData;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class TemperatureData extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -2936564626530830944L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TemperatureData\",\"namespace\":\"producer\",\"fields\":[{\"name\":\"ProducerID\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"Temperature\",\"type\":\"int\"},{\"name\":\"Humidity\",\"type\":\"double\"},{\"name\":\"Timestamp\",\"type\":{\"type\":\"record\",\"name\":\"TimeRecord\",\"fields\":[{\"name\":\"Year\",\"type\":\"int\"},{\"name\":\"Month\",\"type\":\"int\"},{\"name\":\"Day\",\"type\":\"int\"},{\"name\":\"Hour\",\"type\":\"int\"},{\"name\":\"Minute\",\"type\":\"int\"},{\"name\":\"Second\",\"type\":\"int\"}]}}],\"version\":\"1\"}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<TemperatureData> ENCODER =
      new BinaryMessageEncoder<TemperatureData>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<TemperatureData> DECODER =
      new BinaryMessageDecoder<TemperatureData>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   */
  public static BinaryMessageDecoder<TemperatureData> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   */
  public static BinaryMessageDecoder<TemperatureData> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<TemperatureData>(MODEL$, SCHEMA$, resolver);
  }

  /** Serializes this TemperatureData to a ByteBuffer. */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /** Deserializes a TemperatureData from a ByteBuffer. */
  public static TemperatureData fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.String ProducerID;
   private int Temperature;
   private double Humidity;
   private producer.TimeRecord Timestamp;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TemperatureData() {}

  /**
   * All-args constructor.
   * @param ProducerID The new value for ProducerID
   * @param Temperature The new value for Temperature
   * @param Humidity The new value for Humidity
   * @param Timestamp The new value for Timestamp
   */
  public TemperatureData(java.lang.String ProducerID, java.lang.Integer Temperature, java.lang.Double Humidity, producer.TimeRecord Timestamp) {
    this.ProducerID = ProducerID;
    this.Temperature = Temperature;
    this.Humidity = Humidity;
    this.Timestamp = Timestamp;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return ProducerID;
    case 1: return Temperature;
    case 2: return Humidity;
    case 3: return Timestamp;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: ProducerID = (java.lang.String)value$; break;
    case 1: Temperature = (java.lang.Integer)value$; break;
    case 2: Humidity = (java.lang.Double)value$; break;
    case 3: Timestamp = (producer.TimeRecord)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'ProducerID' field.
   * @return The value of the 'ProducerID' field.
   */
  public java.lang.String getProducerID() {
    return ProducerID;
  }


  /**
   * Gets the value of the 'Temperature' field.
   * @return The value of the 'Temperature' field.
   */
  public java.lang.Integer getTemperature() {
    return Temperature;
  }


  /**
   * Gets the value of the 'Humidity' field.
   * @return The value of the 'Humidity' field.
   */
  public java.lang.Double getHumidity() {
    return Humidity;
  }


  /**
   * Gets the value of the 'Timestamp' field.
   * @return The value of the 'Timestamp' field.
   */
  public producer.TimeRecord getTimestamp() {
    return Timestamp;
  }


  /**
   * Creates a new TemperatureData RecordBuilder.
   * @return A new TemperatureData RecordBuilder
   */
  public static producer.TemperatureData.Builder newBuilder() {
    return new producer.TemperatureData.Builder();
  }

  /**
   * Creates a new TemperatureData RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new TemperatureData RecordBuilder
   */
  public static producer.TemperatureData.Builder newBuilder(producer.TemperatureData.Builder other) {
    return new producer.TemperatureData.Builder(other);
  }

  /**
   * Creates a new TemperatureData RecordBuilder by copying an existing TemperatureData instance.
   * @param other The existing instance to copy.
   * @return A new TemperatureData RecordBuilder
   */
  public static producer.TemperatureData.Builder newBuilder(producer.TemperatureData other) {
    return new producer.TemperatureData.Builder(other);
  }

  /**
   * RecordBuilder for TemperatureData instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TemperatureData>
    implements org.apache.avro.data.RecordBuilder<TemperatureData> {

    private java.lang.String ProducerID;
    private int Temperature;
    private double Humidity;
    private producer.TimeRecord Timestamp;
    private producer.TimeRecord.Builder TimestampBuilder;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(producer.TemperatureData.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.ProducerID)) {
        this.ProducerID = data().deepCopy(fields()[0].schema(), other.ProducerID);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.Temperature)) {
        this.Temperature = data().deepCopy(fields()[1].schema(), other.Temperature);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.Humidity)) {
        this.Humidity = data().deepCopy(fields()[2].schema(), other.Humidity);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.Timestamp)) {
        this.Timestamp = data().deepCopy(fields()[3].schema(), other.Timestamp);
        fieldSetFlags()[3] = true;
      }
      if (other.hasTimestampBuilder()) {
        this.TimestampBuilder = producer.TimeRecord.newBuilder(other.getTimestampBuilder());
      }
    }

    /**
     * Creates a Builder by copying an existing TemperatureData instance
     * @param other The existing instance to copy.
     */
    private Builder(producer.TemperatureData other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.ProducerID)) {
        this.ProducerID = data().deepCopy(fields()[0].schema(), other.ProducerID);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.Temperature)) {
        this.Temperature = data().deepCopy(fields()[1].schema(), other.Temperature);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.Humidity)) {
        this.Humidity = data().deepCopy(fields()[2].schema(), other.Humidity);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.Timestamp)) {
        this.Timestamp = data().deepCopy(fields()[3].schema(), other.Timestamp);
        fieldSetFlags()[3] = true;
      }
      this.TimestampBuilder = null;
    }

    /**
      * Gets the value of the 'ProducerID' field.
      * @return The value.
      */
    public java.lang.String getProducerID() {
      return ProducerID;
    }

    /**
      * Sets the value of the 'ProducerID' field.
      * @param value The value of 'ProducerID'.
      * @return This builder.
      */
    public producer.TemperatureData.Builder setProducerID(java.lang.String value) {
      validate(fields()[0], value);
      this.ProducerID = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'ProducerID' field has been set.
      * @return True if the 'ProducerID' field has been set, false otherwise.
      */
    public boolean hasProducerID() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'ProducerID' field.
      * @return This builder.
      */
    public producer.TemperatureData.Builder clearProducerID() {
      ProducerID = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'Temperature' field.
      * @return The value.
      */
    public java.lang.Integer getTemperature() {
      return Temperature;
    }

    /**
      * Sets the value of the 'Temperature' field.
      * @param value The value of 'Temperature'.
      * @return This builder.
      */
    public producer.TemperatureData.Builder setTemperature(int value) {
      validate(fields()[1], value);
      this.Temperature = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'Temperature' field has been set.
      * @return True if the 'Temperature' field has been set, false otherwise.
      */
    public boolean hasTemperature() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'Temperature' field.
      * @return This builder.
      */
    public producer.TemperatureData.Builder clearTemperature() {
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'Humidity' field.
      * @return The value.
      */
    public java.lang.Double getHumidity() {
      return Humidity;
    }

    /**
      * Sets the value of the 'Humidity' field.
      * @param value The value of 'Humidity'.
      * @return This builder.
      */
    public producer.TemperatureData.Builder setHumidity(double value) {
      validate(fields()[2], value);
      this.Humidity = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'Humidity' field has been set.
      * @return True if the 'Humidity' field has been set, false otherwise.
      */
    public boolean hasHumidity() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'Humidity' field.
      * @return This builder.
      */
    public producer.TemperatureData.Builder clearHumidity() {
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'Timestamp' field.
      * @return The value.
      */
    public producer.TimeRecord getTimestamp() {
      return Timestamp;
    }

    /**
      * Sets the value of the 'Timestamp' field.
      * @param value The value of 'Timestamp'.
      * @return This builder.
      */
    public producer.TemperatureData.Builder setTimestamp(producer.TimeRecord value) {
      validate(fields()[3], value);
      this.TimestampBuilder = null;
      this.Timestamp = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'Timestamp' field has been set.
      * @return True if the 'Timestamp' field has been set, false otherwise.
      */
    public boolean hasTimestamp() {
      return fieldSetFlags()[3];
    }

    /**
     * Gets the Builder instance for the 'Timestamp' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public producer.TimeRecord.Builder getTimestampBuilder() {
      if (TimestampBuilder == null) {
        if (hasTimestamp()) {
          setTimestampBuilder(producer.TimeRecord.newBuilder(Timestamp));
        } else {
          setTimestampBuilder(producer.TimeRecord.newBuilder());
        }
      }
      return TimestampBuilder;
    }

    /**
     * Sets the Builder instance for the 'Timestamp' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */
    public producer.TemperatureData.Builder setTimestampBuilder(producer.TimeRecord.Builder value) {
      clearTimestamp();
      TimestampBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'Timestamp' field has an active Builder instance
     * @return True if the 'Timestamp' field has an active Builder instance
     */
    public boolean hasTimestampBuilder() {
      return TimestampBuilder != null;
    }

    /**
      * Clears the value of the 'Timestamp' field.
      * @return This builder.
      */
    public producer.TemperatureData.Builder clearTimestamp() {
      Timestamp = null;
      TimestampBuilder = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TemperatureData build() {
      try {
        TemperatureData record = new TemperatureData();
        record.ProducerID = fieldSetFlags()[0] ? this.ProducerID : (java.lang.String) defaultValue(fields()[0]);
        record.Temperature = fieldSetFlags()[1] ? this.Temperature : (java.lang.Integer) defaultValue(fields()[1]);
        record.Humidity = fieldSetFlags()[2] ? this.Humidity : (java.lang.Double) defaultValue(fields()[2]);
        if (TimestampBuilder != null) {
          record.Timestamp = this.TimestampBuilder.build();
        } else {
          record.Timestamp = fieldSetFlags()[3] ? this.Timestamp : (producer.TimeRecord) defaultValue(fields()[3]);
        }
        return record;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<TemperatureData>
    WRITER$ = (org.apache.avro.io.DatumWriter<TemperatureData>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<TemperatureData>
    READER$ = (org.apache.avro.io.DatumReader<TemperatureData>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}
