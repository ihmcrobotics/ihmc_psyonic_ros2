package us.ihmc.psyonicros2;

import us.ihmc.robotics.robotSide.RobotSide;

/**
 * <p>Generic interface for an Ability Hand.</p>
 * <p>
 * The hand's finger actuators are specified using the following indices:
 *    <ol start = 0>
 *       <li>Index finger</li>
 *       <li>Middle finger</li>
 *       <li>Ring finger</li>
 *       <li>Pinky finger</li>
 *       <li>Thumb flexor</li>
 *       <li>Thumb rotator</li>
 *    </ol>
 * </p>
 *
 * <p>Currently only supports command and finger position values.</p>
 */
public interface AbilityHandInterface
{
   int ACTUATOR_COUNT = 6;

   /**
    * Get this hand's side.
    *
    * @return This hand's side.
    */
   RobotSide getHandSide();

   /**
    * Get the current {@link AbilityHandCommandType}.
    *
    * @return The current command type.
    */
   AbilityHandCommandType getCommandType();

   /**
    * Set the current {@link AbilityHandCommandType}.
    *
    * @param commandType The command type.
    */
   void setCommandType(AbilityHandCommandType commandType);

   /**
    * Get the command value at the specified index.
    *
    * @param index Index to read the value from.
    * @return The command value.
    */
   float getCommandValue(int index);

   /**
    * Set the command value at the specified index.
    *
    * @param index Index at which to set the value.
    * @param value The value to set.
    */
   void setCommandValue(int index, float value);

   /**
    * Set the command values.
    *
    * @param values The command values.
    */
   default void setCommandValues(float[] values)
   {
      for (int i = 0; i < ACTUATOR_COUNT && i < values.length; ++i)
         setCommandValue(i, values[i]);
   }

   /**
    * Get the position of the actuator at the specified index.
    *
    * @param index Index to read the position from.
    * @return The position value in degrees.
    */
   float getActuatorPosition(int index);

   /**
    * Set the position of the actuator at the specified index.
    *
    * @param index Index at which to set the position value, in degrees.
    * @param value The value to set
    */
   void setActuatorPosition(int index, float value);

   /**
    * Set the actuator positions.
    *
    * @param positions The actuator positions, in degrees.
    */
   default void setActuatorPositions(float[] positions)
   {
      for (int i = 0; i < ACTUATOR_COUNT && i < positions.length; ++i)
         setActuatorPosition(i, positions[i]);
   }
}
