pluginManagement {
   plugins {
      id("us.ihmc.ihmc-build") version "1.1.1"
   }
}

buildscript {
   repositories {
      maven { url = uri("https://plugins.gradle.org/m2/") }
      mavenCentral()
      mavenLocal()
   }
   dependencies {
      classpath("us.ihmc:ihmc-build:1.1.1")
      classpath("us.ihmc:ros2-msg-to-pubsub-generator:1.2.3")
   }
}
