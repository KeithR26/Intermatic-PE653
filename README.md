Intermatic-PE653
SmartThings Z-Wave Device Type for the Intermatic PE653 Pool Controller

Overview:
This is a Groovy "Device Type" for the SmartThings home automation platform which supports the Intermatic PE653 Z-Wave Pool Control system.
This code is an enhancement on an original work by @bigpunk6. See below for features.

Current Author: @KeithR26

Original Author: @bigpunk6
Full credit goes to bigpunk6, without whom this derivation never would have come to light.

Release Notes, Version 2.01  08/10/2016:

1. This release delivers a major UI redesign. The Thermostat control was overly large while providing limited utility. As I added 9 buttons for the pool circuits and VSP speeds the UI had gotten disorganized and long, requiring scrolling. I took some inspriration from the PE953 remote, which lists the 5 pool circuits across the top. I opted for small buttons to enable a compact, single page display format. I'm hoping everyone is ok with the button size. Note that the one remaining large button is to the left of the pool temperature. The button only appears if you have "both" pool & spa, othewise the button is hidden. Similarly, if you have a VSP, then four small buttons appear to the right of the temperature, otherwise they are likewise hidden. The "Power" controls are removed as they did nothing. The Speed slider now supports a "zero" value which will turn off the VSP (assuming it was turned on by this app). You can now choose an alternate icon for the app amd change pool/spa mode on the "Things" page.

2. Extended the "Pump Type" configuration to add options for VSP. If you have a VSP it is important to set this using the settings "gear" to enable the VSP buttons. So far I cannot detect whether or not the PE653 has a VSP since it appears to be dynamically detected, and does not appear on a configuration page that I can see. Someday I hope to detect this from the controller.

3. Changed the ST "things" exposed by the Multi-channel SmartApp for the VSP from a "dimmer" to four separate on/off switches (Switch Endpoint 7-10) corresponding to the four speeds. These switches, as well as the four VSP buttons on the main UI function as "radio buttons", meaning that selecting any one of them deselects any other button that was previously on (only one on at a time). If you deselect the switch or button that is on, then the VSP is deselected and will turn off, if enabled by this app.

4. Added commands to interrogate the version info from the PE653 and display it in the IDE in the device info.

5. A new option has been added to allow configuring the delay between Z-wave commands. The default is 1000 milliseconds. This was the fixed value used in the previous version. Initial tests I have done suggest this could be much small, which will improve respobse time, but if too small may compromise reliability. For the adventurous, feel free to trying reducing this delay and see how far toward zero you can get.
6. Added a diagnostic mode (Debug Leve = High) which attemppts to interrogate every PE653 configuration page and displays this information in the logs. This causes a lot of message traffic so don't enable High level for an ongoing basis.




Release Notes, Version 2.00  07/14/2016:

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
