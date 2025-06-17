byte LEFT=0
byte RIGHT=1

# Specifies the side of the robot of the hand being referred to
byte hand_side 255

# Specifies the command type (one of POSITION, VELOCITY, CURRENT, DUTY)
byte command_type

float32[6] command_values
