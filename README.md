# ihmc_psyonic_ros2
A ROS package for PSYONIC Ability Hands.
Includes ROS interfaces for commands and statuses, helpful Java classes,
meshes of the hand components, and URDF descriptions of the hands.

## Using ihmc_psyonic_ros2 as a Git Submodule
You may add ihmc_psyonic_ros2 into your ROS workspace as a git submodule as such:

```shell
# cd into the ROS 2 workspace source directory
cd <your_ros2_ws>/src

# Clone ihmc_psyonic_ros2 and set it up as a git submodule
git submodule add https://github.com/ihmcrobotics/psyonic-ability-hand-java.git

# Ensure the submodule is initialized as a git repository locally
git submodule init

# The following is not necessary, but may be useful to set
git config --global submodule.recurse true
```
