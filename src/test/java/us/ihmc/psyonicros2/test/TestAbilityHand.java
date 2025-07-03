package us.ihmc.psyonicros2.test;

import us.ihmc.psyonicros2.AbilityHandCommandType;
import us.ihmc.psyonicros2.AbilityHandInterface;
import us.ihmc.robotics.robotSide.RobotSide;

class TestAbilityHand implements AbilityHandInterface
{
   private final String serialNumber;
   private final RobotSide handSide;
   private AbilityHandCommandType commandType;
   private final float[] commandValues = new float[ACTUATOR_COUNT];
   private final float[] fingerPositions = new float[ACTUATOR_COUNT];

   public TestAbilityHand(String serialNumber, RobotSide handSide)
   {
      this.serialNumber = serialNumber;
      this.handSide = handSide;
   }

   @Override
   public String getSerialNumber()
   {
      return serialNumber;
   }

   @Override
   public RobotSide getHandSide()
   {
      return handSide;
   }

   @Override
   public AbilityHandCommandType getCommandType()
   {
      return commandType;
   }

   @Override
   public void setCommandType(AbilityHandCommandType commandType)
   {
      this.commandType = commandType;
   }

   @Override
   public float getCommandValue(int index)
   {
      return commandValues[index];
   }

   @Override
   public void setCommandValue(int index, float value)
   {
      commandValues[index] = value;
   }

   @Override
   public float getActuatorPosition(int index)
   {
      return fingerPositions[index];
   }

   @Override
   public void setActuatorPosition(int index, float value)
   {
      fingerPositions[index] = value;
   }
}
