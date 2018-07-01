/**
 *  Intermatic PE653 Pool Control System
 *
 *  Original Copyright 2014 bigpunk6
 *  Updated 2018 KeithR26
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  This DTH is now a "Composite Device Type Handler" which supports multiple "Child" devices that appear in
 *	the "Things" list and can be used by SmartApps to control the 5 switches, Pool/Spa mode and 4 VSP speeds.
 *	This requires a second DTH be installed: erocm123 / Switch Child Device
 *
 *  Don't use SamrtThings Multi-channel (deprecated) or Cooper Lee's code (vTile_ms, ms_w_vts). These are incompatible
 *  with the Composite DTH architecture.
 *
 *	Version History
 *	Ver		Date		Author		Changes
 *	1.00	06/15/2016	bigpunk6	Latest version from the original author
 *	2.00	07/14/2016	KeithR26	Updates to make this work with Intermatic firmware v3.4
 *									Added Pool/Spa mode and initial VSP support
 *	2.01	08/10/2016	KeithR26	Major UI redesign
 *									Added 4 switches to set VSP speeds and off
 *									Added 4 "Multi-channel" endpoints for ST VSP control
 *									Added configurable Z-Wave delay
 *									Added PE653 configuration diagnostics in Debug level = High
 *									Added Version Info in IDE and logs
 *									Allow changing icon
 *	2.02	04/26/2017	KeithR26	Fix Thermostat set for v3.4 firmware (force scale = 0)
 *  								Prototype Pool Light Color Control
 *									Implement simple "Macros"
 *	2.03	05/01/2017	KeithR26	Refresh water temp when UI temp is tapped
 *	2.04	05/07/2017	KeithR26	Allow negative temperature offsets. Limit offets to +/- 5 (max supported by PE653)
 *  2.05	05/13/2017  KeithR26	Debug version for Android. Never committed to master
 *  2.06    05/13/2017  KeithR26	Update to fix Temperature display on Android
 *  3.00	05/06/2018	KeithR26	Change to "Composite" DTH since ST deprecated the Multi-channel SmartApp
*/
metadata {
	definition (name: "Intermatic PE653 Pool Control System", author: "KeithR26", namespace:  "KeithR26") {
        capability "Actuator"
		capability "Switch"
		capability "Polling"
		capability "Configuration"
		capability "Refresh"
		capability "Temperature Measurement"
		capability "Sensor"
		capability "Zw Multichannel"
        capability "Thermostat"
        
        attribute "operationMode", "string"
        attribute "firemanTimeout", "string"
        attribute "temperature", "string"
        attribute "temperatureOffsets", "string"
        attribute "poolspaConfig", "string"
        attribute "poolSetpoint", "string"
        attribute "spaSetpoint", "string"
        attribute "poolSpaMode", "string"
        attribute "pumpSpeed", "string"
        attribute "lightColor", "string"
        attribute "lightCircuits", "string"
		attribute "ccVersions", "string"
		attribute "VersionInfo", "string"
		attribute "ManufacturerInfo", "string"
		attribute "groups", "string"
		attribute "debugLevel", "string"
//        attribute "children", "List"

		attribute "switch1", "string"
		attribute "switch2", "string"
		attribute "switch3", "string"
		attribute "switch4", "string"
		attribute "switch5", "string"
		attribute "swVSP1", "string"
		attribute "swVSP2", "string"
		attribute "swVSP3", "string"
		attribute "swVSP4", "string"

        command "poll"
        command "quickSetPool"
        command "quickSetSpa"
		command "quickGetWaterTemp"
		command "setPoolMode"
		command "setSpaMode"
		command "togglePoolSpaMode"
		command "childOn"
		command "childOff"
        command "on1"
		command "off1"
        command "on2"
		command "off2"
        command "on3"
		command "off3"
        command "on4"
		command "off4"
        command "on5"
		command "off5"
		command "setVSPSpeed"
		command "setVSPSpeed0"
		command "setVSPSpeed1"
		command "setVSPSpeed2"
		command "setVSPSpeed3"
		command "setVSPSpeed4"
        command "setMode1"
        command "setMode2"
        command "setMode3"
        command "setMode4"
        command "setLightColor"
        command "setColor"
        command "setClock"
        command "updated"
        
		fingerprint deviceId: "0x1001", inClusters: "0x91,0x73,0x72,0x86,0x81,0x60,0x70,0x85,0x25,0x27,0x43,0x31", outClusters: "0x82"
	}

    preferences {
        input "operationMode1", "enum", title: "Boster/Cleaner Pump",
            options:[1:"No",
                     2:"Uses Circuit-1",
                     3:"Variable Speed pump Speed-1",
                     4:"Variable Speed pump Speed-2",
                     5:"Variable Speed pump Speed-3",
                     6:"Variable Speed pump Speed-4"]
        input "operationMode2", "enum", title: "Pump Type", 
            options:[0:"1 Speed Pump without Booster/Cleaner",
                     1:"1 Speed Pump with Booster/Cleaner",
                     2:"2 Speed Pump without Booster/Cleaner",
                     3:"2 Speed Pump with Booster/Cleaner",
                     4:"Variable Speed Pump without Booster/Cleaner",
                     5:"Variable Speed Pump with Booster/Cleaner",
                     6:"Reserved 6",
                     7:"Reserved 7"]
        input "poolSpa1", "enum", title: "Pool or Spa",
        	options:[0:"Pool",
            		 1:"Spa",
                     2:"Both"]
	    input "fireman", "enum", title: "Fireman Timeout",
            options:["255":"No heater installed",
                     "0":"No cool down period",
                     "1":"1 minute",
                     "2":"2 minute",
                     "3":"3 minute",
                     "4":"4 minute",
                     "5":"5 minute",
                     "6":"6 minute",
                     "7":"7 minute",
                     "8":"8 minute",
                     "9":"9 minute",
                     "10":"10 minute",
                     "11":"11 minute",
                     "12":"12 minute",
                     "13":"13 minute",
                     "14":"14 minute",
                     "15":"15 minute"]
		input "tempOffsetwater", "number", title: "Water temperature offset", range: "-5..5", required: true
        input "tempOffsetair", "number",
            title: "Air temperature offset - Sets the Offset of the air temerature for the add-on Thermometer in degrees Fahrenheit -5F to +5F", range: "-5..5", required: true
        input "debugLevel", "enum", title: "Debug Level", multiple: "true",
        	options:[0:"Off",
            		 1:"Low",
                     2:"High"]
        input "ZWdelay", "number",
            title: "Delay between Z-Wave commands sent (milliseconds). Suggest 1000.", required: true
//Mode 1
        input "M1Label", "text", title: "M1: Display Name:"
        input "M1Sw1", "enum", title: "M1: Circuit 1 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw2", "enum", title: "M1: Circuit 2 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw3", "enum", title: "M1: Circuit 3 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw4", "enum", title: "M1: Circuit 4 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw5", "enum", title: "M1: Circuit 5 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Mode", "enum", title: "M1: Mode to change to:",
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M1Temp", "number", title: "M1: Set Temperature to:", range: "40..104"
        input "M1VSP", "enum", title: "M1: Set VSP Speed to:",
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
//Mode 2
        input "M2Label", "text", title: "M2: Display Name:"
		input "M2Sw1", "enum", title: "M2: Circuit 1 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw2", "enum", title: "M2: Circuit 2 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw3", "enum", title: "M2: Circuit 3 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw4", "enum", title: "M2: Circuit 4 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw5", "enum", title: "M2: Circuit 5 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Mode", "enum", title: "M2: Mode to change to:",
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M2Temp", "number", title: "M2: Set Temperature to:", range: "40..104"
        input "M2VSP", "enum", title: "M2: Set VSP Speed to:",
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
//Mode 3
        input "M3Label", "text", title: "M3: Display Name:"
        input "M3Sw1", "enum", title: "M3: Circuit 1 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw2", "enum", title: "M3: Circuit 2 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw3", "enum", title: "M3: Circuit 3 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw4", "enum", title: "M3: Circuit 4 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw5", "enum", title: "M3: Circuit 5 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Mode", "enum", title: "M3: Mode to change to:",
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M3Temp", "number", title: "M3: Set Temperature to:", range: "40..104"
        input "M3VSP", "enum", title: "M3: Set VSP Speed to:"
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
//Mode 4
        input "M4Label", "text", title: "M4: Display Name:"
        input "M4Sw1", "enum", title: "M4: Circuit 1 action:"
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw2", "enum", title: "M4: Circuit 2 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw3", "enum", title: "M4: Circuit 3 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw4", "enum", title: "M4: Circuit 4 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw5", "enum", title: "M4: Circuit 5 action:",
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Mode", "enum", title: "M4: Mode to change to:",
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M4Temp", "number", title: "M4: Set Temperature to:", range: "40..104"
        input "M4VSP", "enum", title: "M4: Set VSP Speed to:",
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
        input "C1ColorEnabled", "enum", title: "Circuit 1 Color Light Enable:",
        	options:[0:"off",
            		 1:"On"]
        input "C2ColorEnabled", "enum", title: "Circuit 2 Color Light Enable:",
        	options:[0:"off",
            		 1:"On"]
        input "C3ColorEnabled", "enum", title: "Circuit 3 Color Light Enable:",
        	options:[0:"off",
            		 1:"On"]
        input "C4ColorEnabled", "enum", title: "Circuit 4 Color Light Enable:",
        	options:[0:"off",
            		 1:"On"]
        input "C5ColorEnabled", "enum", title: "Circuit 5 Color Light Enable:",
        	options:[0:"off",
            		 1:"On"]
    }

	simulator {
		status "on":  "command: 2003, payload: FF"
		status "off": "command: 2003, payload: 00"
		reply "8E010101,delay 800,6007": "command: 6008, payload: 4004"
		reply "8505": "command: 8506, payload: 02"
		reply "59034002": "command: 5904, payload: 8102003101000000"
		reply "6007":  "command: 6008, payload: 0002"
		reply "600901": "command: 600A, payload: 10002532"
		reply "600902": "command: 600A, payload: 210031"
	}
    
	// tile definitions
	tiles(scale: 2) {

//		standardTile("mainTile", "device.poolSpaMode", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
//			state "on",         label: "on",         action: "setPoolMode", icon: "st.Health & Wellness.health2",  backgroundColor: "#79b821", nextState: "turningOff"
//			state "off",        label: "off",        action: "setSpaMode",  icon: "st.Health & Wellness.health2", backgroundColor: "#ffffff", nextState: "turningOn"
//			state "turningOn",  label:'Turning on',  action: "setSpaMode",        icon: "st.Health & Wellness.health2",  backgroundColor: "#79b821", nextState: "turningOff"
//			state "turningOff", label:'Turning off', action: "setPoolMode",         icon: "st.Health & Wellness.health2", backgroundColor: "#ffffff", nextState: "turningOn"
//			state "disabled",   label:'',            icon: "st.Health & Wellness.health2", backgroundColor: "#bc2323"  //"#ffffff"
//		}
		valueTile("mainTile", "device.temperature", width: 2, height: 2, inactiveLabel: true ) {
			state "temperature", label:'${currentValue}°', action: "quickGetWaterTemp",icon: "st.Health & Wellness.health2",
					backgroundColors:[
						[value: 32, color: "#153591"],
					    [value: 54, color: "#1e9cbb"],
				    	[value: 64, color: "#90d2a7"],
				    	[value: 74, color: "#44b621"],
				    	[value: 90, color: "#f1d801"],
				    	[value: 98, color: "#d04e00"],
				    	[value: 110, color: "#bc2323"]
					]
        }
		valueTile("temperatureTile", "device.temperature", width: 2, height: 2, inactiveLabel: true ) {
			state "temperature", label:'${currentValue}°', action: "quickGetWaterTemp",
					backgroundColors:[
						[value: 32, color: "#153591"],
					    [value: 54, color: "#1e9cbb"],
				    	[value: 64, color: "#90d2a7"],
				    	[value: 74, color: "#44b621"],
				    	[value: 90, color: "#f1d801"],
				    	[value: 98, color: "#d04e00"],
				    	[value: 110, color: "#bc2323"]
					]
		}
		valueTile("airTempTile", "device.airTemp", width: 2, height: 1, inactiveLabel: true ) {
			state "airTemp", label:'${currentValue}°',
					backgroundColors:[
						[value: 32, color: "#153591"],
					    [value: 54, color: "#1e9cbb"],
				    	[value: 64, color: "#90d2a7"],
				    	[value: 74, color: "#44b621"],
				    	[value: 90, color: "#f1d801"],
				    	[value: 98, color: "#d04e00"],
				    	[value: 110, color: "#bc2323"]
					]
		}
		valueTile("airTempLabel", "device.airTempLabel", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
			state "airTemp", label:'  AIR:', backgroundColor:"#ffffff"
		}
        controlTile("poolSliderControl", "device.poolSetpoint", "slider", width: 2, height: 1, inactiveLabel: false, range:"(40..104)") {
			state "PoolSetpoint", action:"quickSetPool", backgroundColor:"#d04e00"
		}
		valueTile("poolSetpoint", "device.poolSetpoint", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
			state "pool", label:' POOL:', backgroundColor:"#ffffff"
//			state "pool", label:'pool ${currentValue}°', backgroundColor:"#ffffff"
		}
		controlTile("spaSliderControl", "device.spaSetpoint", "slider", width: 2, height: 1, inactiveLabel: false, range:"(40..104)") {
			state "SpaSetpoint", action:"quickSetSpa", backgroundColor: "#1e9cbb"
		}
		valueTile("spaSetpoint", "device.spaSetpoint", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
			state "spa", label:'   SPA:', backgroundColor:"#ffffff"
//			state "spa", label:'spa  ${currentValue}°', backgroundColor:"#ffffff"
		}
        controlTile("pumpSpeedSliderControl", "device.pumpSpeed", "slider", width: 4, height: 1, inactiveLabel: false, range:"(0..4)") {
			state "pumpSpeed", action:"setVSPSpeed", backgroundColor:"#d04e00"
		}
		valueTile("pumpSpeed", "device.pumpSpeed", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "pumpSpeed", label:'Speed:', backgroundColor:"#ffffff"
//			state "pumpSpeed", label:'speed ${currentValue}', backgroundColor:"#ffffff"
		}
        controlTile("lightColorSliderControl", "device.lightColor", "slider", width: 2, height: 1, inactiveLabel: false, range:"(1..14)") {
			state "color", action:"setLightColor", backgroundColor:"#d04e00"
		}
//		valueTile("temperatureTile", "device.temperature", width: 2, height: 2, inactiveLabel: true) {
		valueTile("lightColor", "device.lightColor", width: 1, height: 1, inactiveLabel: true, decoration: "flat") {
			state "color", action:"setColor", label:'COLOR:', backgroundColor:"#ffffff"
//			state "color", action:"setColor", label:'color ${currentValue}', backgroundColor:"#ffffff"
		}
//		standardTile("lightColor", "device.lightColor", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
//			state "color", action:"setColor", label:'color ${currentValue}', backgroundColor:"#ffffff", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/Pool.png"
//		}

		standardTile("poolSpaMode", "device.poolSpaMode", width: 2, height: 2, decoration: "flat") {
			state "on",         label: "",           action: "setPoolMode", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/spa.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "",           action: "setSpaMode",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/Pool.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label: 'changing',   action: "setPoolMode",        icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/spa.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label: 'changing',   action: "setSpaMode",         icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/Pool.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "disabled",   label:'',            icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/all-white.png", backgroundColor: "#ffffff"
		}
		standardTile("switch1", "device.switch1", width: 1, height: 1, decoration: "flat") {
			state "on",         label: "on",         action: "off1", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw1-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "off",        action: "on1",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw1-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off1", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw1-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on1",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw1-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch2", "device.switch2", width: 1, height: 1, decoration: "flat") {
			state "on",         label: "on",         action: "off2", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw2-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "off",        action: "on2",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw2-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off2", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw2-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on2",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw2-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch3", "device.switch3", width: 1, height: 1, decoration: "flat") {
			state "on",         label: "on",         action: "off3", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw3-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "off",        action: "on3",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw3-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off3", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw3-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on3",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw3-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch4", "device.switch4", width: 1, height: 1, decoration: "flat") {
			state "on",         label: "on",         action: "off4", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw4-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "off",        action: "on4",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw4-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off4", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw4-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on4",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw4-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch5", "device.switch5", width: 1, height: 1, decoration: "flat") {
			state "on",         label: "on",         action: "off5", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw5-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "off",        action: "on5",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw5-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off5", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw5-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on5",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/sw5-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("swVSP1", "device.swVSP1", width: 1, height: 1, decoration: "flat") {
			state "off",        label: "off",        action: "setVSPSpeed1", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp1-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "on",         label: "on",         action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp1-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOn",  label:'Turning on',  action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp1-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "setVSPSpeed1", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp1-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "disabled",   label:'',                                    icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/all-white.png",backgroundColor: "#ffffff"
		}
        standardTile("swVSP2", "device.swVSP2", width: 1, height: 1, decoration: "flat") {
			state "off",        label: "off",        action: "setVSPSpeed2", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp2-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "on",         label: "on",         action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp2-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOn",  label:'Turning on',  action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp2-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "setVSPSpeed2", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp2-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "disabled",   label:'',                                    icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/all-white.png",backgroundColor: "#ffffff"
		}
        standardTile("swVSP3", "device.swVSP3", width: 1, height: 1, decoration: "flat") {
			state "off",        label: "off",        action: "setVSPSpeed3", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp3-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "on",         label: "on",         action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp3-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOn",  label:'Turning on',  action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp3-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "setVSPSpeed3", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp3-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "disabled",   label:'',                                    icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/all-white.png",backgroundColor: "#ffffff"
		}
        standardTile("swVSP4", "device.swVSP4", width: 1, height: 1, decoration: "flat") {
			state "off",        label: "off",        action: "setVSPSpeed4", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp4-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "on",         label: "on",         action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp4-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOn",  label:'Turning on',  action: "setVSPSpeed0", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp4-on.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "setVSPSpeed4", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/vsp4-off.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "disabled",   label:'',                                    icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/all-white.png",backgroundColor: "#ffffff"
		}
        standardTile("swM1", "device.swM1", width: 1, height: 1, decoration: "flat") {
			state "disabled",   label:'',            action: "setMode1",     icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/M1-off.png",backgroundColor: "#ffffff", nextState: "disabled"
		}
        standardTile("swM2", "device.swM2", width: 1, height: 1, decoration: "flat") {
			state "disabled",   label:'',            action: "setMode2",     icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/M2-off.png",backgroundColor: "#ffffff", nextState: "disabled"
		}
        standardTile("swM3", "device.swM3", width: 1, height: 1, decoration: "flat") {
			state "disabled",   label:'',            action: "setMode3",     icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/M3-off.png",backgroundColor: "#ffffff", nextState: "disabled"
		}
        standardTile("swM4", "device.swM4", width: 1, height: 1, decoration: "flat") {
			state "disabled",   label:'',            action: "setMode4",     icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/M4-off.png", backgroundColor:"#ffffff", nextState: "disabled"
		}
		valueTile("M1Name", "device.M1Name", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "M1Name", label:'${currentValue}', backgroundColor:"#ffffff", action: "setMode1"
		}
		valueTile("M2Name", "device.M2Name", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "M2Name", label:'${currentValue}', backgroundColor:"#ffffff", action: "setMode2"
		}
		valueTile("M3Name", "device.M3Name", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "M3Name", label:'${currentValue}', backgroundColor:"#ffffff", action: "setMode3"
		}
		valueTile("M4Name", "device.M4Name", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "M4Name", label:'${currentValue}', backgroundColor:"#ffffff", action: "setMode4"
		}
        standardTile("blank1", "device.blank", width: 2, height: 1, decoration: "flat") {
			state "on",         icon: "st.Health & Wellness.health2",  backgroundColor: "#ffffff"
		}
        standardTile("refresh", "device.switch", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		valueTile("clock", "device.clock", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "clockName", label:'${currentValue}', backgroundColor:"#ffffff", action: "setClock"
		}
		valueTile("heaterLabel", "device.heaterLabel", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
			state "airTemp", label:'  HEAT:', backgroundColor:"#ffffff"
		}
		standardTile("heaterTile", "device.heater", width: 2, height: 1, canChangeIcon: true) {
			state "off",        label: "heater off",   icon: "st.Health & Wellness.health2",  backgroundColor: "#ffffff"
			state "on",         label: "*HEATING*",  icon: "st.Health & Wellness.health2", backgroundColor: "#bc2323"
		}
//        standardTile("configure", "device.configure", width: 1, height: 1, inactiveLabel: false, decoration: "flat") {
//			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
//		}
        standardTile("blank3", "device.blank3", width: 1, height: 1, decoration: "flat") {
			state "on",     label: "", icon: "st.Health & Wellness.health2",  backgroundColor: "#ffffff"
		}
        
	main "mainTile"
//	main "temperatureTile"
        details([
            "blank3",
            "switch1","switch2","switch3","switch4","switch5",
            "poolSpaMode",
        	"temperatureTile",
            "swVSP1","swVSP2","swVSP3","swVSP4",
			"poolSetpoint", "poolSliderControl",
            "swM1", "M1Name",
            "spaSetpoint", "spaSliderControl", 
            "swM2", "M2Name",
            "lightColor","lightColorSliderControl",
            "swM3", "M3Name",
			"heaterLabel", "heaterTile",
            "swM4", "M4Name",
			"airTempLabel", "airTempTile",
			"refresh",
            "clock",
//            "blank1",
//            "configure",
            ])
	}
}

// Constants for PE653 configuration parameter locations
def getDELAY () {ZWdelay}								// How long to delay between commands to device (configured)
def getMIN_DELAY () {"800"}								// Minimum delay between commands to device (configured)
def getVERSION () {"Ver 3.00"}							// Keep track of handler version
def getPOOL_SPA_SCHED_PARAM () { 21 }					// Pool/Spa mode Schedule #3 - 0x15
def getPOOL_SPA_CHAN () { 39 }							// Pool/Spa channel - 0x27
def getPOOL_SPA_EP () { 6 }								// Pool/Spa endpoint - 6
def getVSP_SCHED_NO (int spd) { (35 + (spd * 3)) }		// VSP Speed 1 Schedule #3 - 0x26
def getVSP_SPEED (int sched) { ((sched - 35) / 3) }		// Convert from sched to speed
def getVSP_CHAN_NO (int spd) { (16 + (spd - 1)) }		// VSP Speed 1 Channel  - 0x10 - 0x13
def getVSP_EP (int spd) { (6 + spd) }					// VSP Endpoint 7 - 10
def getVSP_SPEED_FROM_CHAN (int chan) { ((chan - 16) + 1) }	// Convert from channel to speed - 0x10
def getVSP_ENABLED () { (operationMode2 >= "4") ? 1 : 0 }	// True if a Variable Speed Pump Configured
def getPOOL_SPA_COMBO () { (poolSpa1 == "2") ? 1 : 0 }	// True if both Pool and Spa
def getSWITCH_NAME (int instance) {
	def swNames = ["switch1","switch2","switch3","switch4","switch5","poolSpaMode","swVSP1","swVSP2","swVSP3","swVSP4"]
    return swNames[instance - 1]
}
// Return the list supported command classes by PE653. The following are the versions for firmware v3.4
// ccVersions: {"20":1,"25":1,"27":1,"31":1,"43":1,"60":2,"70":1,"72":1,"73":1,"81":1,"85":1,"86":1,"91":1} 
def getSupportedCmdClasses () {[
	0x20,	//	Basic
	0x25,	//	Switch Binary
	0x27,	//	Switch All
	0x31,	//	Sensor Multilevel
	0x43,	//	Thermostat setpoint
	0x60,	//	Multi Instance
	0x70,	//	Configuration
	0x72,	//	Manufacturer Specific
	0x73,	//	Powerlevel
	0x81,	//	Clock
	0x85,	//	Association
	0x86,	//	Version
	0x91	//	Manufacturer Proprietary
    ]
}

// Main entry point for messages from the device
def parse(String description) {
	def result = null
    def command = null
    byte[] payload = []
	if (description.startsWith("Err")) {
        log.warn "Error in Parse"
	    result = createEvent(descriptionText:description, isStateChange:true)
	} else {
//		try {
            def command1 = description.split('command:')[1]
            command = command1.split(',')[0]
            def payloadStr = description.split('payload:')[1]
//			log.debug "cmd: ${command}   payloadStr: ${payloadStr}"
            if (command.contains("9100")) {
				payload = payloadStr.replace(" ","").decodeHex()
                if (debugLevel > "0") {
//                    log.debug(">>>>> unParsed cmd - description:$description ")
                }
				result = zwaveEventManufacturerProprietary(payload, payloadStr)
			} else {
//				def cmd = zwave.parse(description, [0x20: 1, 0x25:1, 0x27:1, 0x31:1, 0x43:1, 0x60:3, 0x70:2, 0x81:1, 0x85:1, 0x86: 1, 0x73:1])
                def cmd = zwave.parse(description, [0x20: 1, 0x25:1, 0x27:1, 0x31:1, 0x43:1, 0x60:3, 0x70:2, 0x72:1, 0x81:1, 0x85:2, 0x86: 1, 0x73:1, 0x91:1])
                if (debugLevel > "0") {
                    log.debug(">>>>> ${cmd} - description:$description ")
                }
                if (cmd) {
                    result = zwaveEvent(cmd)
                } else {
                    log.debug("----- Parse() parsed to NULL:  description:$description")
                    return null
                }
            }
//        } catch (e) {
//			log.warn("..... Exception in Parse() ${cmd} - description:${description} exceptioon ${e}")
//        }
	}
	delayResponseLog(result)
}

private List setPoolSetpointInternal(Double degrees) {
	log.debug "setPoolSetpointInternal degrees=${degrees}"
    def cmds = []
	def deviceScale = state.scale ?: 1
	def deviceScaleString = deviceScale == 2 ? "C" : "F"
    def locationScale = getTemperatureScale()
	def p = (state.precision == null) ? 1 : state.precision

    def convertedDegrees
    if (locationScale == "C" && deviceScaleString == "F") {
    	convertedDegrees = celsiusToFahrenheit(degrees)
    } else if (locationScale == "F" && deviceScaleString == "C") {
    	convertedDegrees = fahrenheitToCelsius(degrees)
    } else {
    	convertedDegrees = degrees
    }
    deviceScale = 0			// Cannot send scale = 1 to PE653 or it will ignore the request
	cmds << zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 1, scale: deviceScale, precision: p, scaledValue: convertedDegrees)
	cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1)
//    log.trace "setPoolSetpoint: setpointType: 1  scale: $deviceScale  precision: $p  scaledValue: $convertedDegrees"
	cmds
}

private List setSpaSetpointInternal(Double degrees) {
    log.debug "setSpaSetpointInternal degrees=${degrees}"
    def cmds = []
	def deviceScale = state.scale ?: 1
	def deviceScaleString = deviceScale == 2 ? "C" : "F"
    def locationScale = getTemperatureScale()
	def p = (state.precision == null) ? 1 : state.precision

    def convertedDegrees
    if (locationScale == "C" && deviceScaleString == "F") {
    	convertedDegrees = celsiusToFahrenheit(degrees)
    } else if (locationScale == "F" && deviceScaleString == "C") {
    	convertedDegrees = fahrenheitToCelsius(degrees)
    } else {
    	convertedDegrees = degrees
    }
    deviceScale = 0			// Cannot send scale = 1 to PE653 ver 3.4 or it will ignore the request
//    log.trace "setSpaSetpoint: setpointType: 7  scale: $deviceScale  precision: $p  scaledValue: $convertedDegrees"
	cmds << zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 7, scale: deviceScale, precision: p,  scaledValue: convertedDegrees)
	cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 7)
    cmds
}

// Ask the controller for the water temperature
private List getWaterTemp() {
    log.debug "getWaterTemp()"
    [zwave.sensorMultilevelV1.sensorMultilevelGet()]
}

//Reports

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
	state.VersionInfo = "Versions: Firmware v${cmd.applicationVersion}.${cmd.applicationSubVersion}   DTH: ${VERSION}   zWaveLibraryType: ${cmd.zWaveLibraryType}    zWaveProtocol: v${cmd.zWaveProtocolVersion}.${cmd.zWaveProtocolSubVersion}"
	createEvent(name: "VersionInfo", value: state.VersionInfo, displayed: false, descriptionText: state.VersionInfo)
}

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionCommandClassReport cmd) {
	if (cmd.commandClassVersion) {
    	def cls = String.format("%02X", cmd.requestedCommandClass)
        state.ccVersions[cls] = cmd.commandClassVersion
		createEvent(name: "ccVersions", value: util.toJson(state.ccVersions), displayed: false, descriptionText:"")
	} else {
    	[]
    }
}

def zwaveEvent(physicalgraph.zwave.commands.clockv1.ClockReport cmd) {
	def time1 = ""
    time1 = "${String.format("%02d",cmd.hour)}:${String.format("%02d",cmd.minute)}"
	log.debug "from PE653: ${time1}"    
	state.VersionInfo = "ClockReport: ${time1}"
	createEvent(name: "clock", value: "${time1}", displayed: false, descriptionText: "PE653 Clock: ${time1}")
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointSupportedReport cmd) {
//	log.debug "thermostatSetpointSupportedReport !!!"
	def cmds = []
    state.cnfSendParmOne = 1		// Resend the request for "Guarenteed response"
    state.cnfParallelGets = 0		// Reset for unresponsive parm numbers
    if (debugLevel > "1") {
        cmds.addAll(nextConfig())
	}
    
	cmds
}

def zwaveEventManufacturerProprietary(byte [] payload, payloadStr) {
	log.debug "ManufacturerProprietary event, [1]:${String.format("%02X",payload[1])}  [4]:${String.format("%02X",payload[4])}  payload: ${payloadStr}"
    def rslt = []
	byte [] oldResp  = [1,2,3,4]
	def respType = 0
	def diffCnt = 0
	def oldP = ""
    def newP = ""
	def oldD = ""
    def newD = ""
    def head = ""
    if (payload[1] == 0x40 && payload[4] == -124) {
    	respType = 84
        oldResp = state.manProp1
        state.manProp1 = payload
        rslt = process84Event(payload)
    } else if (payload[1] == 0x40 && payload[4] == -121) {
    	respType = 87
        oldResp = state.manProp2
        state.manProp2 = payload
        rslt = process87Event(payload)
    } else if (payload[1] == 0x41) {
    	respType = 41
        oldResp = state.manProp3
        state.manProp3 = payload
//        rslt = process41Event(payload)
     } else {
     	log.warn "Unexpected ManufacturersProprietary event received !!"
     }
//     log.debug "respType:${respType}  oldResp: ${oldResp}"
     if (oldResp == null) {oldResp = (byte[])[0,1,2,3,4] as byte [];log.debug "==null forced to array"}
     for (def i=0;i<payload.length;i++) {
   		oldP += " ${String.format("%03X",oldResp[i])}"
   		newP += " ${String.format("%03X",payload[i])}"
   		oldD += " ${String.format("%03d",oldResp[i])}"
   		newD += " ${String.format("%03d",payload[i])}"
		if (oldResp[i] != payload[i] && (
        	(respType == 84 && (i != CLOCK_MINUTE_84 && i != CLOCK_HOUR_84)) ||
        	(respType == 87 && (i != CLOCK_MINUTE_87 && i != CLOCK_HOUR_87)) ||
        	(respType == 41 && (i != 99))
        )) {
        	diffCnt++
            head += " ${String.format("%03d",i)} "
        } else {
            head += "___ "
        }
     }
     if (diffCnt && debugLevel >= "1") {
//	     log.debug "respType:${respType}  differences:${diffCnt}\n__ __ ${head}\nnew-: ${newP}\nold-- :   ${oldP}\nnew: ${newD}\nold- :   ${oldD}"
	     log.debug "respType:${respType}  differences:${diffCnt}\n__ __ ${head}\nnew-: ${newP}\nold-- :   ${oldP}"
     } 
	rslt
}

	def getCLOCK_MINUTE_84 () { 16 }			// Clock Minute
	def getCLOCK_HOUR_84 () { 15 }				// Clock Hour
	def getWATER_TEMP_84 () { 12 }				// Water Temperature
	def getAIR_TEMP_84 () { 13 }				// Air Temperature
	def getSWITCHES_84 () { 8 }					// Bit mask of 5 switches. SW1 = 01X, SW5 = 10X
	def getPOOL_SPA_MODE_84 () { 11 }			// Pool/Spa mode. 01x Pool mode, 00x Spa mode
	def getVSP_SPEED_84 () { 20 }				// VSP Speed bit mask. 01x = VSP1, 08x = VSP4

// Received a ManufacturerProprietary message. Pull the important details and update the UI controls
def process84Event(byte [] payload) {
//	log.debug "process84Event payload: ${payload}"
    def rslt = []
    def map = [:]
	def str = ""
    def val = 0
	def ch = 0

	def swMap = ['1':1, '2':2, '3':4, '4':8, '5':16]
	for (sw in swMap) {
    	if (payload[SWITCHES_84] & sw.value) {
            val = 0xFF
        } else {
            val = 0
        }
		rslt.addAll(createMultipleEvents(sw.key.toInteger(), val, (val == 0) ? "off": "on"))
    }

	for (vsp in ['1':1, '2':2, '3':4, '4':8]) {
    	if (payload[VSP_SPEED_84] & vsp.value) {
            val = 0xFF
        } else {
            val = 0
        }
        ch = getVSP_EP(vsp.key.toInteger())
		rslt.addAll(createMultipleEvents(ch, val, (val == 0) ? "off": "on"))
    }

//	Set Pool/Spa mode indicator
	val = ((payload[POOL_SPA_MODE_84] & 0x01) == 0) ? 0xFF : 0
	rslt.addAll(createMultipleEvents(POOL_SPA_EP, val, (val == 0) ? "off": "on"))

//	Update Water Temperature
    rslt << createEvent(name: "temperature", value: payload[WATER_TEMP_84], unit: "F", displayed: false)

//	Update Air Temperature
    rslt << createEvent(name: "airTemp", value: "${payload[AIR_TEMP_84]}", unit: "F", displayed: false)

//	Update Clock
    def time1 = "${String.format("%02d",payload[CLOCK_HOUR_84])}:${String.format("%02d",payload[CLOCK_MINUTE_84])}"
//	log.debug "from PE653: ${time1}"
	state.VersionInfo = "ClockReport: ${time1}"
	rslt << createEvent(name: "clock", value: "${time1}", displayed: false, descriptionText: "PE653 Clock: ${time1}")

	rslt
}

	def getHEATER_87 () { 15 }					// Heater. 04x = on, 00x = off
	def getCLOCK_MINUTE_87 () { 25 }			// Clock Minute
	def getCLOCK_HOUR_87 () { 24 }				// Clock Hour

// Received a ManufacturerProprietary message. Pull the important details and update the UI controls
def process87Event(byte [] payload) {
//	log.debug "process87Event payload: ${payload}"
    def rslt = []
    def val = ((payload[HEATER_87] & 0x04) == 0) ? "off" : "on"
    rslt << createEvent(name: "heater", value: "$val", isStateChange: true, displayed: true, descriptionText: "Heater is ${val}")

	rslt
}

// ManufacturerSpecificReport(manufacturerId: 5, manufacturerName: Intermatic, productId: 1619, productTypeId: 20549) 
def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv1.ManufacturerSpecificReport cmd) {
	log.debug "ManufacturerSpecificReport !!!"
	state.ManufacturerInfo = "ManufacturerInfo:  manufacturerId: $manufacturerId, manufacturerName: $manufacturerName, productId: $productId, productTypeId: $productTypeId"
	createEvent(name: "ManufacturerInfo", value: state.ManufacturerInfo, displayed: true, descriptionText: state.ManufacturerInfo)
}

def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
    def map = [:]
	def cmds = []
    def myValue = ""
    def externalValue = ""
    def paramNum = cmd.parameterNumber
	map.value = cmd.configurationValue[0]
    map.name = ""
	map.displayed = true
    switch (paramNum) {
        case 1:
			map.name = "operationMode"
			cmds << createEvent(map)
			break;
        case 2:
			map.name = "firemanTimeout"
			cmds << createEvent(map)
			break;
        case 3:
			map.name = "temperatureOffsets"
			cmds << createEvent(map)
			break;
	}
    if (debugLevel > "1") {
        def lst = [cmd.size]
	    state.cnfAttemptsLeft[paramNum] = 0
		state.cnfParallelGets = state.cnfParallelGets - 1
        for (def i=0;i<cmd.size;i++) {
            lst << cmd.configurationValue[i]
        }
        state.cnfData[paramNum] = lst
        cmds.addAll(nextConfig())
	}
	cmds
}

// Display the previous and current configuration data and the differences
private List compareConfig() {
//	log.trace "cnfData1:${state.cnfData}"
//	log.trace "cnfData2:${state.cnfData2}"
    def dif1 = state.cnfData - state.cnfData2
    def dif2 = state.cnfData2 - state.cnfData
	log.trace "dif1:$dif1"    
	log.trace "dif2:$dif2"
    []
}

// Save any previous configuration data, then initiializes and initiates a fresh set of queries
private List startConfig() {
	if (state.cnfData) {
		state.cnfData2 = state.cnfData
    } else {
		state.cnfData2 = [:]
    }
    
	state.cnfData = [:]
    state.cnfAttemptsLeft = []
	state.cnfParallelGets = 0
    state.cnfGetGoal = 10
	state.cnfSendParmOne = 1
	def cmds = []

    for (def i=0;i<=75;i++) {
    	state.cnfAttemptsLeft[i] = 3
    }
	state.cnfAttemptsLeft[0] = 2
	nextConfig()
}

// Prepare the next batch of configuration queries
private List nextConfig() {
	def cmds = []
	while ((state.cnfGetGoal > 0) && (state.cnfParallelGets < state.cnfGetGoal)) {
        def nextInx = -1
	    def maxAttemptsLeft = 0
        state.cnfAttemptsLeft.eachWithIndex {itm, i ->
            if (i > 1 && itm > maxAttemptsLeft) {
                nextInx = i
                maxAttemptsLeft = itm
            }
        }
		if (nextInx >= 0) {
            state.cnfParallelGets = state.cnfParallelGets + 1
			state.cnfAttemptsLeft[nextInx] = state.cnfAttemptsLeft[nextInx] - 1
//			log.debug "nextConfig() nextInx:$nextInx  maxAttemptsLeft:$maxAttemptsLeft cnfParallelGets:${state.cnfParallelGets}"
            cmds << zwave.configurationV2.configurationGet(parameterNumber: nextInx)
        } else {
            state.cnfGetGoal = 0
	        state.cnfSendParmOne = 0
            log.trace "Config Get Complete: cnfData:${state.cnfData}"
            compareConfig()
        }
	}
	if (state.cnfSendParmOne == 1) {
	    cmds << zwave.thermostatSetpointV1.thermostatSetpointSupportedGet()
        state.cnfSendParmOne = 0
	}    
	if (state.cnfGetGoal) {
//		runIn(5, timerConfig)
    }
	cmds
}

// Called from runIn timer
def List timerConfig() {
	delayResponseLog(restartConfig())
}

// Push out the next batch of configuration queries
private List restartConfig() {
	state.cnfParallelGets = 0
	state.cnfSendParmOne = 1
	nextConfig()
}	

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv1.SensorMultilevelReport cmd) {
    def map = [:]
    map.value = cmd.scaledSensorValue.toString()
    map.unit = cmd.scale == 1 ? "F" : "C"
    map.name = "temperature"
    createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.thermostatsetpointv1.ThermostatSetpointReport cmd) {
	def map = [:]
	map.value = cmd.scaledValue.toString()
	map.unit = cmd.scale == 1 ? "F" : "C"
	map.displayed = true
	switch (cmd.setpointType) {
		case 1:
			map.name = "poolSetpoint"
			break;
		case 7:
			map.name = "spaSetpoint"
			break;
		default:
			return [:]
	}
	// So we can respond with same format
	state.size = cmd.size
	state.scale = cmd.scale
	state.precision = cmd.precision
	createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
    if (cmd.value == 0) {
		createEvent(name: "switch", value: "off")
	} else if (cmd.value == 255) {
		createEvent(name: "switch", value: "on")
	}
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
    def result = []
    if (cmd.value == 0) {
		result = createEvent(name: "switch", value: "off")
	} else if (cmd.value == 255) {
		result = createEvent(name: "switch", value: "on")
	}
    result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicGet cmd) {
    def cmds = []
	int val = device.currentValue("switch1").equals("on") ? 0xFF : 0
    cmds << zwave.basicV1.basicReport(value: val)
	delayBetweenLog(cmds)
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceReport cmd) {
	log.debug("MultiInstanceReport cmd=${cmd}")
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelEndPointReport cmd) {
	log.debug("MultiChannelEndPointReport cmd=${cmd}")
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
    []
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationGroupingsReport cmd) {
    state.groups = cmd.supportedGroupings
    []
}


// Multi-channel event from the device
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceCmdEncap cmd) {
	def rslt = []
    def String myStr = (cmd.parameter[0] == 0) ? "off": "on"
    def sw = 0
	def encapsulatedCommand = cmd.encapsulatedCommand([0x32: 3, 0x25: 1, 0x20: 1])
	if (encapsulatedCommand) {
		switch(cmd.instance) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
                sw = cmd.instance
                break;

            case POOL_SPA_CHAN:
                sw = POOL_SPA_EP
                break;
                
            case getVSP_CHAN_NO(1):
            case getVSP_CHAN_NO(2):
            case getVSP_CHAN_NO(3):
            case getVSP_CHAN_NO(4):
                sw = (cmd.instance - getVSP_CHAN_NO(1)) + 7
                break;
			default:
                log.warn "..... MultiInstanceCmdEncap  - UNKNOWN INSTANCE=${cmd.instance}"
                return []
        }

//    def String myStr = (cmd.parameter[0] == 0) ? "off": "on"
		rslt.addAll(createMultipleEvents(sw, cmd.parameter[0], myStr))
	} else {
		log.warn "MultiInstanceCmdEncap: Could not de-encapsulate!!!"
	}
    rslt
}

// Used to update our own switches state as well as the exposed Multi-channel switches
private List sendMultipleEvents (Integer endpoint, Integer externalParm, String myParm) {
	if (debugLevel > "1") {
        log.debug "..... sendMultipleEvents( endpoint:$endpoint, externalParm:$externalParm, myParm:$myParm)"
    }
	def rslt = createMultipleEvents(endpoint, externalParm, myParm)
	rslt.each {e ->
		if (debugLevel > "0") {
			log.debug "<<<<< Event: $e"
        }
		sendEvent(e)
    }
	null
}


// Used to update our own switches state as well as the child devices
// Two Events: One event is immediately sent to the child device and another is returned to our own UI control
private List createMultipleEvents (Integer endpoint, Integer externalParm, String myParm) {
	def rslt = []
	if (debugLevel > "1") {
        log.debug "..... createMultipleEvents( endpoint:$endpoint, externalParm:$externalParm, myParm:$myParm)"
    }

	def children = getChildDevices()
//	log.debug("children.size = ${children.size}")
//	children.each {ch ->
//    	log.debug("ch ${ch}")
//	}

    def dni = "${device.deviceNetworkId}-ep${endpoint}"
	def devObj = getChildDevices()?.find { it.deviceNetworkId == dni }
//	log.debug("CME: devObj = ${devObj}")
	if (devObj) {
		devObj.sendEvent(name: "switch", value: "$myParm", isStateChange: true, displayed: true, descriptionText: "$myParm event sent from parent device")
        rslt << "Note:Event ${myParm} to child: ${devObj}"
    } else {
    	log.trace "CME: CANT'T FIND CHILD DEVICE: ${dni}"
    }

    def sw = getSWITCH_NAME(endpoint)
    rslt << createEvent(name: "$sw", value: "$myParm", isStateChange: true, displayed: true, descriptionText: "($sw set to $myParm)")
    rslt
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.warn "Captured zwave command $cmd"
	createEvent(descriptionText: "$device.displayName: $cmd", isStateChange: true)
}

//Commands

// Called occasionally although not consistently
def List poll() {
	log.debug "+++++ poll()"
    delayBetweenLog(addRefreshCmds([]))
}

private initUILabels() {
	sendEvent(name: "M1Name", value: (M1Label ? "${M1Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M1 Label to ${M1Label}")
	sendEvent(name: "M2Name", value: (M2Label ? "${M2Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M2 Label to ${M2Label}")
	sendEvent(name: "M3Name", value: (M3Label ? "${M3Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M3 Label to ${M3Label}")
	sendEvent(name: "M4Name", value: (M4Label ? "${M4Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M4 Label to ${M4Label}")
    if ( !VSP_ENABLED ) {
    	sendEvent(name: "swVSP1", value: "disabled", displayed: true, descriptionText:"")
    	sendEvent(name: "swVSP2", value: "disabled", displayed: true, descriptionText:"")
    	sendEvent(name: "swVSP3", value: "disabled", displayed: true, descriptionText:"")
    	sendEvent(name: "swVSP4", value: "disabled", displayed: true, descriptionText:"")
    }
	if ( !POOL_SPA_COMBO ) {
    	sendEvent(name: "poolSpaMode", value: "disabled", displayed: true, descriptionText:"poolSpaMode is disabled")
    }
}

// Called only by an explicit push of the "Refresh" button
def List refresh() {
	log.debug "+++++ refresh()  DTH:${VERSION}  state.Versioninfo=${state.VersionInfo}"
    def cmds = []
    
/*
	cmds.addAll(getPoolSpaMode())
	for (int sw=1;sw<=5;sw++) {		// Request state of all 5 PE653 switches
		cmds.addAll(getChanState( sw ))
	}
    cmds.addAll(getVSPSpeed())
*/
	cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1)
    cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 7)

	cmds << zwave.versionV1.versionGet()
	cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet()
//	cmds << zwave.associationV2.associationGroupingsGet()
//	cmds << zwave.multiInstanceV1.multiInstanceGet(commandClass:37)

 	if (debugLevel <= "1") {
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 1)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 2)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 3)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 19)
	} else {
    	compareConfig()
        state.ccVersions = [:]
        getSupportedCmdClasses().each {cc ->
            cmds << zwave.versionV1.versionCommandClassGet(requestedCommandClass: cc)
        }
	    cmds.addAll( restartConfig() )
    }
	delayBetweenLog(addRefreshCmds(cmds))
}

def List updated() {
	log.debug "+++++ updated()    DTH:${VERSION}  state.Versioninfo=${state.VersionInfo}"

	createChildDevices()
	initUILabels()
    state.lightCircuitsList = getLightCircuits()
	delayBetweenLog(addRefreshCmds(internalConfigure()))
}

def List configure() {
	log.debug "+++++ configure()    DTH:${VERSION}  state.Versioninfo=${state.VersionInfo}"
	initUILabels()
	delayBetweenLog(addRefreshCmds(internalConfigure()))
}

private List internalConfigure() {
    def opMode2 = operationMode2.toInteger() & 0x03
    def int tempWater = tempOffsetwater.toInteger()
    def int tempAir   = tempOffsetair.toInteger()
	def cmds = []
	if (tempWater < 0) tempWater = 256 + tempWater
	if (tempAir < 0)   tempAir   = 256 + tempAir

    cmds << zwave.configurationV2.configurationSet(parameterNumber: 1,  size: 2, configurationValue: [operationMode1.toInteger(), opMode2.toInteger()])
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 3,  size: 4, configurationValue: [tempWater, tempAir, 0, 0])
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 19, size: 1, configurationValue: [poolSpa1.toInteger()])
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 2,  size: 1, configurationValue: [fireman.toInteger()])

	if (debugLevel <= "1") {
		cmds << zwave.configurationV2.configurationGet(parameterNumber: 1)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 2)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 3)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 19)
	} else {
		cmds.addAll(startConfig())
		log.trace "state=$state"
    }
	log.trace "VSP_ENABLED: ${VSP_ENABLED}"
    if ( VSP_ENABLED ) {
    	cmds.addAll(getVSPSpeed())
    }
	log.trace "POOL_SPA_COMBO:${POOL_SPA_COMBO}"
	if ( POOL_SPA_COMBO ) {
    	cmds.addAll(getPoolSpaMode())
    }

//    cmds << zwave.associationV2.associationGroupingsGet()
//    cmds << zwave.associationV2.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId)
//    cmds << zwave.associationV2.associationGet(groupingIdentifier:1)
	cmds
}

private void createChildDevices() {
	state.oldLabel = device.label
	def oldChildren = getChildDevices()
	log.trace("Existing children: ${oldChildren}")

	for (childNo in 1..5) {
		addOrReuseChildDevice(childNo, "${device.displayName} (S${childNo})", oldChildren)
    }
	if ( POOL_SPA_COMBO ) {
		def childNo = 6
		addOrReuseChildDevice(childNo, "${device.displayName} (Pool/Spa)", oldChildren)
	}
	if ( VSP_ENABLED ) {
        for (childNo in 7..10) {
			addOrReuseChildDevice(childNo, "${device.displayName} (VSP${childNo-6})", oldChildren)
        }
    }
	removeChildDevices(oldChildren)
}

private Object addOrReuseChildDevice(childNo, name, List oldChildren){
	def Object devObj = null
    def dni = "${device.deviceNetworkId}-ep${childNo}"
//	log.trace("addOrReuseChildDevice dni=${dni} oldChildren.size=${oldChildren.size} oldChildren: ${oldChildren}")

	devObj = oldChildren.find {it.deviceNetworkId == dni}
    if ( devObj ) {
//		log.trace("found existing device=${devObj.name} dni=${devObj.deviceNetworkId}")
    	oldChildren.remove(devObj)
//		log.trace(" after remove dni=${dni} oldChildren.size=${oldChildren.size}")
    } else {
        try {
            log.trace("addChildDevice(namespace=\"erocm123\",DTH Name=\"Switch Child Device\", dni=\"${dni}\", hubId=null,"+
                      "properties=[completedSetup: true, label: \"${name}\","+
                      "isComponent: false, componentName: \"ep${childNo}\", componentLabel: \"Switch ${childNo}\"]")

            addChildDevice("erocm123", "Switch Child Device", dni, null, [completedSetup: true, label: name,
                            isComponent: false, componentName: "ep${childNo}", componentLabel: "Switch ${childNo}"])
        } catch (e) {
            log.trace("addChildDevice failed: ${e}")
        }
	}
}

private removeChildDevices(List oldChildren){
	log.debug("RemoveChildDevices(before) count=${oldChildren.size} children: ${oldChildren}")
    try {
        oldChildren.each {child ->
//	    	log.debug("remove child name=${child.name} displayName=${child.displayName} label=${child.label} dni=${child.deviceNetworkId} id=${child.id}")
            deleteChildDevice(child.deviceNetworkId)
        }
    } catch (e) {
        log.debug "Error deleting ${child}, either it didn't exist or probably locked into a SmartApps: ${e}"
    }
}

// Request a report back from with the Clock time from the PE653
private List getClock() {
	log.debug "+++++ getClock"
	def cmds =[zwave.clockV1.clockGet()]
	cmds
}

// Set the PE653 clock from the mobile client clock
def List setClock() {
	def cmds = []
	log.debug "+++++ setClock()"
    def nowCal = Calendar.getInstance(location.timeZone)
	def time2 = "${String.format("%02d",nowCal.get(Calendar.HOUR_OF_DAY))}:${String.format("%02d",nowCal.get(Calendar.MINUTE))}"
//	log.debug "Time:${time2}"
	cmds << zwave.clockV1.clockSet(hour: "${nowCal.get(Calendar.HOUR_OF_DAY)}".toInteger(), minute: "${nowCal.get(Calendar.MINUTE)}".toInteger())
	cmds << createEvent(name: "clock", value: "${time2}", displayed: false, descriptionText: "PE653 Clock: ${time2}")

	delayBetweenLog(cmds)
}

// Query the four VSP scheduled to determine which speed is enabled
private List getVSPSpeed() {
	def cmds = []
	log.debug "+++++ getVSPSpeed()"
    if ( VSP_ENABLED ) {
        state.pumpSpeed = '0'		// Assume off unless a schedule is returned on
        for (int sp=1;sp<=4;sp++) {
            cmds.addAll(getChanState(getVSP_CHAN_NO(sp)))
        }
    }
	cmds
}

// Select a VSP speed and request a report to confirm
private List setVSPSpeedAndGet(Integer speed) {
	log.debug "+++++ setVSPSpeedAndGet()  speed=${speed}"
	def cmds = []
	cmds.addAll(setVSPSpeedInternal(speed))
	cmds.addAll(getVSPSpeed())
	cmds
}

// Select a VSP speed by forcing the appropriate schedule to always on. speed is from 1-4
// A speed of zero will disable all 4 speeds (off).
// Called based on commands from the Multi-channel SmartApp
private List setVSPSpeedInternal(Integer speed) {
//	log.debug "+++++ setVSPSpeedInternal()  speed=${speed}"
	def cmds = []
    if (speed) {
		cmds.addAll(setChanState(getVSP_CHAN_NO(speed),0xFF))
    } else {
        for (int sp=1;sp<=4;sp++) {
            cmds.addAll(setChanState(getVSP_CHAN_NO(sp), 0))
//          The following should not be necessary except I don't consistently get replies to the ConfigurationGet
//    		sendMultipleEvents ((getVSP_EP(sp)), speed, "$speed")
    	}
    }
	cmds
}

def List getPoolSpaMode() {
	def cmds = []
	if ( POOL_SPA_COMBO ) {
		cmds = getChanState(POOL_SPA_CHAN)
    }
    cmds
}

private List setPoolSpaMode(Integer val) {
	def cmds = []
	def myValue = ""
	cmds.addAll(setChanState(POOL_SPA_CHAN, val))
	if (val == 0xFF) {
        myValue = "on"
    } else {
        myValue = "off"
    }
//  The following should not be necessary except I don't consistently get replies to the BasicSet on Channel 6 (inst: 0x27)
//	sendMultipleEvents (POOL_SPA_EP, val, myValue)
    cmds
}

private List setSpaModeInternal() {
	log.debug "+++++ setSpaModeInternal"
	def cmds = []
	cmds.addAll(setPoolSpaMode(0xFF))
//	cmds.addAll(getPoolSpaMode())
	cmds
}

private List setPoolModeInternal() {
	log.debug "+++++ setPoolMode"
	def cmds = []
	cmds.addAll(setPoolSpaMode(0))
//	cmds.addAll(getPoolSpaMode())
	cmds
}

private def List togglePoolSpaModeInternal() {
	log.debug "+++++ togglePoolSpaMode: poolSpaMode:${device.currentValue("poolSpaMode")}"
	def cmds = []
    if (device.currentValue("poolSpaMode").equals("on")) {
		cmds.addAll(setPoolSpaMode(0))
    } else {
		cmds.addAll(setPoolSpaMode(0xFF))
    }
//	cmds.addAll(getPoolSpaMode())
	cmds
}

// General purpose function to set a schedule to "Always off" or "Always on"
private List setSched(int paramNum, Integer val) {
 	if (debugLevel > "1") {
//		log.debug "+++++ setSched(paramNum:${paramNum}, val:$val)"
    }
	def cmds = []
	if (val == 0) {
        cmds << zwave.configurationV2.configurationSet(configurationValue: [0xFF, 0xFF, 0xFF, 0xFF], size: 4, parameterNumber: paramNum)
    } else {
        cmds << zwave.configurationV2.configurationSet(configurationValue: [0x01, 0x00, 0x9F, 0x05], size: 4, parameterNumber: paramNum)
    }
	cmds
}

def setLightColor(int col) {
	log.debug "+++++ setColor ${col}"
	def cmds = []
	cmds = createEvent(name: "lightColor", value: "${col}", isStateChange: true, displayed: true, descriptionText: "Color set to ${col}")
	delayBetweenLog(cmds)
}

def setColor() {
	def cmds = []
	int blinkCnt = device.currentValue("lightColor").toInteger()
	if (blinkCnt > 14) blinkCnt = 14;
    if (state.lightCircuitsList) {
		cmds.addAll(blink(state.lightCircuitsList, blinkCnt))
    }
	delayBetweenLog(cmds)
}

// Return a list of the Light Circuits selected to have color set
def List getLightCircuits() {
	def lightCircuits = []
	if (C1ColorEnabled == "1") {lightCircuits << 1}
	if (C2ColorEnabled == "1") {lightCircuits << 2}
	if (C3ColorEnabled == "1") {lightCircuits << 3}
	if (C4ColorEnabled == "1") {lightCircuits << 4}
	if (C5ColorEnabled == "1") {lightCircuits << 5}
//	log.trace("lightCircuits=${lightCircuits}  C3ColorEnabled=${C3ColorEnabled}")    
	lightCircuits
}

// Alternately turn a switch off then on a fixed number of times. Used to control the color of Pentair pool lights.
private def blink(List switches, int cnt) {
	log.debug "+++++ blink switches=${switches} cnt=${cnt}"
    def cmds = []
    def dly = MIN_DELAY
	for (int i=1; i<=cnt; i++) {
    	switches.each { sw ->
			if (cmds) {
				cmds << "delay ${dly}"
            }
//            cmds << zwave.multiInstanceV1.multiInstanceCmdEncap(instance: sw, commandClass:37, command:1, parameter:[0])
            cmds.addAll(setChanState(sw, 0))
		    dly = MIN_DELAY
        }
        dly = "${DELAY}"
		switches.each { sw ->
	        cmds << "delay ${dly}"
//            cmds << zwave.multiInstanceV1.multiInstanceCmdEncap(instance: sw, commandClass:37, command:1, parameter:[255])
            cmds.addAll(setChanState(sw, 0xFF))
		    dly = MIN_DELAY
        }
        dly = "${DELAY}"
    }
	switches.each { sw ->
		cmds << "delay ${dly}"
//		cmds <<	zwave.multiInstanceV1.multiInstanceCmdEncap(instance: sw, commandClass:37, command:2)
        cmds.addAll(getChanState(sw))
    }
//log.trace "blink() cmds=${cmds}"
	cmds
}

// Called by a button press for one of the "Mode" selections  (eg: M1, M2, M3, M4)
def setMode(int mode) {
	def cmds = []
	List MxSw
	String MxMode, MxTemp, MxVSP
//	log.trace "M1Sw1=${M1Sw1} M1Sw2=${M1Sw2} M1Sw3=${M1Sw3} M1Sw4=${M1Sw4} M1Sw5=${M1Sw5} M1Mode=${M1Mode} M1Temp=${M1Temp} M1VSP=${M1VSP}"
	switch(mode) {
    case 1:
    	MxSw = [M1Sw1, M1Sw2, M1Sw3, M1Sw4, M1Sw5]; MxMode = M1Mode; MxTemp = M1Temp; MxVSP = M1VSP; break
    case 2:
    	MxSw = [M2Sw1, M2Sw2, M2Sw3, M2Sw4, M2Sw5]; MxMode = M2Mode; MxTemp = M2Temp; MxVSP = M2VSP; break
    case 3:
    	MxSw = [M3Sw1, M3Sw2, M3Sw3, M3Sw4, M3Sw5]; MxMode = M3Mode; MxTemp = M3Temp; MxVSP = M3VSP; break
    case 4:
    	MxSw = [M4Sw1, M4Sw2, M4Sw3, M4Sw4, M4Sw5]; MxMode = M4Mode; MxTemp = M4Temp; MxVSP = M4VSP; break
    }
	log.debug "+++++ setMode ${mode} MxSw=${MxSw} MxMode=${MxMode} MxTemp=${MxTemp} MxVSP=${MxVSP}"

    if (MxMode == "1") {
        cmds.addAll(setPoolModeInternal())
    } else if (MxMode == "2") {
        cmds.addAll(setPoolModeInternal())
        cmds.addAll(setPoolSetpointInternal(MxTemp.toDouble()))
    } else if (MxMode == "3") {
        cmds.addAll(setSpaModeInternal())
    } else if (MxMode == "4") {
        cmds.addAll(setSpaModeInternal())
        cmds.addAll(setSpaSetpointInternal(MxTemp.toDouble()))
    }
	MxSw.eachWithIndex {action, idx ->
//    	log.debug " action=${action} idx=${idx}"
    	if (action == "1") {
        	cmds.addAll(setChanState(idx.toInteger()+1,1))
        } else if (action == "2") {
        	cmds.addAll(setChanState(idx.toInteger()+1,0))
        }
    }
    if (VSP_ENABLED && MxVSP != "5") {
    	cmds.addAll(setVSPSpeedInternal(MxVSP.toInteger()))
    }
//	cmds.addAll(getRefreshCmds())
//log.trace "setMode: cmds(before)=${cmds}"
	cmds
}

// Called from anywhere that needs the UI controls updated following a Set
private List getRefreshCmds() {
	log.debug "+++++ getRefreshCmds"
	def cmds =[
		new physicalgraph.device.HubAction("910005400102870301"),
		new physicalgraph.device.HubAction("910005400101830101"),
//		new physicalgraph.device.HubAction("91000541010100"),
	]
	cmds
}

private List getTestCmds() {
	log.debug "+++++ getTestCmds"
	def cmds =[
//		new physicalgraph.device.HubAction("91000541010100"),
//		zwave.manufacturerProprietaryV1.manufacturerProprietary(payload: "05400101830101")
	]
	cmds
}


def on() {
	log.debug "+++++ on()"
    delayBetweenLog([
        zwave.basicV1.basicSet(value: 0xFF),
        zwave.basicV1.basicGet()
    ])
}

def off() {
	log.debug "+++++ off()"
    delayBetweenLog([
        zwave.basicV1.basicSet(value: 0x00),
        zwave.basicV1.basicGet()
    ])
}

//Request Switch State
private List getChanState(ch) {
	log.debug "+++++ getChanState($ch)"
	def cmds =[
	    zwave.multiInstanceV1.multiInstanceCmdEncap(instance:ch).encapsulate(zwave.switchBinaryV1.switchBinaryGet())
	]
}

// Set switch instance on/off
private List setChanState(ch, on) {
	log.debug "+++++ setChanState($ch, $on)"
	def cmds =[
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: ch).encapsulate(zwave.switchBinaryV1.switchBinarySet(switchValue: (on ? 0xFF : 0))),
	]
}


// Set switch state and request report back
private List setChanStateAndGet(ch, on) {
	log.debug "+++++ setChanStateAndGet($ch, $on)"
	def cmds = []
	cmds = setChanState(ch, on)
    cmds.addAll(getChanState(ch))
	cmds
}

def List childOn(dni)  {
	log.trace("childOn called in parent: dni=${dni} channelNumber(dni)=${channelNumber(dni)}")
	delayBetweenLog(addRefreshCmds(cmdFromChild(channelNumber(dni), 0xFF)))
}

def List childOff(dni)  {
	log.trace("childOff called in parent: dni=${dni} channelNumber(dni)=${channelNumber(dni)}")
	delayBetweenLog(addRefreshCmds(cmdFromChild(channelNumber(dni), 0)))
}

def List refresh(dni)  {
	log.trace("refresh called in parent: dni=${dni} channelNumber(dni)=${channelNumber(dni)}")
//	delayBetweenLog(addRefreshCmds(cmdFromChild(channelNumber(dni), 0)))
}

private channelNumber(String dni) {
	dni.split("-ep")[-1] as Integer
}

// On or Off from a child device. Take action depending on which type of child device
private List cmdFromChild(int childNo, int val) {
	def rslt = []
	if (debugLevel > "0") {
	    log.debug "+++++ cmdFromChild: childNo:$childNo  val:$val"
    }

    switch (childNo) {
        case 1:
        case 2:
        case 3:
        case 4:
        case 5:
	        rslt.addAll(setChanState(childNo, val))
        break
        case POOL_SPA_EP:
			if (val) {
	        	rslt.addAll(setSpaModeInternal())
            } else {
	        	rslt.addAll(setPoolModeInternal())
            }
        break
        case getVSP_EP(1):
        case getVSP_EP(2):
        case getVSP_EP(3):
        case getVSP_EP(4):
        // Convert switch endpoint to a VSP speed
			if (val) {
				rslt.addAll(setVSPSpeedInternal( childNo - 6 ))
           	} else {
		        rslt.addAll(setVSPSpeedInternal( 0 ))
            }
        break
    }
	rslt
}

def List addRefreshCmds(List cmds)  {
	cmds.addAll(getRefreshCmds())
    cmds
}

// Called by switch presses on the circuit buttons.
def List on1()  { delayBetweenLog(addRefreshCmds(setChanState(1, 0xFF))) }
def List on2()  { delayBetweenLog(addRefreshCmds(setChanState(2, 0xFF))) }
def List on3()  { delayBetweenLog(addRefreshCmds(setChanState(3, 0xFF))) }
def List on4()  { delayBetweenLog(addRefreshCmds(setChanState(4, 0xFF))) }
def List on5()  { delayBetweenLog(addRefreshCmds(setChanState(5, 0xFF))) }
def List off1() { delayBetweenLog(addRefreshCmds(setChanState(1, 0))) }
def List off2() { delayBetweenLog(addRefreshCmds(setChanState(2, 0))) }
def List off3() { delayBetweenLog(addRefreshCmds(setChanState(3, 0))) }
def List off4() { delayBetweenLog(addRefreshCmds(setChanState(4, 0))) }
def List off5() { delayBetweenLog(saddRefreshCmds(etChanState(5, 0))) }

// Called by individual button methods below
def List setVSPSpeed(sp)       {delayBetweenLog(setVSPSpeedAndGet(sp)) }
// Called by switch presses on the VSP buttons.
def List setVSPSpeed0()        {delayBetweenLog(addRefreshCmds(setVSPSpeedInternal(0))) }
def List setVSPSpeed1()        {delayBetweenLog(addRefreshCmds(setVSPSpeedInternal(1))) }
def List setVSPSpeed2()        {delayBetweenLog(addRefreshCmds(setVSPSpeedInternal(2))) }
def List setVSPSpeed3()        {delayBetweenLog(addRefreshCmds(setVSPSpeedInternal(3))) }
def List setVSPSpeed4()        {delayBetweenLog(addRefreshCmds(setVSPSpeedInternal(4))) }
def List setMode1()            {delayBetweenLog(addRefreshCmds(setMode(1))) }
def List setMode2()            {delayBetweenLog(addRefreshCmds(setMode(2))) }
def List setMode3()            {delayBetweenLog(addRefreshCmds(setMode(3))) }
def List setMode4()            {delayBetweenLog(addRefreshCmds(setMode(4))) }

def List setSpaMode()          {delayBetweenLog(addRefreshCmds(setSpaModeInternal())) }
def List setPoolMode()         {delayBetweenLog(addRefreshCmds(setPoolModeInternal())) }
def List togglePoolSpaMode()   {delayBetweenLog(addRefreshCmds(togglePoolSpaModeInternal())) }

def List quickSetSpa(degrees)  {delayBetweenLog(addRefreshCmds(setSpaSetpointInternal("${degrees}".toDouble())))}
def List quickSetPool(degrees) {delayBetweenLog(addRefreshCmds(setPoolSetpointInternal("${degrees}".toDouble())))}
def List quickGetWaterTemp()   {delayBetweenLog(addRefreshCmds(getTestCmds())) }


// Called from Parse for responses from the device
def delayResponseLog(parm, dly=DELAY, responseFlg=true) {
	delayBetweenLog(parm, dly, responseFlg)
}

// Called from all commands
def delayBetweenLog(parm, dly=DELAY, responseFlg=false) {
//	log.debug "delayBetweenLog parm[${parm.size}] dly=$dly responseFlg=${responseFlg}"
	def lst = parm
	def cmds =[]
	def evts =[]
	def devStr = ""
    def evtStr = ""
    def fmt = ""
    if (!(parm in List)) {
    	lst = [parm]
    }
    lst.each {l ->
	    if (l instanceof List) {
			log.warn "UNEXPECTED instanceOf List: l -> ${l}"
        } else if (l in List) {
			log.warn "UNEXPECTED in LIST: l -> ${l}"
        } else {
//			log.trace "l -> ${l}"
        }
		if (l instanceof physicalgraph.device.HubAction) {
            cmds << l
            devStr = devStr.concat("\n<<<<< HubAction: $l")
//        	log.trace "instanceof physicalgraph.device.HubAction"
        } else if (l instanceof String || l instanceof GString) {
        	if (l.take(5) == "Note:") {
                evtStr = evtStr.concat("\n<<<<< Event: $l")
            } else {
                cmds << l
                devStr = devStr.concat(", ${l}")
            }
//            log.trace "## String: $l"
        } else if (l instanceof List) {
            cmds << l
//            log.trace "#### LIST: $l"
        } else if (l instanceof Map) {
//          evts << l
// example:	createEvent(name: "$sw", value: "$myParm", isStateChange: true, displayed: true, descriptionText: "($sw set to $myParm)")
			if (device.currentValue(l.name) == l.value) {
//            	log.debug "<<<<< Event unnecessary. name:${l.name} dev:${device.currentValue(l.name)} == evt:${l.value}"
            } else {
            	log.debug "<<<<< Event NECESSARY. name:${l.name} dev:${device.currentValue(l.name)} == evt:${l.value}"
                evts << l
//                sendEvent(l)
                evtStr = evtStr.concat("\n<<<<< Event: $l")
            }
//            log.trace "## Map: $l"
        } else {
        	if (responseFlg) {
	            fmt = response(l)
            } else {
	            fmt = l.format()
            }
            if (cmds) {
				def c = cmds.last()			//check if there is already a delay prior to this
	            if (!(c instanceof String || c instanceof GString) || c.take(6) != "delay ") {
	                cmds << "delay $dly"
    	            devStr = devStr.concat(", delay $dly")
                }
            }
            devStr = devStr.concat("\n<<<<< Dev cmd: $l  --> $fmt")
            cmds << fmt
//			log.trace "## HubAction: $l,   format()=$fmt"
        }
    }
    evts.addAll(cmds)
	if (evts) {
        if (debugLevel > "0") {
            log.debug "<<<<< rspFlg=${responseFlg} dly:$dly/${DELAY}${evtStr}${devStr}"
        }
		evts
    } else {
        if (debugLevel > "0") {
            log.debug "<<<<< rspFlg=${responseFlg} dly:$dly/${DELAY} No Commands or Events"
        }
    	null
    }
}

// Only called from parse().
def old_delayResponseLog(parm, dly=DELAY) {
	def lst = parm
	def cmds =[]
	def evts =[]
	def devStr = ""
    def evtStr = ""
    if (!(parm in List)) {
    	lst = [parm]
    }
    lst.each {l ->
        if (l instanceof physicalgraph.device.HubAction) {
            cmds << l
            devStr = devStr.concat("\n<<<<< HubAction: $l")
//        	log.trace "instanceof physicalgraph.device.HubAction"
        } else if (l instanceof String) {
            cmds << l
//            log.trace "## String: $l"
        } else if (l instanceof List) {
            cmds << l
//            log.trace "#### LIST: $l"
        } else if (l instanceof Map) {
// example:	createEvent(name: "$sw", value: "$myParm", isStateChange: true, displayed: true, descriptionText: "($sw set to $myParm)")
			if (device.currentValue(l.name) == l.value) {
//            	log.debug "<<<<< Event unnecessary. name:${l.name} dev:${device.currentValue(l.name)} == evt:${l.value}"
            } else {
                evts << l
                evtStr = evtStr.concat("\n<<<<< Event: $l")
            	log.debug "<<<<< Event NECESSARY. name:${l.name} dev:${device.currentValue(l.name)} == evt:${l.value}"
            }
//            log.trace "## Map: $l"
        } else {
            def fmt = response(l)
            if (cmds) {
            	c = cmds.last()			//check if there is already a delay priot to this
	            if (c.take(6) != "delay ") {
                    cmds << "delay $dly"
                    devStr = devStr.concat(", delay $dly")
                }
            }
            devStr = devStr.concat("\n<<<<< Dev resp: $l  --> $fmt")
            cmds << fmt
//			log.trace "## HubAction: $l,   response()=$fmt"
        }
    }
    evts.addAll(cmds)
	if (evts) {
        if (debugLevel > "0") {
            log.debug "<<<<< dly:$dly/${DELAY}${evtStr}${devStr}"
        }
		evts
    } else {
        if (debugLevel > "0") {
            log.debug "<<<<< dly:$dly/${DELAY} No Commands or Events"
        }
    	null
    }
}