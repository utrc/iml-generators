package com.utc.utrc.hermes.iml.gen.lustre.tests

import com.google.inject.Inject
import com.utc.utrc.hermes.iml.ImlParseHelper
import com.utc.utrc.hermes.iml.custom.ImlCustomFactory
import com.utc.utrc.hermes.iml.gen.lustre.df.SynchDf
import com.utc.utrc.hermes.iml.gen.lustre.df.generator.LustreGenerator
import com.utc.utrc.hermes.iml.gen.lustre.df.model.LustreModel
import com.utc.utrc.hermes.iml.gen.systems.Systems
import com.utc.utrc.hermes.iml.iml.Import
import com.utc.utrc.hermes.iml.iml.Model
import com.utc.utrc.hermes.iml.iml.NamedType
import com.utc.utrc.hermes.iml.tests.ImlInjectorProvider
import com.utc.utrc.hermes.iml.tests.TestHelper
import com.utc.utrc.hermes.iml.util.FileUtil
import org.eclipse.xtext.testing.InjectWith
import org.eclipse.xtext.testing.XtextRunner
import org.eclipse.xtext.testing.validation.ValidationTestHelper
import org.junit.Test
import org.junit.runner.RunWith
import static org.junit.Assert.assertTrue

@RunWith(XtextRunner)
@InjectWith(ImlInjectorProvider)
class LustreTranslatorTests {
	
	@Inject extension ImlParseHelper
	
	@Inject extension ValidationTestHelper
	
	@Inject extension TestHelper
	
	@Inject 
	Systems sys ;
	
	@Inject 
	SynchDf sdf ;
	
	@Inject
	LustreGenerator gen ;
	
	@Test
	def void testFilter() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/filter.iml"),true) ;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Filter") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutEventDataPort__aadl_dot_Command_Impl__aadl_dot_Command_Impl__ = struct { 
    data : aadl_dot_Command_Impl;
    event : aadl_dot_Command_Impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InEventDataPort__aadl_dot_Command_Impl__aadl_dot_Command_Impl__ = struct { 
    data : aadl_dot_Command_Impl;
    event : aadl_dot_Command_Impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};
type aadl_dot_Command_Impl = struct { 
    HMAC : bool
};



node aadl_dot_good_command(cmd : bool)
returns (_return : bool)
let
    _return = aadl_dot_good_HMAC(cmd);
tel

node aadl_dot_good_HMAC(hmac : bool)
returns (_return : bool)
let
    _return = ((hmac) = (true)) or ((hmac) = (false));
tel

node aadl_dot_Filter (filter_in : iml_dot_systems_dot_InEventDataPort__aadl_dot_Command_Impl__aadl_dot_Command_Impl__)
returns (filter_out : iml_dot_systems_dot_OutEventDataPort__aadl_dot_Command_Impl__aadl_dot_Command_Impl__)
var REQ2 : bool;
var REQ3 : bool;
var REQ1 : bool;
var assumption : bool;
var guarantee : bool;
let
    REQ2 = (filter_out.data.HMAC) = (true); 
    REQ3 = aadl_dot_good_command(filter_out.data.HMAC); 
    REQ1 = (filter_in.data.HMAC) = (true); 
    assumption = REQ1; 
    guarantee = REQ2 and REQ3; 
tel

"
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
			
	}
	
	@Test
	def void testSW() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/SW.iml"),true) ;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("MC_SW_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Command_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_Command_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type examples_dot_SW_dot_FlightPattern = enum {
    examples_dot_SW_dot_FlightPattern_dot_ZigZag,
    examples_dot_SW_dot_FlightPattern_dot_StraightLine,
    examples_dot_SW_dot_FlightPattern_dot_Perimeter
};
type examples_dot_SW_dot_Mission_dot_Impl = struct { 
    wp9 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp2 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp1 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp4 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp3 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp6 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp5 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp8 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp7 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp10 : examples_dot_SW_dot_Coordinate_dot_Impl
};

type examples_dot_SW_dot_Command_dot_Impl = struct { 
    Pattern : examples_dot_SW_dot_FlightPattern;
    HMAC : bool;
    Map : examples_dot_SW_dot_Map_dot_Impl
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Command_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_Command_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_Coordinate_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type examples_dot_SW_dot_MissionWindow_dot_Impl = struct { 
    crc : bool;
    wp2 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp1 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp4 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp3 : examples_dot_SW_dot_Coordinate_dot_Impl
};

type iml_dot_systems_dot_OutDataPort__examples_dot_SW_dot_Mission_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_Mission_dot_Impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};
type examples_dot_SW_dot_Map_dot_Impl = struct { 
    wp2 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp1 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp4 : examples_dot_SW_dot_Coordinate_dot_Impl;
    wp3 : examples_dot_SW_dot_Coordinate_dot_Impl
};

type iml_dot_systems_dot_InDataPort__examples_dot_SW_dot_Mission_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_Mission_dot_Impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_MissionWindow_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_MissionWindow_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_MissionWindow_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_MissionWindow_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type examples_dot_SW_dot_Coordinate_dot_Impl = struct { 
    alt : int;
    lat : int;
    long : int
};

type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_Coordinate_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};



node examples_dot_SW_dot_good_mission_window(win : examples_dot_SW_dot_MissionWindow_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_good_coordinate(win.wp1) and examples_dot_SW_dot_good_coordinate(win.wp2) and examples_dot_SW_dot_good_coordinate(win.wp3) and examples_dot_SW_dot_good_coordinate(win.wp4);
tel

node examples_dot_SW_dot_good_gs_command(cmd : examples_dot_SW_dot_Command_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_good_map(cmd.Map) and examples_dot_SW_dot_good_pattern(cmd.Pattern) and examples_dot_SW_dot_good_HMAC(cmd.HMAC);
tel

node examples_dot_SW_dot_good_pattern(pattern : examples_dot_SW_dot_FlightPattern)
returns (_return : bool)
let
    _return = pattern = examples_dot_SW_dot_FlightPattern_dot_ZigZag or (pattern = examples_dot_SW_dot_FlightPattern_dot_StraightLine) or (pattern = examples_dot_SW_dot_FlightPattern_dot_Perimeter);
tel

node examples_dot_SW_dot_good_map(map : examples_dot_SW_dot_Map_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_good_coordinate(map.wp1) and examples_dot_SW_dot_good_coordinate(map.wp2) and examples_dot_SW_dot_good_coordinate(map.wp3) and examples_dot_SW_dot_good_coordinate(map.wp4);
tel

node examples_dot_SW_dot_good_coordinate(coord : examples_dot_SW_dot_Coordinate_dot_Impl)
returns (_return : bool)
let
    _return = coord.lat  >= -90 and coord.lat  <= 90 and coord.long  >= -180 and coord.long  <= 180 and coord.alt  >= 0 and coord.alt  <= 15000;
tel

node examples_dot_SW_dot_good_mission(mission : examples_dot_SW_dot_Mission_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_good_coordinate(mission.wp1) and examples_dot_SW_dot_good_coordinate(mission.wp2) and examples_dot_SW_dot_good_coordinate(mission.wp3) and examples_dot_SW_dot_good_coordinate(mission.wp4) and examples_dot_SW_dot_good_coordinate(mission.wp5) and examples_dot_SW_dot_good_coordinate(mission.wp6) and examples_dot_SW_dot_good_coordinate(mission.wp7) and examples_dot_SW_dot_good_coordinate(mission.wp8) and examples_dot_SW_dot_good_coordinate(mission.wp9) and examples_dot_SW_dot_good_coordinate(mission.wp10);
tel

node examples_dot_SW_dot_good_HMAC(hmac : bool)
returns (_return : bool)
let
    _return = (hmac = true) or (hmac = false);
tel

node examples_dot_SW_dot_FlightPlanner (recv_map : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Command_dot_Impl__; position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__)
returns (flight_plan : iml_dot_systems_dot_OutDataPort__examples_dot_SW_dot_Mission_dot_Impl__)
var a1 : bool;
var a2 : bool;
var assumption : bool;
var guarantee : bool;
let
    a1 = recv_map.data.HMAC = true; 
    a2 = examples_dot_SW_dot_good_gs_command(recv_map.data); 
    assumption = a1 and a2; 
    guarantee = examples_dot_SW_dot_good_mission(flight_plan.data); 
tel

node examples_dot_SW_dot_UARTDriver (waypoint_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_MissionWindow_dot_Impl__; position_status_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__)
returns (position_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__; waypoint_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_MissionWindow_dot_Impl__)
var assumption : bool;
var guarantee : bool;
let
    assumption = examples_dot_SW_dot_good_mission_window(waypoint_in.data); 
    guarantee = waypoint_out.data.crc = true; 
tel

node examples_dot_SW_dot_RadioDriver (send_status_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__; recv_map_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Command_dot_Impl__)
returns (send_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__; recv_map_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Command_dot_Impl__)
var assumption : bool;
var guarantee : bool;
let
    assumption = recv_map_in.data.HMAC = true; 
    guarantee = recv_map_out.data.HMAC = true; 
tel

node examples_dot_SW_dot_WaypointManager (position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__; flight_plan : iml_dot_systems_dot_InDataPort__examples_dot_SW_dot_Mission_dot_Impl__)
returns (waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_MissionWindow_dot_Impl__)
var assumption : bool;
var guarantee : bool;
let
    assumption = examples_dot_SW_dot_good_mission(flight_plan.data); 
    guarantee = examples_dot_SW_dot_good_mission_window(waypoint.data); 
tel

node examples_dot_SW_dot_MC_SW_dot_Impl (recv_map : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Command_dot_Impl__; position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__)
returns (waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_MissionWindow_dot_Impl__; send_status : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__)
var WPM_waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_MissionWindow_dot_Impl__; 
var RADIO_recv_map_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Command_dot_Impl__; 
var UART_position_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_Coordinate_dot_Impl__; 
var FPLN_flight_plan : iml_dot_systems_dot_OutDataPort__examples_dot_SW_dot_Mission_dot_Impl__; 
let
    (WPM_waypoint) = examples_dot_SW_dot_WaypointManager ( UART_position_status_out, FPLN_flight_plan);
    (send_status, RADIO_recv_map_out) = examples_dot_SW_dot_RadioDriver ( UART_position_status_out, recv_map);
    (UART_position_status_out, waypoint) = examples_dot_SW_dot_UARTDriver ( WPM_waypoint, position_status);
    (FPLN_flight_plan) = examples_dot_SW_dot_FlightPlanner ( RADIO_recv_map_out, UART_position_status_out);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}
	
	@Test
	def void testSWAgree() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/SW_agree.iml"),true) ;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("MC_SW_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__examples_dot_SW_dot_agree_dot_Mission_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_Mission_dot_Impl;
    direction : iml_dot_systems_dot_Direction
};

type examples_dot_SW_dot_agree_dot_Command_dot_Impl = struct { 
    Pattern : examples_dot_SW_dot_agree_dot_FlightPattern;
    HMAC : bool;
    Map : examples_dot_SW_dot_agree_dot_Map_dot_Impl
};

type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type examples_dot_SW_dot_agree_dot_Map_dot_Impl = struct { 
    wp2 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp1 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp4 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp3 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl
};

type examples_dot_SW_dot_agree_dot_Mission_dot_Impl = struct { 
    wp9 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp2 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp1 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp4 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp3 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp6 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp5 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp8 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp7 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp10 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};
type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Command_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_Command_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl = struct { 
    crc : bool;
    wp2 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp1 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp4 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    wp3 : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl
};

type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Command_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_Command_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type examples_dot_SW_dot_agree_dot_FlightPattern = enum {
    examples_dot_SW_dot_agree_dot_FlightPattern_dot_ZigZag,
    examples_dot_SW_dot_agree_dot_FlightPattern_dot_StraightLine,
    examples_dot_SW_dot_agree_dot_FlightPattern_dot_Perimeter
};
type examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl = struct { 
    alt : int;
    lat : int;
    long : int
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__examples_dot_SW_dot_agree_dot_Mission_dot_Impl__ = struct { 
    data : examples_dot_SW_dot_agree_dot_Mission_dot_Impl;
    direction : iml_dot_systems_dot_Direction
};



node examples_dot_SW_dot_agree_dot_good_mission_window(win : examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_agree_dot_good_coordinate(win.wp1) and examples_dot_SW_dot_agree_dot_good_coordinate(win.wp2) and examples_dot_SW_dot_agree_dot_good_coordinate(win.wp3) and examples_dot_SW_dot_agree_dot_good_coordinate(win.wp4);
tel

node examples_dot_SW_dot_agree_dot_good_mission(mission : examples_dot_SW_dot_agree_dot_Mission_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp1) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp2) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp3) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp4) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp5) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp6) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp7) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp8) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp9) and examples_dot_SW_dot_agree_dot_good_coordinate(mission.wp10);
tel

node examples_dot_SW_dot_agree_dot_good_gs_command(cmd : examples_dot_SW_dot_agree_dot_Command_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_agree_dot_good_map(cmd.Map) and examples_dot_SW_dot_agree_dot_good_pattern(cmd.Pattern) and examples_dot_SW_dot_agree_dot_good_HMAC(cmd.HMAC);
tel

node examples_dot_SW_dot_agree_dot_good_map(map : examples_dot_SW_dot_agree_dot_Map_dot_Impl)
returns (_return : bool)
let
    _return = examples_dot_SW_dot_agree_dot_good_coordinate(map.wp1) and examples_dot_SW_dot_agree_dot_good_coordinate(map.wp2) and examples_dot_SW_dot_agree_dot_good_coordinate(map.wp3) and examples_dot_SW_dot_agree_dot_good_coordinate(map.wp4);
tel

node examples_dot_SW_dot_agree_dot_good_pattern(pattern : examples_dot_SW_dot_agree_dot_FlightPattern)
returns (_return : bool)
let
    _return = pattern = examples_dot_SW_dot_agree_dot_FlightPattern_dot_ZigZag or (pattern = examples_dot_SW_dot_agree_dot_FlightPattern_dot_StraightLine) or (pattern = examples_dot_SW_dot_agree_dot_FlightPattern_dot_Perimeter);
tel

node examples_dot_SW_dot_agree_dot_good_HMAC(hmac : bool)
returns (_return : bool)
let
    _return = (hmac = true) or (hmac = false);
tel

node examples_dot_SW_dot_agree_dot_good_coordinate(coord : examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl)
returns (_return : bool)
let
    _return = coord.lat  >= -90 and coord.lat  <= 90 and coord.long  >= -180 and coord.long  <= 180 and coord.alt  >= 0 and coord.alt  <= 15000;
tel

node imported examples_dot_SW_dot_agree_dot_FlightPlanner (recv_map : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Command_dot_Impl__; position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__)
returns (flight_plan : iml_dot_systems_dot_OutDataPort__examples_dot_SW_dot_agree_dot_Mission_dot_Impl__)
(*@contract
    guarantee \"Req003_FlightPlanner\" examples_dot_SW_dot_agree_dot_good_mission(flight_plan.data);
    assume \"Req001_FlightPlanner\" recv_map.data.HMAC = true;
    assume \"Req002_FlightPlanner\" examples_dot_SW_dot_agree_dot_good_gs_command(recv_map.data);
*) 

node imported examples_dot_SW_dot_agree_dot_UARTDriver (waypoint_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl__; position_status_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__)
returns (position_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__; waypoint_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl__)
(*@contract
    guarantee \"Req002_UARTDriver\" waypoint_out.data.crc = true;
    assume \"Req001_UARTDriver\" examples_dot_SW_dot_agree_dot_good_mission_window(waypoint_in.data);
*) 

node imported examples_dot_SW_dot_agree_dot_RadioDriver (send_status_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__; recv_map_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Command_dot_Impl__)
returns (send_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__; recv_map_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Command_dot_Impl__)
(*@contract
    guarantee \"Req002_RadioDriver\" recv_map_out.data.HMAC = true;
    assume \"Req001_RadioDriver\" recv_map_in.data.HMAC = true;
*) 

node imported examples_dot_SW_dot_agree_dot_WaypointManager (position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__; flight_plan : iml_dot_systems_dot_InDataPort__examples_dot_SW_dot_agree_dot_Mission_dot_Impl__)
returns (waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl__)
(*@contract
    guarantee \"Req002_WaypointManager\" examples_dot_SW_dot_agree_dot_good_mission_window(waypoint.data);
    assume \"Req001_WaypointManager\" examples_dot_SW_dot_agree_dot_good_mission(flight_plan.data);
*) 

node examples_dot_SW_dot_agree_dot_MC_SW_dot_Impl (recv_map : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Command_dot_Impl__; position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__)
returns (waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl__; send_status : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__)
(*@contract
    guarantee \"Req003_MC_SW\" waypoint.data.crc = true;
    assume \"Req001_MC_SW\" recv_map.data.HMAC = true;
    assume \"Req002_MC_SW\" recv_map.data.HMAC = true;
*) 
var WPM_waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_MissionWindow_dot_Impl__; 
var RADIO_recv_map_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Command_dot_Impl__; 
var UART_position_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__examples_dot_SW_dot_agree_dot_Coordinate_dot_Impl__; 
var FPLN_flight_plan : iml_dot_systems_dot_OutDataPort__examples_dot_SW_dot_agree_dot_Mission_dot_Impl__; 
let
    (WPM_waypoint) = examples_dot_SW_dot_agree_dot_WaypointManager ( UART_position_status_out, FPLN_flight_plan);
    (send_status, RADIO_recv_map_out) = examples_dot_SW_dot_agree_dot_RadioDriver ( UART_position_status_out, recv_map);
    (UART_position_status_out, waypoint) = examples_dot_SW_dot_agree_dot_UARTDriver ( WPM_waypoint, position_status);
    (FPLN_flight_plan) = examples_dot_SW_dot_agree_dot_FlightPlanner ( RADIO_recv_map_out, UART_position_status_out);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}

	@Test
	def void testSWAgreeImlAutoGenerated() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/SW_agree_imlAutoGenerated_v2.iml"),true) ;
//		var Model m = parse(FileUtil.readFileContent("models/synchdf/SW_Dec04_2019.IML"),true) ;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("MC_SW_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		gen.displayMapLustre2Iml();
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type agree_dot_Mission_dot_Impl = struct { 
    wp9 : agree_dot_Coordinate_dot_Impl;
    wp2 : agree_dot_Coordinate_dot_Impl;
    wp1 : agree_dot_Coordinate_dot_Impl;
    wp4 : agree_dot_Coordinate_dot_Impl;
    wp3 : agree_dot_Coordinate_dot_Impl;
    wp6 : agree_dot_Coordinate_dot_Impl;
    wp5 : agree_dot_Coordinate_dot_Impl;
    wp8 : agree_dot_Coordinate_dot_Impl;
    wp7 : agree_dot_Coordinate_dot_Impl;
    wp10 : agree_dot_Coordinate_dot_Impl
};

type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_MissionWindow_dot_Impl__ = struct { 
    data : agree_dot_MissionWindow_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_MissionWindow_dot_Impl__ = struct { 
    data : agree_dot_MissionWindow_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_MissionWindow_dot_Impl = struct { 
    crc : bool;
    wp2 : agree_dot_Coordinate_dot_Impl;
    wp1 : agree_dot_Coordinate_dot_Impl;
    wp4 : agree_dot_Coordinate_dot_Impl;
    wp3 : agree_dot_Coordinate_dot_Impl
};

type agree_dot_Command_dot_Impl = struct { 
    Pattern : agree_dot_FlightPattern;
    HMAC : bool;
    Map : agree_dot_Map_dot_Impl
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};
type agree_dot_Coordinate_dot_Impl = struct { 
    alt : int;
    lat : int;
    long : int
};

type iml_dot_systems_dot_InDataPort__agree_dot_Mission_dot_Impl__ = struct { 
    data : agree_dot_Mission_dot_Impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__ = struct { 
    data : agree_dot_Coordinate_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__ = struct { 
    data : agree_dot_Command_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__agree_dot_Mission_dot_Impl__ = struct { 
    data : agree_dot_Mission_dot_Impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__ = struct { 
    data : agree_dot_Coordinate_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_Map_dot_Impl = struct { 
    wp2 : agree_dot_Coordinate_dot_Impl;
    wp1 : agree_dot_Coordinate_dot_Impl;
    wp4 : agree_dot_Coordinate_dot_Impl;
    wp3 : agree_dot_Coordinate_dot_Impl
};

type agree_dot_FlightPattern = enum {
    agree_dot_FlightPattern_dot_ZigZag,
    agree_dot_FlightPattern_dot_StraightLine,
    agree_dot_FlightPattern_dot_Perimeter
};
type iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__ = struct { 
    data : agree_dot_Command_dot_Impl;
    event : bool;
    direction : iml_dot_systems_dot_Direction
};



node agree_dot_good_mission_window(win : agree_dot_MissionWindow_dot_Impl)
returns (_return : bool)
let
    _return = agree_dot_good_coordinate(win.wp1) and agree_dot_good_coordinate(win.wp2) and agree_dot_good_coordinate(win.wp3) and agree_dot_good_coordinate(win.wp4);
tel

node agree_dot_good_command(cmd : agree_dot_Command_dot_Impl)
returns (_return : bool)
let
    _return = agree_dot_good_map(cmd.Map) and agree_dot_good_pattern(cmd.Pattern) and agree_dot_good_HMAC(cmd.HMAC);
tel

node agree_dot_good_map(map : agree_dot_Map_dot_Impl)
returns (_return : bool)
let
    _return = agree_dot_good_coordinate(map.wp1) and agree_dot_good_coordinate(map.wp2) and agree_dot_good_coordinate(map.wp3) and agree_dot_good_coordinate(map.wp4);
tel

node agree_dot_good_pattern(pattern : agree_dot_FlightPattern)
returns (_return : bool)
let
    _return = pattern = agree_dot_FlightPattern_dot_ZigZag or pattern = agree_dot_FlightPattern_dot_StraightLine or pattern = agree_dot_FlightPattern_dot_Perimeter;
tel

node agree_dot_good_coordinate(coord : agree_dot_Coordinate_dot_Impl)
returns (_return : bool)
let
    _return = coord.lat  >= 90 and coord.lat  <= 90 and coord.long  >= 180 and coord.long  <= 180 and coord.alt  >= 0 and coord.alt  <= 15000;
tel

node agree_dot_good_HMAC(hmac : bool)
returns (_return : bool)
let
    _return = (hmac = true) or (hmac = false);
tel

node agree_dot_good_mission(mission : agree_dot_Mission_dot_Impl)
returns (_return : bool)
let
    _return = agree_dot_good_coordinate(mission.wp1) and agree_dot_good_coordinate(mission.wp2) and agree_dot_good_coordinate(mission.wp3) and agree_dot_good_coordinate(mission.wp4) and agree_dot_good_coordinate(mission.wp5) and agree_dot_good_coordinate(mission.wp6) and agree_dot_good_coordinate(mission.wp7) and agree_dot_good_coordinate(mission.wp8) and agree_dot_good_coordinate(mission.wp9) and agree_dot_good_coordinate(mission.wp10);
tel

node imported agree_dot_FlightPlanner_dot_Impl (recv_map : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__; position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__)
returns (flight_plan : iml_dot_systems_dot_OutDataPort__agree_dot_Mission_dot_Impl__)
(*@contract
    guarantee \"Req003_FlightPlanner\" agree_dot_good_mission(flight_plan.data);
    assume \"Req001_FlightPlanner\" agree_dot_good_command(recv_map.data);
    assume \"Req002_FlightPlanner\" recv_map.data.HMAC = true;
*) 

node imported agree_dot_UARTDriver_dot_Impl (waypoint_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_MissionWindow_dot_Impl__; position_status_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__)
returns (position_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__; waypoint_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_MissionWindow_dot_Impl__)
(*@contract
    guarantee \"Req002_UARTDriver\" waypoint_out.data.crc = true;
    assume \"Req001_UARTDriver\" agree_dot_good_mission_window(waypoint_in.data);
*) 

node imported agree_dot_RadioDriver_dot_Impl (send_status_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__; recv_map_in : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__)
returns (send_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__; recv_map_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__)
(*@contract
    guarantee \"Req002_RadioDriver\" recv_map_out.data.HMAC = true;
    assume \"Req001_RadioDriver\" recv_map_in.data.HMAC = true;
*) 

node imported agree_dot_WaypointManager_dot_Impl (position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__; flight_plan : iml_dot_systems_dot_InDataPort__agree_dot_Mission_dot_Impl__)
returns (waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_MissionWindow_dot_Impl__)
(*@contract
    guarantee \"Req002_WaypointManager\" agree_dot_good_mission_window(waypoint.data);
    assume \"Req001_WaypointManager\" agree_dot_good_mission(flight_plan.data);
*) 

node agree_dot_MC_SW_dot_Impl (recv_map : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__; position_status : iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__)
returns (waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_MissionWindow_dot_Impl__; send_status : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__)
(*@contract
    guarantee \"Req003_MC_SW\" waypoint.data.crc = true;
    assume \"Req001_MC_SW\" recv_map.data.HMAC = true;
    assume \"Req002_MC_SW\" recv_map.data.HMAC = true;
*) 
var WPM_waypoint : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_MissionWindow_dot_Impl__; 
var RADIO_recv_map_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__; 
var UART_position_status_out : iml_dot_systems_dot_OutEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__; 
var FPLN_flight_plan : iml_dot_systems_dot_OutDataPort__agree_dot_Mission_dot_Impl__; 
let
    position_status = iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Coordinate_dot_Impl__{event = UART.position_status_in.event; direction = iml_dot_systems_dot_Direction_dot_IN; data = };
    recv_map = iml_dot_systems_dot_InEventDataPort__iml_dot_lang_dot_Bool__agree_dot_Command_dot_Impl__{event = RADIO.recv_map_in.event; direction = iml_dot_systems_dot_Direction_dot_IN; data = };
    (UART.waypoint_out.event = waypoint.event);
    (UART.position_status_out.event = RADIO.send_status_in.event);
    (UART.position_status_out.event = FPLN.position_status.event);
    (UART.position_status_out.event = WPM.position_status.event);
    (WPM.waypoint.event = UART.waypoint_in.event);
    (RADIO.recv_map_out.event = FPLN.recv_map.event);
    (RADIO.send_status_out.event = send_status.event);
    (WPM_waypoint) = agree_dot_WaypointManager_dot_Impl ( UART_position_status_out, FPLN_flight_plan);
    (send_status, RADIO_recv_map_out) = agree_dot_RadioDriver_dot_Impl ( UART_position_status_out, recv_map);
    (UART_position_status_out, waypoint) = agree_dot_UARTDriver_dot_Impl ( WPM_waypoint, position_status);
    (FPLN_flight_plan) = agree_dot_FlightPlanner_dot_Impl ( RADIO_recv_map_out, UART_position_status_out);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}	


	@Test
	def void testUTRC_Explain_yes() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/UTRC_Explain_yes.iml"),true) ;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("top_level_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		gen.displayMapLustre2Iml();
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node imported agree_dot_UTRC_Explain_Yes_dot_B (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < Input.data + 15;
    assume \"REQ1\" Input.data  < 20;
*) 

node imported agree_dot_UTRC_Explain_Yes_dot_C (Input1 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Input2 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ1\" Output.data = Input1.data + Input2.data;
*) 

node imported agree_dot_UTRC_Explain_Yes_dot_A (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < 2 * Input.data;
    assume \"REQ1\" Input.data  < 20;
*) 

node agree_dot_UTRC_Explain_Yes_dot_top_level_dot_Impl (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < 50;
    assume \"REQ1\" Input.data  < 10;
*) 
var A_sub_Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var B_sub_Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
let
    (A_sub_Output) = agree_dot_UTRC_Explain_Yes_dot_A ( Input);
    (Output) = agree_dot_UTRC_Explain_Yes_dot_C ( A_sub_Output, B_sub_Output);
    (B_sub_Output) = agree_dot_UTRC_Explain_Yes_dot_B ( A_sub_Output);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}	
	
	@Test
	def void testUTRC_ExternalOutAlsoUsedAsInternalIn() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/testExternalOutAlsoUsedAsInternalIn.iml"),true) ;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("top_level_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node imported agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_B (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < Input.data + 15;
    assume \"REQ1\" Input.data  < 20;
*) 

node imported agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_C (Input1 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Input2 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ1\" Output.data = Input1.data + Input2.data;
*) 

node imported agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_A (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < 2 * Input.data;
    assume \"REQ1\" Input.data  < 20;
*) 

node agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_top_level_dot_Impl (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; Output2 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < 50;
    assume \"REQ1\" Input.data  < 10;
*) 
var A_sub_Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
let
    (A_sub_Output) = agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_A ( Input);
    (Output) = agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_C ( A_sub_Output, Output2);
    (Output2) = agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_B ( A_sub_Output);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}	

	@Test
	def void testUTRC_DanglingInternalOut() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/testDanglingInternalOut.iml"),true) ;
		m.assertNoErrors;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("top_level_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node imported agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_B (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; Output2 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < Input.data + 15;
    assume \"REQ1\" Input.data  < 20;
*) 

node imported agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_C (Input1 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Input2 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ1\" Output.data = Input1.data + Input2.data;
*) 

node imported agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_A (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < 2 * Input.data;
    assume \"REQ1\" Input.data  < 20;
*) 

node agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_top_level_dot_Impl (Input : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ2\" Output.data  < 50;
    assume \"REQ1\" Input.data  < 10;
*) 
var A_sub_Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var B_sub_Output : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var B_sub_Output2 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__;
let
    (A_sub_Output) = agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_A ( Input);
    (Output) = agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_C ( A_sub_Output, B_sub_Output);
    (B_sub_Output, B_sub_Output2) = agree_dot_UTRC_ExternalOutAlsoUsedAsInternalIn_dot_B ( A_sub_Output);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}	

	@Test
	def void testIOWA_InputA() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/InputA1.iml"),true) ;
//		var Model m = parse(FileUtil.readFileContent("models/synchdf/InputA2.iml"),true) ;
		m.assertNoErrors;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Lock_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		gen.displayMapLustre2Iml();
//		System.out.println();
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "
type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node Since(X : bool; Y : bool)
returns (SinceXY : bool)
let
    SinceXY = (X or Y and (false -> pre(SinceXY)));
tel

node HasHappened(X : bool)
returns (Y : bool)
let
    Y = (X or (false -> pre(Y)));
tel

node agree_dot_InputA1_dot_Control_dot_Impl (MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Request : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ4\" Granted.data => Code.data = CurrentCode.data;
    guarantee \"REQ5\" (true -> (HasHappened(MasterKey.data and Request.data) and CurrentCode.data  <> pre(CurrentCode.data) => MasterKey.data));
    guarantee \"REQ2\" not (HasHappened(Request.data and MasterKey.data)) => not (Granted.data);
    guarantee \"REQ3\" (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)) => Granted.data;
    assume \"REQ1\" 0  <= Code.data;
    guarantee \"REQ6\" Granted.data => Request.data and not (MasterKey.data);
*) 
let
    CurrentCode = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((-1 -> (if MasterKey.data and Request.data then (Code.data) else (pre(CurrentCode.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Granted = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputA1_dot_Keypad_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var PreRequest : bool = ((false -> pre(Request.data)));
    guarantee \"REQ4\" Code.data  >= 0;
    guarantee \"REQ5\" Request.data => Press.data;
    guarantee \"REQ2\" (not (Request.data) -> (not (PreRequest) -> true));
    guarantee \"REQ3\" (true -> Request.data => not (PreRequest) and not (pre(PreRequest)));
    assume \"REQ1\" 0  <= Digit.data;
*) 
var PressedDigits : int;
var IncompleteCode : bool;
var ElapsedTime : int;
var ExpirationTime : int;
let
    PressedDigits = ((if Press.data then (1) else (0)) -> (if ElapsedTime = ExpirationTime then (0) else ((if not (Press.data) then (pre(PressedDigits)) else ((if pre(PressedDigits) = 3 then (1) else (pre(PressedDigits) + 1))))))); 
    IncompleteCode = (false -> (0  < pre(PressedDigits) and pre(PressedDigits)  < 3)); 
    ElapsedTime = (0 -> (if Press.data then (0) else ((if pre(ElapsedTime)  < ExpirationTime and IncompleteCode then (pre(ElapsedTime) + 1) else (0))))); 
    ExpirationTime = 9; 
    Code = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((if Press.data then (((Digit.data -> (if PressedDigits = 1 then (Digit.data) else (10 * pre(Code.data) + Digit.data))))) else ((0 -> pre(Code.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Request = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = ((false -> Press.data and PressedDigits = 3)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputA1_dot_Timer_dot_Impl (Granted : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Running : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)
var Timer : int;
let
    Timer = (0 -> (if pre(Running.data) then (pre(Timer) - 1) else ((if Granted.data then (4) else (0))))); 
    Running = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Timer  > 0); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputA1_dot_Lock_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Unlocking : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var C : int = ((if Unlocking.data then (((1 -> pre(C) + 1))) else (0)));
    var Locks : bool = ((false -> pre(Unlocking.data) and not (Unlocking.data)));
    guarantee \"REQ2\" (C  <= 4);
    guarantee \"REQ3\" Since(Locks,not (Press.data)) => not (Unlocking.data);
    assume \"REQ1\" 0  <= Digit.data;
*) 
var keypad_Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var keypad_Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var control_Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
let
    (Unlocking) = agree_dot_InputA1_dot_Timer_dot_Impl ( control_Granted);
    (keypad_Request, keypad_Code) = agree_dot_InputA1_dot_Keypad_dot_Impl ( Digit, Press);
    (control_Granted, CurrentCode) = agree_dot_InputA1_dot_Control_dot_Impl ( MasterKey, keypad_Request, keypad_Code);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}

	@Test
	def void testIOWA_InputB() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/InputB.iml"),true) ;
		m.assertNoErrors;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Lock_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node HasHappened(X : bool)
returns (Y : bool)
let
    Y = (X or (false -> pre(Y)));
tel

node Since(X : bool; Y : bool)
returns (SinceXY : bool)
let
    SinceXY = (X or Y and (false -> pre(SinceXY)));
tel

node agree_dot_InputB_dot_Control_dot_Impl (MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Request : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ4\" Granted.data => Code.data = CurrentCode.data;
    guarantee \"REQ5\" (true -> (HasHappened(MasterKey.data and Request.data) and CurrentCode.data  <> pre(CurrentCode.data) => MasterKey.data));
    guarantee \"REQ2\" not (HasHappened(Request.data and MasterKey.data)) => not (Granted.data);
    guarantee \"REQ3\" (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)) => Granted.data;
    assume \"REQ1\" 0  <= Code.data;
    guarantee \"REQ6\" Granted.data => Request.data and not (MasterKey.data);
*) 
let
    CurrentCode = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((-1 -> (if MasterKey.data and Request.data then (Code.data) else (pre(CurrentCode.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Granted = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputB_dot_Keypad_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var PreRequest : bool = ((false -> pre(Request.data)));
    guarantee \"REQ4\" Code.data  >= 0;
    guarantee \"REQ5\" Request.data => Press.data;
    guarantee \"REQ2\" (not (Request.data) -> (not (PreRequest) -> true));
    guarantee \"REQ3\" (true -> Request.data => not (PreRequest) and not (pre(PreRequest)));
    assume \"REQ1\" 0  <= Digit.data;
*) 
var PressedDigits : int;
var IncompleteCode : bool;
var ElapsedTime : int;
var ExpirationTime : int;
let
    PressedDigits = ((if Press.data then (1) else (0)) -> (if ElapsedTime = ExpirationTime then (0) else ((if not (Press.data) then (pre(PressedDigits)) else ((if pre(PressedDigits) = 3 then (1) else (pre(PressedDigits) + 1))))))); 
    IncompleteCode = (false -> (0  < pre(PressedDigits) and pre(PressedDigits)  < 3)); 
    ElapsedTime = (0 -> (if Press.data then (0) else ((if pre(ElapsedTime)  < ExpirationTime and IncompleteCode then (pre(ElapsedTime) + 1) else (0))))); 
    ExpirationTime = 9; 
    Code = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((if Press.data then (((Digit.data -> (if PressedDigits = 1 then (Digit.data) else (10 * pre(Code.data) + Digit.data))))) else ((0 -> pre(Code.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Request = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = ((false -> Press.data and PressedDigits = 3)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputB_dot_Timer_dot_Impl (Granted : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Running : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)
var Timer : int;
let
    Timer = (0 -> (if pre(Running.data) then (pre(Timer) - 1) else ((if Granted.data then (4) else (0))))); 
    Running = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Timer  > 0); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputB_dot_Lock_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Unlocking : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var C : int = ((if Unlocking.data then (((1 -> pre(C) + 1))) else (0)));
    var Locks : bool = ((false -> pre(Unlocking.data) and not (Unlocking.data)));
    guarantee \"REQ2\" (C  <= 3);
    guarantee \"REQ3\" Since(Locks,not (Press.data)) => not (Unlocking.data);
    assume \"REQ1\" 0  <= Digit.data;
*) 
var keypad_Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var keypad_Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var control_Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
let
    (Unlocking) = agree_dot_InputB_dot_Timer_dot_Impl ( control_Granted);
    (keypad_Request, keypad_Code) = agree_dot_InputB_dot_Keypad_dot_Impl ( Digit, Press);
    (control_Granted, CurrentCode) = agree_dot_InputB_dot_Control_dot_Impl ( MasterKey, keypad_Request, keypad_Code);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}

	@Test
	def void testIOWA_InputC() {
		
		var Model m = parse(FileUtil.readFileContent("models/synchdf/InputC.iml"),true) ;
		m.assertNoErrors;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Lock_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node HasHappened(X : bool)
returns (Y : bool)
let
    Y = (X or (false -> pre(Y)));
tel

node Since(X : bool; Y : bool)
returns (SinceXY : bool)
let
    SinceXY = (X or Y and (false -> pre(SinceXY)));
tel

node agree_dot_InputC_dot_Control_dot_Impl (MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Request : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ4\" Granted.data => Code.data = CurrentCode.data;
    guarantee \"REQ5\" (true -> (HasHappened(MasterKey.data and Request.data) and CurrentCode.data  <> pre(CurrentCode.data) => MasterKey.data));
    guarantee \"REQ2\" not (HasHappened(Request.data and MasterKey.data)) => not (Granted.data);
    guarantee \"REQ3\" (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)) => Granted.data;
    assume \"REQ1\" 0  <= Code.data;
    guarantee \"REQ6\" Granted.data => Request.data and not (MasterKey.data);
*) 
let
    CurrentCode = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((-1 -> (if MasterKey.data and Request.data then (Code.data) else (pre(CurrentCode.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Granted = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputC_dot_Keypad_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var PreRequest : bool = ((false -> pre(Request.data)));
    guarantee \"REQ4\" Code.data  >= 0;
    guarantee \"REQ5\" Request.data => Press.data;
    guarantee \"REQ2\" (not (Request.data) -> (not (PreRequest) -> true));
    guarantee \"REQ3\" (true -> Request.data => not (PreRequest) and not (pre(PreRequest)));
    assume \"REQ1\" 0  <= Digit.data;
*) 
var PressedDigits : int;
var IncompleteCode : bool;
var ElapsedTime : int;
var ExpirationTime : int;
let
    PressedDigits = ((if Press.data then (1) else (0)) -> (if ElapsedTime = ExpirationTime then (0) else ((if not (Press.data) then (pre(PressedDigits)) else ((if pre(PressedDigits) = 3 then (1) else (pre(PressedDigits) + 1))))))); 
    IncompleteCode = (false -> (0  < pre(PressedDigits) and pre(PressedDigits)  < 3)); 
    ElapsedTime = (0 -> (if Press.data then (0) else ((if pre(ElapsedTime)  < ExpirationTime and IncompleteCode then (pre(ElapsedTime) + 1) else (0))))); 
    ExpirationTime = 9; 
    Code = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((if Press.data then (((Digit.data -> (if PressedDigits = 1 then (Digit.data) else (10 * pre(Code.data) + Digit.data))))) else ((0 -> pre(Code.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Request = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = ((false -> Press.data and PressedDigits = 3)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputC_dot_Timer_dot_Impl (Granted : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Running : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)
var Timer : int;
let
    Timer = (0 -> (if pre(Running.data) then (pre(Timer) - 1) else ((if Granted.data then (4) else (0))))); 
    Running = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Timer  > 0); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputC_dot_Lock_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Unlocking : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var C : int = ((if Unlocking.data then (((1 -> pre(C) + 1))) else (0)));
    var Locks : bool = ((false -> pre(Unlocking.data) and not (Unlocking.data)));
    guarantee \"REQ4\" Since(Locks,not (Press.data)) => not (Unlocking.data);
    guarantee \"REQ5\" Unlocking.data => CurrentCode.data * CurrentCode.data  < 1000000;
    assume \"REQ2\" Digit.data  <= 9;
    guarantee \"REQ3\" (C  <= 4);
    assume \"REQ1\" 0  <= Digit.data;
*) 
var keypad_Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var keypad_Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var control_Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
let
    (Unlocking) = agree_dot_InputC_dot_Timer_dot_Impl ( control_Granted);
    (keypad_Request, keypad_Code) = agree_dot_InputC_dot_Keypad_dot_Impl ( Digit, Press);
    (control_Granted, CurrentCode) = agree_dot_InputC_dot_Control_dot_Impl ( MasterKey, keypad_Request, keypad_Code);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}


	@Test
	def void testIOWA_InputDEF() {
		
		var mList = newArrayList();
		
		var rs = parseDir("models/DEF",true) ;
		var Model m = rs.resources.map[it.contents.get(0) as Model].findFirst[it.name == "agree.InputD"]
		m.assertNoErrors;
		mList.add(m);
		
		for (Import i : m.imports) {
			val String nm = i.importedNamespace.replace(".*", "");
			val mi = rs.resources.map[it.contents.get(0) as Model].findFirst[it.name == nm]
			mi.assertNoErrors;
			mList.add(mi);
		}
		
		for (Model mi : mList) {
			sys.process(mi) ;			
		}

		sdf.systems = sys;
		
		for (Model mi : mList) {
			sdf.process(mi);			
		}		

		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Lock_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node HasHappened(X : bool)
returns (Y : bool)
let
    Y = (X or (false -> pre(Y)));
tel

node Since(X : bool; Y : bool)
returns (SinceXY : bool)
let
    SinceXY = (X or Y and (false -> pre(SinceXY)));
tel

node agree_dot_InputE_dot_Control_dot_Impl (MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Request : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ4\" Granted.data => Code.data = CurrentCode.data;
    guarantee \"REQ5\" (true -> (HasHappened(MasterKey.data and Request.data) and CurrentCode.data  <> pre(CurrentCode.data) => MasterKey.data));
    guarantee \"REQ2\" not (HasHappened(Request.data and MasterKey.data)) => not (Granted.data);
    guarantee \"REQ3\" (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)) => Granted.data;
    assume \"REQ1\" 0  <= Code.data;
    guarantee \"REQ6\" Granted.data => Request.data and not (MasterKey.data);
*) 
let
    CurrentCode = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((-1 -> (if MasterKey.data and Request.data then (Code.data) else (pre(CurrentCode.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Granted = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputF_dot_Keypad_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var PreRequest : bool = ((false -> pre(Request.data)));
    guarantee \"REQ4\" (true -> Request.data => not (PreRequest) and not (pre(PreRequest)));
    guarantee \"REQ5\" Code.data  >= 0;
    assume \"REQ2\" Digit.data  <= 9;
    guarantee \"REQ3\" (not (Request.data) -> (not (PreRequest) -> true));
    assume \"REQ1\" 0  <= Digit.data;
    guarantee \"REQ6\" Request.data => Press.data;
    guarantee \"REQ7\" (Code.data * Code.data)  < 1000000;
*) 
var PressedDigits : int;
var IncompleteCode : bool;
var ElapsedTime : int;
var ExpirationTime : int;
let
    PressedDigits = ((if Press.data then (1) else (0)) -> (if ElapsedTime = ExpirationTime then (0) else ((if not (Press.data) then (pre(PressedDigits)) else ((if pre(PressedDigits) = 3 then (1) else (pre(PressedDigits) + 1))))))); 
    IncompleteCode = (false -> (0  < pre(PressedDigits) and pre(PressedDigits)  < 3)); 
    ElapsedTime = (0 -> (if Press.data then (0) else ((if pre(ElapsedTime)  < ExpirationTime and IncompleteCode then (pre(ElapsedTime) + 1) else (0))))); 
    ExpirationTime = 9; 
    Code = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((if Press.data then (((Digit.data -> (if PressedDigits = 1 then (Digit.data) else (10 * pre(Code.data) + Digit.data))))) else ((0 -> pre(Code.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Request = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = ((false -> Press.data and PressedDigits = 3)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputD_dot_Timer_dot_Impl (Granted : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Running : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)
var Timer : int;
let
    Timer = (0 -> (if pre(Running.data) then (pre(Timer) - 1) else ((if Granted.data then (4) else (0))))); 
    Running = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Timer  > 0); direction = iml_dot_systems_dot_Direction_dot_IN};
tel

node agree_dot_InputD_dot_Lock_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Unlocking : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var C : int = ((if Unlocking.data then (((1 -> pre(C) + 1))) else (0)));
    var Locks : bool = ((false -> pre(Unlocking.data) and not (Unlocking.data)));
    guarantee \"REQ4\" Since(Locks,not (Press.data)) => not (Unlocking.data);
    guarantee \"REQ5\" Unlocking.data => (CurrentCode.data * CurrentCode.data)  < 1000000;
    assume \"REQ2\" Digit.data  <= 9;
    guarantee \"REQ3\" (C  <= 4);
    assume \"REQ1\" 0  <= Digit.data;
*) 
var keypad_Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var keypad_Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var control_Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
let
    (Unlocking) = agree_dot_InputD_dot_Timer_dot_Impl ( control_Granted);
    (keypad_Request, keypad_Code) = agree_dot_InputF_dot_Keypad_dot_Impl ( Digit, Press);
    (control_Granted, CurrentCode) = agree_dot_InputE_dot_Control_dot_Impl ( MasterKey, keypad_Request, keypad_Code);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}	

	@Test
	def void testIOWA_InputE() {
		
		var Model m = parse(FileUtil.readFileContent("models/DEF/InputE.iml"),true) ;
		m.assertNoErrors;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Control_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node HasHappened(X : bool)
returns (Y : bool)
let
    Y = (X or (false -> pre(Y)));
tel

node agree_dot_InputE_dot_Control_dot_Impl (MasterKey : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Request : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__)
returns (Granted : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; CurrentCode : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    guarantee \"REQ4\" Granted.data => Code.data = CurrentCode.data;
    guarantee \"REQ5\" (true -> (HasHappened(MasterKey.data and Request.data) and CurrentCode.data  <> pre(CurrentCode.data) => MasterKey.data));
    guarantee \"REQ2\" not (HasHappened(Request.data and MasterKey.data)) => not (Granted.data);
    guarantee \"REQ3\" (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)) => Granted.data;
    assume \"REQ1\" 0  <= Code.data;
    guarantee \"REQ6\" Granted.data => Request.data and not (MasterKey.data);
*) 
let
    Granted = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = (Request.data and (Code.data = CurrentCode.data) and not (MasterKey.data)); direction = iml_dot_systems_dot_Direction_dot_IN};
    CurrentCode = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((-1 -> (if MasterKey.data and Request.data then (Code.data) else (pre(CurrentCode.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}


	@Test
	def void testIOWA_InputF() {
		
		var Model m = parse(FileUtil.readFileContent("models/DEF/InputF.iml"),true) ;
		m.assertNoErrors;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("Keypad_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node agree_dot_InputF_dot_Keypad_dot_Impl (Digit : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; Press : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (Request : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; Code : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)
(*@contract
    var PreRequest : bool = ((false -> pre(Request.data)));
    guarantee \"REQ4\" (true -> Request.data => not (PreRequest) and not (pre(PreRequest)));
    guarantee \"REQ5\" Code.data  >= 0;
    assume \"REQ2\" Digit.data  <= 9;
    guarantee \"REQ3\" (not (Request.data) -> (not (PreRequest) -> true));
    assume \"REQ1\" 0  <= Digit.data;
    guarantee \"REQ6\" Request.data => Press.data;
    guarantee \"REQ7\" (Code.data * Code.data)  < 1000000;
*) 
var PressedDigits : int;
var IncompleteCode : bool;
var ElapsedTime : int;
var ExpirationTime : int;
let
    PressedDigits = ((if Press.data then (1) else (0)) -> (if ElapsedTime = ExpirationTime then (0) else ((if not (Press.data) then (pre(PressedDigits)) else ((if pre(PressedDigits) = 3 then (1) else (pre(PressedDigits) + 1))))))); 
    IncompleteCode = (false -> (0  < pre(PressedDigits) and pre(PressedDigits)  < 3)); 
    ElapsedTime = (0 -> (if Press.data then (0) else ((if pre(ElapsedTime)  < ExpirationTime and IncompleteCode then (pre(ElapsedTime) + 1) else (0))))); 
    ExpirationTime = 9; 
    Code = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__{data = ((if Press.data then (((Digit.data -> (if PressedDigits = 1 then (Digit.data) else (10 * pre(Code.data) + Digit.data))))) else ((0 -> pre(Code.data))))); direction = iml_dot_systems_dot_Direction_dot_IN};
    Request = iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__{data = ((false -> Press.data and PressedDigits = 3)); direction = iml_dot_systems_dot_Direction_dot_IN};
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}

	@Test
	def void testSWAgreeImlDeliveryDrone() {
		
		var mList = newArrayList();
		
		//var Model m = parse(FileUtil.readFileContent("models/agree/DeliveryDrone.iml"),true) ;
		var rs = parseDir("models/agree",true) ;
		var Model m = rs.resources.map[it.contents.get(0) as Model].findFirst[it.name == "agree.DeliveryDrone"]
//		var rs = parseDir("models/testDD",true) ;
//		var Model m = rs.resources.map[it.contents.get(0) as Model].findFirst[it.name == "DeliveryDrone"]
		m.assertNoErrors;
		mList.add(m);
		
		for (Import i : m.imports) {
			val String nm = i.importedNamespace.replace(".*", "");
			val mi = rs.resources.map[it.contents.get(0) as Model].findFirst[it.name == nm]
			mi.assertNoErrors;
			mList.add(mi);
		}
		
//		EcoreUtil2.getContainerOfType(ele, NamedType)
		
		for (Model mi : mList) {
			sys.process(mi) ;			
		}

		sdf.systems = sys;
		
		for (Model mi : mList) {
			sdf.process(mi);			
		}		

		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("DeliveryDroneSystem_dot_Impl") as NamedType;
//		var NamedType nodetype = m.findSymbol("DeliveyDrone_dot_Impl") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_Data_Types_dot_InputBus_dot_impl = struct { 
    connected : bool;
    update_order : bool;
    order : agree_dot_Data_Types_dot_DeliveryOrder_dot_impl
};

type iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_DeliveryStatus__ = struct { 
    data : agree_dot_Data_Types_dot_DeliveryStatus;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_Data_Types_dot_DeliveryOrder_dot_impl = struct { 
    target_picture : int;
    target_position : agree_dot_Data_Types_dot_Position_dot_impl;
    item_value : real
};

type iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_RadioResponse_dot_impl__ = struct { 
    data : agree_dot_Data_Types_dot_RadioResponse_dot_impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};
type iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_InputBus_dot_impl__ = struct { 
    data : agree_dot_Data_Types_dot_InputBus_dot_impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__ = struct { 
    data : agree_dot_Data_Types_dot_Position_dot_impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__ = struct { 
    data : bool;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_Data_Types_dot_Position_dot_impl = struct { 
    x : real;
    y : real
};

type iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__ = struct { 
    data : agree_dot_Data_Types_dot_Position_dot_impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_DeliveryStatus__ = struct { 
    data : agree_dot_Data_Types_dot_DeliveryStatus;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_Data_Types_dot_RadioResponse_dot_impl = struct { 
    target_confirmed : bool;
    data_available : bool
};

type iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_ProbePlannerSignals_dot_impl__ = struct { 
    data : agree_dot_Data_Types_dot_ProbePlannerSignals_dot_impl;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_Data_Types_dot_DeliveryStatus = enum {
    agree_dot_Data_Types_dot_DeliveryStatus_dot_NOT_STARTED,
    agree_dot_Data_Types_dot_DeliveryStatus_dot_IN_PROGRESS,
    agree_dot_Data_Types_dot_DeliveryStatus_dot_COMPLETED,
    agree_dot_Data_Types_dot_DeliveryStatus_dot_FAILED
};
type iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_RadioResponse_dot_impl__ = struct { 
    data : agree_dot_Data_Types_dot_RadioResponse_dot_impl;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__ = struct { 
    data : int;
    direction : iml_dot_systems_dot_Direction
};

type agree_dot_Data_Types_dot_ProbePlannerSignals_dot_impl = struct { 
    is_target_clear : bool;
    is_target_location : bool
};


const WAITING_CONFIRMATION_THRESHOLD : int = 10;
const ITEM_VALUE_THRESHOLD : real = 100.0;

node InitiallyX(X : bool)
returns (Y : bool)
let
    Y = ((X -> true));
tel

node close_locations(p1 : agree_dot_Data_Types_dot_Position_dot_impl; p2 : agree_dot_Data_Types_dot_Position_dot_impl)
returns (are_close : bool)
let
    are_close = (p1 = p2);
tel

node HasHappened(X : bool)
returns (Y : bool)
let
    Y = (X or (false -> pre(X)));
tel

node FirstLocation(X : agree_dot_Data_Types_dot_Position_dot_impl)
returns (Y : agree_dot_Data_Types_dot_Position_dot_impl)
let
    Y = ((X -> pre(Y)));
tel

node imported agree_dot_DeliveryDrone_dot_Radio (radio_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; comm_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_RadioResponse_dot_impl__)
returns (radio_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_RadioResponse_dot_impl__; comm_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)
(*@contract
    guarantee \"REQ2\" not (radio_in.data) => not (radio_out.data.data_available);
    guarantee \"REQ1\" (comm_in.data.data_available = radio_out.data.data_available and comm_in.data.target_confirmed = radio_out.data.target_confirmed);
*) 

node imported agree_dot_DeliveryDrone_dot_FlightControl (response_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; move_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (state_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; motor_cmd : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)

node imported agree_dot_DeliveryDrone_dot_Camera (camera_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (camera_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__)

node imported agree_dot_DeliveryDrone_dot_Actuation (motor_cmd_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (response_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)

node imported agree_dot_DeliveryDrone_dot_GPS (satellite_pos_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)
returns (gps_pos_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)
(*@contract
    guarantee \"REQ1\" close_locations(gps_pos_out.data,satellite_pos_in.data);
*) 

node imported agree_dot_DeliveryDrone_dot_DeliveryItemMechanism (delivery_cmd_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__)
returns (delivery_status_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_DeliveryStatus__)
(*@contract
    guarantee \"REQ2\" (true -> not (delivery_cmd_in.data) => delivery_status_out.data = pre(delivery_status_out.data));
    guarantee \"REQ3\" delivery_cmd_in.data => delivery_status_out.data  <> agree_dot_Data_Types_dot_DeliveryStatus_dot_NOT_STARTED;
    guarantee \"REQ1\" InitiallyX(delivery_status_out.data = agree_dot_Data_Types_dot_DeliveryStatus_dot_NOT_STARTED);
*) 

node imported agree_dot_DeliveryDrone_dot_PositionEstimator (gps_pos_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; imu_pos_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; pos_act_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)
returns (est_pos_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)
(*@contract
    guarantee \"REQ1\" close_locations(est_pos_out.data,gps_pos_in.data);
*) 

node imported agree_dot_DeliveryDrone_dot_DeliveryPlanner (bus_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_InputBus_dot_impl__; camera_result : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Int__; nav_location : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; radio_response : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_RadioResponse_dot_impl__; delivery_status : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_DeliveryStatus__)
returns (p : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_ProbePlannerSignals_dot_impl__; camera_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; delivery_cmd_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; bus_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; radio_cmd : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; cmd_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; dest_location : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)
(*@contract
    var valid_recognition : bool = (HasHappened(recognizing_location and p.data.is_target_clear and p.data.is_target_location));
    var delivery_done : bool = (delivered_or_aborted and close_locations(nav_location.data,truck_position));
    var waiting_delivery_order : bool = (not (delivered_or_aborted) and close_locations(nav_location.data,truck_position) and not (bus_in.data.connected) and not (have_order));
    var waiting_proof_of_delivery : bool = ((false -> pre(delivery_status.data = agree_dot_Data_Types_dot_DeliveryStatus_dot_COMPLETED)) and close_locations(nav_location.data,order.target_position) and (false -> not (pre(proof_stored))));
    var connected_to_truck : bool = (not (delivered_or_aborted) and close_locations(nav_location.data,truck_position) and bus_in.data.connected);
    var have_order : bool = (HasHappened(bus_in.data.connected and bus_in.data.update_order));
    var returning_to_truck : bool = (delivered_or_aborted and not (close_locations(nav_location.data,truck_position)) and not (waiting_proof_of_delivery));
    var steps_in_confirming_mode : int = ((if confirming_location then (((1 -> pre(steps_in_confirming_mode) + 1))) else ((0 -> pre(steps_in_confirming_mode)))));
    var flying_to_target : bool = (not (delivered_or_aborted) and have_order and (close_locations(nav_location.data,truck_position) and not (bus_in.data.connected)) or (not (close_locations(nav_location.data,truck_position)) and not (close_locations(nav_location.data,order.target_position))));
    var truck_position : agree_dot_Data_Types_dot_Position_dot_impl = (FirstLocation(nav_location.data));
    var proof_stored : bool = (HasHappened(delivery_status.data = agree_dot_Data_Types_dot_DeliveryStatus_dot_COMPLETED and close_locations(nav_location.data,order.target_position)));
    var target_confirmed : bool = (HasHappened(confirming_location and radio_response.data.data_available and radio_response.data.target_confirmed));
    var recognizing_location : bool = (not (delivered_or_aborted) and have_order and close_locations(nav_location.data,order.target_position) and (false -> not (pre(valid_recognition))));
    var dropping_package : bool = (not (delivered_or_aborted) and close_locations(nav_location.data,order.target_position) and (false -> pre(valid_recognition)) and (false -> (order.item_value  > ITEM_VALUE_THRESHOLD) => pre(target_confirmed)));
    var delivery_aborted : bool = (HasHappened((recognizing_location and not (p.data.is_target_clear) or not (p.data.is_target_location)) or delivery_status.data = agree_dot_Data_Types_dot_DeliveryStatus_dot_FAILED or (radio_response.data.data_available and not (radio_response.data.target_confirmed)) or (steps_in_confirming_mode  > WAITING_CONFIRMATION_THRESHOLD)));
    var delivered_or_aborted : bool = ((false -> pre(delivery_status.data = agree_dot_Data_Types_dot_DeliveryStatus_dot_COMPLETED or delivery_aborted)));
    var confirming_location : bool = (not (delivered_or_aborted) and close_locations(nav_location.data,order.target_position) and (false -> pre(valid_recognition)) and (order.item_value  > ITEM_VALUE_THRESHOLD) and (false -> not (pre(target_confirmed))));
    var order : agree_dot_Data_Types_dot_DeliveryOrder_dot_impl = ((bus_in.data.order -> (if pre(have_order) then (pre(order)) else (bus_in.data.order))));
    guarantee \"REQ4\" delivery_cmd_out.data = dropping_package;
    guarantee \"REQ2\" cmd_out.data => not (bus_in.data.connected) and have_order;
    guarantee \"REQ3\" radio_cmd.data = confirming_location;
    assume \"REQ1\" (true -> not (pre(cmd_out.data)) => nav_location.data = pre(nav_location.data));
*) 

node imported agree_dot_DeliveryDrone_dot_Navigation (pos_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; state_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; cmd_in : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Bool__; nav_dest_location : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)
returns (nav_location_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; pos_act_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; move_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__)
(*@contract
    guarantee \"REQ2\" close_locations(nav_location_out.data,pos_in.data);
    guarantee \"REQ1\" (true -> not (pre(cmd_in.data)) => nav_location_out.data = pre(nav_location_out.data));
*) 

node imported agree_dot_DeliveryDrone_dot_IMU (launch_pos_in : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)
returns (imu_pos_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__)

node agree_dot_DeliveryDrone_dot_DeliveryDroneSystem_dot_Impl (comm1 : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_RadioResponse_dot_impl__; satellite_sig_pos : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; launch_pos : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; bus1 : iml_dot_systems_dot_InDataPort__agree_dot_Data_Types_dot_InputBus_dot_impl__)
returns (comm2 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; actuation_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; delivery_cmd_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; radio_cmd : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; radio_response : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_RadioResponse_dot_impl__; bus2 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; delivery_status : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_DeliveryStatus__)
(*@contract
    var target_confirmed : bool = (HasHappened(confirmation_requested and comm1.data.data_available and comm1.data.target_confirmed));
    var confirmation_requested : bool = (HasHappened(radio_cmd.data));
    var have_order : bool = (HasHappened(bus1.data.connected and bus1.data.update_order));
    var delivery_started : bool = (delivery_status.data  <> agree_dot_Data_Types_dot_DeliveryStatus_dot_NOT_STARTED);
    var order : agree_dot_Data_Types_dot_DeliveryOrder_dot_impl = ((bus1.data.order -> (if pre(have_order) then (pre(order)) else (bus1.data.order))));
    guarantee \"REQ2\" (true -> (delivery_started and (order.item_value  > ITEM_VALUE_THRESHOLD) => confirmation_requested));
    guarantee \"REQ1\" (true -> (delivery_cmd_out.data and (order.item_value  > ITEM_VALUE_THRESHOLD) => target_confirmed));
*) 
var imu_imu_pos_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; 
var navigation_nav_location_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; 
var navigation_pos_act_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; 
var navigation_move_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var deliveryPlanner_p : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_ProbePlannerSignals_dot_impl__; 
var deliveryPlanner_camera_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var deliveryPlanner_cmd_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var deliveryPlanner_dest_location : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; 
var positionEstimator_est_pos_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; 
var gps_gps_pos_out : iml_dot_systems_dot_OutDataPort__agree_dot_Data_Types_dot_Position_dot_impl__; 
var camera_camera_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Int__; 
var fc_state_out : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
var fc_motor_cmd : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Bool__; 
let
    (imu_imu_pos_out) = agree_dot_DeliveryDrone_dot_IMU ( launch_pos);
    (navigation_nav_location_out, navigation_pos_act_out, navigation_move_out) = agree_dot_DeliveryDrone_dot_Navigation ( positionEstimator_est_pos_out, fc_state_out, deliveryPlanner_cmd_out, deliveryPlanner_dest_location);
    (deliveryPlanner_p, deliveryPlanner_camera_out, delivery_cmd_out, bus2, radio_cmd, deliveryPlanner_cmd_out, deliveryPlanner_dest_location) = agree_dot_DeliveryDrone_dot_DeliveryPlanner ( bus1, camera_camera_out, navigation_nav_location_out, radio_response, delivery_status);
    (positionEstimator_est_pos_out) = agree_dot_DeliveryDrone_dot_PositionEstimator ( gps_gps_pos_out, imu_imu_pos_out, navigation_pos_act_out);
    (delivery_status) = agree_dot_DeliveryDrone_dot_DeliveryItemMechanism ( delivery_cmd_out);
    (gps_gps_pos_out) = agree_dot_DeliveryDrone_dot_GPS ( satellite_sig_pos);
    (actuation_out) = agree_dot_DeliveryDrone_dot_Actuation ( fc_motor_cmd);
    (camera_camera_out) = agree_dot_DeliveryDrone_dot_Camera ( deliveryPlanner_camera_out);
    (fc_state_out, fc_motor_cmd) = agree_dot_DeliveryDrone_dot_FlightControl ( actuation_out, navigation_move_out);
    (radio_response, comm2) = agree_dot_DeliveryDrone_dot_Radio ( radio_cmd, comm1);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
		
//		gen.displayMapLustre2Iml();

	}
	
	@Test
	def void test_SysMLgenExamples() {
		var Model m = parse(FileUtil.readFileContent("models/genExample.iml"),true) ;
		m.assertNoErrors;
		sys.process(m) ;
		sdf.systems = sys;
		sdf.process(m);
		gen.sdf = sdf;
		var NamedType nodetype = m.findSymbol("C") as NamedType;
		var LustreModel lus = new LustreModel() ;
		gen.generateLustreNode(lus,sdf.getNode(ImlCustomFactory.INST.createSimpleTypeReference(nodetype))) ;
		var output = gen.serialize(lus);
//		gen.displayMapLustre2Iml();
//		System.out.println(output);
		
		// following string is generated before refactoring generators
		var expectedOutput = "type iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Real__ = struct { 
    data : real;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Real__ = struct { 
    data : real;
    direction : iml_dot_systems_dot_Direction
};

type iml_dot_systems_dot_Direction = enum {
    iml_dot_systems_dot_Direction_dot_IN,
    iml_dot_systems_dot_Direction_dot_OUT,
    iml_dot_systems_dot_Direction_dot_INOUT
};


node imported Model_dot_B (y2 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Real__; x2 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Real__)
returns (z2 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Real__)
(*@contract
    guarantee \"A1REQ\" z2.data = x2.data + y2.data + 1.0;
*) 

node imported Model_dot_A (x1 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Real__)
returns (y1 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Real__)
(*@contract
    guarantee \"G1REQ\" y1.data  > x1.data;
*) 

node Model_dot_C (y3 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Real__; x3 : iml_dot_systems_dot_InDataPort__iml_dot_lang_dot_Real__)
returns (z3 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Real__)
(*@contract
    guarantee \"G2REQ\" z3.data  > x3.data;
*) 
var my_a1_y1 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Real__; 
var my_a2_y1 : iml_dot_systems_dot_OutDataPort__iml_dot_lang_dot_Real__; 
let
    (my_a1_y1) = Model_dot_A ( x3);
    (my_a2_y1) = Model_dot_A ( y3);
    (z3) = Model_dot_B ( my_a2_y1, my_a1_y1);
    --%MAIN; 
tel


"		
		assertTrue((expectedOutput.replaceAll("\\s+","")).equalsIgnoreCase(output.replaceAll("\\s+", "")))
	}	

}

