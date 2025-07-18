package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import org.junit.jupiter.api.Test;
import us.ihmc.psyonicros2.AbilityHandManager.ControlMode;
import us.ihmc.robotics.robotSide.RobotSide;
import us.ihmc.ros2.ROS2Node;
import us.ihmc.ros2.ROS2NodeBuilder;
import us.ihmc.ros2.ROS2Publisher;
import us.ihmc.ros2.ROS2Subscription;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

import static org.junit.jupiter.api.Assertions.*;
import static us.ihmc.psyonicros2.AbilityHandInterface.ACTUATOR_COUNT;

public class AbilityHandROS2CommunicationTest
{
   private static final AtomicInteger nextDomainId = new AtomicInteger(0);

   @Test
   public void testControllerCommunication() throws InterruptedException
   {
      final int domainId = nextDomainId.getAndIncrement();
      final RobotSide HAND_SIDE = RobotSide.LEFT;
      final AbilityHandCommandType COMMAND_TYPE = AbilityHandCommandType.POSITION;
      final float[] COMMAND_VALUES = new float[] {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, -5.0f};
      final float[] ACTUATOR_POSITIONS = new float[] {5.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f};
      final String SERIAL_NUMBER = "24ABH000";

      // Create a node
      ROS2Node node = new ROS2NodeBuilder().domainId(domainId).build("abilityTestNode");

      // Create a command message and its publisher
      AbilityHandCommand command = new AbilityHandCommand();
      command.setSerialNumber(SERIAL_NUMBER);
      command.setControlMode(ControlMode.POSITION.toByte());
      System.arraycopy(COMMAND_VALUES, 0, command.getGoalPositions(), 0, ACTUATOR_COUNT);
      ROS2Publisher<AbilityHandCommand> publisher = node.createPublisher(AbilityHandROS2API.COMMAND_TOPIC);

      // Create a stats subscription
      AtomicBoolean received = new AtomicBoolean(false);
      AbilityHandState stateReceived = new AbilityHandState();
      ROS2Subscription<AbilityHandState> subscription = node.createSubscription2(AbilityHandROS2API.STATE_TOPIC, stateMessage ->
      {
         synchronized (received)
         {
            received.set(true);
            received.notify();
         }
         stateReceived.set(stateMessage);
      });

      // Initialize a test hand and its manager
      TestAbilityHand testHand = new TestAbilityHand(SERIAL_NUMBER, HAND_SIDE);
      AbilityHandManager manager = new AbilityHandManager(testHand);
      testHand.setActuatorPositions(ACTUATOR_POSITIONS); // Set the state of the hand

      // Create an instance of the communication class
      AbilityHandROS2ControllerCommunication controllerCommunication = new AbilityHandROS2ControllerCommunication("test_controller_comm", domainId);

      // Publish before starting. Nothing should happen
      publisher.publish(command);
      controllerCommunication.publishState(manager);

      // Read values
      controllerCommunication.readCommand(manager);
      manager.update();

      // Assert that no messages were received
      assertFalse(received.get());

      // Start and publish again. Should receive message
      controllerCommunication.start();
      LockSupport.parkNanos((long) 1E9);

      publisher.publish(command);
      controllerCommunication.publishState(manager);

      // Wait for the state message to be received
      synchronized (received)
      {
         if (!received.get())
            received.wait(1000);
      }

      // Read values
      controllerCommunication.readCommand(manager);
      manager.update();

      // Assert that the messages were received
      assertTrue(received.get());
      for (int i = 0; i < ACTUATOR_COUNT; ++i)
      {
         assertEquals(testHand.getActuatorPosition(i), stateReceived.getActuatorPositions()[i]);
         assertEquals(COMMAND_VALUES[i], testHand.getCommandValue(i));
      }
      assertEquals(COMMAND_TYPE, testHand.getCommandType());
      assertEquals(SERIAL_NUMBER, stateReceived.getSerialNumberAsString());

      // Shut things down
      controllerCommunication.shutdown();
      subscription.remove();
      publisher.remove();
      node.destroy();
   }

   @Test
   public void testHardwareCommunication() throws InterruptedException
   {
      final int domainId = nextDomainId.getAndIncrement();
      final RobotSide HAND_SIDE = RobotSide.LEFT;
      final ControlMode CONTROL_MODE = ControlMode.POSITION;
      final float[] GOAL_POSITIONS = new float[] {0.0f, 1.0f, 2.0f, 3.0f, 4.0f, -5.0f};
      final float[] ACTUATOR_POSITIONS = new float[] {5.0f, 4.0f, 3.0f, 2.0f, 1.0f, 0.0f};
      final String SERIAL_NUMBER = "24ABH000";

      // Create a node
      ROS2Node node = new ROS2NodeBuilder().domainId(domainId).build("abilityTestNode");

      // Create a state message and its publisher
      AbilityHandState state = new AbilityHandState();
      state.setSerialNumber(SERIAL_NUMBER);
      state.setHandSide(HAND_SIDE.toByte());
      System.arraycopy(ACTUATOR_POSITIONS, 0, state.getActuatorPositions(), 0, ACTUATOR_COUNT);
      ROS2Publisher<AbilityHandState> statePublisher = node.createPublisher(AbilityHandROS2API.STATE_TOPIC);

      // Create a subscription to command messages
      AtomicBoolean received = new AtomicBoolean(false);
      AbilityHandCommand commandReceived = new AbilityHandCommand();
      ROS2Subscription<AbilityHandCommand> subscription = node.createSubscription2(AbilityHandROS2API.COMMAND_TOPIC, commandMessage ->
      {
         synchronized (received)
         {
            received.set(true);
            received.notify();
         }
         commandReceived.set(commandMessage);
      });

      // Create the communication instance
      AbilityHandROS2HardwareCommunication communication = new AbilityHandROS2HardwareCommunication("test_hardware_comm", domainId);

      // Publish before starting. Nothing should happen
      statePublisher.publish(state);
      LockSupport.parkNanos((long) 1E9);

      // Communication shouldn't have received any messages
      assertNull(communication.readState(SERIAL_NUMBER));
      assertTrue(communication.getAvailableHandSerialNumbers().isEmpty());
      assertNull(communication.getCommand(SERIAL_NUMBER));
      assertFalse(communication.publishCommand(SERIAL_NUMBER));

      // Start the communication
      communication.start();
      LockSupport.parkNanos((long) 1E9);

      // Publish a state message
      statePublisher.publish(state);
      LockSupport.parkNanos((long) 1E9);

      // Now the communications class should have received the state message
      assertEquals(1, communication.getAvailableHandSerialNumbers().size());
      assertEquals(SERIAL_NUMBER, communication.getAvailableHandSerialNumbersList().get(0));

      // Assert the state received is correct
      AbilityHandState stateReceived = communication.readState(SERIAL_NUMBER);
      assertNotNull(stateReceived);
      assertEquals(SERIAL_NUMBER, stateReceived.getSerialNumberAsString());
      assertEquals(HAND_SIDE, RobotSide.fromByte(stateReceived.getHandSide()));
      assertArrayEquals(ACTUATOR_POSITIONS, stateReceived.getActuatorPositions());

      // Make sure the communications created a command message for the hand
      assertNotNull(communication.getCommand(SERIAL_NUMBER));
      assertEquals(SERIAL_NUMBER, communication.getCommand(SERIAL_NUMBER).getSerialNumberAsString());

      // Set the command message and publish it
      communication.getCommand(SERIAL_NUMBER).setControlMode(CONTROL_MODE.toByte());
      System.arraycopy(GOAL_POSITIONS, 0, communication.getCommand(SERIAL_NUMBER).getGoalPositions(), 0, ACTUATOR_COUNT);
      communication.publishCommand(SERIAL_NUMBER);

      // Wait for the subscription to receive it
      synchronized (received)
      {
         if (!received.get())
            received.wait(1000);
      }

      // Make sure subscription received it correctly
      assertTrue(received.get());
      assertEquals(SERIAL_NUMBER, commandReceived.getSerialNumberAsString());
      assertEquals(CONTROL_MODE, ControlMode.fromByte(commandReceived.getControlMode()));
      assertArrayEquals(GOAL_POSITIONS, commandReceived.getGoalPositions());

      // Shut things down
      communication.shutdown();
      statePublisher.remove();
      subscription.remove();
      node.destroy();
   }
}
