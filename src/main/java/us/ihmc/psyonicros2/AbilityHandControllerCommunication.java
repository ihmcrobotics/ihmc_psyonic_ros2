package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import us.ihmc.ros2.ROS2NodeBuilder;
import us.ihmc.ros2.ROS2Publisher;
import us.ihmc.ros2.ROS2Subscription;
import us.ihmc.ros2.RealtimeROS2Node;

/**
 * <p>Hardware side ROS 2 communication for the {@link AbilityHandInterface}. Communicates with external controller.</p>
 * <p>Subscribes to {@link AbilityHandCommand} messages and publishes {@link AbilityHandState} messages.</p>
 */
public class AbilityHandControllerCommunication
{
   private final RealtimeROS2Node node;

   private final AbilityHandState stateMessage;
   private final ROS2Publisher<AbilityHandState> statePublisher;

   private final AbilityHandCommand commandMessage;
   private final AbilityHandMessageListener<AbilityHandCommand> commandListener;
   private final ROS2Subscription<AbilityHandCommand> commandSubscription;

   public AbilityHandControllerCommunication(String nodeName)
   {
      node = new ROS2NodeBuilder().buildRealtime(nodeName);

      stateMessage = new AbilityHandState();
      statePublisher = node.createPublisher(AbilityHandROS2API.STATE_TOPIC);

      commandMessage = new AbilityHandCommand();
      commandListener = new AbilityHandMessageListener<>(AbilityHandCommand::new);
      commandSubscription = node.createSubscription(AbilityHandROS2API.COMMAND_TOPIC, commandListener);
   }

   /**
    * Read the latest command into the hand object.
    *
    * @param handToPack Hand object to pack with the latest command.
    */
   public void readCommand(AbilityHandInterface handToPack)
   {
      if (commandListener.flushAndGetLatest(commandMessage, handToPack.getHandSide()))
      {
         handToPack.setCommandType(AbilityHandCommandType.fromByte(commandMessage.getCommandType()));
         handToPack.setCommandValues(commandMessage.getCommandValues());
      }
   }

   /**
    * Publish the hand's state.
    *
    * @param handToPublish The hand to publish.
    */
   public void publishState(AbilityHandInterface handToPublish)
   {
      stateMessage.setHandSide(handToPublish.getHandSide().toByte());
      System.arraycopy(handToPublish.getFingerPositionsDegrees(), 0, stateMessage.getFingerPositionsDegrees(), 0, AbilityHandInterface.ACTUATOR_COUNT);

      statePublisher.publish(stateMessage);
   }

   /**
    * Initialize the communication. No messages will be received or published until this method is called.
    */
   public void start()
   {
      node.spin();
   }

   /**
    * Shut the communications down. Messages will no longer be received or published.
    */
   public void shutdown()
   {
      node.stopSpinning();

      statePublisher.remove();
      commandSubscription.remove();

      node.destroy();
   }
}
