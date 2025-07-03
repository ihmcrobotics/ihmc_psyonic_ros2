package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import us.ihmc.ros2.ROS2NodeBuilder;
import us.ihmc.ros2.ROS2Publisher;
import us.ihmc.ros2.ROS2Subscription;
import us.ihmc.ros2.RealtimeROS2Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>High level ROS 2 communication for the {@link AbilityHandInterface}. Communicates with low-level hardware control process.</p>
 * <p>Subscribes to {@link AbilityHandState} messages and publishes {@link AbilityHandCommand} messages.</p>
 */
public class AbilityHandHardwareCommunication
{
   private final RealtimeROS2Node node;

   private final AbilityHandMessageListener<AbilityHandState> stateListener;
   private final ROS2Subscription<AbilityHandState> stateSubscription;

   private final Map<String, AbilityHandCommand> commandMessages;
   private final ROS2Publisher<AbilityHandCommand> commandPublisher;

   private final List<String> registeredHandSerialNumbers;

   public AbilityHandHardwareCommunication(String nodeName)
   {
      node = new ROS2NodeBuilder().buildRealtime(nodeName);

      stateListener = new AbilityHandMessageListener<>(AbilityHandState::new);
      stateListener.onNewHandRegistered(this::registerNewHand);
      stateSubscription = node.createSubscription(AbilityHandROS2API.STATE_TOPIC, stateListener);

      commandMessages = new HashMap<>();
      commandPublisher = node.createPublisher(AbilityHandROS2API.COMMAND_TOPIC);

      registeredHandSerialNumbers = new ArrayList<>(2);
   }

   private void registerNewHand(StringBuilder newHandSerialNumber)
   {
      String serialNumber = newHandSerialNumber.toString();
      registeredHandSerialNumbers.add(serialNumber);
      commandMessages.put(serialNumber, new AbilityHandCommand());
   }

   /**
    * <p>Get the serial numbers of the available hands.</p>
    * <p>Treat the list as read-only.</p>
    *
    * @return List of serial numbers of the available hands.
    */
   public List<String> getAvailableHandSerialNumbers()
   {
      return registeredHandSerialNumbers;
   }

   /**
    * Read the latest state message of the specified hand.
    *
    * @param messageToPack Message to pack with the latest state.
    */
   public boolean readState(String serialNumber, AbilityHandState messageToPack)
   {
      return stateListener.readLatestMessage(serialNumber, messageToPack);
   }

   /**
    * Read the latest state message of the specified hand.
    *
    * @param serialNumber Serial number specifying the hand.
    * @return A copy of the latest state message.
    */
   public AbilityHandState readState(String serialNumber)
   {
      AbilityHandState stateMessage = new AbilityHandState();
      if (stateListener.readLatestMessage(serialNumber, stateMessage))
         return stateMessage;

      return null;
   }

   /**
    * <p>Get the command message for the specified hand.</p>.
    * <p>Use this method to set the desired command values.
    * Then publish the command using {@link #publishCommand(String)}</p>
    *
    * @param handSerialNumber Serial number specifying the hand.
    * @return A reference to the command message for the specified hand.
    */
   public AbilityHandCommand getCommand(String handSerialNumber)
   {
      return commandMessages.get(handSerialNumber);
   }

   /**
    * Publish the command for the specified hand.
    *
    * @param handSerialNumber Serial number specifying the hand.
    * @return {@code true} if the message was published. {@code false} if the hand specified wasn't found.
    */
   public boolean publishCommand(String handSerialNumber)
   {
      AbilityHandCommand commandMessage = commandMessages.get(handSerialNumber);
      if (commandMessage != null)
      {
         commandPublisher.publish(commandMessage);
         return true;
      }

      return false;
   }

   /**
    * Start the communication.
    */
   public void start()
   {
      node.spin();
   }

   /**
    * Stop the communication. {@link #start()} can be called again after this method to re-start communication.
    */
   public void stop()
   {
      node.stopSpinning();
   }

   /**
    * Shut the communication down. {@link #start()} cannot be called again after this method.
    */
   public void shutdown()
   {
      stop();

      commandPublisher.remove();
      stateSubscription.remove();

      node.destroy();
   }
}
