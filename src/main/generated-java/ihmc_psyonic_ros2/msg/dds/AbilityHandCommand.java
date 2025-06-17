package ihmc_psyonic_ros2.msg.dds;

import us.ihmc.communication.packets.Packet;
import us.ihmc.euclid.interfaces.Settable;
import us.ihmc.euclid.interfaces.EpsilonComparable;
import java.util.function.Supplier;
import us.ihmc.pubsub.TopicDataType;

public class AbilityHandCommand extends Packet<AbilityHandCommand> implements Settable<AbilityHandCommand>, EpsilonComparable<AbilityHandCommand>
{
   public static final byte LEFT = (byte) 0;
   public static final byte RIGHT = (byte) 1;
   /**
            * Specifies the side of the robot of the hand being referred to
            */
   public byte hand_side_ = (byte) 255;
   public float[] finger_positions_degrees_;

   public AbilityHandCommand()
   {
      finger_positions_degrees_ = new float[6];

   }

   public AbilityHandCommand(AbilityHandCommand other)
   {
      this();
      set(other);
   }

   public void set(AbilityHandCommand other)
   {
      hand_side_ = other.hand_side_;

      for(int i1 = 0; i1 < finger_positions_degrees_.length; ++i1)
      {
            finger_positions_degrees_[i1] = other.finger_positions_degrees_[i1];

      }

   }

   /**
            * Specifies the side of the robot of the hand being referred to
            */
   public void setHandSide(byte hand_side)
   {
      hand_side_ = hand_side;
   }
   /**
            * Specifies the side of the robot of the hand being referred to
            */
   public byte getHandSide()
   {
      return hand_side_;
   }


   public float[] getFingerPositionsDegrees()
   {
      return finger_positions_degrees_;
   }


   public static Supplier<AbilityHandCommandPubSubType> getPubSubType()
   {
      return AbilityHandCommandPubSubType::new;
   }

   @Override
   public Supplier<TopicDataType> getPubSubTypePacket()
   {
      return AbilityHandCommandPubSubType::new;
   }

   @Override
   public boolean epsilonEquals(AbilityHandCommand other, double epsilon)
   {
      if(other == null) return false;
      if(other == this) return true;

      if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.hand_side_, other.hand_side_, epsilon)) return false;

      for(int i3 = 0; i3 < finger_positions_degrees_.length; ++i3)
      {
                if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.finger_positions_degrees_[i3], other.finger_positions_degrees_[i3], epsilon)) return false;
      }


      return true;
   }

   @Override
   public boolean equals(Object other)
   {
      if(other == null) return false;
      if(other == this) return true;
      if(!(other instanceof AbilityHandCommand)) return false;

      AbilityHandCommand otherMyClass = (AbilityHandCommand) other;

      if(this.hand_side_ != otherMyClass.hand_side_) return false;

      for(int i5 = 0; i5 < finger_positions_degrees_.length; ++i5)
      {
                if(this.finger_positions_degrees_[i5] != otherMyClass.finger_positions_degrees_[i5]) return false;

      }

      return true;
   }

   @Override
   public java.lang.String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append("AbilityHandCommand {");
      builder.append("hand_side=");
      builder.append(this.hand_side_);      builder.append(", ");
      builder.append("finger_positions_degrees=");
      builder.append(java.util.Arrays.toString(this.finger_positions_degrees_));
      builder.append("}");
      return builder.toString();
   }
}
