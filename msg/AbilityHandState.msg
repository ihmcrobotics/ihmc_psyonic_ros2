# The hand's serial number. E.g. 24ABH265
string<=8 serial_number

byte LEFT=0
byte RIGHT=1

# Specifies the side of the robot of the hand being referred to
byte hand_side 255

# The actuator positions in degrees
float32[6] actuator_positions
