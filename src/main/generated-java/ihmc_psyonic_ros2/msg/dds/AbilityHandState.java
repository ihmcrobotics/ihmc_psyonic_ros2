package ihmc_psyonic_ros2.msg.dds;

import us.ihmc.communication.packets.Packet;
import us.ihmc.euclid.interfaces.Settable;
import us.ihmc.euclid.interfaces.EpsilonComparable;
import java.util.function.Supplier;
import us.ihmc.pubsub.TopicDataType;

public class AbilityHandState extends Packet<AbilityHandState> implements Settable<AbilityHandState>, EpsilonComparable<AbilityHandState>
{
   public static final byte LEFT = (byte) 0;
   public static final byte RIGHT = (byte) 1;
   /**
            * Specifies the side of the robot of the hand being referred to
            */
   public byte hand_side_ = (byte) 255;
   /**
            * The actuator positions in degrees
            */
   public float[] actuator_positions_;

   public AbilityHandState()
   {
      actuator_positions_ = new float[6];

   }

   public AbilityHandState(AbilityHandState other)
   {
      this();
      set(other);
   }

   public void set(AbilityHandState other)
   {
      hand_side_ = other.hand_side_;

      for(int i1 = 0; i1 < actuator_positions_.length; ++i1)
      {
            actuator_positions_[i1] = other.actuator_positions_[i1];

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


   /**
            * The actuator positions in degrees
            */
   public float[] getActuatorPositions()
   {
      return actuator_positions_;
   }


   public static Supplier<AbilityHandStatePubSubType> getPubSubType()
   {
      return AbilityHandStatePubSubType::new;
   }

   @Override
   public Supplier<TopicDataType> getPubSubTypePacket()
   {
      return AbilityHandStatePubSubType::new;
   }

   @Override
   public boolean epsilonEquals(AbilityHandState other, double epsilon)
   {
      if(other == null) return false;
      if(other == this) return true;

      if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.hand_side_, other.hand_side_, epsilon)) return false;

      for(int i3 = 0; i3 < actuator_positions_.length; ++i3)
      {
                if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.actuator_positions_[i3], other.actuator_positions_[i3], epsilon)) return false;
      }


      return true;
   }

   @Override
   public boolean equals(Object other)
   {
      if(other == null) return false;
      if(other == this) return true;
      if(!(other instanceof AbilityHandState)) return false;

      AbilityHandState otherMyClass = (AbilityHandState) other;

      if(this.hand_side_ != otherMyClass.hand_side_) return false;

      for(int i5 = 0; i5 < actuator_positions_.length; ++i5)
      {
                if(this.actuator_positions_[i5] != otherMyClass.actuator_positions_[i5]) return false;

      }

      return true;
   }

   @Override
   public java.lang.String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append("AbilityHandState {");
      builder.append("hand_side=");
      builder.append(this.hand_side_);      builder.append(", ");
      builder.append("actuator_positions=");
      builder.append(java.util.Arrays.toString(this.actuator_positions_));
      builder.append("}");
      return builder.toString();
   }
}
