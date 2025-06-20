package us.ihmc.psyonicros2;

import ihmc_psyonic_ros2.msg.dds.AbilityHandCommand;
import ihmc_psyonic_ros2.msg.dds.AbilityHandState;
import us.ihmc.communication.packets.Packet;
import us.ihmc.concurrent.ConcurrentRingBuffer;
import us.ihmc.pubsub.subscriber.Subscriber;
import us.ihmc.robotics.robotSide.RobotSide;
import us.ihmc.ros2.NewMessageListener;

import java.util.function.Supplier;

public class AbilityHandMessageListener<T extends Packet<T>> implements NewMessageListener<T>
{
   private static final int QUEUE_SIZE = 5;

   private final T message;
   private final ConcurrentRingBuffer<T> leftMessageQueue;
   private final ConcurrentRingBuffer<T> rightMessageQueue;

   public AbilityHandMessageListener(Supplier<T> newMessageInstantiator)
   {
      message = newMessageInstantiator.get();

      if (!(message instanceof AbilityHandCommand || message instanceof AbilityHandState))
         throw new IllegalArgumentException("Only AbilityHandCommand or AbilityHandState message types allowed.");
      
      leftMessageQueue = new ConcurrentRingBuffer<>(newMessageInstantiator::get, QUEUE_SIZE);
      rightMessageQueue = new ConcurrentRingBuffer<>(newMessageInstantiator::get, QUEUE_SIZE);
   }

   public boolean poll(T data, RobotSide handSide)
   {
      ConcurrentRingBuffer<T> commandQueue = getMessageQueue(handSide);
      if (commandQueue.poll())
      {
         T next = commandQueue.read();
         data.set(next);
         commandQueue.flush();
         return true;
      }
      else
      {
         return false;
      }
   }

   public boolean peek(T data, RobotSide handSide)
   {
      ConcurrentRingBuffer<T> commandQueue = getMessageQueue(handSide);
      if(commandQueue.poll())
      {
         T next = commandQueue.peek();
         data.set(next);
         return true;
      }
      else
      {
         return false;
      }
   }

   public boolean flushAndGetLatest(T data, RobotSide handSide)
   {
      ConcurrentRingBuffer<T> commandQueue = getMessageQueue(handSide);
      if (commandQueue.poll())
      {
         T latest = commandQueue.read();
         T next;
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
   public void onNewDataMessage(@SuppressWarnings("deprecation") Subscriber<T> subscriber)
   {
      subscriber.takeNextData(message, null);

      ConcurrentRingBuffer<T> commandQueue= getMessageQueue(readHandSide(message));
      T messageToPack = commandQueue.next();
      if (messageToPack != null)
      {
         messageToPack.set(message);
         commandQueue.commit();
      }
   }

   private ConcurrentRingBuffer<T> getMessageQueue(RobotSide handSide)
   {
      if (RobotSide.LEFT.equals(handSide))
      {
         return leftMessageQueue;
      }
      else if (RobotSide.RIGHT.equals(handSide))
      {
         return rightMessageQueue;
      }

      throw new IllegalArgumentException("Only LEFT or RIGHT hand side can be accepted.");
   }

   private RobotSide readHandSide(T message)
   {
      if (message instanceof AbilityHandCommand commandMessage)
         return RobotSide.fromByte(commandMessage.getHandSide());
      else if (message instanceof AbilityHandState stateMessage)
         return RobotSide.fromByte(stateMessage.getHandSide());
      else
         throw new IllegalArgumentException("Only AbilityHandCommand or AbilityHandState message types allowed.");

   }
}
