package us.ihmc.psyonicros2;

import static us.ihmc.psyonicros2.AbilityHandInterface.ACTUATOR_COUNT;

public class AbilityHandController
{
   public enum ControlMode
   {
      POSITION, VELOCITY, VEL_TO_POS, GRIP;

      public static final ControlMode[] values = values();

      public static ControlMode fromByte(byte ordinal)
      {
         return values[ordinal];
      }

      public byte toByte()
      {
         return (byte) this.ordinal();
      }
   }

   public enum Grip
   {
      POWER_GRIP, KEY_GRIP, TRIPOD_GRIP, RELAX_GRIP, RUDE_GRIP;

      public static final Grip[] values = values();

      public static Grip fromByte(byte ordinal)
      {
         return values[ordinal];
      }

      public byte toByte()
      {
         return (byte) this.ordinal();
      }
   }

   private static final float TOLERANCE = 2.5f;

   private final AbilityHandInterface hand;

   // High level control
   private ControlMode controlMode = ControlMode.POSITION;
   private byte grip = 0;
   private final float[] goalPositions = new float[ACTUATOR_COUNT];
   private final float[] goalVelocities = new float[ACTUATOR_COUNT];

   public AbilityHandController(AbilityHandInterface hand)
   {
      this.hand = hand;
   }

   public void update()
   {
      switch (controlMode)
      {
         case POSITION -> updatePositionControl();
         case VELOCITY -> updateVelocityControl();
         case VEL_TO_POS -> updateVelToPosControl();
         case GRIP -> updateGripControl();
      }
   }

   private void updatePositionControl()
   {
      hand.setCommandType(AbilityHandCommandType.POSITION);
      hand.setCommandValues(goalPositions);
   }

   private void updateVelocityControl()
   {
      hand.setCommandType(AbilityHandCommandType.VELOCITY);
      hand.setCommandValues(goalVelocities);
   }

   private void updateVelToPosControl()
   {
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         float currentPos = hand.getActuatorPosition(i);

         float velocity;
         if (Math.abs(currentPos - goalPositions[i]) < TOLERANCE)
         {
            velocity = 0;
         }
         else
         {
            float goalVelocity = Math.abs(goalVelocities[i]);
            velocity = (currentPos < goalPositions[i]) ? goalVelocity : -goalVelocity;
         }

         hand.setCommandType(AbilityHandCommandType.VELOCITY);
         hand.setCommandValue(i, velocity);
      }
   }

   private void updateGripControl()
   {

   }

   public AbilityHandInterface getHand()
   {
      return hand;
   }

   public void setControlMode(ControlMode controlMode)
   {
      this.controlMode = controlMode;
   }

   public void setGrip(byte grip)
   {
      this.grip = grip;
   }

   public void setGoalPositions(float[] goalPositions)
   {
      System.arraycopy(goalPositions, 0, this.goalPositions, 0, ACTUATOR_COUNT);
   }

   public void setGoalVelocities(float[] goalVelocities)
   {
      System.arraycopy(goalVelocities, 0, this.goalVelocities, 0, ACTUATOR_COUNT);
   }
}