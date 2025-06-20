package us.ihmc.psyonicros2.test;

import us.ihmc.psyonicros2.AbilityHandCommandType;
import us.ihmc.psyonicros2.AbilityHandInterface;
import us.ihmc.robotics.robotSide.RobotSide;

class TestAbilityHand implements AbilityHandInterface
{
   private final RobotSide handSide;
   private AbilityHandCommandType commandType;
   private final float[] commandValues = new float[ACTUATOR_COUNT];
   private final float[] fingerPositions = new float[ACTUATOR_COUNT];


   public TestAbilityHand(RobotSide handSide)
   {
      this.handSide = handSide;
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
   public float[] getCommandValues()
   {
      return commandValues;
   }

   @Override
   public float[] getFingerPositionsDegrees()
   {
      return fingerPositions;
   }
}
