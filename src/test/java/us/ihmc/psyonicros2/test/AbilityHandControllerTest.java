package us.ihmc.psyonicros2.test;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import us.ihmc.psyonicros2.AbilityHandCommandType;
import us.ihmc.psyonicros2.AbilityHandController;
import us.ihmc.psyonicros2.AbilityHandController.ControlMode;
import us.ihmc.psyonicros2.AbilityHandController.Grip;
import us.ihmc.psyonicros2.AbilityHandInterface;
import us.ihmc.psyonicros2.YoAbilityHand;
import us.ihmc.psyonicros2.YoAbilityHandController;
import us.ihmc.robotics.robotSide.RobotSide;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class AbilityHandControllerTest
{
   private static Stream<Arguments> getControllers()
   {
      TestAbilityHand abilityHand = new TestAbilityHand("24ABH000", RobotSide.LEFT);
      AbilityHandController controller = new AbilityHandController(abilityHand);

      YoAbilityHand yoAbilityHand = new YoAbilityHand(null, "24ABH001", RobotSide.RIGHT);
      YoAbilityHandController yoController = new YoAbilityHandController(null, yoAbilityHand);

      return Stream.of(Arguments.of(controller, abilityHand), Arguments.of(yoController, yoAbilityHand));
   }

   @ParameterizedTest
   @MethodSource("getControllers")
   public void testPositionControl(AbilityHandController controller, AbilityHandInterface hand)
   {
      float[] positions = {10f, 20f, 30f, 40f, 50f, -10f};
      controller.setControlMode(ControlMode.POSITION);
      controller.setGoalPositions(positions);

      controller.update();

      assertEquals(AbilityHandCommandType.POSITION, hand.getCommandType());
      for(int i = 0; i < positions.length; i++)
      {
         assertEquals(positions[i], hand.getCommandValue(i), 1e-6f);
      }
   }

   @ParameterizedTest
   @MethodSource("getControllers")
   public void testVelocityControl(AbilityHandController controller, AbilityHandInterface hand)
   {
      float[] velocities = {1f, 2f, 3f, 4f, 5f, -5f};
      controller.setControlMode(ControlMode.VELOCITY);
      controller.setGoalVelocities(velocities);

      controller.update();

      assertEquals(AbilityHandCommandType.VELOCITY, hand.getCommandType());
      for(int i = 0; i < velocities.length; i++)
      {
         assertEquals(velocities[i], hand.getCommandValue(i), 1e-6f);
      }
   }

   @ParameterizedTest
   @MethodSource("getControllers")
   public void testVelToPosControl(AbilityHandController controller, AbilityHandInterface hand)
   {
      // current < goal => positive, current > goal => negative
      float[] current = {5f, 50f, 0f, 0f, 0f, 0f};
      float[] goals = {10f, 20f, 0f, 0f, 0f, 0f};
      float[] speeds = {2f, 3f, 0f, 0f, 0f, 0f};

      hand.setActuatorPositions(current);
      controller.setControlMode(ControlMode.VEL_TO_POS);
      controller.setGoalPositions(goals);
      controller.setGoalVelocities(speeds);

      controller.update();

      assertEquals(AbilityHandCommandType.VELOCITY, hand.getCommandType());
      assertTrue(hand.getCommandValue(0) > 0, "Index 0 should move positively");
      assertTrue(hand.getCommandValue(1) < 0, "Index 1 should move negatively");
   }

   @ParameterizedTest
   @MethodSource("getControllers")
   public void testGripInitialThumbStage(AbilityHandController controller, AbilityHandInterface hand)
   {
      // Thumb (index 4) must clear first: simulate it at the clear position already
      float[] current = {30f, 30f, 30f, 30f, 30f, 30f};
      hand.setActuatorPositions(current);

      controller.setGoalVelocities(new float[] {5f, 5f, 5f, 5f, 5f, 5f});
      controller.setControlMode(ControlMode.GRIP);
      controller.setGrip(Grip.POWER);

      controller.update();

      assertEquals(AbilityHandCommandType.VELOCITY, hand.getCommandType());

      // Thumb index 4 should be zero (already clear) and stage 0 should run:
      for (int i = 0; i < 4; i++)
         assertTrue(hand.getCommandValue(i) > 0, "Finger " + i + " should start closing");
      assertEquals(0f, hand.getCommandValue(4), 1e-6f, "Thumb should not move on clear");
      assertEquals(0f, hand.getCommandValue(5), 1e-6f, "Pinky shouldn't move in stage 0");
   }
}
