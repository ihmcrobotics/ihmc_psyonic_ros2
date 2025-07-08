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
      POWER (new int[][] {{0, 1, 2, 3}, {5}, {4}}, new float[][] {{100, 100, 100, 100}, {-75}, {75}}),
      KEY   (new int[][] {{0, 1, 2, 3}, {5}, {4}}, new float[][] {{90, 90, 90, 90}, {-20}, {75}}),
      TRIPOD(new int[][] {{0, 1, 2, 3}, {5}, {4}}, new float[][] {{60, 60, 20, 20}, {-75}, {60}}),
      RELAX (new int[][] {{4}, {0, 1, 2, 3, 5}},   new float[][] {{30}, {30, 30, 30, 30, -30}}),
      RUDE  (new int[][] {{0, 1, 2, 3, 4}, {5}},   new float[][] {{100, 10, 100, 100, 20}, {-30}});

      public static final Grip[] values = values();

      final int[][] stages;
      final float[][] positions;

      Grip(int[][] stages, float[][] positions)
      {
         this.stages = stages;
         this.positions = positions;
      }

      public static Grip fromByte(byte ordinal)
      {
         return values[ordinal];
      }

      public byte toByte()
      {
         return (byte) this.ordinal();
      }
   }

   private static final float TOLERANCE = 5.0f;
   private static final float THUMB_CLEAR_POSITION = 30.0f;

   private final AbilityHandInterface hand;

   // High level control
   private ControlMode controlMode = ControlMode.POSITION;
   private Grip grip = null;
   private Grip previousGrip = null;
   private int gripStage = Integer.MAX_VALUE;
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

   private float calculateVelocityToPosition(int actuatorIndex, float goalPosition, float goalVelocity)
   {
      // Get the current position
      float currentPosition = hand.getActuatorPosition(actuatorIndex);

      // If we've reached the goal position, velocity should be 0 (stop moving)
      if (Math.abs(currentPosition - goalPosition) < TOLERANCE)
         return 0.0f;

      // Otherwise velocity should be in the correct direction
      float speed = Math.abs(goalVelocity);
      return currentPosition < goalPosition ? speed : -speed;
   }

   private void updateVelToPosControl()
   {
      hand.setCommandType(AbilityHandCommandType.VELOCITY);

      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         float velocity = calculateVelocityToPosition(i, goalPositions[i], goalVelocities[i]);
         hand.setCommandValue(i, velocity);
      }
   }

   private void updateGripControl()
   {
      // If goal grip changed, reset grip stage
      if (previousGrip != grip)
      {
         gripStage = -1;
         previousGrip = grip;
      }

      // If we’re past the last stage, the grip is completed. No need to do anything
      if (gripStage >= grip.stages.length)
         return;

      // Using velocity to position control
      hand.setCommandType(AbilityHandCommandType.VELOCITY);

      // Move the thumb out of the way before any grip (stage = -1)
      if (gripStage == -1)
      {
         // Calculate the velocity required to read the clear position
         float thumbVelocity = calculateVelocityToPosition(4, THUMB_CLEAR_POSITION, goalVelocities[4]);

         // If velocity is 0 or positive, thumb is already clear. Only move thumb if velocity is negative.
         if (thumbVelocity < 0.0f)
         {
            // Tell thumb to move out of the way
            hand.setCommandValue(4, thumbVelocity);

            // Rest of the fingers shouldn't move
            for (int i = 0; i < ACTUATOR_COUNT; ++i)
               if (i != 4)
                  hand.setCommandValue(i, 0.0f);

            return;
         }

         // Thumb is clear. Start normal grip stages.
         gripStage = 0;
      }

      // Get the actuators that need to move during this stage and their goal positions
      int[] actuatorsToMove = grip.stages[gripStage];
      float[] goalPositions = grip.positions[gripStage];

      boolean stageComplete = true;
      for (int i = 0; i < actuatorsToMove.length; i++)
      {
         int actuatorIndex = actuatorsToMove[i];
         float goalPosition = goalPositions[i];
         float goalVelocity = goalVelocities[actuatorIndex];

         float velocity = calculateVelocityToPosition(actuatorIndex, goalPosition, goalVelocity);

         if (velocity != 0.0f)
            stageComplete = false;

         hand.setCommandValue(actuatorIndex, velocity);
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
