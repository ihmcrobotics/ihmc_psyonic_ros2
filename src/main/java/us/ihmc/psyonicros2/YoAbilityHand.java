package us.ihmc.psyonicros2;

import us.ihmc.robotics.robotSide.RobotSide;
import us.ihmc.yoVariables.registry.YoRegistry;
import us.ihmc.yoVariables.variable.YoDouble;
import us.ihmc.yoVariables.variable.YoEnum;

/**
 * A basic YoVariable-ized implementation of the {@link AbilityHandInterface}.
 * Can be useful in some applications, though it may be necessary to create
 * a custom implementation for other applications.
 */
public class YoAbilityHand implements AbilityHandInterface
{
   private final RobotSide handSide;
   private final YoEnum<AbilityHandCommandType> commandType;
   private final YoDouble[] commandValues = new YoDouble[ACTUATOR_COUNT];
   private final YoDouble[] actuatorPositions = new YoDouble[ACTUATOR_COUNT];

   public YoAbilityHand(YoRegistry registry, RobotSide handSide)
   {
      this.handSide = handSide;

      String prefix = handSide.name() + "AbilityHand";
      commandType = new YoEnum<>(prefix + "CommandType", registry, AbilityHandCommandType.class);
      commandType.set(AbilityHandCommandType.POSITION);

      for (int i = 0; i < ACTUATOR_COUNT; ++i)
      {
         commandValues[i] = new YoDouble(prefix + "Command" + i, registry);
         commandValues[i].set(0.0);

         actuatorPositions[i] = new YoDouble(prefix + "ActuatorPosition" + i, registry);
         actuatorPositions[i].set(0.0);
      }
   }

   @Override
   public RobotSide getHandSide()
   {
      return handSide;
   }

   @Override
   public AbilityHandCommandType getCommandType()
   {
      return commandType.getValue();
   }

   @Override
   public void setCommandType(AbilityHandCommandType commandType)
   {
      this.commandType.set(commandType);
   }

   @Override
   public float getCommandValue(int index)
   {
      return (float) commandValues[index].getValue();
   }

   @Override
   public void setCommandValue(int index, float value)
   {
      commandValues[index].set(value);
   }

   @Override
   public float getActuatorPosition(int index)
   {
      return (float) actuatorPositions[index].getValue();
   }

   @Override
   public void setActuatorPosition(int index, float value)
   {
      actuatorPositions[index].set(value);
   }
}
