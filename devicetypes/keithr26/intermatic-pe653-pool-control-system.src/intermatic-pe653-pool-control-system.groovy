/**
 *  Intermatic PE653 Pool Control System
 *
 *  Original Copyright 2014 bigpunk6
 *  Updated 2017 KeithR26
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
 *  Don't use Cooper Lee's code (vTile_ms, ms_w_vts) he was working on a different implementation than me.
 *
 *  Install my device type then use the Multi-Channel Controll App by SmartThings from the Marketplace under the More section.
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
 *  2.05	05/13/2017	KeithR26	Debug version to triage Android issues
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

		attribute "switch1", "string"
		attribute "switch2", "string"
		attribute "switch3", "string"
		attribute "switch4", "string"
		attribute "switch5", "string"
		attribute "swVSP1", "string"
		attribute "swVSP2", "string"
		attribute "swVSP3", "string"
		attribute "swVSP4", "string"

        command "quickSetPool"
        command "quickSetSpa"
		command "quickGetWaterTemp"
		command "setPoolMode"
		command "setSpaMode"
		command "togglePoolSpaMode"
		command "onMulti"
		command "offMulti"
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
        command "updated"
//		command "epCmd"
//		command "enableEpEvents"
        
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
		input "tempOffsetwater", "number", title: "Water temperature offset", range: "-5..5", defaultValue: 0, required: true
        input "tempOffsetair", "number",
            title: "Air temperature offset - Sets the Offset of the air temerature for the add-on Thermometer in degrees Fahrenheit -5F to +5F", range: "-5..5", defaultValue: 0, required: true
        input "debugLevel", "enum", title: "Debug Level", multiple: "true",
        	options:[0:"Off",
            		 1:"Low",
                     2:"High"], defaultvalue: 0
        input "ZWdelay", "number",
            title: "Delay between Z-Wave commands sent (milliseconds). Suggest 1000.", defaultValue: 1000, required: true
//Mode 1
        input "M1Label", "text", title: "M1: Display Name:", defaultValue: ""
        input "M1Sw1", "enum", title: "M1: Circuit 1 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw2", "enum", title: "M1: Circuit 2 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw3", "enum", title: "M1: Circuit 3 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw4", "enum", title: "M1: Circuit 4 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Sw5", "enum", title: "M1: Circuit 5 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M1Mode", "enum", title: "M1: Mode to change to:", defaultValue: 0,
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M1Temp", "number", title: "M1: Set Temperature to:", range: "40..104", defaultValue: 40
        input "M1VSP", "enum", title: "M1: Set VSP Speed to:", defaultValue: 0,
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
//Mode 2
        input "M2Label", "text", title: "M2: Display Name:", defaultValue: ""
		input "M2Sw1", "enum", title: "M2: Circuit 1 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw2", "enum", title: "M2: Circuit 2 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw3", "enum", title: "M2: Circuit 3 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw4", "enum", title: "M2: Circuit 4 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Sw5", "enum", title: "M2: Circuit 5 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M2Mode", "enum", title: "M2: Mode to change to:", defaultValue: 0,
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M2Temp", "number", title: "M2: Set Temperature to:", range: "40..104", defaultValue: 40
        input "M2VSP", "enum", title: "M2: Set VSP Speed to:", defaultValue: 0,
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
//Mode 3
        input "M3Label", "text", title: "M3: Display Name:", defaultValue: ""
        input "M3Sw1", "enum", title: "M3: Circuit 1 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw2", "enum", title: "M3: Circuit 2 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw3", "enum", title: "M3: Circuit 3 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw4", "enum", title: "M3: Circuit 4 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Sw5", "enum", title: "M3: Circuit 5 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M3Mode", "enum", title: "M3: Mode to change to:", defaultValue: 0,
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M3Temp", "number", title: "M3: Set Temperature to:", range: "40..104", defaultValue: 40
        input "M3VSP", "enum", title: "M3: Set VSP Speed to:", defaultValue: 0,
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
//Mode 4
        input "M4Label", "text", title: "M4: Display Name:", defaultValue: ""
        input "M4Sw1", "enum", title: "M4: Circuit 1 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw2", "enum", title: "M4: Circuit 2 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw3", "enum", title: "M4: Circuit 3 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw4", "enum", title: "M4: Circuit 4 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Sw5", "enum", title: "M4: Circuit 5 action:", defaultValue: 0,
        	options:[0:"No Change",
            		 1:"On",
            		 2:"Off"]
        input "M4Mode", "enum", title: "M4: Mode to change to:", defaultValue: 0,
        	options:[0:"No change",
            		 1:"Pool",
                     2:"Pool & Set Temperature",
            		 3:"Spa",
                     4:"Spa & Set Temperature"]
        input "M4Temp", "number", title: "M4: Set Temperature to:", range: "40..104", defaultValue: 40
        input "M4VSP", "enum", title: "M4: Set VSP Speed to:", defaultValue: 0,
        	options:[5:"No change",
            		 1:"Speed 1",
            		 2:"Speed 2",
            		 3:"Speed 3",
                     4:"Speed 4",
                     0:"Turn off"]
        input "C1ColorEnabled", "enum", title: "Circuit 1 Color Light Enable:", defaultValue: 0,
        	options:[0:"off",
            		 1:"On"]
        input "C2ColorEnabled", "enum", title: "Circuit 2 Color Light Enable:", defaultValue: 0,
        	options:[0:"off",
            		 1:"On"]
        input "C3ColorEnabled", "enum", title: "Circuit 3 Color Light Enable:", defaultValue: 0,
        	options:[0:"off",
            		 1:"On"]
        input "C4ColorEnabled", "enum", title: "Circuit 4 Color Light Enable:", defaultValue: 0,
        	options:[0:"off",
            		 1:"On"]
        input "C5ColorEnabled", "enum", title: "Circuit 5 Color Light Enable:", defaultValue: 0,
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

		standardTile("mainTile", "device.poolSpaMode", width: 1, height: 1, canChangeIcon: true, canChangeBackground: true) {
			state "spa",        label: "spa",        action: "setPoolMode", icon: "st.Health & Wellness.health2",  backgroundColor: "#79b821", nextState: "turningOff"
			state "pool",       label: "pool",       action: "setSpaMode",  icon: "st.Health & Wellness.health2", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "pool",        icon: "st.Health & Wellness.health2",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "spa",         icon: "st.Health & Wellness.health2", backgroundColor: "#ffffff", nextState: "turningOn"
			state "disabled",   label:'',            icon: "st.Health & Wellness.health2", backgroundColor: "#bc2323"  //"#ffffff"
		}
		valueTile("temperatureTile", "device.temperature", width: 2, height: 2, inactiveLabel: true, decoration: "flat" ) {
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
		valueTile("temperatureTile2", "device.temperature", width: 2, height: 2, inactiveLabel: true, decoration: "flat" ) {
			state "temperature", label:'${currentValue}°',
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
		valueTile("temperatureTile2", "device.temperature", width: 2, height: 2, inactiveLabel: false, decoration: "flat" ) {
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
		valueTile("temperatureTile4", "device.temperature", width: 2, height: 2, inactiveLabel: false, decoration: "flat" ) {
			state "temperature", label:'${currentValue}°',
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
		valueTile("temperatureTile5", "device.temperature", width: 2, height: 2, inactiveLabel: true ) {
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
		valueTile("temperatureTile6", "device.temperature", width: 2, height: 2, inactiveLabel: true) {
			state "temperature", label:'${currentValue}°',
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
		valueTile("temperatureTile7", "device.temperature", width: 2, height: 2, inactiveLabel: false ) {
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
		valueTile("temperatureTile8", "device.temperature", width: 2, height: 2, inactiveLabel: false) {
			state "temperature", label:'${currentValue}°',
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

		controlTile("poolSliderControl", "device.poolSetpoint", "slider", width: 4, height: 1, inactiveLabel: false, range:"(40..104)") {
			state "PoolSetpoint", action:"quickSetPool", backgroundColor:"#d04e00"
		}
		valueTile("poolSetpoint", "device.poolSetpoint", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "pool", label:'pool ${currentValue}°', backgroundColor:"#ffffff"
		}
		controlTile("spaSliderControl", "device.spaSetpoint", "slider", width: 4, height: 1, inactiveLabel: false, range:"(40..104)") {
			state "SpaSetpoint", action:"quickSetSpa", backgroundColor: "#1e9cbb"
		}
		valueTile("spaSetpoint", "device.spaSetpoint", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "spa", label:'spa  ${currentValue}°', backgroundColor:"#ffffff"
		}
        controlTile("pumpSpeedSliderControl", "device.pumpSpeed", "slider", width: 4, height: 1, inactiveLabel: false, range:"(0..4)") {
			state "pumpSpeed", action:"setVSPSpeed", backgroundColor:"#d04e00"
		}
		valueTile("pumpSpeed", "device.pumpSpeed", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
			state "pumpSpeed", label:'speed ${currentValue}', backgroundColor:"#ffffff"
		}
        controlTile("lightColorSliderControl", "device.lightColor", "slider", width: 4, height: 1, inactiveLabel: false, range:"(1..14)") {
			state "color", action:"setLightColor", backgroundColor:"#d04e00"
		}
//		valueTile("temperatureTile", "device.temperature", width: 2, height: 2, inactiveLabel: true) {
		valueTile("lightColor", "device.lightColor", width: 2, height: 1, inactiveLabel: true, decoration: "flat") {
			state "color", action:"setColor", label:'color ${currentValue}', backgroundColor:"#ffffff"
		}
//		standardTile("lightColor", "device.lightColor", width: 2, height: 1, inactiveLabel: false, decoration: "flat") {
//			state "color", action:"setColor", label:'color ${currentValue}', backgroundColor:"#ffffff", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/Pool.png"
//		}

		standardTile("poolSpaMode", "device.poolSpaMode", width: 2, height: 2, decoration: "flat") {
			state "spa",        label: "",           action: "setPoolMode", icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/spa.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "pool",       label: "",           action: "setSpaMode",  icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/Pool.png", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label: 'changing',   action: "pool",        icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/spa.png",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label: 'changing',   action: "spa",         icon: "https://raw.githubusercontent.com/KeithR26/Intermatic-PE653/master/Pool.png", backgroundColor: "#ffffff", nextState: "turningOn"
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
        standardTile("blank1", "device.blank", width: 1, height: 1, decoration: "flat") {
			state "on",         icon: "st.Health & Wellness.health2",  backgroundColor: "#ffffff"
		}
        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("configure", "device.configure", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
        standardTile("blank2", "device.blank", width: 2, height: 2, decoration: "flat") {
			state "on",         label: "", icon: "st.Health & Wellness.health2",  backgroundColor: "#ffffff"
		}
        
	main "mainTile"
        details([
            "blank1",
            "switch1","switch2","switch3","switch4","switch5",
            "poolSpaMode",
        	"temperatureTile",
            "temperatureTile2","temperatureTile3","temperatureTile4","temperatureTile5",
            "temperatureTile6","temperatureTile7","temperatureTile8",
            "swVSP1","swVSP2","swVSP3","swVSP4",
            "swM1", "M1Name", "swM2", "M2Name", "swM3", "M3Name", "swM4", "M4Name",
			"poolSliderControl", "poolSetpoint", "spaSliderControl", "spaSetpoint",
//            "pumpSpeedSliderControl", "pumpSpeed",
            "configure","refresh", "blank2",
            "lightColorSliderControl","lightColor"])
	}
}

// Constants for PE653 configuration parameter locations
def getDELAY () {ZWdelay}								// How long to delay between commands to device (configured)
def getMIN_DELAY () {"800"}								// Minimum delay between commands to device (configured)
def getVERSION () {"Ver 2.04"}							// Keep track of handler version
def getPOOL_SPA_SCHED_PARAM () { 21 }					// Pool/Spa mode Schedule #3 - 0x15
def getVSP_SCHED_NO (int spd) { (35 + (spd * 3)) }		// VSP Speed 1 Schedule #3 - 0x26
def getVSP_SPEED (int sched) { ((sched - 35) / 3) }		// Convert from sched to speed
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
	if (description.startsWith("Err")) {
        log.warn "Error in Parse"
	    result = createEvent(descriptionText:description, isStateChange:true)
	} else {
//		def cmd = zwave.parse(description, [0x20: 1, 0x25:1, 0x27:1, 0x31:1, 0x43:1, 0x60:3, 0x70:2, 0x81:1, 0x85:1, 0x86: 1, 0x73:1])
		def cmd = zwave.parse(description, [0x20: 1, 0x25:1, 0x27:1, 0x31:1, 0x43:1, 0x60:3, 0x70:2, 0x81:1, 0x85:2, 0x86: 1, 0x73:1])
		if (debugLevel > "0") {
			log.debug(">>>>> ${cmd} - description:$description ")
    	}
		if (cmd) {
			result = zwaveEvent(cmd)
        } else {
			log.debug("----- Parse() parsed to NULL:  description:$description")
		}
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
//    log.trace "setPoolSetpoint: setpointType: 1  scale: $deviceScale  precision: $p  scaledValue: $convertedDegrees"
	cmds << zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 1, scale: deviceScale, precision: p, scaledValue: convertedDegrees)
	cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1)
//    cmds
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
//    cmds
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
		case POOL_SPA_SCHED_PARAM:
			def instance = 6
			if ((cmd.size != 4) || (cmd.configurationValue[0] != 0xFF) || (cmd.configurationValue[1] != 0xFF) || (cmd.configurationValue[2] != 0xFF) || (cmd.configurationValue[3] != 0xFF)) {
            	myValue = "spa"
                externalValue = 0xFF
            } else {
            	myValue = "pool"
                externalValue = 0x00
            }
			cmds.addAll(createMultipleEvents (instance, 0x25, 0x03, externalValue, myValue))
			break;
		case getVSP_SCHED_NO(1):
		case getVSP_SCHED_NO(2):
		case getVSP_SCHED_NO(3):
		case getVSP_SCHED_NO(4):
            def int instance = getVSP_SPEED(paramNum) + 6
			if ((cmd.size != 4) || (cmd.configurationValue[0] != 0xFF) || (cmd.configurationValue[1] != 0xFF) || (cmd.configurationValue[2] != 0xFF) || (cmd.configurationValue[3] != 0xFF)) {
                state.pumpSpeed = getVSP_SPEED(paramNum).toString()
                map.value = state.pumpSpeed.toInteger()
				map.name = "pumpSpeed"
                cmds << createEvent(map)
            	myValue = "on"
                externalValue = 0xFF
			} else {
            	if (paramNum == getVSP_SCHED_NO(4)) {
                    map.value = state.pumpSpeed.toInteger()
                    map.name = "pumpSpeed"
                    cmds << createEvent(map)
                }
            	myValue = "off"
                externalValue = 0
            }
			cmds.addAll(createMultipleEvents (instance, 0x25, 0x03, externalValue, myValue))
			break;
	}
    if (debugLevel > "1") {
//		log.trace " map:$map map.name.length():${map.name.length()}"    
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

//Fabricate endpoint info based on the number of real endpoints in the device, plus extras for virtual switches
private List createEndpointInfo(int endPoints) {
	def eps = []
	def int endpointCnt = endPoints + 1 + 4		// 1 for Pool/Spa + 4 for VSP speed
	for (def i=1;i<=endpointCnt;i++) {
		if (i==1) {
        	eps << "10012527"
//		} else if (i == 7) {
//        	eps << "110025"
        } else {
        	eps << "100025"
        }
	}            
    log.trace "createEndpointInfo(${endPoints}) endpointCnt=${endpointCnt} eps=${eps}"
	eps
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceReport cmd) {
	state.endpointInfo = createEndpointInfo(cmd.instances)
	[ createEvent(name: "epInfo", value: util.toJson(state.endpointInfo), displayed: true, descriptionText:"")]
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelEndPointReport cmd) {
	state.endpointInfo = createEndpointInfo(cmd.endpoints)
	[ createEvent(name: "epInfo", value: util.toJson(state.endpointInfo), displayed: true, descriptionText:"")]
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
    []
}

def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationGroupingsReport cmd) {
    state.groups = cmd.supportedGroupings
    []
}

/*
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
	log.debug "got here - chan"
    def encapsulatedCommand = cmd.encapsulatedCommand([0x32: 3, 0x25: 1, 0x20: 1])
	log.debug "encapsulatedCommand: $encapsulatedCommand"
	if (encapsulatedCommand) {
		if (state.enabledEndpoints.find { it == cmd.sourceEndPoint }) {
			def formatCmd = ([cmd.commandClass, cmd.command] + cmd.parameter).collect{ String.format("%02X", it) }.join()
            createEvent(name: "epEvent", value: "$cmd.sourceEndPoint:$formatCmd", isStateChange: true, displayed: false, descriptionText: "(fwd to ep $cmd.sourceEndPoint)")
        } else {
			zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint as Integer)
		}
	}
}
*/

// Multi-channel event from the device
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceCmdEncap cmd) {
	def rslt = []
    def String myStr = (cmd.parameter[0] == 0) ? "off": "on"
	def encapsulatedCommand = cmd.encapsulatedCommand([0x32: 3, 0x25: 1, 0x20: 1])
	if (encapsulatedCommand) {
		rslt.addAll(createMultipleEvents(cmd.instance, cmd.commandClass, cmd.command, cmd.parameter[0], myStr))
	} else {
		log.warn "MultiInstanceCmdEncap: Could not de-encapsulate!!!"
	}
    rslt
}

// Used to update our own switches state as well as the exposed Multi-channel switches
private List sendMultipleEvents (Integer instance, Integer cmdClass, Integer cmdVal, Integer externalParm, String myParm) {
	if (debugLevel > "1") {
        log.debug "..... sendMultipleEvents( instance:$instance, cmdClass:$cmdClass, cmdVal:$cmdVal, externalParm:$externalParm, myParm:$myParm)"
    }
	def rslt = createMultipleEvents(instance, cmdClass, cmdVal, externalParm, myParm)
	rslt.each {e ->
		if (debugLevel > "0") {
			log.debug "<<<<< Event: $e"
        }
		sendEvent(e)
    }
	null
}


// Used to update our own switches state as well as the exposed Multi-channel switches
private List createMultipleEvents (Integer instance, Integer cmdClass, Integer cmdVal, Integer externalParm, String myParm) {
	def rslt = []
	if (debugLevel > "1") {
        log.debug "..... createMultipleEvents( instance:$instance, cmdClass:$cmdClass, cmdVal:$cmdVal, externalParm:$externalParm, myParm:$myParm)"
    }
    if (state.enabledEndpoints.find { it == instance }) {
        def formatCmd = ([cmdClass, cmdVal] + externalParm).collect{ String.format("%02X", it) }.join()
        rslt << createEvent(name: "epEvent", value: "$instance:$formatCmd", isStateChange: true, displayed: true, descriptionText: "(fwd to ep $instance)")
    } else {
    	log.trace "CME: CANT'T FIND INSTANCE: $instance  enabledEndpoints:${state.enabledEndpoints}"
    }
    if (cmdClass == 0x25 && cmdVal == 3) {
        def sw = getSWITCH_NAME(instance)
        rslt << createEvent(name: "$sw", value: "$myParm", isStateChange: true, displayed: true, descriptionText: "($sw set to $myParm)")
    }
    rslt
}


def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.warn "Captured zwave command $cmd"
	createEvent(descriptionText: "$device.displayName: $cmd", isStateChange: true)
}

//Commands

// Called from the Multi-command SmartApp with a command string separated by commas
def List epCmd(Integer ep, String cmds) {
	def rslt = []
	if (debugLevel > "0") {
	    log.debug "+++++ epCmd: ep:$ep cmds:$cmds"
    }
    cmds.tokenize(",").each {tok ->
        def op = null
        def val = 0
        
        if (tok.contains('delay')){
        } else if (tok.contains('2001FF')){
            op = "Set"
            val = 0xFF
        } else if (tok.contains('200100')) {
            op = "Set"
        } else if (tok.contains('2001')) {
			val = tok[4..5].toInteger()
            op = "Set"
			log.debug "contained 2001 and parm=${val}"
        } else if (tok.contains('2002')) {
            op = "Get"
        } else if (tok.contains('2502')) {
            op = "Get"
        } else if (tok.contains('2602')) {
            op = "Get"
            log.debug "contained 2602  Switch Multi-level Get"
        } else {
            log.warn "ep Cmd not recognized: $tok"
        }
		if (op) {
			switch (ep) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                    rslt.addAll(epSendToDevice(ep, op, val))
                    break
                case 6:
					if (op.equals("Set")) {
                    	rslt.addAll(setPoolSpaMode(val))
                    } else {
                    	rslt.addAll(getPoolSpaMode())
                    }
                    break
                case 7:
                case 8:
                case 9:
                case 10:
                	// Convert switch endpoint to a VSP speed
					if (op.equals("Set")) {
                        if (val > 0) {
                            val = ep - 6
                        }
                        rslt.addAll(setVSPSpeedInternal( val ))
                    } else {
                    	rslt.addAll(getVSPSpeed())
                    }
                    break
            }
        }
	}
	delayBetweenLog(rslt)
}

// Forward on the Get or Set to the physical devicce as a Multi-channel encapsulated command
private List epSendToDevice(Integer ep, String op, Integer val) {
	def rslt = []
	if (op.equals("Set")) {
		rslt << encap(zwave.switchBinaryV1.switchBinarySet(switchValue: val), ep)
    } else {
		rslt << encap(zwave.switchBinaryV1.switchBinaryGet(), ep)
    }
}

private encap(cmd, endpoint) {
	if (debugLevel > "1") {
		log.debug "..... encap() cmd:$cmd endpoint:$endpoint"
    }
	if (endpoint) {
//		zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:endpoint, sourceEndPoint: endpoint).encapsulate(cmd)
		zwave.multiChannelV3.multiInstanceCmdEncap(instance: endpoint).encapsulate(cmd)
	} else {
		cmd
	}
}

def enableEpEvents(enabledEndpoints) {
	if (debugLevel > "0") {
		log.debug "+++++ enableEpEvents() enabledEndpoints: $enabledEndpoints"
    }
	state.enabledEndpoints = enabledEndpoints.split(",").findAll()*.toInteger()
	log.debug "state: $state"
	null
}

def List poll() {
	log.debug "+++++ poll()"
//	refreshLight()
	getWaterTemp()
}

private initUILabels() {
	sendEvent(name: "M1Name", value: (M1Label ? "${M1Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M1 Label to ${M1Label}")
	sendEvent(name: "M2Name", value: (M2Label ? "${M2Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M2 Label to ${M2Label}")
	sendEvent(name: "M3Name", value: (M3Label ? "${M3Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M3 Label to ${M3Label}")
	sendEvent(name: "M4Name", value: (M4Label ? "${M4Label}" : ""), isStateChange: true, displayed: true, descriptionText: "init M4 Label to ${M4Label}")
}

def List refresh() {
	log.debug "+++++ refresh()  ${state.VersionInfo}"
    def cmds = []
 	if (debugLevel > "1") {
    	compareConfig()
        state.ccVersions = [:]
        getSupportedCmdClasses().each {cc ->
            cmds << zwave.versionV1.versionCommandClassGet(requestedCommandClass: cc)
        }
    }
    cmds.addAll(refreshLight())

 	if (0 || debugLevel <= "1") {
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 1)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 2)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 3)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 19)
	}
	cmds << zwave.versionV1.versionGet()
	cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet()
//	cmds << zwave.associationV2.associationGroupingsGet()
//	cmds << zwave.multiInstanceV1.multiInstanceGet(commandClass:37)
	delayBetweenLog(cmds)
}

// Used by the refresh() command and also by poll()
private List refreshLight() {
    def cmds = []
	cmds.addAll(getWaterTemp())
	cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1)
    cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 7)
    cmds.addAll(getPoolSpaMode())
    cmds.addAll(getVSPSpeed())

	for (int i=1;i<=5;i++) {
//		cmds << zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:i, sourceEndPoint: i).encapsulate(zwave.switchBinaryV1.switchBinaryGet())
	    cmds << zwave.multiInstanceV1.multiInstanceCmdEncap(instance:i).encapsulate(zwave.switchBinaryV1.switchBinaryGet())
	}

 	if (debugLevel > "1") {
	    cmds.addAll( restartConfig() )
    }
    cmds
}

def List updated() {
	log.debug "+++++ updated()  ${state.VersionInfo}"
    def cmds = []
	initUILabels()
	def lightCircuits = []
	if (C1ColorEnabled == "1") {lightCircuits << 1}
	if (C2ColorEnabled == "1") {lightCircuits << 2}
	if (C3ColorEnabled == "1") {lightCircuits << 3}
	if (C4ColorEnabled == "1") {lightCircuits << 4}
	if (C5ColorEnabled == "1") {lightCircuits << 5}
    state.lightCircuitsList = lightCircuits
//log.trace("lightCircuits=${lightCircuits}  C3ColorEnabled=${C3ColorEnabled}")    
	delayBetweenLog(internalConfigure())
}

def List configure() {
	log.debug "+++++ configure()  ${state.VersionInfo}"
//    def cmds = []
	initUILabels()
	delayBetweenLog(internalConfigure())
}

private List internalConfigure() {
//	log.debug "+++++ internalConfigure()  ${state.VersionInfo}"
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

//    cmds << zwave.associationV2.associationGroupingsGet()
//    cmds << zwave.associationV2.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId)
//    cmds << zwave.associationV2.associationGet(groupingIdentifier:1)

	if (debugLevel <= "1") {
		cmds << zwave.configurationV2.configurationGet(parameterNumber: 1)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 2)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 3)
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 19)
	} else {
		cmds.addAll(startConfig())
		log.trace "state=$state"
    }
log.trace "VSP_ENABLED:${VSP_ENABLED}"
    if ( !VSP_ENABLED ) {
    	cmds << createEvent(name: "swVSP1", value: "disabled", displayed: true, descriptionText:"")
    	cmds << createEvent(name: "swVSP2", value: "disabled", displayed: true, descriptionText:"")
    	cmds << createEvent(name: "swVSP3", value: "disabled", displayed: true, descriptionText:"")
    	cmds << createEvent(name: "swVSP4", value: "disabled", displayed: true, descriptionText:"")
    } else {
    	cmds.addAll(getVSPSpeed())
    }
log.trace "POOL_SPA_COMBO:${POOL_SPA_COMBO}"
	if ( !POOL_SPA_COMBO ) {
    	cmds << createEvent(name: "poolSpaMode", value: "disabled", displayed: true, descriptionText:"poolSpaMode is disabled")
    } else {
    	cmds.addAll(getPoolSpaMode())
    }
	cmds    
}

// Query the four VSP scheduled to determine which speed is enabled
private List getVSPSpeed() {
	def cmds = []
	log.debug "+++++ getVSPSpeed()"
    if ( VSP_ENABLED ) {
        state.pumpSpeed = '0'		// Assume off unless a schedule is returned on
        for (int sp=1;sp<=4;sp++) {
            cmds << zwave.configurationV2.configurationGet(parameterNumber: getVSP_SCHED_NO(sp))
        }
    }
	cmds
}

// Select a VSP speed by forcing the appropriate schedule to always on. speed is from 1-4
// Called from the slider tile in the app
private List setVSPSpeedAndGet(Integer speed) {
	log.debug "+++++ setVSPSpeedAndGet()  speed=${speed}"
	def cmds = []
//	def l = setVSPSpeedInternal(speed)
//	log.trace("l = $l")
//	cmds.addAll(l)
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
	for (int sp=1;sp<=4;sp++) {
    	if (sp == speed) {
			cmds.addAll(setSched(getVSP_SCHED_NO(sp), 0xFF))
		} else {
			cmds.addAll(setSched(getVSP_SCHED_NO(sp), 0))
        }
	    // The following should not be necessary except I don't consistently get replies to the ConfigurationGet
//		sendMultipleEvents ((sp + 6), 0x25, 0x03, speed, "$speed")
    }
	cmds
}

def List getPoolSpaMode() {
	def cmds = []
	if ( POOL_SPA_COMBO ) {
		cmds = [zwave.configurationV2.configurationGet(parameterNumber: POOL_SPA_SCHED_PARAM)]
    }
    cmds
}

private List setPoolSpaMode(Integer val) {
	def cmds = []
	def myValue = ""
	cmds.addAll(setSched(POOL_SPA_SCHED_PARAM, val))
	if (val == 0xFF) {
        myValue = "spa"
    } else {
        myValue = "pool"
    }
    // The following should not be necessary except I don't consistently get replies to the ConfigurationGet
	sendMultipleEvents (6, 0x25, 0x03, val, myValue)
    cmds
}

private List setSpaModeInternal() {
	log.debug "+++++ setSpaMode"
	def cmds = []
	cmds.addAll(setPoolSpaMode(0xFF))
	cmds.addAll(getPoolSpaMode())
	cmds
}

private List setPoolModeInternal() {
	log.debug "+++++ setPoolMode"
	def cmds = []
	cmds.addAll(setPoolSpaMode(0))
	cmds.addAll(getPoolSpaMode())
	cmds
}

private def List togglePoolSpaModeInternal() {
	log.debug "+++++ togglePoolSpaMode: poolSpaMode:${device.currentValue("poolSpaMode")}"
	def cmds = []
    if (device.currentValue("poolSpaMode").equals("spa")) {
		cmds.addAll(setPoolSpaMode(0))
    } else {
		cmds.addAll(setPoolSpaMode(0xFF))
    }
	cmds.addAll(getPoolSpaMode())
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
/*
def setPowerLevel(int pwrLvl) {
	log.debug "+++++ setPowerLevel(${pwrLvl})"
    delayBetweenLog([
		zwave.powerlevelV1.powerlevelSet(powerLevel: pwrLvl),
		zwave.powerlevelV1.powerlevelGet()
    ])
}
*/
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
            cmds << zwave.multiInstanceV1.multiInstanceCmdEncap(instance: sw, commandClass:37, command:1, parameter:[0])
		    dly = MIN_DELAY
        }
        dly = "${DELAY}"
		switches.each { sw ->
	        cmds << "delay ${dly}"
            cmds << zwave.multiInstanceV1.multiInstanceCmdEncap(instance: sw, commandClass:37, command:1, parameter:[255])
		    dly = MIN_DELAY
        }
        dly = "${DELAY}"
    }
	switches.each { sw ->
		cmds << "delay ${dly}"
		cmds <<	zwave.multiInstanceV1.multiInstanceCmdEncap(instance: sw, commandClass:37, command:2)
    }
//log.trace "blink() cmds=${cmds}"
	cmds
}

// Called by a button press for one of the "Mode" selections  (eg: M1, M2, M3, M4)
def setMode(int mode) {
	def cmds = []
	List MxSw
	String MxMode, MxTemp, MxVSP
//log.trace "M1Sw1=${M1Sw1} M1Sw2=${M1Sw2} M1Sw3=${M1Sw3} M1Sw4=${M1Sw4} M1Sw5=${M1Sw5} M1Mode=${M1Mode} M1Temp=${M1Temp} M1VSP=${M1VSP}"
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
    	if (action == "1") {
        	cmds.addAll(onMulti(idx+1))
        } else if (action == "2") {
        	cmds.addAll(offMulti(idx+1))
        }
    }
    if (VSP_ENABLED && MxVSP != "5") {
    	cmds.addAll(setVSPSpeedAndGet(MxVSP.toInteger()))
    }
	cmds.addAll(getWaterTemp())
//log.trace "setMode: cmds(before)=${cmds}"
	cmds
}

def on() {
	log.debug "+++++ on()"
    delayBetweenLog([
//		zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: 1, destinationEndPoint: 1, commandClass:37, command:1, parameter:[255]),
//		zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: 1, destinationEndPoint: 1, commandClass:37, command:2)
        zwave.basicV1.basicSet(value: 0xFF),
        zwave.basicV1.basicGet()
    ])
}

def off() {
	log.debug "+++++ off()"
    delayBetweenLog([
//		zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: 1, destinationEndPoint: 1, commandClass:37, command:1, parameter:[0]),
//		zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint: 1, destinationEndPoint: 1, commandClass:37, command:2)
        zwave.basicV1.basicSet(value: 0x00),
        zwave.basicV1.basicGet()
    ])
}

//switch instance
private List onMulti(value) {
	log.debug "+++++ onMulti($value)"
	def cmds =[
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:1, parameter:[255]),
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:2)
	]
}

private List offMulti(value) {
	log.debug "+++++ offMulti($value)"
	def cmds =[
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:1, parameter:[0]),
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:2)
	]
}

// Called by switch presses on the circuit buttons.
def List on1()  { delayBetweenLog(onMulti(1)) }
def List on2()  { delayBetweenLog(onMulti(2)) }
def List on3()  { delayBetweenLog(onMulti(3)) }
def List on4()  { delayBetweenLog(onMulti(4)) }
def List on5()  { delayBetweenLog(onMulti(5)) }
def List off1() { delayBetweenLog(offMulti(1)) }
def List off2() { delayBetweenLog(offMulti(2)) }
def List off3() { delayBetweenLog(offMulti(3)) }
def List off4() { delayBetweenLog(offMulti(4)) }
def List off5() { delayBetweenLog(offMulti(5)) }

// Called by slider control.
def List setVSPSpeed(sp) { delayBetweenLog(setVSPSpeedAndGet(sp)) }
// Called by switch presses on the VSP buttons.
def List setVSPSpeed0() { delayBetweenLog(setVSPSpeed(0)) }
def List setVSPSpeed1() { delayBetweenLog(setVSPSpeed(1)) }
def List setVSPSpeed2() { delayBetweenLog(setVSPSpeed(2)) }
def List setVSPSpeed3() { delayBetweenLog(setVSPSpeed(3)) }
def List setVSPSpeed4() { delayBetweenLog(setVSPSpeed(4)) }
def List setMode1() { delayBetweenLog(setMode(1)) }
def List setMode2() { delayBetweenLog(setMode(2)) }
def List setMode3() { delayBetweenLog(setMode(3)) }
def List setMode4() { delayBetweenLog(setMode(4)) }

def List setSpaMode() {delayBetweenLog(setSpaModeInternal()) }
def List setPoolMode() {delayBetweenLog(setPoolModeInternal()) }
def List togglePoolSpaMode() {delayBetweenLog(togglePoolSpaModeInternal()) }

def List quickSetSpa(degrees) {delayBetweenLog(setSpaSetpointInternal(degrees), 3000)}
def List quickSetPool(degrees) {delayBetweenLog(setPoolSetpointInternal(degrees), 3000)}
def List quickGetWaterTemp()  {delayBetweenLog(getWaterTemp()) }

// Called from all commands
def delayBetweenLog(parm, dly=DELAY) {
	def lst = parm
	def cmds =[]
	def evts =[]
	def devStr = ""
    def evtStr = ""
    if (!(parm in List)) {
    	lst = [parm]
    }
    lst.each {l ->
//log.trace "l -> ${l}"
		if (l instanceof physicalgraph.device.HubAction) {log.trace "instanceof physicalgraph.device.HubAction"}
//        if (l instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet) {log.trace "$l instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet"}
		//if (l instanceof physicalgraph.HubAction) {log.trace "delay instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet"}
        if (l instanceof String || l instanceof GString) {
            cmds << l
            devStr = devStr.concat(", ${l}")
//            log.trace "## String: $l"
        } else if (l instanceof List) {
            cmds << l
//            log.trace "#### LIST: $l"
        } else if (l instanceof Map) {
//          evts << l
			sendEvent(l)
			evtStr = evtStr.concat("\n<<<<< Event: $l")
//            log.trace "## Map: $l"
        } else {
            def fmt = l.format()
            if (cmds) {
				def c = cmds.last()			//check if there is already a delay prior to this
//log.trace "cmds=${cmds} c=${c}"
//	            if (c instanceof String) { log.trace "c is String take(6)=${c.take(6)}" }
//	            if (c instanceof GString) { log.trace "c is GString take(6)=${c.take(6)}" }
//	            if ((c instanceof String || c instanceof GString) && c.take(6) != "delay ") { log.trace "not = delay" }
//	            if ((c instanceof String || c instanceof GString) && c.take(6) == "delay ") { log.trace "= delay" }

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

// Only called from parse().
def delayResponseLog(parm, dly=DELAY) {
	def lst = parm
	def cmds =[]
	def evts =[]
	def devStr = ""
    def evtStr = ""
    if (!(parm in List)) {
    	lst = [parm]
    }
    lst.each {l ->
        if (l instanceof physicalgraph.device.HubAction) {log.trace "instanceof physicalgraph.device.HubAction"}
//      if (l instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet) {log.trace "$l instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet"}
//		if (l instanceof physicalgraph.HubAction) {log.trace "delay instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet"}
        if (l instanceof String) {
            cmds << l
//            log.trace "## String: $l"
        } else if (l instanceof List) {
            cmds << l
//            log.trace "#### LIST: $l"
        } else if (l instanceof Map) {
            evts << l
            evtStr = evtStr.concat("\n<<<<< Event: $l")
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