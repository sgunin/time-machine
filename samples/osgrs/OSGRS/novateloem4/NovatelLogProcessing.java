/*

	Copyright (C) 2007 SNAPlab

	http://www.gmat.unsw.edu.au/snap/

	This file is part of OSGRS.

	OSGRS is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	OSGRS is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with OSGRS; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 */

package OSGRS.novateloem4;

/** This class is of the oem4 package which is responsible for processing log data */

import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import OSGRS.Util.Bitmask;
import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;
import OSGRS.dataType.AlmanacSatelliteParameters;
import OSGRS.dataType.GPSAlmanac;
import OSGRS.dataType.IonUTCModel;
import OSGRS.dataType.RawAlmanac;
import OSGRS.dataType.RawIonUTCModel;
import OSGRS.dataType.ReferenceTime;
import OSGRS.dataType.SatelliteEphemeris;
import OSGRS.dataType.SatellitesInView;

/**
 * This class is of the oem4 package which is responsible for processing novatel Logs
 * @author Manosh Fernando, Nam Hoang
 *
 */
public class NovatelLogProcessing
{
	/** The logger used for debug for this OSGRS instance */
	private Logger logger;

	/** The full name of this class */
	private static final String thisClass = "NovatelLogProcessing";

	/**
	 * The value of PI from the GPS Spec.k
	 */
	public final static double PI = 3.1415926535898;

	/** The conversion for semi circles to radians. */
	public final static double SCALE_SEMI_CIRCLES_TO_RADIANS = PI;

	/** The conversion for radians to semi circles. */
	public final static double SCALE_RADIANS_TO_SEMI_CIRCLES = 1.0 / SCALE_SEMI_CIRCLES_TO_RADIANS;

	/**
	 * 
	 * @param RAWGPSSUBFRAMLog
	 * @param ephemerisGPSTow Time of Week from which 
	 * @return
	 */

	public static ReferenceTime processRAWGPSSUBFRAMELog(NovatelLog RAWGPSSUBFRAMELog)
	{
		short GPSWeek;
		int GPSTow;
		byte satID;
		short tlmMessage;
		byte antiSpoof;
		byte alert;
		byte tlmReserved;
		Date referenceTimeTimestamp;

		String RAWGPSSUBFRAMELogString = RAWGPSSUBFRAMELog.getLog();

		referenceTimeTimestamp = RAWGPSSUBFRAMELog.getLogTimestamp();

		//Logger logger = Logger.getLogger("ReferenceTime Processing@" + System.currentTimeMillis());

		//logger.setLevel(Level.FINE);

		//DebugLogger.recordLogToFile(logger);

		//RawGPSSUBFRAME processing

		String[] RAWGPSSUBFRAMEArray = RAWGPSSUBFRAMELogString.split(";");

		String RAWGPSSUBFRAMELogHeader = RAWGPSSUBFRAMEArray[0];

		//System.out.println(RAWGPSSUBFRAMELogHeader);

		String[] RAWGPSSUBFRAMELogHeaderParameters = RAWGPSSUBFRAMELogHeader.split(",");

		GPSWeek = (short)(Short.parseShort(RAWGPSSUBFRAMELogHeaderParameters[5]) % 1024);

		//System.out.println("GPSWeek: " + GPSWeek);

		//System.out.println("RAWGPSSUBFRAMELogHeader: " + RAWGPSSUBFRAMELogHeader);

		String RAWGPSSUBFRAMEParameters = RAWGPSSUBFRAMEArray[1];

		//System.out.println("RAWGPSSUBFRAMEParameters: " + RAWGPSSUBFRAMEParameters);

		String[] RAWGPSSUBFRAMEParametersArray = RAWGPSSUBFRAMEParameters.split(",");

		satID = (byte)(Integer.parseInt(RAWGPSSUBFRAMEParametersArray[1])-1);

		//System.out.println("SatID: " + satID);

		String RawSubframe = RAWGPSSUBFRAMEParametersArray[3];

		//System.out.println("RawSubframe: " + RawSubframe );

		byte sf1[] = new byte[31];

		String tempString;
		int tempValue;

		int idx = 1;

		for (int i = 0; i < 60; i = i + 2)
		{
			tempString = RawSubframe.substring(i, i + 2);  // each position in the array contains 2 chars (8bits)
			tempValue = Integer.parseInt(tempString, 16);
			sf1[idx] = (byte)tempValue;

			idx++;
		}

		Bitmask bm = new Bitmask(sf1);

		String sf1InBinary = bm.getBinary(0, 239);

		//TLM

		int tlmMessageHighBit 	= (sf1[2] & 0xFF) << 6;

		int tlmMessageLowBit 	= (sf1[3] & 0xFC) >> 2;

		tlmMessage = (short)(tlmMessageHighBit|tlmMessageLowBit);

		//ugly debuging starts here

//		if (tlmMessage < 0 || tlmMessage > ReferenceTime.MAXIMUM_VALUE_14_BITS || tlmMessage != GPSWeek)
//		{
//		//logger.warning("tlmMessage: " + this.tlmMessage + " is set outside of acceptable range of values");

//		Date date = new Date();

//		System.out.println("ReferenceTimeproblem@" + date.getTime() + " " + date.toString());

//		Logger logger = Logger.getLogger("ReferenceTime problem@" + System.currentTimeMillis());
//		logger.setLevel(DebugLogger.getOutputVerbosity());

//		if(logger.getLevel() != Level.OFF)
//		{
//		DebugLogger.recordLogToFile(logger);
//		}

//		logger.fine("tlmMessage: " + tlmMessage + " satID: " + satID);
//		logger.fine("tlm message in binary " + Integer.toBinaryString(tlmMessage));
//		logger.fine("tlmMessageHighBit: " + Integer.toBinaryString(tlmMessageHighBit));
//		logger.fine("tlmMessageLowBit: " + Integer.toBinaryString(tlmMessageLowBit));
//		logger.fine(RAWGPSSUBFRAMELogString);
//		logger.fine("Log Timestamp: " + referenceTimeTimestamp.toString());

//		//throw new IllegalArgumentException("tlmMessage: " + this.tlmMessage + " is set outside of acceptable range of values");
//		}

		//ugly debugiing ends here

		//System.out.println("tlmMessage: " + tlmMessage);

		//System.out.println("Reference Time.." + "tlm message verified " + (tlmMessage == (short)Integer.parseInt(sf1InBinary.substring(8,22),2)));

		tlmReserved = (byte)((sf1[3] & 0x03));

		GPSTow = Integer.parseInt(sf1InBinary.substring(24, 41), 2);

		//System.out.println("Reference Time.." + "tlm messaage reserve bits verified " + (tlmReserved == Integer.parseInt(sf1InBinary.substring(22, 24), 2)));

		alert = (byte)((sf1[6] & 0x40) >> 6);

		//System.out.println("Reference Time.." + "alert verified " + (alert == Integer.parseInt(sf1InBinary.substring(41, 42), 2)));

		antiSpoof = (byte)((sf1[6] & 0x20) >> 5);

		//System.out.println("Reference Time.." + "antiSpoof verified " + (antiSpoof == Integer.parseInt(sf1InBinary.substring(42, 43), 2)));

		return new ReferenceTime(GPSWeek, GPSTow, satID, tlmMessage, antiSpoof, alert, tlmReserved, referenceTimeTimestamp);
	}

	public static SatelliteEphemeris getSatelliteEphemerisFromNovatelLog(NovatelLog RAWEPHEMLog)
	{
		/*--------------------------------------------------------------- */
		short PRN;
		short satID;

		// Satellite Health and Clock information

		short GPSWeekNumberShort;
		byte ura_predictedUserRangeAccuracyCode;
		int satelliteHealth;
		short tgd_estimatedGroupDelayDifferentialSeconds;
		int iodc_issuedOfDataClock;
		int toc_clockDataReferenceTimeSecondsOfGPSWeek;
		short af2_satelliteClockDriftRateSecondsPerSquareSecond;
		int af1_satelliteClockDriftSecondsPerSecond;
		int af0_satelliteClockBiasSeconds;

		// Satellite Ephemeris data

		short IODE;
		int Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres;
		int DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond;
		long M0_meanAnomoloyAtReferenceTimeSemiCircles;
		int Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians;
		long e_eccentricity;
		int Cus_amplitudeSineHarmonicCorrectionLatitudeRadians;
		long sqrtA_squareRootOfSemiMajorAxisMetres;
		int toe_timeOfEphemerisSecondsOfGPSWeek;
		byte FIT_intervalFlag;
		short AODO_ageOfDataOffset;
		int Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians;
		long OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles;
		int Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians;
		long i0_inclinationAngleAtReferenceTimeSemiCircles;
		int Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres;
		long omega_argumentOfPerigreeSemiCircles;
		int OMEGADOT_rightAscensionSemiCirclesPerSecond;
		short IDOT_rateOfInclinationAngleSemiCirclesPerSecond;
		double nmctTime = 0;


//		byte codesOnL2Channel = (byte)0x00;
		int codesOnL2Channel;
		byte L2PDataFlag;
//		byte L2PDataFlag = (byte)0x00;

//		Subframe 1 reserved data

		int sf1ReservedData23Bit;

		int sf1ReservedData24ABit;

		int sf1ReservedData24BBit;

		int sf1ReservedData16Bit;

//		TLM Word

		short tlmMessage;

		byte tlmReservedBits;

//		byte tlmReservedBit2;

//		HOW Word

		int towCountMessage;

		byte alertFlag;

		byte antiSpoofFlag;

//		raw subframe data

		String rawSubframeDataString;

		Date logTimestamp = RAWEPHEMLog.getLogTimestamp();

		String novatelLog = RAWEPHEMLog.getLog();



		/*--------------------------------------------------------------- */

//		System.out.println("SatelliteEphemeris.getSatelliteEphemerisFromNovatelLog novatelLog: " + novatelLog);

		StringTokenizer st = new StringTokenizer(novatelLog, ";");

		String novatelLogHeader = st.nextToken(); //this is the log header

		String rawEphemerisLog = st.nextToken();

		st = new StringTokenizer(novatelLogHeader,",");

		st.nextToken(); //discard
		st.nextToken(); //discard

		int logSequence = Integer.parseInt(st.nextToken());

		assert (novatelLogHeader.contains("RAWEPHEMA"));

		st = new StringTokenizer(rawEphemerisLog, ",");

		PRN = Short.parseShort(st.nextToken());

		satID = (short)(PRN - 1);

		short ephemReferenceWeek = Short.parseShort(st.nextToken());

		double ephemRefTimeSeconds = Double.parseDouble(st.nextToken());

		String subframe1 = st.nextToken();
		String subframe2 = st.nextToken();

		String subframe3WithCRC = st.nextToken();

		st = new StringTokenizer(subframe3WithCRC, "*");

		String subframe3 = st.nextToken();


		Logger logger = Logger.getLogger("SatelliteEphemeris." + PRN + " " + System.currentTimeMillis());

		logger.setLevel(DebugLogger.getOutputVerbosity());

		if(logger.getLevel() != Level.OFF)
		{
			DebugLogger.recordLogToFile(logger);
		}


//		extract subframe hex strings

		subframe1 = subframe1.toUpperCase();
		subframe2 = subframe2.toUpperCase();
		subframe3 = subframe3.toUpperCase();

		logger.fine("subframe1 UC: " + subframe1);
		logger.fine("subframe2 UC: " + subframe2);
		logger.fine("subframe3 UC: " + subframe3);

		rawSubframeDataString = subframe1 + subframe2 + subframe3;

		logger.fine("subframe1-3: " + rawSubframeDataString);



//		System.out.println("subframe1 length: " + subframe1.length() + "subframe2 length :" + subframe2.length() + "subframe3 length: " + subframe3.length());

		String CRC = st.nextToken(); //this is the CRC error checking

		/**- The Novatel OEM4 subframe data comes in 60hex characters x3 each containing 30 bytes (240 bits) -- */

		assert (subframe1.length() == 60 && subframe2.length() == 60 && subframe3.length() == 60);

		byte sf1[] = new byte[31];
		byte sf2[] = new byte[31];
		byte sf3[] = new byte[31];

		String tempString;
		int tempValue;

		int idx = 1;


		for (int i = 0; i < 60; i = i + 2)
		{
			tempString = subframe1.substring(i, i + 2);  // each position in the array contains 2 chars (8bits)
			tempValue = Integer.parseInt(tempString, 16);
			sf1[idx] = (byte)tempValue;

//			System.out.println("SatelliteEphemeris.getSatelliteEphemerisFromNovatelLog idx: " + idx + " string: " + tempString + " tempValue: " + tempValue + " sf1[i]: " + sf1[idx]);

			tempString = subframe2.substring(i, i + 2);
			tempValue = Integer.parseInt(tempString, 16);
			sf2[idx] = (byte)tempValue;

			tempString = subframe3.substring(i, i + 2);
			tempValue = Integer.parseInt(tempString, 16);
			sf3[idx] = (byte)tempValue;

			idx++;
		}

		/*-- Debug only --*/
//		System.out.println("PRN:" + PRN +
//		" Ephem week:" + ephemReferenceWeek +
//		" Ephem ref time:" +ephemRefTimeSeconds +
//		" Subframe 1:" +subframe1 +
//		" Subframe 2:" +subframe2 +
//		" Subframe 3:" +subframe3 +
//		" CRC:" + CRC);

//		Subframe 1 first ------------------------------------------------------

//		The first byte is the TLM word preamble, this is hardcoded and will always be the first 8bits
		assert (sf1[1] == (byte)0x8B);

		Bitmask bm = new Bitmask(sf1);

		String sf1InBinary = bm.getBinary(0, 239); //get the binary representation of SUBFRAME1 -- makes life easier

		assert((byte)Integer.parseInt(sf1InBinary.substring(0,8),2) == sf1[1]); //this also works if you ever decide to use it

		assert(sf1InBinary.length() == 240); //240 bits per subframe ((6x10) parity bits removed)

//		NEED TO GET THE TLM AND HOW HERE

//		TLM

		int tlmMessageHighBit 	= (sf1[2] & 0xFF) << 6;

		int tlmMessageLowBit 	= (sf1[3] & 0xFC) >> 2;

		tlmMessage = (short)(tlmMessageHighBit|tlmMessageLowBit);

//		System.out.println("sat ephem tlmMessage: " + tlmMessage);

//		logger.fine("Satellite Ephemeris.." + "tlm message verified " + (tlmMessage == (short)Integer.parseInt(sf1InBinary.substring(8,22),2))); 

//		logger.fine("processed tlm " + tlmMessage + " bitmask tlmMessage " + Integer.parseInt(sf1InBinary.substring(8,22)));

//		logger.fine("processed tlmmessage: " + Integer.toBinaryString(tlmMessage) + " bitmask tlmmessage: " + Integer.parseInt(sf1InBinary.substring(8,22),2));

		tlmReservedBits = (byte)((sf1[3] & 0x03));

		logger.fine("Satellite ephemeris.." + "tlm messaage reserve bits verified " + (tlmReservedBits == Integer.parseInt(sf1InBinary.substring(22, 24), 2)));

//		HOW

		towCountMessage = Integer.parseInt(sf1InBinary.substring(24, 41), 2);

		alertFlag = (byte)((sf1[6] & 0x40) >> 6);

		logger.fine("Satellite Ephemeris.." + "alertFlag verified " + (alertFlag == Integer.parseInt(sf1InBinary.substring(41, 42), 2)));

		antiSpoofFlag = (byte)((sf1[6] & 0x20) >> 5);

		logger.fine("Satellite Ephemeris.." + "antiSpoofFlag verified " + (antiSpoofFlag == Integer.parseInt(sf1InBinary.substring(42, 43), 2)));

//		TODO>>>>>>>>>>>>>>

		/*####Week Number####*/

//		The week number is from bit 49 for 10 bits
//		This is because 10 bits of parity beforehand have been removed from the log by the novatel rr
		int weeknoHighBits = (sf1[7] & 0xFF) << 2;
		int weeknoLowBits =  (sf1[8] & 0xC0) >> 6;

		GPSWeekNumberShort = (short)(weeknoHighBits | weeknoLowBits);

		logger.fine("SatelliteEphemeris.." + "GPS Week verified " + (GPSWeekNumberShort == (short)Integer.parseInt(sf1InBinary.substring(48,58),2)));

		/*####Codes on L2####*/

		codesOnL2Channel= Integer.parseInt(sf1InBinary.substring(58,60)); //check this again


//		System.out.println(sf1InBinary.substring(58,60));

		/*####URA####*/

		ura_predictedUserRangeAccuracyCode = (byte)(sf1[8] & 0x0F);

//		System.out.println((sf1[8] & 0x0F) == Integer.parseInt(sf1InBinary.substring(60,64),2)); //works

		/*####Sat health####*/

		satelliteHealth = (sf1[9] & 0xFF) >> 2;

//		System.out.println((sf1[9] & 0xFF) >> 2 == Integer.parseInt(sf1InBinary.substring(64,70),2)); //works

		/*########*/

		int iodcHighBits = (sf1[9] & 0x03) << 8;
		int iodcLowBits  = (sf1[22] & 0xFF);

		iodc_issuedOfDataClock = (iodcHighBits | iodcLowBits);

//		System.out.println("PRN: " + PRN + " iodc: " +iodc_issuedOfDataClock);


		/*####L2PDataFlag####*/

		L2PDataFlag = (byte)(Integer.parseInt(sf1InBinary.substring(72,73)));

		/*####subframe1 reserved data###*/

		sf1ReservedData23Bit = Integer.parseInt(sf1InBinary.substring(73, 96),2);

		sf1ReservedData24ABit = Integer.parseInt(sf1InBinary.substring(96, 120),2);

		sf1ReservedData24BBit = Integer.parseInt(sf1InBinary.substring(120, 144),2);

		sf1ReservedData16Bit = Integer.parseInt(sf1InBinary.substring(144, 160),2);

		/*####TGD####*/

		tgd_estimatedGroupDelayDifferentialSeconds = (short)(sf1[21] & 0xFF);


//		tgd_estimatedGroupDelayDifferentialSeconds = twosComplementToInteger(twosComplementTGD, 8);
//		tgd_estimatedGroupDelayDifferentialSeconds *= Math.pow(2, -31);

		logger.fine("Satellite Ephemeris.." +"tgd verified: " + (tgd_estimatedGroupDelayDifferentialSeconds == Long.parseLong(sf1InBinary.substring(160,168),2)));

		/*####toc####*/

		int tocHighBits = (sf1[23] & 0xFF) << 8;
		int tocLowBits = (sf1[24] & 0xFF);

		toc_clockDataReferenceTimeSecondsOfGPSWeek = tocHighBits | tocLowBits;


		logger.fine("Satellite Ephemeris.." +"toc verified: " + (toc_clockDataReferenceTimeSecondsOfGPSWeek == Integer.parseInt(sf1InBinary.substring(176,192),2)));
//		toc_clockDataReferenceTimeSecondsOfGPSWeek = rawToc * Math.pow(2, 4);

		/*####AFx####*/

		af2_satelliteClockDriftRateSecondsPerSquareSecond = (short)(sf1[25] & 0xFF);

		logger.fine("Satellite Ephemeris.." + "af2 verified: "+ (af2_satelliteClockDriftRateSecondsPerSquareSecond == Integer.parseInt(sf1InBinary.substring(192,200),2)));

//		af2_satelliteClockDriftRateSecondsPerSquareSecond = twosComplementToInteger(twosComplementAf2, 8);
//		af2_satelliteClockDriftRateSecondsPerSquareSecond *= Math.pow(2, -55);

		int af1HighBits = (sf1[26] & 0xFF) << 8;
		int af1LowBits =   sf1[27] & 0xFF;

		af1_satelliteClockDriftSecondsPerSecond = af1HighBits | af1LowBits;

		logger.fine("Satellite Ephemeris.." + "af1 verified: "+ (af1_satelliteClockDriftSecondsPerSecond == Integer.parseInt(sf1InBinary.substring(200,216),2)));

//		af1_satelliteClockDriftSecondsPerSecond = twosComplementToInteger(twosComplementAf1, 16);
//		af1_satelliteClockDriftSecondsPerSecond *= Math.pow(2, -43);

		int af0VeryHighBits = (sf1[28] & 0xFF) << 14;
		int af0HighBits = (sf1[29] & 0xFF) << 6;
		int af0LowBits = (sf1[30] & 0xFF) >> 2;

		af0_satelliteClockBiasSeconds = (af0VeryHighBits | af0HighBits | af0LowBits);

		logger.fine("Satellite Ephemeris.." + "af0 verified: "+ (af0_satelliteClockBiasSeconds == Integer.parseInt(sf1InBinary.substring(216,238),2)));

//		af0_satelliteClockBiasSeconds = twosComplementToInteger(twosComplementAf0, 22);
//		af0_satelliteClockBiasSeconds *= Math.pow(2, -31);

//		Subframe 2 -------------------------------------------------------------

		bm = new Bitmask(sf2);

		String sf2InBinary = bm.getBinary(0, 239);

//		The first 2 bytes are the TLM word preamble
		assert (sf2[1] == (byte)0x8B);

		/*####IODE####*/

		IODE = (short)(sf2[7] & 0xFF);

		logger.fine("Satellite Ephemeris.." + " IODE Verified: " + (IODE == Short.parseShort(sf2InBinary.substring(48,56),2)));

		assert (IODE >= 0);

		/*####CRS####*/

		int crsHighBits = (sf2[8] & 0xFF) << 8;
		int crsLowBits =  (sf2[9] & 0xFF);

		Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres = crsHighBits | crsLowBits;

		logger.fine("Satellite Ephemeris.." + "Crs verified: "+ (Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres == Long.parseLong(sf2InBinary.substring(56,72),2)));
//		Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres = twosComplementToInteger(twosComplementCrs, 16);
//		Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres *= Math.pow(2, -5);

		/*####Delta N####*/

		int deltanHighBits = (sf2[10] & 0xFF) << 8;
		int deltanLowBits =  (sf2[11] & 0xFF);

		DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond = deltanHighBits | deltanLowBits;

		logger.fine("Satellite Ephemeris.." + "Delta N verified: "+ (DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond == Long.parseLong(sf2InBinary.substring(73,88),2)));
//		DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond = twosComplementToInteger(twosComplementDeltan, 16);
//		DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond *= Math.pow(2, -43);

		/*####M0####*/

		long m01 = ((long)(sf2[12] & 0xFF)) << 24;
		int m02 = (sf2[13] & 0xFF) << 16;
		int m03 = (sf2[14] & 0xFF) << 8;
		int m04 = (sf2[15] & 0xFF);

		M0_meanAnomoloyAtReferenceTimeSemiCircles = m01 | m02 | m03 | m04;

//		M0_meanAnomoloyAtReferenceTimeSemiCircles = twosComplementToInteger(twosComplementM0, 32);
//		M0_meanAnomoloyAtReferenceTimeSemiCircles *= Math.pow(2, -31);

		long m0CheckTwos = Long.parseLong(sf2InBinary.substring(88,120),2);
//		double m0Check_semicricles = twosComplementToInteger(m0CheckTwos, 32);
//		m0Check_semicricles *= Math.pow(2, -31);

		logger.fine("Satellite Ephemeris.." + "M0 verified: "+ (M0_meanAnomoloyAtReferenceTimeSemiCircles == m0CheckTwos));

		/*####CUC####*/

		int cucHighBits = (sf2[16] & 0xFF) << 8;
		int cucLowBits =  (sf2[17] & 0xFF);
		Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians = cucHighBits | cucLowBits;

		logger.fine("Satellite Ephemeris.." + "Cuc verified: "+ (Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians == Long.parseLong(sf2InBinary.substring(120,136),2)));

//		Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians = twosComplementToInteger(twosComplementCuc, 16);
//		Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians *= Math.pow(2, -29);

		/*####Eccentricity####*/

		long e1 = ((long)(sf2[18] & 0xFF)) << 24;
		int e2 = (sf2[19] & 0xFF) << 16;
		int e3 = (sf2[20] & 0xFF) << 8;
		int e4 = (sf2[21] & 0xFF);

		e_eccentricity = e1 | e2 | e3 | e4;

		logger.fine("Satellite Ephemeris.." + "e_eccentricity verified: "+ (e_eccentricity == Long.parseLong(sf2InBinary.substring(136,168),2)));
//		e_eccentricity *= Math.pow(2, -33);

		/*####CUS####*/

		int cusHighBits = (sf2[22] & 0xFF) << 8;
		int cusLowBits =  (sf2[23] & 0xFF);

		Cus_amplitudeSineHarmonicCorrectionLatitudeRadians = cusHighBits | cusLowBits;


		logger.fine("Satellite Ephemeris.." + "Cus verified: "+ (Cus_amplitudeSineHarmonicCorrectionLatitudeRadians == Long.parseLong(sf2InBinary.substring(168,184),2)));


		long CUS_TEST = GNSSUtil.twosComplementToInteger(Cus_amplitudeSineHarmonicCorrectionLatitudeRadians, 16);

		logger.finest("RAW CUS: " + Integer.toBinaryString(Cus_amplitudeSineHarmonicCorrectionLatitudeRadians) + " CUS : " + Long.toBinaryString(GNSSUtil.putIntoTwosComplementForm(CUS_TEST,16)));

		/*####SQRT A####*/

		long sqrtA1 = ((long)(sf2[24] & 0xFF)) << 24;
		int sqrtA2 = (sf2[25] & 0xFF) << 16;
		int sqrtA3 = (sf2[26] & 0xFF) << 8;
		int sqrtA4 = (sf2[27] & 0xFF);

		sqrtA_squareRootOfSemiMajorAxisMetres = sqrtA1 | sqrtA2 | sqrtA3 | sqrtA4;

		logger.fine("Satellite Ephemeris.." + "sqrtA verified: "+ (sqrtA_squareRootOfSemiMajorAxisMetres == Long.parseLong(sf2InBinary.substring(184,216),2)));
//		sqrtA_squareRootOfSemiMajorAxisMetres *= Math.pow(2, -19);

		/*####TOE####*/

		int toeHighBits = (sf2[28] & 0xFF) << 8;
		int toeLowBits =  (sf2[29] & 0xFF);

		toe_timeOfEphemerisSecondsOfGPSWeek = toeHighBits | toeLowBits;

		logger.fine("Satellite Ephemeris.." + "toe verified: "+ (toe_timeOfEphemerisSecondsOfGPSWeek == Long.parseLong(sf2InBinary.substring(216,232),2)));
//		toe_timeOfEphemerisSecondsOfGPSWeek *= Math.pow(2, 4);

		/*####FIT Interval Flag####*/

		FIT_intervalFlag = (byte)(sf2[30] & 0x80);

		logger.fine("Satellite Ephemeris.."+ "FIT interval flag verified: " + (FIT_intervalFlag == Integer.parseInt(sf2InBinary.substring(232,233),2)));

		/*####AODO####*/

		AODO_ageOfDataOffset = (short)((sf2[30] & 0x7C) >> 2);
		AODO_ageOfDataOffset *= 900;

		assert (AODO_ageOfDataOffset > 0 && AODO_ageOfDataOffset <= 27900);

		logger.fine("Satellite Ephemeris.." + " AODO verified " + ((AODO_ageOfDataOffset/900) == Short.parseShort(sf2InBinary.substring(233,238),2)));

//		Subframe 3 -------------------------------------------------------------


//		The first byte is the TLM word preamble
		assert (sf3[1] == (byte)0x8B);

		bm = new Bitmask(sf3);
		String sf3InBinary = bm.getBinary(0, 239);

//		System.out.println((byte)Integer.parseInt(sf3InBinary.substring(0,8),2) == (byte)0x8b); //works

		/*####CIC####*/

		int cicHighBits = (sf3[7] & 0xFF) << 8;
		int cicLowBits =   sf3[8] & 0xFF;

		Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians = cicHighBits | cicLowBits;

		logger.fine("Satellite Ephemeris.." + "cic verified: "+ (Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians == Long.parseLong(sf3InBinary.substring(48,64),2)));

//		Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians = twosComplementToInteger(twosComplementCic, 16);
//		Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians *= Math.pow(2, -29);

		/*####OMEGA0####*/

		long omega01 = ((long)(sf3[9] & 0xFF)) << 24;
		int omega02 = (sf3[10] & 0xFF) << 16;
		int omega03 = (sf3[11] & 0xFF) << 8;
		int omega04 = (sf3[12] & 0xFF);

		OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles = omega01 | omega02 | omega03 | omega04;

		logger.fine("Satellite Ephemeris.." + "OMEGA0 verified: "+ (OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles == Long.parseLong(sf3InBinary.substring(64,96),2)));
//		OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles = twosComplementToInteger(twosComplementOmega0, 32);
//		OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles *= Math.pow(2, -31);

		/*####CIS####*/

		int cisHighBits = (sf3[13] & 0xFF) << 8;
		int cisLowBits =   sf3[14] & 0xFF;

		Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians = cisHighBits | cisLowBits;

		logger.fine("Satellite Ephemeris.." + "CIS verified: "+ (Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians == Long.parseLong(sf3InBinary.substring(96,112),2)));

//		Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians = twosComplementToInteger(twosComplementCis, 16);
//		Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians *= Math.pow(2, -29);

		/*####IO####*/

		long i01 = ((long)(sf3[15] & 0xFF)) << 24;
		int i02 = (sf3[16] & 0xFF) << 16;
		int i03 = (sf3[17] & 0xFF) << 8;
		int i04 = (sf3[18] & 0xFF);

		i0_inclinationAngleAtReferenceTimeSemiCircles = i01 | i02 | i03 | i04;

		logger.fine("Satellite Ephemeris.." + "i0 verified: "+ (i0_inclinationAngleAtReferenceTimeSemiCircles == Long.parseLong(sf3InBinary.substring(112,144),2)));
//		i0_inclinationAngleAtReferenceTimeSemiCircles = twosComplementToInteger(twosComplementi0, 32);
//		i0_inclinationAngleAtReferenceTimeSemiCircles *= Math.pow(2, -31);

		/*####CRC####*/

		int crcHighBits = (sf3[19] & 0xFF) << 8;
		int crcLowBits =   sf3[20] & 0xFF;

		Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres = crcHighBits | crcLowBits;

		logger.fine("Satellite Ephemeris.." + "Crc verified: "+ (Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres == Long.parseLong(sf3InBinary.substring(144,160),2)));

//		Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres = twosComplementToInteger(twosComplementCrc, 16);
//		Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres *= Math.pow(2, -5);

		/*####Omega####*/

		long omega1 = ((long)(sf3[21] & 0xFF)) << 24;
		int omega2 = (sf3[22] & 0xFF) << 16;
		int omega3 = (sf3[23] & 0xFF) << 8;
		int omega4 = (sf3[24] & 0xFF);

		omega_argumentOfPerigreeSemiCircles = omega1 | omega2 | omega3 | omega4;

		logger.fine("Satellite Ephemeris.." + "omega_argumentofsemicircles verified: "+ (omega_argumentOfPerigreeSemiCircles == Long.parseLong(sf3InBinary.substring(160,192),2)));
//		double scaledOmega = twosComplementToInteger(omega_argumentOfPerigreeSemiCircles, 32);
//		scaledOmega *= Math.pow(2, -31);

//		logger.fine("Scaled Omega: " + scaledOmega);

//		long omegatest = twosComplementToInteger(omega_argumentOfPerigreeSemiCircles,32);

//		logger.finest("PRN: " + PRN + " RAW omega: " + ( omega_argumentOfPerigreeSemiCircles) + " omega : " + putIntoTwosComplementForm(omegatest,32));

		/*####OMEGA Dot####*/

		int omegaDot1 = (sf3[25] & 0xFF) << 16;
		int omegaDot2 = (sf3[26] & 0xFF) << 8;
		int omegaDot3 = (sf3[27] & 0xFF);

		OMEGADOT_rightAscensionSemiCirclesPerSecond = omegaDot1 | omegaDot2 | omegaDot3;

		logger.fine("Satellite Ephemeris.." + "omega_dot verified: "+ (OMEGADOT_rightAscensionSemiCirclesPerSecond == Long.parseLong(sf3InBinary.substring(192,216),2)));
//		OMEGADOT_rightAscensionSemiCirclesPerSecond = twosComplementToInteger(twosComplementOmegaDot, 24);
//		OMEGADOT_rightAscensionSemiCirclesPerSecond *= Math.pow(2, -43);

		/*####I DOT####*/

		int idotHighBits = (sf3[29] & 0xFF) << 6;
		int idotLowBits =  (sf3[30] & 0xFF) >> 2; //fixed

		IDOT_rateOfInclinationAngleSemiCirclesPerSecond = (short)(idotHighBits | idotLowBits);

		logger.fine("Satellite Ephemeris.." + "IDOT verified: "+ (IDOT_rateOfInclinationAngleSemiCirclesPerSecond == Long.parseLong(sf3InBinary.substring(224,238),2)));

//		IDOT_rateOfInclinationAngleSemiCirclesPerSecond = twosComplementToInteger(twosComplementIdot, 14);
//		IDOT_rateOfInclinationAngleSemiCirclesPerSecond *= Math.pow(2, -43);

		/*#### NMCT Validity Time####*/

		double toe = toe_timeOfEphemerisSecondsOfGPSWeek;
		double aodo = AODO_ageOfDataOffset;

		double offset = toe % 7200;  //2hrs

//		Offset will normall be 0 because the toe is on the 2 hour boundary

//		System.out.println("TriangulatorRealTimeTestTool.calculateNMCTTime offset: " + offset);

		if (offset == 0)
		{
			nmctTime = toe - aodo;
		}

		if (offset > 0)

		{
			nmctTime = toe - offset + 7200 - aodo;
		}






//		process raw subframe data


//		/############################################### END #######################################




//		make a new object passing it the params
		SatelliteEphemeris thisSatellite = new SatelliteEphemeris
		(
				PRN,
				satID,

				// Satellite Health and Clock information

				GPSWeekNumberShort,
				ura_predictedUserRangeAccuracyCode,
				satelliteHealth,
				tgd_estimatedGroupDelayDifferentialSeconds,
				iodc_issuedOfDataClock,
				toc_clockDataReferenceTimeSecondsOfGPSWeek,
				af2_satelliteClockDriftRateSecondsPerSquareSecond,
				af1_satelliteClockDriftSecondsPerSecond,
				af0_satelliteClockBiasSeconds,

				// Satellite Ephemeris data

				IODE,
				Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres,
				DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond,
				M0_meanAnomoloyAtReferenceTimeSemiCircles,
				Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians,
				e_eccentricity,
				Cus_amplitudeSineHarmonicCorrectionLatitudeRadians,
				sqrtA_squareRootOfSemiMajorAxisMetres,
				toe_timeOfEphemerisSecondsOfGPSWeek,
				FIT_intervalFlag,
				AODO_ageOfDataOffset,
				Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians,
				OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles,
				Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians,
				i0_inclinationAngleAtReferenceTimeSemiCircles,
				Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres,
				omega_argumentOfPerigreeSemiCircles,
				OMEGADOT_rightAscensionSemiCirclesPerSecond,
				IDOT_rateOfInclinationAngleSemiCirclesPerSecond,

				codesOnL2Channel,
				L2PDataFlag,
//				logSequence,
				sf1ReservedData23Bit,
				sf1ReservedData24ABit,
				sf1ReservedData24BBit,
				sf1ReservedData16Bit,
				nmctTime,

				tlmMessage,
				tlmReservedBits,
				towCountMessage,
				alertFlag,
				antiSpoofFlag,
				logTimestamp,
				rawSubframeDataString
		);

		return thisSatellite;
	}

	/**
	 * This method grabs Ionospheric and UTC parameters from a Novatel OEM4 source.
	 * @param header The Novatel OEM header
	 * @param UTCDataString The string containing the UTC data
	 * @return The IonUTCModel object
	 */
	public static IonUTCModel getIonosphereAndUTCModelFromNovatelLog(NovatelLog IONUTCLog)
	{

		Logger logger = Logger.getLogger("getIonosphereAndUTCModelFromNovatelLog " + System.currentTimeMillis());

		logger.setLevel(DebugLogger.getOutputVerbosity());

		DebugLogger.recordLogToFile(logger);


		String novatelLog = IONUTCLog.getLog();

		Date logTimestamp = IONUTCLog.getLogTimestamp();

		String[] novatelLogArray = novatelLog.split(";");
		String novatelLogHeader = novatelLogArray[0];
		//System.out.println(novatelLogHeader);
		String decodedIonUTCParameters = novatelLogArray[1];

		//System.out.println(novatelLog);
		//System.out.println(novatelLogArray[0] + " " + novatelLogArray[1]);

		String[] decodedIonUTCParametersArray = decodedIonUTCParameters.split(",");

		//### Ionospheric parameters first
		short alpha0_constant_term =      (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(
				decodedIonUTCParametersArray[0])/(Math.pow(2,-30))),8);
		logger.fine("alpha0_constant_term: " + alpha0_constant_term);

		short alpha1_first_order_term = (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(decodedIonUTCParametersArray[1])/
				(Math.pow(2,-27))),8);
		logger.fine("alpha1_first_order_term: " + alpha1_first_order_term);

		short alpha2_second_order_term = (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(decodedIonUTCParametersArray[2])/
				(Math.pow(2,-24))),8);
		logger.fine("alpha2_second_order_term: " + alpha2_second_order_term);

		short alpha3_third_order_term = (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(decodedIonUTCParametersArray[3])/
				(Math.pow(2,-24))),8);
		logger.fine("alpha3_third_order_term: " + alpha3_third_order_term);

		short beta0_constant_term =       (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(decodedIonUTCParametersArray[4])/
				(Math.pow(2,11))),8);
		logger.fine("beta0_constant_term: " + beta0_constant_term);

		short beta1_first_order_term =    (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(decodedIonUTCParametersArray[5])/
				(Math.pow(2,14))),8);
		logger.fine("beta1_first_order_term: " + beta1_first_order_term);

		short beta2_second_order_term =   (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(decodedIonUTCParametersArray[6])/
				(Math.pow(2,16))),8);
		logger.fine("beta2_second_order_term: " + beta2_second_order_term);

		short beta3_third_order_term =    (short)SatelliteEphemeris.putIntoTwosComplementForm
		(Math.round(Double.parseDouble(decodedIonUTCParametersArray[7])/
				(Math.pow(2,16))),8);

		logger.fine("beta3_third_order_term: " + beta3_third_order_term);

		//### UTC parameters

		short WNt = (short)(Short.parseShort(decodedIonUTCParametersArray[8])%256);
		logger.fine("WNt: " + WNt);

		short tot = (short)Math.round((Double.parseDouble(decodedIonUTCParametersArray[9])/Math.pow(2,12)));
		logger.fine("tot: " + tot);

		long A0_second_alpha_constant_term = SatelliteEphemeris.putIntoTwosComplementForm(Math.round(Double.parseDouble(decodedIonUTCParametersArray[10])/Math.pow(2, -30)),32);
		logger.fine("A0_second_alpha_constant_term: " + A0_second_alpha_constant_term);

		//System.out.println("########: " + A0_second_alpha_constant_term);

		int A1_second_alpha_first_order_term = (int)SatelliteEphemeris.putIntoTwosComplementForm(Math.round(Double.parseDouble(decodedIonUTCParametersArray[11])/Math.pow(2, -50)),24);
		logger.fine("A1_second_alpha_first_order_term: " + A1_second_alpha_first_order_term);

		short WNlsf = (short)(Short.parseShort(decodedIonUTCParametersArray[12])%256);
		logger.fine("WNlsf: " + WNlsf);

		//System.out.println("decoded DN: " + decodedIonUTCParametersArray[13]);

		//short DN = (short)(Integer.reverse((Integer.parseInt(decodedIonUTCParametersArray[13]))) >>> 24);//put into right justified format
		short DN = Short.parseShort(decodedIonUTCParametersArray[13]);
		logger.fine("DN: " + DN);

		//System.out.println("decodedIonUTCParametersArray[13]: " + decodedIonUTCParametersArray[13]);

		//System.out.println(Integer.toBinaryString(DN));
		//System.out.println("DN MSB right justified:" + DN + " MSB LEFT: " + (Integer.reverse(DN) >>> 24));

		short dtLS = (short)SatelliteEphemeris.putIntoTwosComplementForm(Short.parseShort(decodedIonUTCParametersArray[14]),8);
		logger.fine("dtLS: " + dtLS);

		short dtLSF = (short)SatelliteEphemeris.putIntoTwosComplementForm(Short.parseShort(decodedIonUTCParametersArray[15]),8);
		logger.fine("dtLSF: " + dtLSF);

		return new IonUTCModel(
				//Ionospheric PARAMS
				alpha0_constant_term,
				alpha1_first_order_term,
				alpha2_second_order_term,
				alpha3_third_order_term,
				beta0_constant_term,
				beta1_first_order_term,
				beta2_second_order_term,
				beta3_third_order_term,

				//UTC PARAMS
				WNt,
				tot,
				A0_second_alpha_constant_term,
				A1_second_alpha_first_order_term,
				WNlsf,
				DN,
				dtLS,
				dtLSF,
				logTimestamp);
	}


	public static SatellitesInView processGPGSVLogs (NovatelLog[] GPGSVLogs)
	{
		short numberOfSatsInView;

		short[] satsInView;

		int n = 0; //index array for satsInView

		Date SIVTimestamp;

		SIVTimestamp = GPGSVLogs[0].getLogTimestamp();

		//System.out.println(SIVTimestamp);

		String GPGSVLogString = GPGSVLogs[0].getLog();

		String[] SplitGPGSVLogString = GPGSVLogString.split(",");

		int totalNumberOfMessages = Integer.parseInt(SplitGPGSVLogString[1]);

		//System.out.println("totalNumberOfMessages: " + totalNumberOfMessages);

		//int messageNumber = Integer.parseInt(SplitGPGSVLogString[2]);

		numberOfSatsInView = Short.parseShort(SplitGPGSVLogString[3]);

		satsInView = new short[numberOfSatsInView];

		//System.out.println("numberOfSatsInView: " + numberOfSatsInView);

		for (int i = 0; i < totalNumberOfMessages; i++)
		{
			String GPGSVLog = GPGSVLogs[i].getLog();

			//System.out.println("Log: " + GPGSVLog);

			String[] splitGPGSVLog = GPGSVLog.split(",");

			for (int x = 4; x < splitGPGSVLog.length; x += 4)
			{
				satsInView[n] = (short)(Integer.parseInt(splitGPGSVLog[x]));
				n++;
			}
		}

		for(int i = 0; i < satsInView.length; i++ )
		{
			// System.out.println(satsInView[i]);
		}

		return new SatellitesInView(numberOfSatsInView, satsInView, SIVTimestamp);
	}

	public static RawAlmanac[] getRawAlmanacFromRAWALMA (NovatelLog RAWALMALog)
	{
		RawAlmanac[] rawAlmanacArray = new RawAlmanac [64];

		Date date = new Date();

		Logger logger = Logger.getLogger("getRawAlmanacFromRAWALMA" + System.currentTimeMillis());

		logger.setLevel(DebugLogger.getOutputVerbosity());

		if(logger.getLevel() != Level.OFF)
		{
			DebugLogger.recordLogToFile(logger);
		}

		logger.entering("NovatelLogProcessing", "getRawAlmanacFromRAWALMA");

		String RAWALMALogString = RAWALMALog.getLog();

		RAWALMALogString = RAWALMALogString.toUpperCase();

		logger.fine("Log: " + RAWALMALogString);

		String[] splitRAWALMALogString = RAWALMALogString.split(";");

		String RAWALMALogHeader = splitRAWALMALogString[0];

		logger.fine("RAWALMALogHeader: " + RAWALMALogHeader);

		String RAWALMALogParameters = splitRAWALMALogString[1];

		logger.fine("RAWALMALogParameters: " + RAWALMALogParameters);

		String[] splitRAWALMALogParameters = RAWALMALogParameters.split(",");

		for (int i = 3; i < splitRAWALMALogParameters.length; i++)
		{
			byte satID = (byte)(Integer.parseInt(splitRAWALMALogParameters[i]));

			logger.fine("i: " + i + " satID: " + satID);

			i++;

			if (satID >= 0 && satID < splitRAWALMALogParameters.length)
			{
				String rawAlmanacString = splitRAWALMALogParameters[i];

				if (rawAlmanacString.length()>60)
				{
					logger.fine("rawAlmanacString contains \"*\" " );

					logger.fine("rawAlmanacString: " + rawAlmanacString);

					//String[] splitRawAlmanacString = rawAlmanacString.split("*"); for some reason split didn't work

					rawAlmanacString = rawAlmanacString.substring(0, 60);

					logger.fine("rawAlmanacString: " + rawAlmanacString);
				}

				logger.fine("rawAlmanacString: " + rawAlmanacString);

				rawAlmanacArray[satID] = new RawAlmanac(satID, rawAlmanacString, date);
			}  else
			{
				logger.fine("out of bounds satID. satID: " + satID);
			}
		}

		logger.exiting("NovatelLogProcessing", "getRawAlmanacFromRAWALMA");

		return rawAlmanacArray;
	}

	public static RawIonUTCModel getRawIonUTCModelFromRAWALMALog (NovatelLog RAWALMALog)
	{

		Logger logger = Logger.getLogger("getRawIonUTCModelFromRAWALMALog@" + System.currentTimeMillis());

		logger.setLevel(DebugLogger.getOutputVerbosity());

		if(logger.getLevel() != Level.OFF)
		{
			DebugLogger.recordLogToFile(logger);
		}

		logger.entering(thisClass, "getRawIonUTCModelFromRAWALMALog");

		String rawHexString;

		String rawIonosphereModelString;

		String rawUTCModelString;

		Date date = new Date();

		RawAlmanac[] rawAlmanacArray = getRawAlmanacFromRAWALMA(RAWALMALog);

		//RawAlmanac.printRawAlmanacToLog(rawAlmanacArray);

		//subframe 4 page 18 is where ionUTC information lies, this page has 'SVID' of 56

		if ((rawAlmanacArray[56] != null) && (rawAlmanacArray[56].getSVID() == 56))
		{
			rawHexString = rawAlmanacArray[56].getRawAlmanacString();
			logger.fine("rawHexString: " + rawHexString);
		} else
		{
			rawHexString = "8B062854E432780C01FFFF2C00FDFF00001900000005638A0E4B070EAAAB";
			logger.fine("correct page not found");
		}

		rawIonosphereModelString = rawHexString.substring(14,30);

		logger.fine("rawIonosphereModelString: " + rawIonosphereModelString);

		rawUTCModelString = rawHexString.substring(30, 56);

		logger.fine("rawUTCModelString: " + rawUTCModelString);

		logger.exiting(thisClass, "getRawIonUTCModelFromRAWALMALog");
		return new RawIonUTCModel(rawIonosphereModelString, rawUTCModelString, date);
	}

	public static void printHexSubframe(NovatelLog RAWGPSSUBFRAMELog)
	{
		String RAWGPSSUBFRAMELogString = RAWGPSSUBFRAMELog.getLog();
		//System.out.println(RAWGPSSUBFRAMELogString);

		int satID = RAWGPSSUBFRAMELog.getSatID();
		double secondsIntoGPSWeek;
		String rawHexSubframe;

		String[] splitRAWGPSSUBFRAMELogString = RAWGPSSUBFRAMELogString.split(";");

		String RAWGPSSUBFRAMELogHeader = splitRAWGPSSUBFRAMELogString[0];
		String[] splitRAWGPSSUBFRAMELogHeader = RAWGPSSUBFRAMELogHeader.split(",");

		String RAWGPSSUBFRAMELogParameters = splitRAWGPSSUBFRAMELogString[1];
		String[] splitRAWGPSSUBFRAMELogParameters = RAWGPSSUBFRAMELogParameters.split(",");

		//System.out.println("RAWGPSSUBFRAMELogHeader: " + RAWGPSSUBFRAMELogHeader);
		//System.out.println("RAWGPSSUBFRAMELogParameters: " + RAWGPSSUBFRAMELogParameters);

		secondsIntoGPSWeek = Double.parseDouble(splitRAWGPSSUBFRAMELogHeader[6]);
		//System.out.println("secondsIntoGPSWeek: " + secondsIntoGPSWeek);

		rawHexSubframe = splitRAWGPSSUBFRAMELogParameters[3];
		//System.out.println("rawHexSubframe: " + rawHexSubframe);

		String first15HexCharacters = rawHexSubframe.substring(0, 15);
		//System.out.println("first15HexCharacters: " + first15HexCharacters);

		long first60Bits = Long.parseLong(first15HexCharacters, 16);
		//System.out.println("first60Bits: " + first60Bits);

		String first60BitsBinaryString = Long.toBinaryString(first60Bits);
		//System.out.println("first60BitsBinaryString: " + first60BitsBinaryString);

		System.out.println("OEM4 - " + "satID: " + satID + " seconds: " + secondsIntoGPSWeek + " first60Bits: " 
				+ first60BitsBinaryString);

	}

	public static GPSAlmanac getGPSAlmanacFromALMANACLog(NovatelLog ALMANACLog)
	{
		Date almanacTimestamp;
		byte numSatsTotal = 0;
		short weekNumber;

		AlmanacSatelliteParameters[] almanacSatelliteParametersArray;
		int x = 0; //index variable for above array

		almanacTimestamp = ALMANACLog.getLogTimestamp();

		String[] splitAlmanacLog = ALMANACLog.getLog().split(";");

		String almanacLogHeader = splitAlmanacLog[0];
		//System.out.println("almanacLogHeader: " + almanacLogHeader);

		String almanacLogParameters = splitAlmanacLog[1];
		//System.out.println("almanacLogParameters: " + almanacLogParameters);

		String[] splitAlmanacLogParameters = almanacLogParameters.split(",");

		numSatsTotal = (byte)Integer.parseInt(splitAlmanacLogParameters[0]);
		//System.out.println("numSatsTotal: " + numSatsTotal);

		weekNumber = (short)(Integer.parseInt(splitAlmanacLogParameters[2])%256);
		//System.out.println("weekNumber: " + weekNumber);

		almanacSatelliteParametersArray = new AlmanacSatelliteParameters[numSatsTotal];

		for(int i = 1; i < splitAlmanacLogParameters.length; i+=17)
		{
			byte satID;
			int e;
			short t_oa;
			int deltaT;
			int omegaDot;
			short svHealth;
			int semiMajorAxisA;
			int omega0;
			int argumentOfPerigeeOmega;
			int meanAnomalyOfReferenceM;
			short af0;
			short af1;

			int n = i; //n is index for individual parameters

			satID = (byte)(Integer.parseInt(splitAlmanacLogParameters[n]) - 1);
			//System.out.println("n: " + n + " satID: " + satID);
			n+=2; //skip weeknumber

			double tempT_oa = Double.parseDouble(splitAlmanacLogParameters[n]);
			t_oa = (short)Math.round((tempT_oa/Math.pow(2, 12)));
			//System.out.println("n: " + n + " t_oa: " + t_oa);
			n++;

			double tempE = Double.parseDouble(splitAlmanacLogParameters[n]);
			e = (int)Math.round((tempE/Math.pow(2, -21)));
			//System.out.println("n: " + n +" e: " + e);
			n++;

			double tempOmegaDot = Double.parseDouble(splitAlmanacLogParameters[n]);
			int workingOmegaDot = (int)Math.round((tempOmegaDot * SCALE_RADIANS_TO_SEMI_CIRCLES)/Math.pow(2, -38));
			omegaDot = (int)GNSSUtil.putIntoTwosComplementForm(workingOmegaDot, 16);
			//System.out.println("n: " + n + " omegaDot: " + omegaDot);
			n++;

			double tempOmega0 = Double.parseDouble(splitAlmanacLogParameters[n]);
			int workingOmega0 = (int)Math.round((tempOmega0 * SCALE_RADIANS_TO_SEMI_CIRCLES)/Math.pow(2, -23));
			omega0 = (int)GNSSUtil.putIntoTwosComplementForm(workingOmega0, 24);
			//System.out.println("n: " + n + " omega0: " + omega0);
			n++;

			double tempArgumentOfPerigeeOmega = Double.parseDouble(splitAlmanacLogParameters[n]);
			int workingArgumentOfPerigeeOmega = (int)Math.round((tempArgumentOfPerigeeOmega * SCALE_RADIANS_TO_SEMI_CIRCLES)/Math.pow(2, -23));
			argumentOfPerigeeOmega = (int)GNSSUtil.putIntoTwosComplementForm(workingArgumentOfPerigeeOmega, 24);
			//System.out.println("n: " + n + " argumentOfPerigeeOmega: " + argumentOfPerigeeOmega);
			n++;

			double tempMeanAnomalyOfReferenceM = Double.parseDouble(splitAlmanacLogParameters[n]);
			int workingMeanAnomalyOfReferenceM = (int)Math.round((tempMeanAnomalyOfReferenceM * SCALE_RADIANS_TO_SEMI_CIRCLES)/Math.pow(2, -23));
			meanAnomalyOfReferenceM = (int)GNSSUtil.putIntoTwosComplementForm(workingMeanAnomalyOfReferenceM, 24);
			//System.out.println("n: " + n + " meanAnomalyOfReferenceM: " + meanAnomalyOfReferenceM);
			n++;

			double tempAf0 = Double.parseDouble(splitAlmanacLogParameters[n]);
			//System.out.println("satID: " + satID + " tempAf0: " + tempAf0);
			int workingAf0 = (int)Math.round(tempAf0/Math.pow(2, -20));
			af0 = (short)GNSSUtil.putIntoTwosComplementForm(workingAf0, 11);
			//System.out.println("n: " + n + " af0: " + af0);
			n++;

			double tempAf1 = Double.parseDouble(splitAlmanacLogParameters[n]);
			int workingAf1 = (int)Math.round(tempAf1/Math.pow(2, -38));
			af1 = (short)GNSSUtil.putIntoTwosComplementForm(workingAf1, 11);
			//System.out.println("n: " + n + " af1: " + af1);
			n+=2; //skip corrected mean motion

			double tempSemiMajorAxis = Double.parseDouble(splitAlmanacLogParameters[n]);
			semiMajorAxisA = (int)(Math.round(Math.sqrt(tempSemiMajorAxis)/Math.pow(2, -11)));
			//System.out.println("n: " + n +" semiMajorAxisA: " + semiMajorAxisA);
			n++;

			double tempDeltaT = Double.parseDouble(splitAlmanacLogParameters[n]);
			int workingDeltaT = (int)Math.round((tempDeltaT * SCALE_RADIANS_TO_SEMI_CIRCLES)/Math.pow(2, -19));
			deltaT = (int)GNSSUtil.putIntoTwosComplementForm(workingDeltaT, 16);
			//System.out.println("n: " + n + " deltaT: " + deltaT);
			n+=3; //skip to sv health from almanac

			short tempSvHealth = Short.parseShort(splitAlmanacLogParameters[n]);
			svHealth = tempSvHealth;
			//System.out.println("n: " + n + " svHealth: " + svHealth);

			if((x >= 0) && (x < numSatsTotal))
			{
				almanacSatelliteParametersArray[x] = new AlmanacSatelliteParameters(
						satID,
						e,
						t_oa,
						deltaT,
						omegaDot,
						svHealth,
						semiMajorAxisA,
						omega0,
						argumentOfPerigeeOmega,
						meanAnomalyOfReferenceM,
						af0,
						af1);

				x++;
			}
		}

		return new GPSAlmanac(almanacTimestamp,numSatsTotal, weekNumber,
				almanacSatelliteParametersArray);
	}

	/**
	 * @return the logger
	 */
	public Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @param logger the logger to set
	 */
	public void setLogger(final Logger logger)
	{
		this.logger = logger;
	}

}