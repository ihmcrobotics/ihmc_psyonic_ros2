package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import us.ihmc.ros2.ROS2NodeBuilder;
import us.ihmc.ros2.ROS2Publisher;
import us.ihmc.ros2.ROS2Subscription;
import us.ihmc.ros2.RealtimeROS2Node;

/**
 * <p>Controller side ROS 2 communication for the {@link AbilityHandInterface}. Communicates with low-level hardware control process.</p>
 * <p>Subscribes to {@link AbilityHandState} messages and publishes {@link AbilityHandCommand} messages.</p>
 */
public class AbilityHandHardwareCommunication
{
   private final RealtimeROS2Node node;

   private final AbilityHandState stateMessage;
   private final AbilityHandMessageListener<AbilityHandState> stateListener;
   private final ROS2Subscription<AbilityHandState> stateSubscription;
   private boolean receivedFirstState = false;

   private final AbilityHandCommand commandMessage;
   private final ROS2Publisher<AbilityHandCommand> commandPublisher;

   public AbilityHandHardwareCommunication(String nodeName)
   {
      node = new ROS2NodeBuilder().buildRealtime(nodeName);

      stateMessage = new AbilityHandState();
      stateListener = new AbilityHandMessageListener<>(AbilityHandState::new);
      stateSubscription = node.createSubscription(AbilityHandROS2API.STATE_TOPIC, stateListener);

      commandMessage = new AbilityHandCommand();
      commandPublisher = node.createPublisher(AbilityHandROS2API.COMMAND_TOPIC);
   }

   /**
    * Read the latest state into the hand object.
    *
    * @param handToPack Hand object to pack with the latest state.
    */
   public void readState(AbilityHandInterface handToPack)
   {
      if (stateListener.flushAndGetLatest(stateMessage, handToPack.getHandSide()))
      {
         handToPack.setFingerPositions(stateMessage.getFingerPositionsDegrees());
         receivedFirstState = true;
      }
   }

   /**
    * Publish the hand's command.
    *
    * @param handToPublish The hand to publish.
    */
   public void publishCommand(AbilityHandInterface handToPublish)
   {
      commandMessage.setHandSide(handToPublish.getHandSide().toByte());
      commandMessage.setCommandType(handToPublish.getCommandType().toByte());
      System.arraycopy(handToPublish.getCommandValues(), 0, commandMessage.getCommandValues(), 0, AbilityHandInterface.ACTUATOR_COUNT);

      commandPublisher.publish(commandMessage);
   }

   /**
    * Whether this object has received a state message.
    *
    * @return {@code true} if a state message has been received.
    */
   public boolean hasReceivedFirstState()
   {
      return receivedFirstState;
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
