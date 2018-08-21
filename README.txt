# BluetoothMonitor

[Description]
Monitors the bluetooth device you select from the drop down list and will shutdown after the specified duration once you disconnect from that device.

[Brief]

Blah.

[Updating project]
 
If the build.xml file is not present you can use the follwing command to update the project and generate a new build.xml file along with the other necessary files and or directories(Only run while in the projects root directory):
 
android update project --name BluetoothMonitor --subprojects --target 9 --path .


To list targets run:

android list targets
