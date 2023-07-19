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

package OSGRS.novateloem3;

import java.util.Date;
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

/** This class is of the oem4 package which is responsible for processing log data */

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

	public final static int MAX_NUMBER_OF_SATELLITES = 32;

	/**
	 * This method is in testing for the oem 3 reciever!!
	 */

	public static SatellitesInView processGPGSVLogs (NovatelLog[] GPGSVLogs)
	{
		short numberOfSatsInView;

		short[] satsInView;

		int n = 0; //index array for satsInView

		Date SIVTimestamp;

		SIVTimestamp = GPGSVLogs[0].getLogTimestamp();

//		System.out.println(SIVTimestamp);

		String GPGSVLogString = GPGSVLogs[0].getLog();

		String[] SplitGPGSVLogString = GPGSVLogString.split(",");

		int totalNumberOfMessages = Integer.parseInt(SplitGPGSVLogString[1]);

//		System.out.println("totalNumberOfMessages: " + totalNumberOfMessages);

		//int messageNumber = Integer.parseInt(SplitGPGSVLogString[2]);

		numberOfSatsInView = Short.parseShort(SplitGPGSVLogString[3]);

		satsInView = new short[numberOfSatsInView];

//		System.out.println("numberOfSatsInView: " + numberOfSatsInView);

		for (int i = 0; i < totalNumberOfMessages; i++)
		{
			String GPGSVLog = GPGSVLogs[i].getLog();

//			System.out.println("Log: " + GPGSVLog);

			String[] splitGPGSVLog = GPGSVLog.split(",");

			for (int x=4; x < splitGPGSVLog.length; x+=4)
			{
				satsInView[n] = (short)(Integer.parseInt(splitGPGSVLog[x]));
				n++;
			}
		}

		for(int i = 0; i < satsInView.length; i++ )
		{
//			System.out.println(satsInView[i]);
		}

		return new SatellitesInView(numberOfSatsInView, satsInView, SIVTimestamp);
	}

	public static RawIonUTCModel getRawIonUTCModelFromRASALogs(NovatelLog RASALog)
	{
		//get subframe 4 page 18

		String[] splitRASALog = RASALog.getLog().split(",");

		String subframe4Page18 = null;

		String rawIonosphereModelString;

		String rawUTCModelString;

		for(int  i = 0; i < splitRASALog.length; i++)
		{
			//System.out.println("i: " + i);
			if(splitRASALog[i].equals("4") && splitRASALog[i+1].equals("18"))
			{
				//System.out.println("found subframe 4 page 18");
				subframe4Page18 = splitRASALog[i+2];
				break; // exit for loop because p18 has been found,
				//no point searching rest of splitRASALogarray
			}
		}

		//System.out.println("Subframe 4, page 18: " + subframe4Page18);

		//extract Raw IonoModel and UTC data

		rawIonosphereModelString = subframe4Page18.substring(14,30);

		//System.out.println("rawIonosphereModelString: " + rawIonosphereModelString);

		rawUTCModelString = subframe4Page18.substring(30, 56);

		//System.out.println("rawUTCModelString: " + rawUTCModelString);

		return new RawIonUTCModel(rawIonosphereModelString, rawUTCModelString, RASALog.getLogTimestamp());
	}

	public static RawAlmanac[] getRawAlmanacFromRASALog(NovatelLog RASALog)
	{
		RawAlmanac[] rawAlmanacArray = new RawAlmanac[NovatelLogProcessing.MAX_NUMBER_OF_SATELLITES];

		String[] splitRASALog = RASALog.getLog().split(",");

//		for (int i = 9; i < splitRASALog.length; i++)
//		{
//		//System.out.println(i + ": " + splitRASALog[i]);
//		}

		for(int  i = 9; i < splitRASALog.length; i += 3)
		{
			//System.out.println(splitRASALog[i]);

			//get SVID (it's 6 LSB of first byte in word 3
			int firstByteOfWord3 = Integer.parseInt(splitRASALog[i].substring(12, 14), 16);

			//System.out.println("firstByteOfWord3: " + firstByteOfWord3);

			int svID = firstByteOfWord3 & 0x3F;

			//System.out.println("svID: " + svID);

			if ((svID > 0) && (svID < NovatelLogProcessing.MAX_NUMBER_OF_SATELLITES))
			{
				rawAlmanacArray[svID-1] = new RawAlmanac((byte)svID, splitRASALog[i], RASALog.getLogTimestamp());
			}

		}

		return rawAlmanacArray;
	}

	public static SatelliteEphemeris getSatelliteEphemerisFromREPALog(NovatelLog REPALog)
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
		Date logTimestamp = REPALog.getLogTimestamp();

//		process log

		String REPALogString = REPALog.getLog();

//		System.out.println(REPALogString);

		String[] splitREPALogString = REPALogString.split(",");

		PRN = Short.parseShort(splitREPALogString[1]);

		satID = (short)(PRN - 1);

		Logger logger = Logger.getLogger("getSatelliteEphemerisFromREPALog" + PRN);
		logger.setLevel(DebugLogger.getOutputVerbosity());
		if(logger.getLevel() != Level.OFF)
		{
			DebugLogger.recordLogToFile(logger);
		}

//		System.out.println("PRN: " + PRN + " satID: " + satID);

		GPSWeekNumberShort = (short)0; //need to decode this properly

		String subframe1 = splitREPALogString[2];
//		System.out.println("subframe1: " + subframe1);

		String subframe2 = splitREPALogString[3];
//		System.out.println("subframe2: " + subframe2);

		String subframe3WithCRC = splitREPALogString[4];
//		System.out.println("subframe3withCRC: " + subframe3WithCRC);

		String[] splitSubframe3WithCRC = subframe3WithCRC.split("\\*"); //need to escape * here (\*)

		String subframe3 = splitSubframe3WithCRC[0];
//		System.out.println("subframe3: " + subframe3);

		rawSubframeDataString = subframe1 + subframe2 + subframe3;
//		System.out.println("rawSubframeDataString: " + rawSubframeDataString);

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

//			System.out.println((sf1[9] & 0xFF) >> 2 == Integer.parseInt(sf1InBinary.substring(64,70),2)); //works

			/*########*/

			int iodcHighBits = (sf1[9] & 0x03) << 8;
			int iodcLowBits  = (sf1[22] & 0xFF);

			iodc_issuedOfDataClock = (iodcHighBits | iodcLowBits);

//			System.out.println("PRN: " + PRN + " iodc: " +iodc_issuedOfDataClock);


			/*####L2PDataFlag####*/

			L2PDataFlag = (byte)(Integer.parseInt(sf1InBinary.substring(72,73)));

			/*####subframe1 reserved data###*/

			sf1ReservedData23Bit = Integer.parseInt(sf1InBinary.substring(73, 96),2);

			sf1ReservedData24ABit = Integer.parseInt(sf1InBinary.substring(96, 120),2);

			sf1ReservedData24BBit = Integer.parseInt(sf1InBinary.substring(120, 144),2);

			sf1ReservedData16Bit = Integer.parseInt(sf1InBinary.substring(144, 160),2);

			/*####TGD####*/

			tgd_estimatedGroupDelayDifferentialSeconds = (short)(sf1[21] & 0xFF);


//			tgd_estimatedGroupDelayDifferentialSeconds = twosComplementToInteger(twosComplementTGD, 8);
//			tgd_estimatedGroupDelayDifferentialSeconds *= Math.pow(2, -31);

			logger.fine("Satellite Ephemeris.." +"tgd verified: " + (tgd_estimatedGroupDelayDifferentialSeconds == Long.parseLong(sf1InBinary.substring(160,168),2)));

			/*####toc####*/

			int tocHighBits = (sf1[23] & 0xFF) << 8;
			int tocLowBits = (sf1[24] & 0xFF);

			toc_clockDataReferenceTimeSecondsOfGPSWeek = tocHighBits | tocLowBits;


			logger.fine("Satellite Ephemeris.." +"toc verified: " + (toc_clockDataReferenceTimeSecondsOfGPSWeek == Integer.parseInt(sf1InBinary.substring(176,192),2)));
//			toc_clockDataReferenceTimeSecondsOfGPSWeek = rawToc * Math.pow(2, 4);

			/*####AFx####*/

			af2_satelliteClockDriftRateSecondsPerSquareSecond = (short)(sf1[25] & 0xFF);

			logger.fine("Satellite Ephemeris.." + "af2 verified: "+ (af2_satelliteClockDriftRateSecondsPerSquareSecond == Integer.parseInt(sf1InBinary.substring(192,200),2)));

//			af2_satelliteClockDriftRateSecondsPerSquareSecond = twosComplementToInteger(twosComplementAf2, 8);
//			af2_satelliteClockDriftRateSecondsPerSquareSecond *= Math.pow(2, -55);

			int af1HighBits = (sf1[26] & 0xFF) << 8;
			int af1LowBits =   sf1[27] & 0xFF;

			af1_satelliteClockDriftSecondsPerSecond = af1HighBits | af1LowBits;

			logger.fine("Satellite Ephemeris.." + "af1 verified: "+ (af1_satelliteClockDriftSecondsPerSecond == Integer.parseInt(sf1InBinary.substring(200,216),2)));

//			af1_satelliteClockDriftSecondsPerSecond = twosComplementToInteger(twosComplementAf1, 16);
//			af1_satelliteClockDriftSecondsPerSecond *= Math.pow(2, -43);

			int af0VeryHighBits = (sf1[28] & 0xFF) << 14;
			int af0HighBits = (sf1[29] & 0xFF) << 6;
			int af0LowBits = (sf1[30] & 0xFF) >> 2;

			af0_satelliteClockBiasSeconds = (af0VeryHighBits | af0HighBits | af0LowBits);

			logger.fine("Satellite Ephemeris.." + "af0 verified: "+ (af0_satelliteClockBiasSeconds == Integer.parseInt(sf1InBinary.substring(216,238),2)));

//			af0_satelliteClockBiasSeconds = twosComplementToInteger(twosComplementAf0, 22);
//			af0_satelliteClockBiasSeconds *= Math.pow(2, -31);

			//Subframe 2 -------------------------------------------------------------

			bm = new Bitmask(sf2);

			String sf2InBinary = bm.getBinary(0, 239);

			//The first 2 bytes are the TLM word preamble
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
//			Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres = twosComplementToInteger(twosComplementCrs, 16);
//			Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres *= Math.pow(2, -5);

			/*####Delta N####*/

			int deltanHighBits = (sf2[10] & 0xFF) << 8;
			int deltanLowBits =  (sf2[11] & 0xFF);

			DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond = deltanHighBits | deltanLowBits;

			logger.fine("Satellite Ephemeris.." + "Delta N verified: "+ (DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond == Long.parseLong(sf2InBinary.substring(73,88),2)));
//			DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond = twosComplementToInteger(twosComplementDeltan, 16);
//			DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond *= Math.pow(2, -43);

			/*####M0####*/

			long m01 = ((long)(sf2[12] & 0xFF)) << 24;
			int m02 = (sf2[13] & 0xFF) << 16;
			int m03 = (sf2[14] & 0xFF) << 8;
			int m04 = (sf2[15] & 0xFF);

			M0_meanAnomoloyAtReferenceTimeSemiCircles = m01 | m02 | m03 | m04;

//			M0_meanAnomoloyAtReferenceTimeSemiCircles = twosComplementToInteger(twosComplementM0, 32);
//			M0_meanAnomoloyAtReferenceTimeSemiCircles *= Math.pow(2, -31);

			long m0CheckTwos = Long.parseLong(sf2InBinary.substring(88,120),2);
//			double m0Check_semicricles = twosComplementToInteger(m0CheckTwos, 32);
//			m0Check_semicricles *= Math.pow(2, -31);

			logger.fine("Satellite Ephemeris.." + "M0 verified: "+ (M0_meanAnomoloyAtReferenceTimeSemiCircles == m0CheckTwos));

			/*####CUC####*/

			int cucHighBits = (sf2[16] & 0xFF) << 8;
			int cucLowBits =  (sf2[17] & 0xFF);
			Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians = cucHighBits | cucLowBits;

			logger.fine("Satellite Ephemeris.." + "Cuc verified: "+ (Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians == Long.parseLong(sf2InBinary.substring(120,136),2)));

//			Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians = twosComplementToInteger(twosComplementCuc, 16);
//			Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians *= Math.pow(2, -29);

			/*####Eccentricity####*/

			long e1 = ((long)(sf2[18] & 0xFF)) << 24;
			int e2 = (sf2[19] & 0xFF) << 16;
			int e3 = (sf2[20] & 0xFF) << 8;
			int e4 = (sf2[21] & 0xFF);

			e_eccentricity = e1 | e2 | e3 | e4;

			logger.fine("Satellite Ephemeris.." + "e_eccentricity verified: "+ (e_eccentricity == Long.parseLong(sf2InBinary.substring(136,168),2)));
//			e_eccentricity *= Math.pow(2, -33);

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
//			sqrtA_squareRootOfSemiMajorAxisMetres *= Math.pow(2, -19);

			/*####TOE####*/

			int toeHighBits = (sf2[28] & 0xFF) << 8;
			int toeLowBits =  (sf2[29] & 0xFF);

			toe_timeOfEphemerisSecondsOfGPSWeek = toeHighBits | toeLowBits;

			logger.fine("Satellite Ephemeris.." + "toe verified: "+ (toe_timeOfEphemerisSecondsOfGPSWeek == Long.parseLong(sf2InBinary.substring(216,232),2)));
//			toe_timeOfEphemerisSecondsOfGPSWeek *= Math.pow(2, 4);

			/*####FIT Interval Flag####*/

			FIT_intervalFlag = (byte)(sf2[30] & 0x80);

			logger.fine("Satellite Ephemeris.."+ "FIT interval flag verified: " + (FIT_intervalFlag == Integer.parseInt(sf2InBinary.substring(232,233),2)));

			/*####AODO####*/

			AODO_ageOfDataOffset = (short)((sf2[30] & 0x7C) >> 2);
			AODO_ageOfDataOffset *= 900;

			assert (AODO_ageOfDataOffset > 0 && AODO_ageOfDataOffset <= 27900);

			logger.fine("Satellite Ephemeris.." + " AODO verified " + ((AODO_ageOfDataOffset/900) == Short.parseShort(sf2InBinary.substring(233,238),2)));

			//Subframe 3 -------------------------------------------------------------


			//The first byte is the TLM word preamble
			assert (sf3[1] == (byte)0x8B);

			bm = new Bitmask(sf3);
			String sf3InBinary = bm.getBinary(0, 239);

//			System.out.println((byte)Integer.parseInt(sf3InBinary.substring(0,8),2) == (byte)0x8b); //works

			/*####CIC####*/

			int cicHighBits = (sf3[7] & 0xFF) << 8;
			int cicLowBits =   sf3[8] & 0xFF;

			Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians = cicHighBits | cicLowBits;

			logger.fine("Satellite Ephemeris.." + "cic verified: "+ (Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians == Long.parseLong(sf3InBinary.substring(48,64),2)));

//			Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians = twosComplementToInteger(twosComplementCic, 16);
//			Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians *= Math.pow(2, -29);

			/*####OMEGA0####*/

			long omega01 = ((long)(sf3[9] & 0xFF)) << 24;
			int omega02 = (sf3[10] & 0xFF) << 16;
			int omega03 = (sf3[11] & 0xFF) << 8;
			int omega04 = (sf3[12] & 0xFF);

			OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles = omega01 | omega02 | omega03 | omega04;

			logger.fine("Satellite Ephemeris.." + "OMEGA0 verified: "+ (OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles == Long.parseLong(sf3InBinary.substring(64,96),2)));
//			OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles = twosComplementToInteger(twosComplementOmega0, 32);
//			OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles *= Math.pow(2, -31);

			/*####CIS####*/

			int cisHighBits = (sf3[13] & 0xFF) << 8;
			int cisLowBits =   sf3[14] & 0xFF;

			Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians = cisHighBits | cisLowBits;

			logger.fine("Satellite Ephemeris.." + "CIS verified: "+ (Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians == Long.parseLong(sf3InBinary.substring(96,112),2)));

//			Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians = twosComplementToInteger(twosComplementCis, 16);
//			Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians *= Math.pow(2, -29);

			/*####IO####*/

			long i01 = ((long)(sf3[15] & 0xFF)) << 24;
			int i02 = (sf3[16] & 0xFF) << 16;
			int i03 = (sf3[17] & 0xFF) << 8;
			int i04 = (sf3[18] & 0xFF);

			i0_inclinationAngleAtReferenceTimeSemiCircles = i01 | i02 | i03 | i04;

			logger.fine("Satellite Ephemeris.." + "i0 verified: "+ (i0_inclinationAngleAtReferenceTimeSemiCircles == Long.parseLong(sf3InBinary.substring(112,144),2)));
//			i0_inclinationAngleAtReferenceTimeSemiCircles = twosComplementToInteger(twosComplementi0, 32);
//			i0_inclinationAngleAtReferenceTimeSemiCircles *= Math.pow(2, -31);

			/*####CRC####*/

			int crcHighBits = (sf3[19] & 0xFF) << 8;
			int crcLowBits =   sf3[20] & 0xFF;

			Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres = crcHighBits | crcLowBits;

			logger.fine("Satellite Ephemeris.." + "Crc verified: "+ (Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres == Long.parseLong(sf3InBinary.substring(144,160),2)));

//			Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres = twosComplementToInteger(twosComplementCrc, 16);
//			Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres *= Math.pow(2, -5);

			/*####Omega####*/

			long omega1 = ((long)(sf3[21] & 0xFF)) << 24;
			int omega2 = (sf3[22] & 0xFF) << 16;
			int omega3 = (sf3[23] & 0xFF) << 8;
			int omega4 = (sf3[24] & 0xFF);

			omega_argumentOfPerigreeSemiCircles = omega1 | omega2 | omega3 | omega4;

			logger.fine("Satellite Ephemeris.." + "omega_argumentofsemicircles verified: "+ (omega_argumentOfPerigreeSemiCircles == Long.parseLong(sf3InBinary.substring(160,192),2)));
			//double scaledOmega = twosComplementToInteger(omega_argumentOfPerigreeSemiCircles, 32);
			//scaledOmega *= Math.pow(2, -31);

			//logger.fine("Scaled Omega: " + scaledOmega);

			//long omegatest = twosComplementToInteger(omega_argumentOfPerigreeSemiCircles,32);

			//		logger.finest("PRN: " + PRN + " RAW omega: " + ( omega_argumentOfPerigreeSemiCircles) + " omega : " + putIntoTwosComplementForm(omegatest,32));

			/*####OMEGA Dot####*/

			int omegaDot1 = (sf3[25] & 0xFF) << 16;
			int omegaDot2 = (sf3[26] & 0xFF) << 8;
			int omegaDot3 = (sf3[27] & 0xFF);

			OMEGADOT_rightAscensionSemiCirclesPerSecond = omegaDot1 | omegaDot2 | omegaDot3;

			logger.fine("Satellite Ephemeris.." + "omega_dot verified: "+ (OMEGADOT_rightAscensionSemiCirclesPerSecond == Long.parseLong(sf3InBinary.substring(192,216),2)));
//			OMEGADOT_rightAscensionSemiCirclesPerSecond = twosComplementToInteger(twosComplementOmegaDot, 24);
//			OMEGADOT_rightAscensionSemiCirclesPerSecond *= Math.pow(2, -43);

			/*####I DOT####*/

			int idotHighBits = (sf3[29] & 0xFF) << 6;
			int idotLowBits =  (sf3[30] & 0xFF) >> 2; //fixed

			IDOT_rateOfInclinationAngleSemiCirclesPerSecond = (short)(idotHighBits | idotLowBits);

			logger.fine("Satellite Ephemeris.." + "IDOT verified: "+ (IDOT_rateOfInclinationAngleSemiCirclesPerSecond == Long.parseLong(sf3InBinary.substring(224,238),2)));

//			IDOT_rateOfInclinationAngleSemiCirclesPerSecond = twosComplementToInteger(twosComplementIdot, 14);
//			IDOT_rateOfInclinationAngleSemiCirclesPerSecond *= Math.pow(2, -43);

			/*#### NMCT Validity Time####*/ 

//			double toe = toe_timeOfEphemerisSecondsOfGPSWeek;
//			double aodo = AODO_ageOfDataOffset;

//			double offset = toe % 7200;  //2hrs

//			//Offset will normall be 0 because the toe is on the 2 hour boundary

//			//System.out.println("TriangulatorRealTimeTestTool.calculateNMCTTime offset: " + offset);

//			if (offset == 0)
//			{
//			nmctTime = toe - aodo;
//			}

//			if (offset > 0)

//			{
//			nmctTime = toe - offset + 7200 - aodo;
//			}

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
//					logSequence,
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

			//return null;
	}

	public static ReferenceTime getReferenceTimeFRMALog(NovatelLog FRMALog)
	{
		short GPSWeek; //no need to initialize as this is overwritten in response manager
		int GPSTow; //no need to initialize as this is overwritten in response manager
		byte satID;
		short tlmMessage;
		byte antiSpoof;
		byte alert;
		byte tlmReserved;
		Date referenceTimeTimestamp;

		String FRAMALogString =  FRMALog.getLog();

		satID = (byte)FRMALog.getSatID();
		//System.out.println("satID: " + satID + " Log: " + FRAMALogString);

		referenceTimeTimestamp = FRMALog.getLogTimestamp();

		String[] splitFRMALog = FRAMALogString.split(",");

		double secondsInGPSWeek = Double.parseDouble(splitFRMALog[2]);

		String rawSubframeInHexWithCRC = splitFRMALog[6];
		//System.out.println(RawSubframeInHexWithCRC);

		String[] splitRawSubframeInHexWithCRC = rawSubframeInHexWithCRC.split("\\*");

		String rawSubframeInHex = splitRawSubframeInHexWithCRC[0];

		//System.out.println("OEM3: " + "satID: " + satID + " seconds: " + secondsInGPSWeek + " rawSubframeInHex: " 
		//+ rawSubframeInHex);

		String first15HexCharacters = rawSubframeInHex.substring(0, 15);
		//System.out.println(first15HexCharacters);

		long first60Bits = Long.parseLong(first15HexCharacters, 16);
		//System.out.println("first60Bits: " + first60Bits);

		String first60BitsBinaryString = Long.toBinaryString(first60Bits);
		//System.out.println(first60BitsBinaryString);

//		System.out.println("OEM3 - " + "satID: " + satID + " seconds: " + secondsInGPSWeek + " first60Bits: " 
//		+ first60BitsBinaryString);

		String tlmMessageBinary = first60BitsBinaryString.substring(8, 22);
		//System.out.println("tlmMessageBinary: " + tlmMessageBinary + " length: " + tlmMessageBinary.length());

		tlmMessage = Short.parseShort(tlmMessageBinary, 2);
		//System.out.println("tlmMessage: " + tlmMessage);

		String tlmReservedBinary = first60BitsBinaryString.substring(22, 24);
		//System.out.println("tlmReservedBinary: " + tlmReservedBinary + " length: " + tlmReservedBinary.length());

		tlmReserved = Byte.parseByte(tlmReservedBinary, 2);
		//System.out.println("tlmReserved: " + tlmReserved);

		String alertBinary = first60BitsBinaryString.substring(47, 48);
		//System.out.println("alertBinary: " + alertBinary + " length: " + alertBinary.length());

		alert = Byte.parseByte(alertBinary, 2);
		//System.out.println("alert: " + alert);

		String antiSpoofBinary = first60BitsBinaryString.substring(48, 49);
		//System.out.println("antiSpoofBinary: " + antiSpoofBinary + " length: " + antiSpoofBinary.length());

		antiSpoof = Byte.parseByte(antiSpoofBinary, 2);
		//System.out.println("antiSpoof: " + antiSpoof);

		return new ReferenceTime((short)0,0,satID, tlmMessage, antiSpoof, alert, tlmReserved, referenceTimeTimestamp);
	}

	public static IonUTCModel getIonUTCModelFromRawIonUTCModel (RawIonUTCModel rawIonUTCModel)
	{	
		//ionospheric model
		short alpha0_constant_term;
		short alpha1_first_order_term;
		short alpha2_second_order_term;
		short alpha3_third_order_term;
		short beta0_constant_term;
		short beta1_first_order_term;
		short beta2_second_order_term;
		short beta3_third_order_term;

		//UTC Model
		long A0_second_alpha_constant_term;
		int A1_second_alpha_first_order_term;
		short dtLS;
		short tot;
		short WNt;
		short WNlsf;
		short DN;
		short dtLSF;

		Date ionUTCTimestamp = rawIonUTCModel.getRawIonUTCModelTimestamp();

		String rawIonoData = rawIonUTCModel.getRawIonosphereModelString();
		//System.out.println("rawIonoData: " + rawIonoData);

//		int rawIonoData1 = Integer.parseInt(rawIonoData.substring(0, 6), 16);
//		System.out.println("rawIonoData1: " + rawIonoData1);

//		int rawIonoData2 = Integer.parseInt(rawIonoData.substring(6, 12), 16);
//		System.out.println("rawIonoData2: " + rawIonoData2);

//		int rawIonoData3 = Integer.parseInt(rawIonoData.substring(12, 16), 16);
//		System.out.println("rawIonoData3: " + rawIonoData3);

//		String binaryRawIonoData = Integer.toBinaryString(rawIonoData1) + Integer.toBinaryString(rawIonoData2)
//		+ Integer.toBinaryString(rawIonoData3);

//		System.out.println("binaryRawIonoData: " + binaryRawIonoData);

		assert (rawIonoData.length() == 16);

		byte[] rawIonoDataByteArray = new byte[8];

		String tempString;
		int tempValue;

		int idx = 0;

		for (int i = 0; i < rawIonoData.length(); i = i + 2)
		{
			tempString = rawIonoData.substring(i, i + 2);  // each position in the array contains 2 chars (8bits)
			tempValue = Integer.parseInt(tempString, 16);
			rawIonoDataByteArray[idx] = (byte)tempValue;

			idx++;
		}

		Bitmask bm1 = new Bitmask(rawIonoDataByteArray);

		String binaryRawIonoData = bm1.getBinary(0, 63);
		//System.out.println("binaryRawIonoData: " + binaryRawIonoData + " length: " + binaryRawIonoData.length());

		alpha0_constant_term = Short.parseShort(binaryRawIonoData.substring(0, 8), 2);
		//System.out.println("alpha0_constant_term: " + alpha0_constant_term);

		alpha1_first_order_term = Short.parseShort(binaryRawIonoData.substring(8, 16), 2);
		//System.out.println("alpha1_first_order_term: " + alpha1_first_order_term);

		alpha2_second_order_term = Short.parseShort(binaryRawIonoData.substring(16, 24), 2);
		//System.out.println("alpha2_second_order_term: " + alpha2_second_order_term);

		alpha3_third_order_term = Short.parseShort(binaryRawIonoData.substring(24, 32), 2);
		//System.out.println("alpha3_third_order_term: " + alpha3_third_order_term);

		beta0_constant_term = Short.parseShort(binaryRawIonoData.substring(32, 40), 2);
		//System.out.println("beta0_constant_term: " + beta0_constant_term);

		beta1_first_order_term = Short.parseShort(binaryRawIonoData.substring(40, 48), 2);
		//System.out.println("beta1_first_order_term: " + beta1_first_order_term);

		beta2_second_order_term = Short.parseShort(binaryRawIonoData.substring(48, 56), 2);
		//System.out.println("beta2_second_order_term: " + beta2_second_order_term);

		beta3_third_order_term = Short.parseShort(binaryRawIonoData.substring(56, 64), 2);
		//System.out.println("beta3_third_order_term: " + beta3_third_order_term);


		String rawUTCData = rawIonUTCModel.getRawUTCModelString();
		//System.out.println("rawUTCData: " + rawUTCData);

		assert (rawUTCData.length() == 26);

		byte[] rawUTCDataByteArray = new byte[13];

		idx = 0;

		for (int i = 0; i < rawUTCData.length(); i = i + 2)
		{
			tempString = rawUTCData.substring(i, i + 2);  // each position in the array contains 2 chars (8bits)
			tempValue = Integer.parseInt(tempString, 16);
			rawUTCDataByteArray[idx] = (byte)tempValue;

			idx++;
		}

		Bitmask bm2 = new Bitmask(rawUTCDataByteArray);

		String binaryRawUTCData = bm2.getBinary(0, 103);
		//System.out.println("binaryRawUTCData: " + binaryRawUTCData + " length: " + binaryRawUTCData.length());

		A1_second_alpha_first_order_term = Integer.parseInt(binaryRawUTCData.substring(0, 24), 2);
		//System.out.println("A1_second_alpha_first_order_term: " + A1_second_alpha_first_order_term);

		A0_second_alpha_constant_term = Long.parseLong(binaryRawUTCData.substring(24, 56), 2);
		//System.out.println("A0_second_alpha_constant_term: " + A0_second_alpha_constant_term);

		tot = Short.parseShort(binaryRawUTCData.substring(56, 64), 2);
		//System.out.println("tot: " + tot);

		WNt = Short.parseShort(binaryRawUTCData.substring(64, 72), 2);
		//System.out.println("WNt: " + WNt);

		dtLS = Short.parseShort(binaryRawUTCData.substring(72, 80), 2);
		//System.out.println("dtLS: " + dtLS);

		WNlsf = Short.parseShort(binaryRawUTCData.substring(80, 88), 2);
		//System.out.println("WNlsf: " + WNlsf);

		DN = Short.parseShort(binaryRawUTCData.substring(88, 96), 2);
		//System.out.println("DN: " + DN);

		dtLSF = Short.parseShort(binaryRawUTCData.substring(96, 104), 2);
		//System.out.println("dtLSF: " + dtLSF);

		return new IonUTCModel(

				alpha0_constant_term,
				alpha1_first_order_term,
				alpha2_second_order_term,
				alpha3_third_order_term,
				beta0_constant_term,
				beta1_first_order_term,
				beta2_second_order_term,
				beta3_third_order_term,

				WNt,
				tot,
				A0_second_alpha_constant_term,
				A1_second_alpha_first_order_term,
				WNlsf,
				DN,
				dtLS,
				dtLSF,
				ionUTCTimestamp);

	}

	public static GPSAlmanac getGPSAlmanacFromGPALMLogs(NovatelLog[] GPALMLogs)
	{
		Date almanacTimestamp;
		byte numSatsTotal = 0;
		short weekNumber;

		GPSAlmanac gpsAlmanac;

		AlmanacSatelliteParameters[] almanacSatelliteParametersArray;

		//get all logs into local array, to prevent 

		NovatelLog[] securedGPALMLogs = new NovatelLog[GPALMLogs.length];

		NovatelLog tempGPALMALog;

		for(int i = 0; i < securedGPALMLogs.length; i++)
		{
			tempGPALMALog = GPALMLogs[i];
			if(tempGPALMALog != null)
			{
				securedGPALMLogs[numSatsTotal] = tempGPALMALog;
				numSatsTotal++;
			}
		}

		if(numSatsTotal > 0)
		{
			almanacSatelliteParametersArray = new AlmanacSatelliteParameters[numSatsTotal];
			//int n = 0;  //index variable for above array

			//get some parameters from the first nvoatel log
			NovatelLog firstGPALMLog = securedGPALMLogs[0];
			almanacTimestamp = firstGPALMLog.getLogTimestamp();

			String[] splitFirstGPALMLog = firstGPALMLog.getLog().split(",");
			weekNumber = (short)(Short.parseShort(splitFirstGPALMLog[4]) % 256);
			//System.out.println("weekNumber: " + weekNumber);

			for(int i = 0; i < numSatsTotal; i++)
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

				String[] splitGPALMLog = securedGPALMLogs[i].getLog().split(",");

				//System.out.println("i: " + i + " log: " + securedGPALMLogs[i].getLog());

				satID = (byte)(Integer.parseInt(splitGPALMLog[3]) -1);
				//System.out.println("satID: " + satID);

				svHealth = (short)Integer.parseInt(splitGPALMLog[5], 16);
				//System.out.println("svHealth: " + svHealth);

				e = Integer.parseInt(splitGPALMLog[6], 16);
				//System.out.println("e: " + e);

				t_oa = (short)Integer.parseInt(splitGPALMLog[7], 16);
				//System.out.println("t_oa: " + t_oa);

				deltaT = Integer.parseInt(splitGPALMLog[8], 16);
				//System.out.println("deltaT: " + deltaT);

				omegaDot = Integer.parseInt(splitGPALMLog[9], 16);
				//System.out.println("omegaDot: " + omegaDot);

				semiMajorAxisA = Integer.parseInt(splitGPALMLog[10], 16);
				//System.out.println("semiMajorAxisA: " + semiMajorAxisA);

				argumentOfPerigeeOmega = Integer.parseInt(splitGPALMLog[11], 16);
				//System.out.println("argumentOfPerigeeOmega: " + argumentOfPerigeeOmega);

				omega0 = Integer.parseInt(splitGPALMLog[12], 16);
				//System.out.println("omega0: " + omega0);

				meanAnomalyOfReferenceM = Integer.parseInt(splitGPALMLog[13], 16);
				//System.out.println("meanAnomalyOfReferenceM: " + meanAnomalyOfReferenceM);

				af0 = (short)Integer.parseInt(splitGPALMLog[14], 16);

				af0 = (short)(af0 & 0x7FF);

				//System.out.println("af0: " + af0);

				String[] splitAf1 = splitGPALMLog[15].split("\\*"); //getting rid of crc

				af1 = (short)Integer.parseInt(splitAf1[0], 16);
				//System.out.println("af1: " + af1);

				af1 = (short)(af1 & 0x7FF); 

				almanacSatelliteParametersArray[i] = 
					new AlmanacSatelliteParameters(
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

			}

			return new GPSAlmanac(almanacTimestamp, numSatsTotal, weekNumber,almanacSatelliteParametersArray);

		}else
		{
			return null; //this occurs when a log array containing no GPALM logs were passed to the method
		}

		//System.out.println("numSatsTotal: " + numSatsTotal);


		//return null;
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
