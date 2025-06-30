package us.ihmc.psyonicros2;

import us.ihmc.robotics.robotSide.RobotSide;

public class AbilityHandController implements AbilityHandInterface
{

   private enum ControlMode
   {IDLE, VEL_TO_POS}

   private ControlMode controlMode = ControlMode.IDLE;
   private float velToPosTarget = Float.NaN;
   private float velToPosSpeed = 0.0f;
   private final AbilityHandHardwareCommunication communication;
   private final RobotSide handSide;
   private AbilityHandCommandType commandType = AbilityHandCommandType.POSITION;
   private final float[] commandValues = new float[ACTUATOR_COUNT];
   private final float[] actuatorPositions = new float[ACTUATOR_COUNT];
   private final float[] controlFinger = new float[ACTUATOR_COUNT];

   public AbilityHandController(RobotSide handSide, AbilityHandHardwareCommunication communication)
   {
      this.handSide = handSide;
      this.communication = communication;
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         commandValues[i] = 30.0f;
         actuatorPositions[i] = 30.0f;
         controlFinger[i] = 30.0f;
      }
   }

   public void setAllFingers(float position)
   {
      setCommandType(AbilityHandCommandType.POSITION);
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         if (i != 4 && i != 5)
         {
            controlFinger[i] = position;
            setCommandValue(i, position);
         }
      }
      communication.publishCommand(this);
   }

   public void startVelToPos(float targetPosition, float velocity)
   {
      velToPosTarget = targetPosition;
      velToPosSpeed = velocity;
      controlMode = ControlMode.VEL_TO_POS;
   }

   public void processVelToPos(int excludedIndex)
   {
      if (controlMode != ControlMode.VEL_TO_POS)
         return;

      if (velToPosSpeed < 0.0f)
      {
         if (moveSkipped(excludedIndex))
         {
            setCommandType(AbilityHandCommandType.VELOCITY);
            for (int i = 0; i < ACTUATOR_COUNT; i++)
            {
               if (i != excludedIndex)
               {
                  float v = (i == 5) ? -velToPosSpeed : velToPosSpeed;
                  setCommandValue(i, v);
               }
               else
               {
                  setCommandValue(i, 0.0f);
               }
            }
            communication.publishCommand(this);

            boolean allOthersReached = true;
            for (int i = 0; i < ACTUATOR_COUNT; i++)
            {
               if (i == excludedIndex)
                  continue;
               float pos = getActuatorPosition(i);
               if (i == 5)
                  pos = -pos;
               if ((velToPosSpeed < 0 && pos > velToPosTarget) || (velToPosSpeed > 0 && pos < velToPosTarget))
               {
                  allOthersReached = false;
                  break;
               }
            }
            if (allOthersReached)
               controlMode = ControlMode.IDLE;
         }
      }
      else
      {
         setCommandType(AbilityHandCommandType.VELOCITY);
         for (int i = 0; i < ACTUATOR_COUNT; i++)
         {
            if (i != excludedIndex)
            {
               float v = (i == 5) ? -velToPosSpeed : velToPosSpeed;
               setCommandValue(i, v);
            }
            else
            {
               setCommandValue(i, 0.0f);
            }
         }
         communication.publishCommand(this);

         boolean reached = true;
         for (int i = 0; i < ACTUATOR_COUNT; i++)
         {
            if (i == excludedIndex)
               continue;
            float pos = getActuatorPosition(i);
            if (i == 5)
               pos = -pos;
            if ((velToPosSpeed > 0 && pos < velToPosTarget) || (velToPosSpeed < 0 && pos > velToPosTarget))
            {
               reached = false;
               break;
            }
         }
         if (reached)
         {
            if (moveSkipped(excludedIndex))
               controlMode = ControlMode.IDLE;
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
            float v = (i == 5) ? -velToPosSpeed : velToPosSpeed;
            setCommandValue(i, v);
         }
         else
         {
            setCommandValue(i, 0);
         }
      }
      communication.publishCommand(this);
      boolean reached = true;
      for (int i = 0; i < ACTUATOR_COUNT; i++)
      {
         if (i == index)
         {
            float pos = getActuatorPosition(i);
            if ((velToPosSpeed > 0 && pos < velToPosTarget / 2) || (velToPosSpeed < 0 && pos > velToPosTarget))
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

   public void publish()
   {
      communication.publishCommand(this);
   }

   public void update()
   {
      communication.readState(this);
   }

   public float getControlSliderValue(int index)
   {
      return controlFinger[index];
   }

   public void setControlSliderValue(int index, float v)
   {
      controlFinger[index] = v;
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
