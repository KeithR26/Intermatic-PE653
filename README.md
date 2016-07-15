 Intermatic-PE653
SmartThings Z-Wave Device Type for the Intermatic PE653 Pool Controller

Overview:
This is a Groovy "Device Type" for the SmartThings home automation platform which supports the Intermatic PE653 Z-Wave Pool Control system.
This code is an enhancement on an original work by @bigpunk6. See below for features.

Current Author: @KeithR26

Original Author: @bigpunk6
Full credit goes to bigpunk6, without whom this derivation never would have come to light.

New Features:

1. The main issue I battled with the original code is that it did not seem to work with my firmware level (v3.4). This may be due to changes made by Intermatic since the original @bigpunk6 version. Clearly the original code worked for some people. This conjecture is further supported by the fact that the Vera users group has also been unable to work with v3.4. For my version of firmware I found that it did not support the Z-Wave Multichannelv3 Command Class (0x25 version 3 in particular). My firmware does support v2 of Multichannel. Specifically I had to implement zwave.commands.multichannelv3.MultiInstanceCmdEncap instead of zwave.commands.multichannelv3.MultiChannelCmdEncap. I further verified this by polling the PE653 for its supported version level for Multichannel and it does in fact report v2.

2. I noticed that a very early version of bigpunks code directly implemented switches in the device type. Later he removed these in favor of supporting the multichannel SmarApp, which is avery cool feature by the way, and crutial for allowing fine grained control of the different circuits by SmartThings routines and SmartApps. I liked both options so I added the 5 switches back in and added support to send the necessary events to both the internal switches as well as the multichannel "virtual switches" which appear separately on the "things" tab after you install the ST Multichannel SmartApp. bigpunk6 is absolutely right here, don't add the @cooperslee apps, this device type directly supports adding the "things".

3. I needed support for switching between pool and spa modes. Although I did find some documentation on the commad classes implemented by the PE653 and the configuration parameters it suports, I never found a documented way to control the "mode". I came up with a back door approach of modifying the "PS Schedule" in real time to basically reconfigure the controller to be in Spa mode from 12:01 AM through 11:59PM when I want the Spa on, and resetting the schedule back off when I want pool mode. Although it is documented that the controller supports three schedules for each "switch", I discovered by trial and errors that there are another 3 for the pool/spa schedule control (config parm 19-21). I added a pool/spa "switch" which sends the configuration commands to enable/disable this schedule. I also added a "Switch Endpoint 6" to the Things so you can control it elsewhere in ST.

4. I don't have a Variable Speed Pump (VSP) but I know others (like @JDogg016) do and have been desparate for a way to control it. Since I don;t have one I can't test this directly but I did add code and controls to try this indirectly. I took a similar back door approach as the pool/spa method above. There are 4 sets of 3 schedules for each of the 4 speeds of the VSP. I added a slider control with values from 1-4. Depending on which speed you set it will set the third schedule for that speed to "always on"  (12:01 AM - 11:59PM). I believe this has a reasonable chance of working, but I will need feedback and log results to validte this. I also added a "Dimmer Endpoint 7" for external ST control. There are very likely bugs in this area.

5. I observed that the PE653 advertizes support of a command class called "PowerLevel", which may be a mroe direct way of controlling the VSP. I added yet another slider for this called "power" that is worth trying. @JDogg016, give this a try as well).

6. While I was trying to troubleshoot my firmware version I needed to understand the command class supported versions so I added code to the "refresh" button to query all the supported command classes. The result is stored in the "state" variable which you can see in the ide if you look at the details of the device.

7. I added a lot to the logging in the device type so I can see better what is being sent to the device and what is coming back. You can control how much logging is done by a new configuration parameter in the deviice type. If you tap the settings icon in the upper right of the device type in the mobile app you will see a new parameter at the bottom called "Debug Level". You can set this to Off/Low/High. While troubleshooting, set this to High and this will create the most extensive logging data for viewing in the IDE.

Installation:
The installation of this device type is exactly the same as the bigpunk6 version so I will not repeat the process of pairing and installing as this is covered extensively in the Community Forum. I wil just summarize the following:

1. Pair the controller to ST
2. Install the device type and assign it to the PE653 device
3. install the ST Multichannel SmartApp.
4. Set device configuration options (name, pump type, pool/spa, etc)
5. Set the Debug Level to Low or High.
6. Go into the IDE Live Logging on your computer. Filter the logging for only this device.
7. Exercise the device type, ideally while in view of the pool.
8. Take screen shots of the log data.
9. Post them on the forum if you have issues.

I can't promise to be super responsive but I'll do my best.

Known Issues:
* The code is not allowing me set the thermostats for the pool or spa. It reads them if I set them with the remote, but nothing I have done allows me to change the settings. I have not been able to crack this one.

* I am stil having some issues with the switches setting their state correctly in some cases. I can see that the events are firing but sometimes the switch in the mobile UI does not change even though the device does respond to the commands.

Disclaimers:
* This code should be considered experimental. It has only been tested by me and only for limited applications.
* If you have a working device type now, be very sure you save a backup copy of that code so you can easily backtrack if needed.
* This code was tested against Intermatic PR653 v3.4, and may not work appropriately against other firmware versions (even though I am hopefull it will work on prior firmware versions as well).
