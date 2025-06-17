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
   /**
            * Specifies the command type (one of POSITION, VELOCITY, CURRENT, DUTY)
            */
   public byte command_type_;
   public float[] command_values_;

   public AbilityHandCommand()
   {
      command_values_ = new float[6];

   }

   public AbilityHandCommand(AbilityHandCommand other)
   {
      this();
      set(other);
   }

   public void set(AbilityHandCommand other)
   {
      hand_side_ = other.hand_side_;

      command_type_ = other.command_type_;

      for(int i1 = 0; i1 < command_values_.length; ++i1)
      {
            command_values_[i1] = other.command_values_[i1];

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
            * Specifies the command type (one of POSITION, VELOCITY, CURRENT, DUTY)
            */
   public void setCommandType(byte command_type)
   {
      command_type_ = command_type;
   }
   /**
            * Specifies the command type (one of POSITION, VELOCITY, CURRENT, DUTY)
            */
   public byte getCommandType()
   {
      return command_type_;
   }


   public float[] getCommandValues()
   {
      return command_values_;
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

      if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.command_type_, other.command_type_, epsilon)) return false;

      for(int i3 = 0; i3 < command_values_.length; ++i3)
      {
                if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.command_values_[i3], other.command_values_[i3], epsilon)) return false;
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

      if(this.command_type_ != otherMyClass.command_type_) return false;

      for(int i5 = 0; i5 < command_values_.length; ++i5)
      {
                if(this.command_values_[i5] != otherMyClass.command_values_[i5]) return false;

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
      builder.append("command_type=");
      builder.append(this.command_type_);      builder.append(", ");
      builder.append("command_values=");
      builder.append(java.util.Arrays.toString(this.command_values_));
      builder.append("}");
      return builder.toString();
   }
}
