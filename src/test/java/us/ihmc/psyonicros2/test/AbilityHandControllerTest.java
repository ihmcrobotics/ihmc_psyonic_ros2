package us.ihmc.psyonicros2.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.ihmc.psyonicros2.AbilityHandController;
import us.ihmc.psyonicros2.AbilityHandController.ControlMode;
import us.ihmc.psyonicros2.AbilityHandController.Grip;
import us.ihmc.psyonicros2.AbilityHandCommandType;
import us.ihmc.psyonicros2.AbilityHandInterface;
import us.ihmc.robotics.robotSide.RobotSide;

import java.util.Arrays;

import static us.ihmc.psyonicros2.AbilityHandInterface.ACTUATOR_COUNT;
import static org.junit.jupiter.api.Assertions.*;

public class AbilityHandControllerTest
{
   private TestAbilityHand testHand;
   private AbilityHandController controller;

   @BeforeEach
   public void setUp()
   {
      testHand = new TestAbilityHand();
      controller = new AbilityHandController(testHand);
   }

   @Test
   public void testPositionControl()
   {
      float[] positions = {10f, 20f, 30f, 40f, 50f, -10f};
      controller.setControlMode(ControlMode.POSITION);
      controller.setGoalPositions(positions);

      controller.update();

      assertEquals(AbilityHandCommandType.POSITION, testHand.getCommandType());
      assertArrayEquals(positions, testHand.getLastCommandValues(), 1e-6f);
   }

   @Test
   public void testVelocityControl()
   {
      float[] velocities = {1f, 2f, 3f, 4f, 5f, -5f};
      controller.setControlMode(ControlMode.VELOCITY);
      controller.setGoalVelocities(velocities);

      controller.update();

      assertEquals(AbilityHandCommandType.VELOCITY, testHand.getCommandType());
      assertArrayEquals(velocities, testHand.getLastCommandValues(), 1e-6f);
   }

   @Test
   public void testVelToPosControl()
   {
      // current < goal => positive, current > goal => negative
      float[] current = {5f, 50f, 0f, 0f, 0f, 0f};
      float[] goals = {10f, 20f, 0f, 0f, 0f, 0f};
      float[] speeds = {2f, 3f, 0f, 0f, 0f, 0f};

      testHand.setActuatorPositions(current);
      controller.setControlMode(ControlMode.VEL_TO_POS);
      controller.setGoalPositions(goals);
      controller.setGoalVelocities(speeds);

      controller.update();

      assertEquals(AbilityHandCommandType.VELOCITY, testHand.getCommandType());
      float[] cmd = testHand.getLastCommandValues();
      assertTrue(cmd[0] > 0, "Index 0 should move positively");
      assertTrue(cmd[1] < 0, "Index 1 should move negatively");
   }

   @Test
   public void testGripInitialThumbStage()
   {
      // Thumb (index 4) must clear first: simulate it at the clear position already
      float[] current = {30f, 30f, 30f, 30f, 30f, 30f};
      testHand.setActuatorPositions(current);

      controller.setGoalVelocities(new float[] {5f, 5f, 5f, 5f, 5f, 5f});
      controller.setControlMode(ControlMode.GRIP);
      controller.setGrip(Grip.POWER);

      controller.update();

      assertEquals(AbilityHandCommandType.VELOCITY, testHand.getCommandType());
      float[] cmd = testHand.getLastCommandValues();

      // Thumb index 4 should be zero (already clear) and stage 0 should run:
      for (int i = 0; i < 4; i++)
         assertTrue(cmd[i] > 0, "Finger " + i + " should start closing");
      assertEquals(0f, cmd[4], 1e-6f, "Thumb should not move on clear");
      assertEquals(0f, cmd[5], 1e-6f, "Pinky shouldn't move in stage 0");
   }

   /**
    * Simple test stub for AbilityHandInterface.
    * Clears its command buffer on each setCommandType(...)
    * so tests can assert a fresh snapshot of that update's commands.
    */
   private static class TestAbilityHand implements AbilityHandInterface
   {
      private final float[] actuatorPositions = new float[ACTUATOR_COUNT];
      private final float[] lastCommandValues = new float[ACTUATOR_COUNT];
      private AbilityHandCommandType commandType;

      @Override
      public float getActuatorPosition(int index)
      {
         return actuatorPositions[index];
      }

      @Override
      public void setActuatorPosition(int index, float value)
      {

      }

      public void setActuatorPositions(float[] positions)
      {
         System.arraycopy(positions, 0, actuatorPositions, 0, ACTUATOR_COUNT);
      }

      @Override
      public void setCommandType(AbilityHandCommandType type)
      {
         this.commandType = type;
         // clear out any old commands
         Arrays.fill(lastCommandValues, 0f);
      }

      @Override
      public float getCommandValue(int index)
      {
         return 0;
      }

      @Override
      public void setCommandValues(float[] values)
      {
         System.arraycopy(values, 0, lastCommandValues, 0, ACTUATOR_COUNT);
      }

      @Override
      public void setCommandValue(int index, float value)
      {
         lastCommandValues[index] = value;
      }

      // Unused for these tests:
      @Override
      public String getSerialNumber()
      {
         return null;
      }

      @Override
      public RobotSide getHandSide()
      {
         return null;
      }

      // Expose for assertions:
      public AbilityHandCommandType getCommandType()
      {
         return commandType;
      }

      public float[] getLastCommandValues()
      {
         return lastCommandValues;
      }
   }
}
