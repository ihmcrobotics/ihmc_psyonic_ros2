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

   private static final int[][][] GRIP_FINGER_STAGES = {
         {{0, 1, 2, 3},
          {5},
          {4}},
         {{0}, {1, 2, 3, 4}, {5}},
         {{0, 1, 2}, {3, 4, 5}},
         {{4}, {0, 1, 2, 3, 5}},
         {{0, 1, 2, 3, 4}, {5}}};

   private static final float[][][] GRIP_STAGE_POSITIONS = {
         {{90, 90, 90, 90}, {-50}, {50}},
         {{50}, {70, 70, 70, 70}, {-30}},
         {{60, 60, 60}, {90, 90, -90}},
         {{30}, {30, 30, 30, 30, -30}},
         {{100, 30, 100, 100, 20}, {-30}}};

   private static final float TOLERANCE = 7.5f;

   private final AbilityHandInterface hand;

   // High level control
   private ControlMode controlMode = ControlMode.POSITION;
   private int gripStage = 0;
   private Grip grip;
   private final float[] goalPositions;
   private final float[] goalVelocities;

   public AbilityHandController(AbilityHandInterface hand)
   {
      this.hand = hand;
      goalPositions = new float[] {30.0f, 30.0f, 30.0f, 30.0f, 30.0f, -30.0f};
      goalVelocities = new float[] {0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f};
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
      int gripIdx = grip.ordinal();
      int[][] stages = GRIP_FINGER_STAGES[gripIdx];
      float[][] positions = GRIP_STAGE_POSITIONS[gripIdx];

      // if we’re past the last stage, go back to position mode
      if (gripStage >= stages.length)
      {
         controlMode = ControlMode.POSITION;
         gripStage = 0;
         return;
      }
      int[] fingersThisStage = stages[gripStage];
      float[] targetsThisStage = positions[gripStage];

      hand.setCommandType(AbilityHandCommandType.VELOCITY);

      boolean stageComplete = true;

      for (int idx = 0; idx < fingersThisStage.length; idx++)
      {
         int i = fingersThisStage[idx];
         float target = targetsThisStage[idx];
         float current = hand.getActuatorPosition(i);

         float vel;
         if (Math.abs(current - target) < TOLERANCE)
         {
            vel = 0f;
         }
         else
         {
            float speed = Math.abs(30);
            vel = (current < target) ? speed : -speed;
            stageComplete = false;
         }

         hand.setCommandValue(i, vel);
      }

      if (stageComplete)
      {
         gripStage++;
      }
   }

   public AbilityHandInterface getHand()
   {
      return hand;
   }

   public void setControlMode(ControlMode controlMode)
   {
      this.controlMode = controlMode;
   }

   public void setGrip(Grip grip)
   {
      this.grip = grip;
   }

   public void setGoalPosition(int index, float goalPosition)
   {
      goalPositions[index] = goalPosition;
   }

   public void setGoalPositions(float[] goalPositions)
   {
      for (int i = 0; i < ACTUATOR_COUNT; ++i)
         setGoalPosition(i, goalPositions[i]);
   }

   public void setGoalVelocity(int index, float goalVelocity)
   {
      goalVelocities[index] = goalVelocity;
   }

   public void setGoalVelocities(float[] goalVelocities)
   {
      for (int i = 0; i < ACTUATOR_COUNT; ++i)
         setGoalVelocity(i, goalVelocities[i]);
   }
}
