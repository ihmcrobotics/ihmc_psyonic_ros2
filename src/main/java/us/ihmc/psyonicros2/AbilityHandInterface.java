package us.ihmc.psyonicros2;

import us.ihmc.robotics.robotSide.RobotSide;

/**
 * <p>Generic interface for an Ability Hand.</p>
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
    * Get the command array. It is a {@link #ACTUATOR_COUNT} long array with the following indices:
    * <ol start="0">
    *    <li>Index finger command</li>
    *    <li>Middle finger command</li>
    *    <li>Ring finger command</li>
    *    <li>Pinky finger command</li>
    *    <li>Thumb flexor command</li>
    *    <li>Thumb rotator command</li>
    * </ol>
    *
    * @return The command array.
    */
   float[] getCommandValues();

   /**
    * Set the command values.
    *
    * @param values The command values.
    */
   default void setCommandValues(float[] values)
   {
      System.arraycopy(values, 0, getCommandValues(), 0, ACTUATOR_COUNT);
   }

   /**
    * Get the finger position array. It is a {@link #ACTUATOR_COUNT} long array with the following indices:
    * <ol start="0">
    *    <li>Index finger position</li>
    *    <li>Middle finger position</li>
    *    <li>Ring finger position</li>
    *    <li>Pinky finger position</li>
    *    <li>Thumb flexor position</li>
    *    <li>Thumb rotator position</li>
    * </ol>
    *
    * @return The finger position array.
    */
   float[] getFingerPositionsDegrees();

   /**
    * Set the current finger positions. Should be set from values read from the hand.
    *
    * @param positionsDegrees Array of finger positions in degrees.
    */
   default void setFingerPositions(float[] positionsDegrees)
   {
      System.arraycopy(positionsDegrees, 0, getFingerPositionsDegrees(), 0, ACTUATOR_COUNT);
   }
}
