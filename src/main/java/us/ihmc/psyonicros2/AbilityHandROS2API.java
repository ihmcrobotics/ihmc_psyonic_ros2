package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import us.ihmc.ros2.ROS2Topic;

/**
 * Collection of the ROS 2 topics for communicating with the {@code AbilityHand*Communication} classes.
 */
public class AbilityHandROS2API
{
   public static final ROS2Topic<?> ROOT_TOPIC = new ROS2Topic<>().withModule("ability_hand");
   public static final ROS2Topic<AbilityHandState> STATE_TOPIC = ROOT_TOPIC.withSuffix("state").withType(AbilityHandState.class);
   public static final ROS2Topic<AbilityHandCommand> COMMAND_TOPIC = ROOT_TOPIC.withSuffix("command").withType(AbilityHandCommand.class);
}
