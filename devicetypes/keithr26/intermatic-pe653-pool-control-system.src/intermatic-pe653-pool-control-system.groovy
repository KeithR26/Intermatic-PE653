/**
 *  Intermatic PE653 Pool Control System
 *
 *  Original Copyright 2014 bigpunk6
 *  Updated 2016 KeithR26
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
        attribute "temperatureOffsets", "string"
        attribute "poolspaConfig", "string"
        attribute "poolSetpoint", "string"
        attribute "spaSetpoint", "string"
        attribute "poolSpaMode", "string"
        attribute "powerlevel", "string"
        attribute "pumpSpeed", "string"
		attribute "ccVersions", "string"
		attribute "debugLevel", "string"

		attribute "switch1", "string"
		attribute "switch2", "string"
		attribute "switch3", "string"
		attribute "switch4", "string"
		attribute "switch5", "string"

        command "quickSetPool"
        command "quickSetSpa"
		command "setPoolMode"
		command "setSpaMode"
		command "setPowerLevel"
		command "setVSPSpeed"
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
                     3:"2 Speed Pump with Booster/Cleaner"]
        input "poolSpa1", "enum", title: "Pool or Spa", options:[0:"Pool",1:"Spa",2:"Both"]
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
        input "tempOffsetwater", "number", title: "Water temperature offset", defaultValue: 0, required: true
        input "tempOffsetair", "number",
            title: "Air temperature offset - Sets the Offset of the air temerature for the add-on Thermometer in degrees Fahrenheit -20F to +20F", defaultValue: 0, required: true
        input "debugLevel", "enum", title: "Debug Level", options:[0:"Off",1:"Low",2:"High"], defaultvalue: 0
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
        multiAttributeTile(name:"temperature", type: "thermostat", width: 6, height: 4){
			tileAttribute ("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState "temperature", label:'${currentValue}°',
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
            tileAttribute ("device.poolSetpoint", key: "VALUE_CONTROL") {
				attributeState "poolSetpoint", action:"quickSetPool"
			}
			tileAttribute ("device.spaSetpoint", key: "SECONDARY_CONTROL") {
				attributeState "spaSetpoint", label:'Spa set to ${currentValue}°F'
			}
            
		}
        controlTile("poolSliderControl", "device.poolSetpoint", "slider", height: 2, width: 4, inactiveLabel: false, range:"(40..104)") {
			state "PoolSetpoint", action:"quickSetPool", backgroundColor:"#d04e00"
		}
		valueTile("poolSetpoint", "device.poolSetpoint", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "pool", label:'${currentValue}° pool', backgroundColor:"#ffffff"
		}
		controlTile("spaSliderControl", "device.spaSetpoint", "slider", height: 2, width: 4, inactiveLabel: false, range:"(40..104)") {
			state "SpaSetpoint", action:"quickSetSpa", backgroundColor: "#1e9cbb"
		}
		valueTile("spaSetpoint", "device.spaSetpoint", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "spa", label:'${currentValue}° spa', backgroundColor:"#ffffff"
		}
        controlTile("powerSliderControl", "device.powerLevel", "slider", height: 2, width: 4, inactiveLabel: false, range:"(0..100)") {
			state "PowerLevel", action:"setPowerLevel", backgroundColor:"#d04e00"
		}
		valueTile("powerlevel", "device.powerlevel", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "powerlevel", label:'power ${currentValue}', backgroundColor:"#ffffff"
		}
        controlTile("pumpSpeedSliderControl", "device.pumpSpeed", "slider", height: 2, width: 4, inactiveLabel: false, range:"(1..4)") {
			state "pumpSpeed", action:"setVSPSpeed", backgroundColor:"#d04e00"
		}
		valueTile("pumpSpeed", "device.pumpSpeed", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "pumpSpeed", label:'speed ${currentValue}', backgroundColor:"#ffffff"
		}
        standardTile("refresh", "device.switch", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
        standardTile("configure", "device.configure", width: 2, height: 2, inactiveLabel: false, decoration: "flat") {
			state "configure", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}

/*		standardTile("poolSpaMode", "device.poolSpaMode", width: 2, height: 2, canChangeIcon: true) {
			state "spa",  label: "spa",  action: "setPoolMode", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "pool", label: "pool", action: "setSpaMode",  icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
		standardTile("switch1", "device.switch1", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: "switch1", action: "off1", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: "switch1", action: "on1", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
        standardTile("switch2", "device.switch2", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: "switch2", action: "off2", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: "switch2", action: "on2", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
        standardTile("switch3", "device.switch3", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: "switch3", action: "off3", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: "switch3", action:"on3", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
        standardTile("switch4", "device.switch4", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: "switch4", action: "off4", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: "switch4", action:"on4", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
        standardTile("switch5", "device.switch5", width: 2, height: 2, canChangeIcon: true) {
			state "on", label: "switch5", action: "off5", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: "switch5", action:"on5", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
*/		standardTile("poolSpaMode", "device.poolSpaMode", width: 2, height: 2, canChangeIcon: true) {
			state "spa",        label: "spa",        action: "setPoolMode", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "pool",       label: "pool",       action: "setSpaMode",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "pool",        icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "spa",         icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		}
		standardTile("switch1", "device.switch1", width: 2, height: 2, canChangeIcon: true) {
			state "on",         label: "switch1",    action: "off1", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "switch1",    action: "on1",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off1", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on1",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch2", "device.switch2", width: 2, height: 2, canChangeIcon: true) {
			state "on",         label: "switch2",    action: "off2", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "switch2",    action: "on2",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off2", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on2",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch3", "device.switch3", width: 2, height: 2, canChangeIcon: true) {
			state "on",         label: "switch3",    action: "off3", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "switch3",    action: "on3",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off3", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on3",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch4", "device.switch4", width: 2, height: 2, canChangeIcon: true) {
			state "on",         label: "switch4",    action: "off4", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "switch4",    action: "on4",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off4", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on4",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        standardTile("switch5", "device.switch5", width: 2, height: 2, canChangeIcon: true) {
			state "on",         label: "switch5",    action: "off5", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "off",        label: "switch5",    action: "on5",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
			state "turningOn",  label:'Turning on',  action: "off5", icon: "st.switches.switch.on",  backgroundColor: "#79b821", nextState: "turningOff"
			state "turningOff", label:'Turning off', action: "on5",  icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState: "turningOn"
		}
        
	main "temperature"
        details(["temperature", "poolSliderControl", "poolSetpoint", "spaSliderControl", "spaSetpoint",
        	"powerSliderControl", "powerlevel", "pumpSpeedSliderControl", "pumpSpeed", "poolSpaMode",
            "switch1","switch2","switch3","switch4","switch5","configure","refresh"])
	}
}

// Constants for PE653 configuration parameter locations
def getDELAY () {1000}									// How long to delay between command to device
def getPOOL_SPA_SCHED_PARAM () { 21 }					// Pool/Spa mode Schedule #3 - 0x15
def getVSP_Sched_No (int spd) { (35 + (spd * 3)) }		// VSP Speed 1 Schedule #3 - 0x26
def getVSP_Speed (int sched) { ((paramNum - 35) / 3) }	// Convert from sched to speed
def getSwitchName (int instance) {
	def swNames = ["switch1","switch2","switch3","switch4","switch5","poolSpaMode","pumpSpeed"]
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
	0x91	//	Manufactirer Proprietary
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
	delayBetweenLog(result)
}

def quickSetPool(degrees) {
    log.debug "quickSetPool $degrees"
	setPoolSetpoint(degrees, DELAY)
}

def setPoolSetpoint(degrees, delay = 30000) {
	setPoolSetpoint(degrees.toDouble(), delay)
}

def setPoolSetpoint(Double degrees, Integer delay = 30000) {
	log.trace "setPoolSetpoint($degrees, $delay)"
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
	cmds << zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 1, scale: deviceScale, precision: p, scaledValue: convertedDegrees)
	cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1)
	delayBetweenLog(cmds, delay)
}

def quickSetSpa(degrees) {
	setSpaSetpoint(degrees, DELAY)
}

def setSpaSetpoint(degrees, delay = 30000) {
	setSpaSetpoint(degrees.toDouble(), delay)
}

def setSpaSetpoint(Double degrees, Integer delay = 30000) {
    log.trace "setSpaSetpoint($degrees, $delay)"
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
	cmds << zwave.thermostatSetpointV1.thermostatSetpointSet(setpointType: 7, scale: deviceScale, precision: p,  scaledValue: convertedDegrees)
	cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 7)
	delayBetweenLog(cmds, delay)
}

//Reports

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
	log.debug "-->Version: ${cmd}"
	state.VersionReport = cmd
	null    
}

def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionCommandClassReport cmd) {
	if (cmd.commandClassVersion) {
    	def cls = String.format("%02X", cmd.requestedCommandClass)
        state.ccVersions[cls] = cmd.commandClassVersion
		createEvent(name: "ccVersions", value: util.toJson(state.ccVersions), displayed: false, descriptionText:"")
	} else {
    	null
    }
}

def zwaveEvent(physicalgraph.zwave.commands.powerlevelv1.PowerlevelReport cmd) {
    def map = [:]
	map.value = cmd.powerLevel
	map.displayed = true
	map.name = "powerlevel"
    createEvent(map)
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
    state.cnfAttemptsLeft[paramNum] = 0
	state.cnfParallelGets = state.cnfParallelGets - 1
    switch (paramNum) {
        case 1:
			map.name = "operationMode"
			cmds << createEvent(map)
//	        state.cnfSendParmOne = 1		// Resend the request for Parm one
//			state.cnfParallelGets = 0		// Reset for unresponsive parm numbers
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
			cmds.addll(createMultipleEvents (instance, 0x25, 0x03, externalValue, myValue))
			break;
		case getVSP_Sched_No(1):
		case getVSP_Sched_No(2):
		case getVSP_Sched_No(3):
		case getVSP_Sched_No(4):
			map.name = "pumpSpeed"
			state.pumpSpeed = getVSP_Speed(paramNum).toString()
			if (debugLevel > "1") {
				log.trace "VSP speed detected=${state.pumpSpeed}"
            }
			if ((cmd.size != 4) || (cmd.configurationValue[0] != 0xFF) || (cmd.configurationValue[1] != 0xFF) || (cmd.configurationValue[2] != 0xFF) || (cmd.configurationValue[3] != 0xFF)) {
            	myValue = state.pumpSpeed
                externalValue = getVSP_Speed(paramNum)
				cmds.addAll(createMultipleEvents (7, 0x25, 0x03, externalValue, myValue))
			} else {
				if (debugLevel > "1") {
					log.trace "Speed ${state.pumpSpeed} NOT on"
                }
            }
			break;
	}
//log.trace " map:$map map.name.length():${map.name.length()}"    
/*	def lst = []
	if ((map.name.length() > 0) || (cmd.size != 4) || (cmd.configurationValue[0] != 0xFF) || (cmd.configurationValue[1] != 0xFF) || (cmd.configurationValue[2] != 0xFF) || (cmd.configurationValue[3] != 0xFF)) {
		lst = [cmd.size]
        for (def i=0;i<cmd.size;i++) {
        	lst << cmd.configurationValue[i]
        }
        state.cnfData[paramNum] = lst
		if (map.name.length() == 0) {
//			log.trace "Unexpected config: #${paramNum} lst:${lst}]"
        }
    }
//	cmds.addAll(responseList(nextConfig(), DELAY))
*/
	cmds
}
/*
private List saveConfig() {
	state.cnfData2 = state.cnfData
	log.trace "cnfData2:${state.cnfData2}"
	startConfig()    
}

private List compareConfig() {
	log.trace "cnfData1:${state.cnfData}"
	log.trace "cnfData2:${state.cnfData2}"
    def dif1 = state.cnfData - state.cnfData2
    def dif2 = state.cnfData2 - state.cnfData
	log.trace "dif1:$dif1"    
	log.trace "dif2:$dif2"
    []
}

private List startConfig() {
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
//    log.debug "startConfig() cnfAttemptsLeft:$state.cnfAttemptsLeft"
	nextConfig()
}

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
			log.debug "nextConfig() nextInx:$nextInx  maxAttemptsLeft:$maxAttemptsLeft cnfParallelGets:${state.cnfParallelGets}"
            cmds << zwave.configurationV2.configurationGet(parameterNumber: nextInx)
        } else {
            state.cnfGetGoal = 0
            log.trace "Config Get Complete: cnfData:${state.cnfData}"
        }
	}
	if (state.cnfSendParmOne == 1) {
        cmds << zwave.configurationV2.configurationGet(parameterNumber: 1)
        state.cnfSendParmOne = 0
	}    
//  runIn(5, timerConfig)
	cmds
}

// Called from runIn timer
def List timerConfig() {
	delayBetweenLog(restartConfig())
}	

private List restartConfig() {
	state.cnfParallelGets = 0
	state.cnfSendParmOne = 1
	nextConfig()
}	
*/
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
//	map.displayed = false
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

    cmds << zwave.basicv1.BasicReport(device.switch1)
	delayBetweenLog(cmds)
}

/*
private List loadEndpointInfo() {
	if (state.endpointInfo) {
		state.endpointInfo
	} else if (device.currentValue("epInfo")) {
		fromJson(device.currentValue("epInfo"))
	} else {
		[]
	}
}
*/

//Fabricate endpoint info based on the number of real endpoints in the device, plus extras for virtual switches
private List createEndpointInfo(int endPoints) {
	def eps = []
	def int endpointCnt = endPoints + 1 + 1		// 1 for Pool/Spa + 1 for VSP speed
    log.trace "createEndpointInfo(${endPoints}) endpointCnt=$endpointCnt"
//	updateDataValue("endpoints", endpointCnt.toString())
	for (def i=1;i<=endpointCnt;i++) {
		if (i==1) {
        	eps << "10012527"
		} else if (i == 7) {
        	eps << "110025"
        } else {
        	eps << "100025"
        }
	}            
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
/*
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelEndPointReport cmd) {
    updateDataValue("endpoints", cmd.endPoints.toString())
	if (!state.endpointInfo) {
		state.endpointInfo = loadEndpointInfo()
	}
	if (state.endpointInfo.size() > cmd.endPoints) {
		cmd.endpointInfo
	}
	state.endpointInfo = [null] * cmd.endPoints
	[ createEvent(name: "epInfo", value: util.toJson(state.endpointInfo), displayed: true, descriptionText:""),
	//response(zwave.associationV2.associationGroupingsGet())
	//  response(zwave.multiChannelV3.multiChannelCapabilityGet(endPoint: 1))
    ]
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCapabilityReport cmd) {
    def result = []
	def cmds = []
	if(!state.endpointInfo) state.endpointInfo = []
	state.endpointInfo[cmd.endPoint - 1] = cmd.format()[6..-1]
	if (cmd.endPoint < getDataValue("endpoints").toInteger()) {
		cmds = zwave.multiChannelV3.multiChannelCapabilityGet(endPoint: cmd.endPoint + 1).format()
	} else {
		log.debug "endpointInfo: ${state.endpointInfo.inspect()}"
	}
	result << createEvent(name: "epInfo", value: util.toJson(state.endpointInfo), displayed: true, descriptionText:"")
	if(cmds) result << response(cmds)
	result
}
*/
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
/*
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiInstanceCmdEncap cmd) {
	def rslt = []
	def encapsulatedCommand = cmd.encapsulatedCommand([0x32: 3, 0x25: 1, 0x20: 1])
	if (encapsulatedCommand) {
		if (state.enabledEndpoints.find { it == cmd.instance }) {
			def formatCmd = ([cmd.commandClass, cmd.command] + cmd.parameter).collect{ String.format("%02X", it) }.join()
			if (debugLevel > "1") {
				log.debug "..... encapsulatedCommand: $encapsulatedCommand  -  $formatCmd"
            }
			rslt << createEvent(name: "epEvent", value: "$cmd.instance:$formatCmd", isStateChange: true, displayed: false, descriptionText: "(fwd to ep $cmd.instance)")
			rslt << createEvent(name: "switch${cmd.instance}", value: "${(cmd.parameter[0] == 0?"off":"on")}", isStateChange: true, displayed: false, descriptionText: "(set local switch$cmd.instance)")
        } else {
			log.warn "MultiInstanceCmdEncap-B: endpoint not found. enabledEndpoints:${state.enabledEndpoints}"
			rslt << zwaveEvent(encapsulatedCommand, cmd.instance as Integer)
        }
	} else {
		log.warn "MultiInstanceCmdEncap: Could not de-encapsulate!!!"
	}
    rslt
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
        def sw = getSwitchName(instance)
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
//			log.debug "contained $tok"
        } else if (tok.contains('2001FF')){
            op = "Set"
            val = 0xFF
//            log.debug "contained 2001FF"
        } else if (tok.contains('200100')) {
            op = "Set"
//            log.debug "contained 200100"
        } else if (tok.contains('2001')) {
			val = tok[4..5].toInteger()
            op = "Set"
			log.debug "contained 2001 and parm=${val}"
        } else if (tok.contains('2002')) {
            op = "Get"
//            log.debug "contained 2002"
        } else if (tok.contains('2502')) {
            op = "Get"
//            log.debug "contained 2502"
        } else if (tok.contains('2602')) {
            op = "Get"
            log.debug "contained 2602  Switch Multi-level Get"
        } else {
            log.warn "ep Cmd not recognized: $tok"
        }
		if (op) {
//log.debug "op:$op"
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
					if (op.equals("Set")) {
                        if (val > 4) {
                            val = 4
                        } else if (val < 1) {
                            val = 1
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
//		rslt << encap(zwave.switchBinaryV1.switchBinaryGet(), ep)
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

def poll() {
	log.debug "+++++ poll()"
    [zwave.sensorMultilevelV1.sensorMultilevelGet()]
}

def installed() {
	log.debug "+++++ installed()"
	refresh()
}

def updated() {
	log.debug "+++++ updated()"
	refresh()
}

def refresh() {
	log.debug "+++++ refresh()"
    def cmds = []
	cmds << zwave.sensorMultilevelV1.sensorMultilevelGet()
    cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 1)
    cmds << zwave.thermostatSetpointV1.thermostatSetpointGet(setpointType: 7)
    cmds << zwave.thermostatSetpointV1.thermostatSetpointSupportedGet()
	cmds << zwave.configurationV2.configurationGet(parameterNumber: 1)
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 2)
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 3)
//    cmds << zwave.configurationV2.configurationGet(parameterNumber: 19)
    cmds.addAll(getPoolSpaMode())
    cmds.addAll(getVSPSpeed())
	cmds << zwave.manufacturerSpecificV1.manufacturerSpecificGet()
	cmds << zwave.versionV1.versionGet()
 	if (debugLevel > "1") {
        state.ccVersions = [:]
        getSupportedCmdClasses().each {cc ->
            cmds << zwave.versionV1.versionCommandClassGet(requestedCommandClass: cc)
        }
    }
	cmds << zwave.associationV2.associationGroupingsGet()

//	cmds << zwave.multiChannelV3.multiChannelCapabilityGet(endPoint: 1)
//	cmds << zwave.multiChannelV3.multiChannelEndPointGet()
	cmds << zwave.multiInstanceV1.multiInstanceGet(commandClass:37)

	for (int i=1;i<=5;i++) {
//		cmds << zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:i, sourceEndPoint: i).encapsulate(zwave.switchBinaryV1.switchBinaryGet())
	    cmds << zwave.multiInstanceV1.multiInstanceCmdEncap(instance:i).encapsulate(zwave.switchBinaryV1.switchBinaryGet())
	}
	cmds << zwave.powerlevelV1.powerlevelGet()

//    cmds.addAll( restartConfig() )
	delayBetweenLog(cmds)
}

def configure() {
	log.debug "+++++ configure()"
    def cmds = []
		cmds << zwave.associationV2.associationGroupingsGet()
//		cmds << zwave.associationV2.associationGet(groupingIdentifier:1)
		cmds << zwave.associationV2.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId)
        cmds << zwave.associationV2.associationGet(groupingIdentifier:1)

		cmds << zwave.configurationV2.configurationSet(configurationValue: [operationMode1.toInteger(), operationMode2.toInteger()], parameterNumber: 1, size: 2)
        cmds << zwave.configurationV2.configurationSet(configurationValue: [tempOffsetwater.toInteger(), tempOffsetair.toInteger(), 0, 0], parameterNumber: 3, size: 4)
        cmds << zwave.configurationV2.configurationSet(configurationValue: [poolSpa1.toInteger()], parameterNumber: 19, size: 1)
        cmds << zwave.configurationV2.configurationSet(configurationValue: [fireman.toInteger()], parameterNumber: 2, size: 1)
//		cmds.addAll(startConfig())

	if (debugLevel > "1") {
		log.trace "state=$state"
    }
	delayBetweenLog(cmds)
}

// Query the four VSP scheduled to determine which speed is enabled
def List getVSPSpeed() {
	def cmds = []
	log.debug "+++++ getVSPSpeed()"

	for (int sp=1;sp<=4;sp++) {
		cmds << zwave.configurationV2.configurationGet(parameterNumber: getVSP_Sched_No(sp))
    }
	cmds
}

// Select a VSP speed by forcing the appropriate schedule to always on. speed is from 1-4
// Called from the slider tile in the app
def List setVSPSpeed(Integer speed) {
	log.debug "+++++ setVSPSpeed()  `speed=${speed}"
	def cmds = []
	cmds.addAll(setVSPSpeedInternal(speed))
	cmds.addAll(getVSPSpeed())
	delayBetweenLog(cmds)
}

// Select a VSP speed by forcing the appropriate schedule to always on. speed is from 1-4
// Called based on commands from the Multi-channel SmartApp
private List setVSPSpeedInternal(Integer speed) {
	def cmds = []
	for (int sp=1;sp<=4;sp++) {
    	if (sp == speed) {
			cmds.addAll(setSched(getVSP_Sched_No(sp), 0xFF))
		} else {
			cmds.addAll(setSched(getVSP_Sched_No(sp), 0))
        }
    }
    // The following should not be necessary except I don;t consistently get replies to the ConfigurationGet
	sendMultipleEvents (7, 0x26, 0x03, speed, "$speed")
	cmds
}

def List getPoolSpaMode() {
	[zwave.configurationV2.configurationGet(parameterNumber: POOL_SPA_SCHED_PARAM)]
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

def List setSpaMode() {
	log.debug "+++++ setSpaMode"
	def cmds = []
	cmds.addAll(setPoolSpaMode(0xFF))
	cmds.addAll(getPoolSpaMode())
	delayBetweenLog(cmds)
}

def List setPoolMode() {
	log.debug "+++++ setPoolMode"
	def cmds = []
	cmds.addAll(setPoolSpaMode(0))
	cmds.addAll(getPoolSpaMode())
	delayBetweenLog(cmds)
}

// General purpose function to set a schedule to "Always off" or "Always on"
private List setSched(int paramNum, Integer val) {
 	if (debugLevel > "1") {
//		log.debug "+++++ setSched(paramNum:${paramNum}, val:$val)"
    }
	def cmds = []
	if (val == 0) {
        cmds << zwave.configurationV2.configurationSet(configurationValue: [0xFF, 0xFF, 0xFF, 0xFF], size: 4, parameterNumber: paramNum)
//        cmds << zwave.configurationV2.configurationGet(parameterNumber: paramNum)
    } else {
        cmds << zwave.configurationV2.configurationSet(configurationValue: [0x01, 0x00, 0x9F, 0x05], size: 4, parameterNumber: paramNum)
//		cmds << zwave.configurationV2.configurationGet(parameterNumber: paramNum)
    }
	cmds
}

def setPowerLevel(int pwrLvl) {
	log.debug "+++++ setPowerLevel(${pwrLvl})"
    delayBetweenLog([
		zwave.powerlevelV1.powerlevelSet(powerLevel: pwrLvl),
		zwave.powerlevelV1.powerlevelGet()
    ])
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
def onMulti(value) {
	log.debug "+++++ onMulti($value)"
	delayBetweenLog([
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:1, parameter:[255]),
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:2)
	], 2300)
}

def offMulti(value) {
	log.debug "+++++ offMulti($value)"
	delayBetweenLog([
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:1, parameter:[0]),
		zwave.multiInstanceV1.multiInstanceCmdEncap(instance: value, commandClass:37, command:2)
	], 2300)
}

//switch1
def on1() {
	onMulti(1)
}

def off1() {
	offMulti(1)
}

//switch2
def on2() {
	onMulti(2)
}

def off2() {
	offMulti(2)
}

//switch3
def on3() {
	onMulti(3)
}

def off3() {
	offMulti(3)
}

//switch4
def on4() {
	onMulti(4)
}

def off4() {
	offMulti(4)
}

//switch5
def on5() {
	onMulti(5)
}

def off5() {
	offMulti(5)
}

//def List delayBetweenLog(List lst) {
//	delayBetweenLog(lst, DELAY)
//	log.debug "DBL lst:$lst"
//	delayBetween(lst, DELAY)
//}

def delayBetweenLog(parm, dly=DELAY) {
	def lst = parm
	def cmds =[]
	def evts =[]
	def devStr = ""
    def evtStr = ""
    if (!(parm in List)) {
    	lst = [parm]
//    	log.trace "DBL: !in List"
    } else {
//    	log.trace "DBL: in List"
    }
    lst.each {l ->
        if (l instanceof physicalgraph.device.HubAction) {log.trace "instanceof physicalgraph.device.HubAction"}
        if (l instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet) {log.trace "delay instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet"}
		//if (l instanceof physicalgraph.HubAction) {log.trace "delay instanceof physicalgraph.zwave.commands.associationv2.AssociationGroupingsGet"}
        if (l instanceof String) {
            cmds << l
            log.trace "## String: $l"
        } else if (l instanceof List) {
            cmds << l
            log.trace "#### LIST: $l"
        } else if (l instanceof Map) {
            evts << l
            evtStr = evtStr.concat("\n<<<<< Event: $l")
//            log.trace "## Map: $l"
        } else {
            def fmt = l.format()
            if (cmds) {
                cmds << "delay $dly"
                devStr = devStr.concat(", delay $dly")
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