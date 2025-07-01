package us.ihmc.psyonicros2;

import us.ihmc.robotics.robotSide.RobotSide;

public class AbilityHandController implements AbilityHandInterface
{

   private enum ControlMode
   {POSITION, VELOCITY}

   private long startTime = 0;
   private ControlMode controlMode = ControlMode.POSITION;
   private float goalPosition = Float.NaN;
   private float goalVelocity = 0.0f;
   private final RobotSide handSide;
   private AbilityHandCommandType commandType = AbilityHandCommandType.POSITION;
   private final float[] commandValues = new float[ACTUATOR_COUNT];
   private final float[] actuatorPositions = new float[ACTUATOR_COUNT];
   private final float[] controlFinger = new float[ACTUATOR_COUNT];

   public AbilityHandController(RobotSide handSide)
   {
      this.handSide = handSide;
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         commandValues[i] = 30.0f;
         if(i==5)
         {
            commandValues[i] = -30.0f;
         }
         actuatorPositions[i] = 30.0f;
         controlFinger[i] = 30.0f;
      }
   }

   public void setAllFingers(float position)
   {
      if (controlMode == ControlMode.VELOCITY)
      {
         return;
      }
      setCommandType(AbilityHandCommandType.POSITION);
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         if (i != 4 && i != 5)
         {
            controlFinger[i] = position;
            setCommandValue(i, position);
         }
         else
         {
            controlFinger[i] = actuatorPositions[i];
            setCommandValue(i, actuatorPositions[i]);
         }
      }
   }

   public void processVelToPos(int excludedIndex)
   {
      if (controlMode == ControlMode.POSITION)
         return;

      if (goalVelocity < 0.0f)
      {
         if (moveSkipped(excludedIndex))
         {
            setCommandType(AbilityHandCommandType.VELOCITY);
            for (int i = 0; i < ACTUATOR_COUNT; i++)
            {
               if (i != excludedIndex)
               {
                  float v = (i == 5) ? -goalVelocity : goalVelocity;
                  setCommandValue(i, v);
               }
               else
               {
                  setCommandValue(i, 0.0f);
               }
            }

            boolean allOthersReached = true;
            for (int i = 0; i < ACTUATOR_COUNT; i++)
            {
               if (i == excludedIndex)
                  continue;
               float pos = getActuatorPosition(i);
               if (i == 5)
                  pos = -pos;
               if ((goalVelocity < 0 && pos > goalPosition) || (goalVelocity > 0 && pos < goalPosition))
               {
                  allOthersReached = false;
                  break;
               }
            }
            if (allOthersReached)
            {
               controlMode = ControlMode.POSITION;
               for(int i = 0; i < ACTUATOR_COUNT; i++)
               {
                  setCommandValue(i, getActuatorPosition(i));
               }
               commandType = AbilityHandCommandType.POSITION;
            }
         }
      }
      else
      {
         setCommandType(AbilityHandCommandType.VELOCITY);
         for (int i = 0; i < ACTUATOR_COUNT; i++)
         {
            if (i != excludedIndex)
            {
               float v = (i == 5) ? -goalVelocity : goalVelocity;
               setCommandValue(i, v);
            }
            else
            {
               setCommandValue(i, 0.0f);
            }
         }

         boolean reached = true;
         for (int i = 0; i < ACTUATOR_COUNT; i++)
         {
            if (i == excludedIndex)
               continue;
            float pos = getActuatorPosition(i);
            if (i == 5)
               pos = -pos;
            if ((goalVelocity > 0 && pos < goalPosition) || (goalVelocity < 0 && pos > goalPosition))
            {
               reached = false;
               break;
            }
         }
         if (reached|| System.currentTimeMillis() - startTime > 5000)
         {
            if (moveSkipped(excludedIndex))
            {
               startTime = System.currentTimeMillis();
               controlMode = ControlMode.POSITION;
               for(int i = 0; i < ACTUATOR_COUNT; i++)
               {
                  setCommandValue(i, getActuatorPosition(i));
               }
               commandType = AbilityHandCommandType.POSITION;
            }
         }
      }
   }

   private boolean moveSkipped(int index)
   {
      setCommandType(AbilityHandCommandType.VELOCITY);
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         if (i == index)
         {
            float v = (i == 5) ? -goalVelocity : goalVelocity;
            setCommandValue(i, v);
         }
         else
         {
            setCommandValue(i, 0);
         }
      }
      boolean reached = true;
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         if (i == index)
         {
            float pos = getActuatorPosition(i);
            if ((goalVelocity > 0 && pos < goalPosition / 2) || (goalVelocity < 0 && pos > goalPosition))
            {
               reached = false;
               break;
            }
         }
      }
      if (reached)
      {
         return true;
      }
      return false;
   }

   public void update(AbilityHandHardwareCommunication communication)
   {
      communication.readState(this);
      processVelToPos(4);
      communication.publishCommand(this);
   }

   public float getControlSliderValue(int index)
   {
      return controlFinger[index];
   }

   public void setControlSliderValue(int index, float v)
   {
      controlFinger[index] = v;
   }

   public void setControlMode(int controlInt)
   {
      if (controlInt == 0)
      {
         controlMode = ControlMode.POSITION;
      }
      else
      {
         controlMode = ControlMode.VELOCITY;
      }
   }

   public ControlMode getControlMode()
   {
      return controlMode;
   }

   public void setGoalPosition(float position)
   {
      goalPosition = position;
   }

   public float getGoalPosition()
   {
      return goalPosition;
   }

   public void setGoalVelocity(float velocity)
   {
      goalVelocity = velocity;
      startTime = System.currentTimeMillis();
   }

   public float getGoalVelocity()
   {
      return goalVelocity;
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
   public void setCommandType(AbilityHandCommandType type)
   {
      this.commandType = type;
   }

   @Override
   public float getCommandValue(int index)
   {
      return commandValues[index];
   }

   @Override
   public void setCommandValue(int index, float v)
   {
      commandValues[index] = v;
   }

   @Override
   public float getActuatorPosition(int index)
   {
      return actuatorPositions[index];
   }

   @Override
   public void setActuatorPosition(int index, float v)
   {
      actuatorPositions[index] = v;
   }
}
