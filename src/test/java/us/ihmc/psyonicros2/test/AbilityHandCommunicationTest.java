package us.ihmc.psyonicros2.test;

import org.junit.jupiter.api.Test;
import us.ihmc.psyonicros2.AbilityHandCommandType;
import us.ihmc.psyonicros2.AbilityHandControllerCommunication;
import us.ihmc.psyonicros2.AbilityHandHardwareCommunication;
import us.ihmc.psyonicros2.AbilityHandInterface;
import us.ihmc.robotics.robotSide.RobotSide;

import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.*;

public class AbilityHandCommunicationTest
{
   @Test
   public void testCommunication()
   {
      final RobotSide handSide = RobotSide.LEFT;
      final AbilityHandCommandType commandType = AbilityHandCommandType.POSITION;

      final float[] commandValues = new float[] {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, -5.0f};
      TestAbilityHand commandHand = new TestAbilityHand(handSide);
      commandHand.setCommandType(commandType);
      commandHand.setCommandValues(commandValues);

      final float[] actuatorPositions = new float[] {5.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f};
      TestAbilityHand stateHand = new TestAbilityHand(handSide);
      stateHand.setActuatorPositions(actuatorPositions);

      AbilityHandHardwareCommunication hardwareCommunication = new AbilityHandHardwareCommunication("test_hardware_comm");
      AbilityHandControllerCommunication controllerCommunication = new AbilityHandControllerCommunication("test_controller_comm");

      // Publish before starting. Nothing should happen
      hardwareCommunication.publishCommand(commandHand);
      controllerCommunication.publishState(stateHand);

      // Read values
      hardwareCommunication.readState(commandHand);
      controllerCommunication.readCommand(stateHand);

      assertFalse(hardwareCommunication.hasReceivedFirstState());

      for (int i = 0; i < AbilityHandInterface.ACTUATOR_COUNT; ++i)
      {
         assertEquals(0, commandHand.getActuatorPosition(i));
         assertEquals(0, stateHand.getCommandValue(i));
      }
      assertNull(stateHand.getCommandType());

      // Start and publish again. Should receive message
      hardwareCommunication.start();
      controllerCommunication.start();

      LockSupport.parkNanos((long) 1E9);

      hardwareCommunication.publishCommand(commandHand);
      controllerCommunication.publishState(stateHand);

      LockSupport.parkNanos((long) 1E9);

      // Read values
      hardwareCommunication.readState(commandHand);
      controllerCommunication.readCommand(stateHand);

      assertTrue(hardwareCommunication.hasReceivedFirstState());

      for (int i = 0; i < AbilityHandInterface.ACTUATOR_COUNT; ++i)
      {
         assertEquals(stateHand.getActuatorPosition(i), commandHand.getActuatorPosition(i));
         assertEquals(commandHand.getCommandValue(i), stateHand.getCommandValue(i));
      }
      assertEquals(commandHand.getCommandType(), stateHand.getCommandType());

      // Shut things down
      hardwareCommunication.shutdown();
      controllerCommunication.shutdown();
   }
}
