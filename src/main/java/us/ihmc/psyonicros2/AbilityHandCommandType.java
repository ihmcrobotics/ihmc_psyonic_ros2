package us.ihmc.psyonicros2;

import java.util.EnumSet;

public enum AbilityHandCommandType
{
   POSITION, VELOCITY, CURRENT, DUTY;

   public static final AbilityHandCommandType[] values = values();
   public static final EnumSet<AbilityHandCommandType> set = EnumSet.allOf(AbilityHandCommandType.class);

   public byte toByte()
   {
      return (byte) this.ordinal();
   }

   public static AbilityHandCommandType fromByte(byte ordinal)
   {
      return values[ordinal];
   }
}
