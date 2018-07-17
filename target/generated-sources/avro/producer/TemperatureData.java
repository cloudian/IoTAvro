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
  private static final long serialVersionUID = -4015155663227788836L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TemperatureData\",\"namespace\":\"producer\",\"fields\":[{\"name\":\"timestamp\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"temperature\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}],\"version\":\"2\"}");
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

   private java.lang.String timestamp;
   private java.lang.String temperature;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TemperatureData() {}

  /**
   * All-args constructor.
   * @param timestamp The new value for timestamp
   * @param temperature The new value for temperature
   */
  public TemperatureData(java.lang.String timestamp, java.lang.String temperature) {
    this.timestamp = timestamp;
    this.temperature = temperature;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return timestamp;
    case 1: return temperature;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: timestamp = (java.lang.String)value$; break;
    case 1: temperature = (java.lang.String)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'timestamp' field.
   * @return The value of the 'timestamp' field.
   */
  public java.lang.String getTimestamp() {
    return timestamp;
  }


  /**
   * Gets the value of the 'temperature' field.
   * @return The value of the 'temperature' field.
   */
  public java.lang.String getTemperature() {
    return temperature;
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

    private java.lang.String timestamp;
    private java.lang.String temperature;

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
      if (isValidValue(fields()[0], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[0].schema(), other.timestamp);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.temperature)) {
        this.temperature = data().deepCopy(fields()[1].schema(), other.temperature);
        fieldSetFlags()[1] = true;
      }
    }

    /**
     * Creates a Builder by copying an existing TemperatureData instance
     * @param other The existing instance to copy.
     */
    private Builder(producer.TemperatureData other) {
            super(SCHEMA$);
      if (isValidValue(fields()[0], other.timestamp)) {
        this.timestamp = data().deepCopy(fields()[0].schema(), other.timestamp);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.temperature)) {
        this.temperature = data().deepCopy(fields()[1].schema(), other.temperature);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'timestamp' field.
      * @return The value.
      */
    public java.lang.String getTimestamp() {
      return timestamp;
    }

    /**
      * Sets the value of the 'timestamp' field.
      * @param value The value of 'timestamp'.
      * @return This builder.
      */
    public producer.TemperatureData.Builder setTimestamp(java.lang.String value) {
      validate(fields()[0], value);
      this.timestamp = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'timestamp' field has been set.
      * @return True if the 'timestamp' field has been set, false otherwise.
      */
    public boolean hasTimestamp() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'timestamp' field.
      * @return This builder.
      */
    public producer.TemperatureData.Builder clearTimestamp() {
      timestamp = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'temperature' field.
      * @return The value.
      */
    public java.lang.String getTemperature() {
      return temperature;
    }

    /**
      * Sets the value of the 'temperature' field.
      * @param value The value of 'temperature'.
      * @return This builder.
      */
    public producer.TemperatureData.Builder setTemperature(java.lang.String value) {
      validate(fields()[1], value);
      this.temperature = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'temperature' field has been set.
      * @return True if the 'temperature' field has been set, false otherwise.
      */
    public boolean hasTemperature() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'temperature' field.
      * @return This builder.
      */
    public producer.TemperatureData.Builder clearTemperature() {
      temperature = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TemperatureData build() {
      try {
        TemperatureData record = new TemperatureData();
        record.timestamp = fieldSetFlags()[0] ? this.timestamp : (java.lang.String) defaultValue(fields()[0]);
        record.temperature = fieldSetFlags()[1] ? this.temperature : (java.lang.String) defaultValue(fields()[1]);
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
