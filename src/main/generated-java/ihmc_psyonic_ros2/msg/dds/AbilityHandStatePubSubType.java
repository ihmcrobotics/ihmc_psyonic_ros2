package ihmc_psyonic_ros2.msg.dds;

/**
* 
* Topic data type of the struct "AbilityHandState" defined in "AbilityHandState_.idl". Use this class to provide the TopicDataType to a Participant. 
*
* This file was automatically generated from AbilityHandState_.idl by us.ihmc.idl.generator.IDLGenerator. 
* Do not update this file directly, edit AbilityHandState_.idl instead.
*
*/
public class AbilityHandStatePubSubType implements us.ihmc.pubsub.TopicDataType<ihmc_psyonic_ros2.msg.dds.AbilityHandState>
{
   public static final java.lang.String name = "ihmc_psyonic_ros2::msg::dds_::AbilityHandState_";
   
   @Override
   public final java.lang.String getDefinitionChecksum()
   {
   		return "830d7c605596ec4fb4e5ce7b667e6ec743af3a68681cdd902c4fb385180b7794";
   }
   
   @Override
   public final java.lang.String getDefinitionVersion()
   {
   		return "local";
   }

   private final us.ihmc.idl.CDR serializeCDR = new us.ihmc.idl.CDR();
   private final us.ihmc.idl.CDR deserializeCDR = new us.ihmc.idl.CDR();

   @Override
   public void serialize(ihmc_psyonic_ros2.msg.dds.AbilityHandState data, us.ihmc.pubsub.common.SerializedPayload serializedPayload) throws java.io.IOException
   {
      serializeCDR.serialize(serializedPayload);
      write(data, serializeCDR);
      serializeCDR.finishSerialize();
   }

   @Override
   public void deserialize(us.ihmc.pubsub.common.SerializedPayload serializedPayload, ihmc_psyonic_ros2.msg.dds.AbilityHandState data) throws java.io.IOException
   {
      deserializeCDR.deserialize(serializedPayload);
      read(data, deserializeCDR);
      deserializeCDR.finishDeserialize();
   }

   public static int getMaxCdrSerializedSize()
   {
      return getMaxCdrSerializedSize(0);
   }

   public static int getMaxCdrSerializedSize(int current_alignment)
   {
      int initial_alignment = current_alignment;

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4) + 8 + 1;
      current_alignment += 1 + us.ihmc.idl.CDR.alignment(current_alignment, 1);

      current_alignment += ((6) * 4) + us.ihmc.idl.CDR.alignment(current_alignment, 4);


      return current_alignment - initial_alignment;
   }

   public final static int getCdrSerializedSize(ihmc_psyonic_ros2.msg.dds.AbilityHandState data)
   {
      return getCdrSerializedSize(data, 0);
   }

   public final static int getCdrSerializedSize(ihmc_psyonic_ros2.msg.dds.AbilityHandState data, int current_alignment)
   {
      int initial_alignment = current_alignment;

      current_alignment += 4 + us.ihmc.idl.CDR.alignment(current_alignment, 4) + data.getSerialNumber().length() + 1;

      current_alignment += 1 + us.ihmc.idl.CDR.alignment(current_alignment, 1);


      current_alignment += ((6) * 4) + us.ihmc.idl.CDR.alignment(current_alignment, 4);

      return current_alignment - initial_alignment;
   }

   public static void write(ihmc_psyonic_ros2.msg.dds.AbilityHandState data, us.ihmc.idl.CDR cdr)
   {
      if(data.getSerialNumber().length() <= 8)
      cdr.write_type_d(data.getSerialNumber());else
          throw new RuntimeException("serial_number field exceeds the maximum length: %d > %d".formatted(data.getSerialNumber().length(), 8));

      cdr.write_type_9(data.getHandSide());

      for(int i0 = 0; i0 < data.getActuatorPositions().length; ++i0)
      {
        	cdr.write_type_5(data.getActuatorPositions()[i0]);	
      }

   }

   public static void read(ihmc_psyonic_ros2.msg.dds.AbilityHandState data, us.ihmc.idl.CDR cdr)
   {
      cdr.read_type_d(data.getSerialNumber());	
      data.setHandSide(cdr.read_type_9());
      	
      for(int i0 = 0; i0 < data.getActuatorPositions().length; ++i0)
      {
        	data.getActuatorPositions()[i0] = cdr.read_type_5();
        	
      }
      	

   }

   @Override
   public final void serialize(ihmc_psyonic_ros2.msg.dds.AbilityHandState data, us.ihmc.idl.InterchangeSerializer ser)
   {
      ser.write_type_d("serial_number", data.getSerialNumber());
      ser.write_type_9("hand_side", data.getHandSide());
      ser.write_type_f("actuator_positions", data.getActuatorPositions());
   }

   @Override
   public final void deserialize(us.ihmc.idl.InterchangeSerializer ser, ihmc_psyonic_ros2.msg.dds.AbilityHandState data)
   {
      ser.read_type_d("serial_number", data.getSerialNumber());
      data.setHandSide(ser.read_type_9("hand_side"));
      ser.read_type_f("actuator_positions", data.getActuatorPositions());
   }

   public static void staticCopy(ihmc_psyonic_ros2.msg.dds.AbilityHandState src, ihmc_psyonic_ros2.msg.dds.AbilityHandState dest)
   {
      dest.set(src);
   }

   @Override
   public ihmc_psyonic_ros2.msg.dds.AbilityHandState createData()
   {
      return new ihmc_psyonic_ros2.msg.dds.AbilityHandState();
   }
   @Override
   public int getTypeSize()
   {
      return us.ihmc.idl.CDR.getTypeSize(getMaxCdrSerializedSize());
   }

   @Override
   public java.lang.String getName()
   {
      return name;
   }
   
   public void serialize(ihmc_psyonic_ros2.msg.dds.AbilityHandState data, us.ihmc.idl.CDR cdr)
   {
      write(data, cdr);
   }

   public void deserialize(ihmc_psyonic_ros2.msg.dds.AbilityHandState data, us.ihmc.idl.CDR cdr)
   {
      read(data, cdr);
   }
   
   public void copy(ihmc_psyonic_ros2.msg.dds.AbilityHandState src, ihmc_psyonic_ros2.msg.dds.AbilityHandState dest)
   {
      staticCopy(src, dest);
   }

   @Override
   public AbilityHandStatePubSubType newInstance()
   {
      return new AbilityHandStatePubSubType();
   }
}
