package ihmc_psyonic_ros2.msg.dds;

/**
* 
* Topic data type of the struct "AbilityHandCommand" defined in "AbilityHandCommand_.idl". Use this class to provide the TopicDataType to a Participant. 
*
* This file was automatically generated from AbilityHandCommand_.idl by us.ihmc.idl.generator.IDLGenerator. 
* Do not update this file directly, edit AbilityHandCommand_.idl instead.
*
*/
public class AbilityHandCommandPubSubType implements us.ihmc.pubsub.TopicDataType<ihmc_psyonic_ros2.msg.dds.AbilityHandCommand>
{
   public static final java.lang.String name = "ihmc_psyonic_ros2::msg::dds_::AbilityHandCommand_";
   
   @Override
   public final java.lang.String getDefinitionChecksum()
   {
   		return "86a940796e14f35db611fcab7c49a593ad5ecbe5b9026e554eda46d5c9823618";
   }
   
   @Override
   public final java.lang.String getDefinitionVersion()
   {
   		return "local";
   }

   private final us.ihmc.idl.CDR serializeCDR = new us.ihmc.idl.CDR();
   private final us.ihmc.idl.CDR deserializeCDR = new us.ihmc.idl.CDR();

   @Override
   public void serialize(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data, us.ihmc.pubsub.common.SerializedPayload serializedPayload) throws java.io.IOException
   {
      serializeCDR.serialize(serializedPayload);
      write(data, serializeCDR);
      serializeCDR.finishSerialize();
   }

   @Override
   public void deserialize(us.ihmc.pubsub.common.SerializedPayload serializedPayload, ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data) throws java.io.IOException
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

      current_alignment += 1 + us.ihmc.idl.CDR.alignment(current_alignment, 1);

      current_alignment += ((6) * 4) + us.ihmc.idl.CDR.alignment(current_alignment, 4);


      return current_alignment - initial_alignment;
   }

   public final static int getCdrSerializedSize(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data)
   {
      return getCdrSerializedSize(data, 0);
   }

   public final static int getCdrSerializedSize(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data, int current_alignment)
   {
      int initial_alignment = current_alignment;

      current_alignment += 1 + us.ihmc.idl.CDR.alignment(current_alignment, 1);


      current_alignment += ((6) * 4) + us.ihmc.idl.CDR.alignment(current_alignment, 4);

      return current_alignment - initial_alignment;
   }

   public static void write(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data, us.ihmc.idl.CDR cdr)
   {
      cdr.write_type_9(data.getHandSide());

      for(int i0 = 0; i0 < data.getFingerPositionsDegrees().length; ++i0)
      {
        	cdr.write_type_5(data.getFingerPositionsDegrees()[i0]);	
      }

   }

   public static void read(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data, us.ihmc.idl.CDR cdr)
   {
      data.setHandSide(cdr.read_type_9());
      	
      for(int i0 = 0; i0 < data.getFingerPositionsDegrees().length; ++i0)
      {
        	data.getFingerPositionsDegrees()[i0] = cdr.read_type_5();
        	
      }
      	

   }

   @Override
   public final void serialize(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data, us.ihmc.idl.InterchangeSerializer ser)
   {
      ser.write_type_9("hand_side", data.getHandSide());
      ser.write_type_f("finger_positions_degrees", data.getFingerPositionsDegrees());
   }

   @Override
   public final void deserialize(us.ihmc.idl.InterchangeSerializer ser, ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data)
   {
      data.setHandSide(ser.read_type_9("hand_side"));
      ser.read_type_f("finger_positions_degrees", data.getFingerPositionsDegrees());
   }

   public static void staticCopy(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand src, ihmc_psyonic_ros2.msg.dds.AbilityHandCommand dest)
   {
      dest.set(src);
   }

   @Override
   public ihmc_psyonic_ros2.msg.dds.AbilityHandCommand createData()
   {
      return new ihmc_psyonic_ros2.msg.dds.AbilityHandCommand();
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
   
   public void serialize(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data, us.ihmc.idl.CDR cdr)
   {
      write(data, cdr);
   }

   public void deserialize(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand data, us.ihmc.idl.CDR cdr)
   {
      read(data, cdr);
   }
   
   public void copy(ihmc_psyonic_ros2.msg.dds.AbilityHandCommand src, ihmc_psyonic_ros2.msg.dds.AbilityHandCommand dest)
   {
      staticCopy(src, dest);
   }

   @Override
   public AbilityHandCommandPubSubType newInstance()
   {
      return new AbilityHandCommandPubSubType();
   }
}
