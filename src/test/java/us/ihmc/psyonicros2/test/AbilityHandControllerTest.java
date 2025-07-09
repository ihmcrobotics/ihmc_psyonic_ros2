package us.ihmc.psyonicros2.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import us.ihmc.psyonicros2.AbilityHandController;
import us.ihmc.psyonicros2.AbilityHandController.ControlMode;
import us.ihmc.psyonicros2.AbilityHandController.Grip;
import us.ihmc.psyonicros2.AbilityHandCommandType;
import us.ihmc.robotics.robotSide.RobotSide;

import static org.junit.jupiter.api.Assertions.*;

public class AbilityHandControllerTest
{
   private TestAbilityHand testHand;
   private AbilityHandController controller;

   @BeforeEach
   public void setUp()
   {
      testHand = new TestAbilityHand("24ABH374", RobotSide.RIGHT);
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
      for(int i = 0; i < positions.length; i++)
      {
         assertEquals(positions[i], testHand.getCommandValue(i), 1e-6f);
      }
   }

   @Test
   public void testVelocityControl()
   {
      float[] velocities = {1f, 2f, 3f, 4f, 5f, -5f};
      controller.setControlMode(ControlMode.VELOCITY);
      controller.setGoalVelocities(velocities);

      controller.update();

      assertEquals(AbilityHandCommandType.VELOCITY, testHand.getCommandType());
      for(int i = 0; i < velocities.length; i++)
      {
         assertEquals(velocities[i], testHand.getCommandValue(i), 1e-6f);
      }
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
      assertTrue(testHand.getCommandValue(0) > 0, "Index 0 should move positively");
      assertTrue(testHand.getCommandValue(1) < 0, "Index 1 should move negatively");
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

      // Thumb index 4 should be zero (already clear) and stage 0 should run:
      for (int i = 0; i < 4; i++)
         assertTrue(testHand.getCommandValue(i) > 0, "Finger " + i + " should start closing");
      assertEquals(0f, testHand.getCommandValue(4), 1e-6f, "Thumb should not move on clear");
      assertEquals(0f, testHand.getCommandValue(5), 1e-6f, "Pinky shouldn't move in stage 0");
   }
}
