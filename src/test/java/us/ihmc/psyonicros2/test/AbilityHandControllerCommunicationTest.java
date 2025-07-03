package us.ihmc.psyonicros2.test;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import org.junit.jupiter.api.Test;
import us.ihmc.psyonicros2.AbilityHandCommandType;
import us.ihmc.psyonicros2.AbilityHandController;
import us.ihmc.psyonicros2.AbilityHandController.ControlMode;
import us.ihmc.psyonicros2.AbilityHandControllerCommunication;
import us.ihmc.psyonicros2.AbilityHandInterface;
import us.ihmc.psyonicros2.AbilityHandROS2API;
import us.ihmc.robotics.robotSide.RobotSide;
import us.ihmc.ros2.ROS2Node;
import us.ihmc.ros2.ROS2NodeBuilder;
import us.ihmc.ros2.ROS2Publisher;
import us.ihmc.ros2.ROS2Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.*;

public class AbilityHandControllerCommunicationTest
{
   @Test
   public void testCommunication()
   {
      final RobotSide handSide = RobotSide.LEFT;
      final AbilityHandCommandType commandType = AbilityHandCommandType.POSITION;

      final float[] commandValues = new float[] {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, -5.0f};

      final float[] actuatorPositions = new float[] {5.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f};
      final String serialNumber = "24ABH374";

      ROS2Node node = new ROS2NodeBuilder().build("abilityTestNode");

      AbilityHandCommand command = new AbilityHandCommand();
      command.setSerialNumber(serialNumber);
      command.setControlMode(ControlMode.POSITION.toByte());
      System.arraycopy(commandValues, 0, command.getGoalPositions(), 0, commandValues.length);
      ROS2Publisher<AbilityHandCommand> publisher = node.createPublisher(AbilityHandROS2API.COMMAND_TOPIC);

      AtomicBoolean received = new AtomicBoolean(false);
      AbilityHandState stateReceived = new AbilityHandState();
      ROS2Subscription<AbilityHandState> subscription = node.createSubscription2(AbilityHandROS2API.STATE_TOPIC, stateMessage ->
      {
         received.set(true);
         stateReceived.set(stateMessage);
      });

      TestAbilityHand testHand = new TestAbilityHand(serialNumber, handSide);
      AbilityHandController controller = new AbilityHandController(testHand);
      testHand.setActuatorPositions(actuatorPositions);

      AbilityHandControllerCommunication controllerCommunication = new AbilityHandControllerCommunication("test_controller_comm");

      // Publish before starting. Nothing should happen
      publisher.publish(command);
      controllerCommunication.publishState(controller);

      // Read values
      controllerCommunication.readCommand(controller);
      controller.update();

      assertFalse(received.get());

      for (int i = 0; i < AbilityHandInterface.ACTUATOR_COUNT; ++i)
      {
         assertEquals(0, testHand.getCommandValue(i));
      }
      assertEquals(AbilityHandCommandType.POSITION, testHand.getCommandType());

      // Start and publish again. Should receive message
      controllerCommunication.start();

      LockSupport.parkNanos((long) 1E9);

      publisher.publish(command);
      controllerCommunication.publishState(controller);

      LockSupport.parkNanos((long) 1E9);

      // Read values
      controllerCommunication.readCommand(controller);
      controller.update();

      assertTrue(received.get());

      for (int i = 0; i < AbilityHandInterface.ACTUATOR_COUNT; ++i)
      {
         assertEquals(testHand.getActuatorPosition(i), stateReceived.getActuatorPositions()[i]);
         assertEquals(commandValues[i], testHand.getCommandValue(i));
      }
      assertEquals(commandType, testHand.getCommandType());
      assertEquals(serialNumber, stateReceived.getSerialNumberAsString());

      // Shut things down
      controllerCommunication.shutdown();
   }
}
