package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import us.ihmc.concurrent.ConcurrentRingBuffer;
import us.ihmc.pubsub.subscriber.Subscriber;
import us.ihmc.robotics.robotSide.RobotSide;
import us.ihmc.ros2.NewMessageListener;

public class AbilityHandCommandListener implements NewMessageListener<AbilityHandCommand>
{
   private static final int QUEUE_SIZE = 5;

   private final AbilityHandCommand commandMessage;
   private final ConcurrentRingBuffer<AbilityHandCommand> leftCommandQueue;
   private final ConcurrentRingBuffer<AbilityHandCommand> rightCommandQueue;

   public AbilityHandCommandListener()
   {
      commandMessage = new AbilityHandCommand();

      leftCommandQueue = new ConcurrentRingBuffer<>(AbilityHandCommand::new, QUEUE_SIZE);
      rightCommandQueue = new ConcurrentRingBuffer<>(AbilityHandCommand::new, QUEUE_SIZE);
   }

   public boolean poll(AbilityHandCommand data, RobotSide handSide)
   {
      ConcurrentRingBuffer<AbilityHandCommand> commandQueue = getCommandQueue(handSide);
      if (commandQueue.poll())
      {
         AbilityHandCommand next = commandQueue.read();
         data.set(next);
         commandQueue.flush();
         return true;
      }
      else
      {
         return false;
      }
   }

   public boolean peek(AbilityHandCommand data, RobotSide handSide)
   {
      ConcurrentRingBuffer<AbilityHandCommand> commandQueue = getCommandQueue(handSide);
      if(commandQueue.poll())
      {
         AbilityHandCommand next = commandQueue.peek();
         data.set(next);
         return true;
      }
      else
      {
         return false;
      }
   }

   public boolean flushAndGetLatest(AbilityHandCommand data, RobotSide handSide)
   {
      ConcurrentRingBuffer<AbilityHandCommand> commandQueue = getCommandQueue(handSide);
      if (commandQueue.poll())
      {
         AbilityHandCommand latest = commandQueue.read();
         AbilityHandCommand next;
         while ((next = commandQueue.read()) != null)
         {
            latest = next;
         }
         data.set(latest);
         commandQueue.flush();
         return true;
      }
      else
      {
         return false;
      }
   }
   
   @Override
   public void onNewDataMessage(@SuppressWarnings("deprecation") Subscriber<AbilityHandCommand> subscriber)
   {
      subscriber.takeNextData(commandMessage, null);

      ConcurrentRingBuffer<AbilityHandCommand> commandQueue= getCommandQueue(RobotSide.fromByte(commandMessage.getHandSide()));
      AbilityHandCommand messageToPack = commandQueue.next();
      if (messageToPack != null)
      {
         messageToPack.set(commandMessage);
         commandQueue.commit();
      }
   }

   private ConcurrentRingBuffer<AbilityHandCommand> getCommandQueue(RobotSide handSide)
   {
      if (RobotSide.LEFT.equals(handSide))
      {
         return leftCommandQueue;
      }
      else if (RobotSide.RIGHT.equals(handSide))
      {
         return rightCommandQueue;
      }

      throw new IllegalArgumentException("Only LEFT or RIGHT hand side can be accepted.");
   }
}
