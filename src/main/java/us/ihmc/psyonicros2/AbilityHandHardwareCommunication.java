package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import us.ihmc.ros2.ROS2NodeBuilder;
import us.ihmc.ros2.ROS2Publisher;
import us.ihmc.ros2.ROS2Subscription;
import us.ihmc.ros2.RealtimeROS2Node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>Controller side ROS 2 communication for the {@link AbilityHandInterface}. Communicates with low-level hardware control process.</p>
 * <p>Subscribes to {@link AbilityHandState} messages and publishes {@link AbilityHandCommand} messages.</p>
 */
public class AbilityHandHardwareCommunication
{
   private final RealtimeROS2Node node;

   private final AbilityHandMessageListener<AbilityHandState> stateListener;
   private final ROS2Subscription<AbilityHandState> stateSubscription;

   private final HashMap<String, AbilityHandCommand> commandMessages;
   private final ROS2Publisher<AbilityHandCommand> commandPublisher;

   private final Set<String> registeredHandSerialNumbers;

   public AbilityHandHardwareCommunication(String nodeName)
   {
      node = new ROS2NodeBuilder().buildRealtime(nodeName);

      stateListener = new AbilityHandMessageListener<>(AbilityHandState::new);
      stateListener.onNewHandRegistered(this::registerNewHand);
      stateSubscription = node.createSubscription(AbilityHandROS2API.STATE_TOPIC, stateListener);

      commandMessages = new HashMap<>();
      commandPublisher = node.createPublisher(AbilityHandROS2API.COMMAND_TOPIC);

      registeredHandSerialNumbers = new HashSet<>();
   }

   private void registerNewHand(StringBuilder newHandSerialNumber)
   {
      String serialNumber = newHandSerialNumber.toString();
      registeredHandSerialNumbers.add(serialNumber);
      commandMessages.put(serialNumber, new AbilityHandCommand());
   }

   public Set<String> getAvailableHandSerialNumbers()
   {
      return registeredHandSerialNumbers;
   }

   /**
    * Read the latest state message.
    *
    * @param messageToPack Message to pack with the latest state.
    */
   public boolean readState(String serialNumber, AbilityHandState messageToPack)
   {
      return stateListener.readLatestMessage(serialNumber, messageToPack);
   }

   public AbilityHandState readState(String serialNumber)
   {
      AbilityHandState stateMessage = new AbilityHandState();
      if (stateListener.readLatestMessage(serialNumber, stateMessage))
         return stateMessage;

      return null;
   }

   public AbilityHandCommand getCommand(String handSerialNumber)
   {
      return commandMessages.get(handSerialNumber);
   }

   /**
    * Publish the command.
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
