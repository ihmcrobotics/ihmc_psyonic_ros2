plugins {
   id("us.ihmc.ihmc-build")
}

ihmc {
   group = "us.ihmc"
   version = "0.0.0"
   vcsUrl = "https://github.com/ihmcrobotics/ihmc_psyonic_ros2"
   openSource = false

   configureDependencyResolution()
   configurePublications()
}

mainDependencies {
   api("us.ihmc:ihmc-commons:0.35.1")
   api("us.ihmc:ros2-library:1.2.3")
}

val generator = us.ihmc.ros2.rosidl.ROS2InterfaceGenerator()
tasks.register("generateMessages") {
   doFirst {
      // Delete old generated files
      delete("src/main/generated-idl")
      delete("src/main/generated-ros1")
      delete("src/main/generated-java")

      // Generate the messages into build/tmp/generateMessages
      generator.addPackageRootToIDLGenerator(file("./").toPath())
      generator.generate(file("src/main/generated-idl").toPath(),
                         file("src/main/generated-ros1").toPath(),
                         file("src/main/generated-java").toPath())

      us.ihmc.ros2.rosidl.ROS2InterfaceGenerator.convertDirectoryToUnixEOL(file("src/main/generated-idl").toPath())
      us.ihmc.ros2.rosidl.ROS2InterfaceGenerator.convertDirectoryToUnixEOL(file("src/main/generated-ros1").toPath())
      us.ihmc.ros2.rosidl.ROS2InterfaceGenerator.convertDirectoryToUnixEOL(file("src/main/generated-java").toPath())
   }
}
