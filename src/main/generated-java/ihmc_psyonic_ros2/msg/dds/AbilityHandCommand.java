package ihmc_psyonic_ros2.msg.dds;

import us.ihmc.communication.packets.Packet;
import us.ihmc.euclid.interfaces.Settable;
import us.ihmc.euclid.interfaces.EpsilonComparable;
import java.util.function.Supplier;
import us.ihmc.pubsub.TopicDataType;

public class AbilityHandCommand extends Packet<AbilityHandCommand> implements Settable<AbilityHandCommand>, EpsilonComparable<AbilityHandCommand>
{
   public static final byte POSITION_CONTROL = (byte) 0;
   public static final byte VELOCITY_CONTROL = (byte) 1;
   public static final byte VEL_TO_POS_CONTROL = (byte) 2;
   public static final byte GRIP_CONTROL = (byte) 3;
   public static final byte POWER_GRIP = (byte) 0;
   public static final byte KEY_GRIP = (byte) 1;
   public static final byte TRIPOD_GRIP = (byte) 2;
   public static final byte RELAX_GRIP = (byte) 3;
   public static final byte RUDE_GRIP = (byte) 4;
   /**
            * The hand's serial number. E.g. 24ABH265
            */
   public java.lang.StringBuilder serial_number_;
   /**
            * Specifies the control mode
            * Default = position control
            */
   public byte control_mode_;
   public byte grip_;
   public float[] goal_positions_;
   public float[] goal_velocities_;

   public AbilityHandCommand()
   {
      serial_number_ = new java.lang.StringBuilder(8);
      goal_positions_ = new float[6];

      goal_velocities_ = new float[6];

   }

   public AbilityHandCommand(AbilityHandCommand other)
   {
      this();
      set(other);
   }

   public void set(AbilityHandCommand other)
   {
      serial_number_.setLength(0);
      serial_number_.append(other.serial_number_);

      control_mode_ = other.control_mode_;

      grip_ = other.grip_;

      for(int i1 = 0; i1 < goal_positions_.length; ++i1)
      {
            goal_positions_[i1] = other.goal_positions_[i1];

      }

      for(int i3 = 0; i3 < goal_velocities_.length; ++i3)
      {
            goal_velocities_[i3] = other.goal_velocities_[i3];

      }

   }

   /**
            * The hand's serial number. E.g. 24ABH265
            */
   public void setSerialNumber(java.lang.String serial_number)
   {
      serial_number_.setLength(0);
      serial_number_.append(serial_number);
   }

   /**
            * The hand's serial number. E.g. 24ABH265
            */
   public java.lang.String getSerialNumberAsString()
   {
      return getSerialNumber().toString();
   }
   /**
            * The hand's serial number. E.g. 24ABH265
            */
   public java.lang.StringBuilder getSerialNumber()
   {
      return serial_number_;
   }

   /**
            * Specifies the control mode
            * Default = position control
            */
   public void setControlMode(byte control_mode)
   {
      control_mode_ = control_mode;
   }
   /**
            * Specifies the control mode
            * Default = position control
            */
   public byte getControlMode()
   {
      return control_mode_;
   }

   public void setGrip(byte grip)
   {
      grip_ = grip;
   }
   public byte getGrip()
   {
      return grip_;
   }


   public float[] getGoalPositions()
   {
      return goal_positions_;
   }


   public float[] getGoalVelocities()
   {
      return goal_velocities_;
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

      if (!us.ihmc.idl.IDLTools.epsilonEqualsStringBuilder(this.serial_number_, other.serial_number_, epsilon)) return false;

      if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.control_mode_, other.control_mode_, epsilon)) return false;

      if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.grip_, other.grip_, epsilon)) return false;

      for(int i5 = 0; i5 < goal_positions_.length; ++i5)
      {
                if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.goal_positions_[i5], other.goal_positions_[i5], epsilon)) return false;
      }

      for(int i7 = 0; i7 < goal_velocities_.length; ++i7)
      {
                if (!us.ihmc.idl.IDLTools.epsilonEqualsPrimitive(this.goal_velocities_[i7], other.goal_velocities_[i7], epsilon)) return false;
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

      if (!us.ihmc.idl.IDLTools.equals(this.serial_number_, otherMyClass.serial_number_)) return false;

      if(this.control_mode_ != otherMyClass.control_mode_) return false;

      if(this.grip_ != otherMyClass.grip_) return false;

      for(int i9 = 0; i9 < goal_positions_.length; ++i9)
      {
                if(this.goal_positions_[i9] != otherMyClass.goal_positions_[i9]) return false;

      }
      for(int i11 = 0; i11 < goal_velocities_.length; ++i11)
      {
                if(this.goal_velocities_[i11] != otherMyClass.goal_velocities_[i11]) return false;

      }

      return true;
   }

   @Override
   public java.lang.String toString()
   {
      StringBuilder builder = new StringBuilder();

      builder.append("AbilityHandCommand {");
      builder.append("serial_number=");
      builder.append(this.serial_number_);      builder.append(", ");
      builder.append("control_mode=");
      builder.append(this.control_mode_);      builder.append(", ");
      builder.append("grip=");
      builder.append(this.grip_);      builder.append(", ");
      builder.append("goal_positions=");
      builder.append(java.util.Arrays.toString(this.goal_positions_));      builder.append(", ");
      builder.append("goal_velocities=");
      builder.append(java.util.Arrays.toString(this.goal_velocities_));
      builder.append("}");
      return builder.toString();
   }
}
