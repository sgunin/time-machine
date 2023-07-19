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

package OSGRS.dataType;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to process raw subframe data
 * and get individual satellite epehemeris pamramaters
 *
 */

public class SatelliteEphemeris implements DataType
{

	/**Timestamp for SatelliteEphemeris */
	private Date satelliteEphemerisTimestamp;

	/** The logger used for debug for this OSGRS instance */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/**
	 * The WGS84 value of the earths rotation rate in radians per second.
	 */
	private final static double OMEGA_DOT_E_EARTHS_ROTATION_RATE_RADIANS_PER_SECOND = 7.2921151467E-05;

	/**
	 * The value of PI from the GPS Spec.k
	 */
	public final static double PI = 3.1415926535898;

	/**
	 * Speed of light in metres per second.
	 */
	public final static double C_SPEED_OF_LIGHT = 2.99792458E+08;


	/** The maximum PRN. PRN is 6 bits. */
	public final static short PRN_MAXIMUM_VALUE = 64;

	/** The maximum value for the GPS week. The GPS week is a 10 bit number */
	public final static short GPS_WEEK_NUMBER_MAX_VALUE = (short)(Math.pow(2,10) - 1);

	/** The minimum valid value of IODE. */
	private final static short IODE_MINIMUM_VALUE = 0;

	/** The maximum valid value of IODE 8 bit number. */
	private final static short IODE_MAXIMUM_VALUE = (short)(Math.pow(2,8) - 1);;

	/** The maximum valid vail of URA. */
	private final static byte URA_CODE_MAX_VALUE = 15;

	/** The value to set the GPS week number if it is not available. */
	public final static short GPS_WEEK_NUMBER_NOT_SUPPLIED = -1;

	/** The minimum valid vail of codesOnL2. */
	private final static byte CODES_ON_L2_MIN_VALUE = 0;

	/** The maximum valid vail of codesOnL2. */
	private final static byte CODES_ON_L2_MAX_VALUE = 2;

	/** The minimum valid vail of L2PFlag. */
	private final static byte L2P_FLAG_MIN_VALUE = 0;

	/** The maximum valid vail of L2PFlag. */
	private final static byte L2P_FLAG_MAX_VALUE = 1;

	/** The maximum value for the TOC & TOE parameter. */
	private final static int GPS_TIME_MAXIMUM_VALUE = (int)(Math.pow(2,16) - 1);

	/** The maximum value for the IODC field. The field is a 10 bit number. */
	private final static int IODC_MAXIMUM_VALUE = (int)(Math.pow(2,10) - 1);

	/** The maximum possible value for satellite health. It is a 6 bit number. */
	private final static int SATELLITE_HEALTH_MAX_VALUE = 63;

	/** The satellite health is ok. */
	private final static int SAT_HEALTH_OK = 0;
	/** The satellite health is parity failure. Some or all parity bad. */

	private final static int SAT_HEALTH_PARITY_FAILURE = 1;

	/** The satellite health is TLM/HOW problem. Any departure from standard format. */
	private final static int SAT_HEALTH_TLM_HOW_FORMAT_PROBLEM = 2;
	/**
	 * The satellite health is Z-Count in HOW bad. Any problem with z-count not
	 * reflecting actual code phase.
	 */
	private final static int SAT_HEALTH_Z_COUNT_IN_HOW_BAD = 3;
	/**
	 * The satellite health problem subframes 1,2,3. one or more elements in words
	 * 3 through 10 of one or more subframes are bad.
	 */
	private final static int SAT_HEALTH_SUBFRAMES_1_2_3 = 4;
	/**
	 * The satellite health problem subframes 4,5. one or more elements in words
	 * 3 through 10 of one or more subframes are bad.
	 */
	private final static int SAT_HEALTH_SUBFRAMES_4_5 = 5;
	/**
	 * Satellite health problem all uploaded data bad. One or more elements in
	 * words 3 through 10 of any one (or more) subframes are bad.
	 */
	private final static int SAT_HEALTH_ALL_UPLOADED_DATA_BAD = 6;
	/**
	 * Satellite health problem All Data Bad. TLM word and/or HOW and one or more
	 * elements in any one (or more) subframes are bad.
	 */
	private final static int SAT_HEALTH_ALL_DATA_BAD = 6;
	/**
	 * This is an array that is used to convert between URA code and the User Range
	 * Accuracy in metres. This is specified in the GPS Signal specification by the
	 * following:
	 * <code>
	 * If N is 6 or less, X = 2 ^ (1 + URA/2) <p>
	 * If N is 6 or more but less than 15, X = 2 ^ (URA + 2) <p>
	 * If N is 15 then use the satellite at your own risk <p>
	 * <code>
	 * <p>
	 * The spec also notes that N=1,3 and 5 are rounded in order to use a lookup table
	 * approach to the conversion from URA code to range in metres.
	 */
	private final static double URA_CODE_TO_ACCURACY_METRES[] =
	{
		2,
		2.8,
		4,
		5.7,
		8,
		11.3,
		16,
		32,
		64,
		128,
		256,
		512,
		1024,
		2048,
		4096,
		Double.MAX_VALUE
	};

	/**
	 * The minimum value of the TGD field.
	 * This is the minimum 2's complement value by the scale factor for the TGD field
	 */
	private final static short GPS_TGD_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,7)-1; * Math.pow(2,-31);

	/**
	 * The maximum value of the TGD field.
	 * This is the maximum 2's complement value by the scale factor for the TGD field
	 */
	private final static short GPS_TGD_FIELD_MAX_VALUE = (short)(Math.pow(2,8) - 1);//(Math.pow(2,7) - 1) * Math.pow(2,-31);

	/**
	 * The minimum value of the af2 field.
	 * This is the minimum 2's complement value by the scale factor for the TGD field
	 */
	private final static short GPS_AF2_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,7) * Math.pow(2,-55);

	/**
	 * The maximum value of the af2 field.
	 * This is the maximum 2's complement value by the scale factor for the af2 field
	 */
	private final static short GPS_AF2_FIELD_MAX_VALUE = (short)(Math.pow(2,8) - 1);//(Math.pow(2,7) - 1) * Math.pow(2,-55);

	/**
	 * The minimum value of the af1 field.
	 * This is the minimum 2's complement value by the scale factor for the af1 field
	 */
	private final static int GPS_AF1_FIELD_MIN_VALUE = 0;

	/**
	 * The maximum value of the af1 field.
	 * This is the maximum 2's complement value by the scale factor for the af1 field
	 */
	private final static int GPS_AF1_FIELD_MAX_VALUE = (int)(Math.pow(2,16) - 1);//(Math.pow(2,15) - 1) * Math.pow(2,-43);


	/**
	 * The minimum value of the af0 field.
	 * This is the minimum 2's complement value by the scale factor for the af0 field
	 */
	private final static double GPS_AF0_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,21) * Math.pow(2,-31);

	/**
	 * The maximum value of the af0 field.
	 * This is the maximum 2's complement value by the scale factor for the af0 field
	 */
	private final static double GPS_AF0_FIELD_MAX_VALUE = (Math.pow(2,22) - 1);//(Math.pow(2,21) - 1) * Math.pow(2,-31);

	/**
	 * The minimum value of the crs and crc fields.
	 * This is the minimum 2's complement value by the scale factor for the crs field
	 */
	private final static double GPS_CR_FIELD_MIN_VALUE = 0;

	/**
	 * The maximum value of the crs and crc fields.
	 * This is the maximum 2's complement value by the scale factor for the crs field
	 */
	private final static double GPS_CR_FIELD_MAX_VALUE = (Math.pow(2,16) - 1);//(Math.pow(2,15) - 1) * Math.pow(2,-5);

	/**
	 * The minimum value of the deltaN field.
	 * This is the minimum 2's complement value by the scale factor for the deltaN field
	 */
	private final static double GPS_DELTAN_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,15) * Math.pow(2,-43);

	/**
	 * The maximum value of the deltaN field.
	 * This is the maximum 2's complement value by the scale factor for the deltaN field
	 */
	private final static double GPS_DELTAN_FIELD_MAX_VALUE = (Math.pow(2,16) - 1);//(Math.pow(2,15) - 1) * Math.pow(2,-43);

	/**
	 * The minimum value of the m0, omega0, i0, omega fields.
	 * This is the minimum 2's complement value by the scale factor for the m0 field
	 */
	private final static long GPS_32BIT_31SF__FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,31) * Math.pow(2,-31);

	/**
	 * The maximum value of the m0, omega0, i0, omega.
	 * This is the maximum 2's complement value by the scale factor for the m0 field
	 */
	private final static long GPS_32BIT_31SF__FIELD_MAX_VALUE = (long)(Math.pow(2,32) - 1);//(Math.pow(2,31) - 1) * Math.pow(2,-31);

	/**
	 * The minimum value of the cuc, cus, cic, cis  field.
	 * This is the minimum 2's complement value by the scale factor for the cuc field
	 */
	private final static int GPS_C_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,15) * Math.pow(2,-29);

	/**
	 * The maximum value of the cuc, cus, cic, cis.
	 * This is the maximum 2's complement value by the scale factor for the cuc field
	 */
	private final static int GPS_C_FIELD_MAX_VALUE = (int)(Math.pow(2,16) - 1);//(Math.pow(2,15) - 1) * Math.pow(2,-29);

	/**
	 * The maximum value of the e field.
	 */
	private final static long GPS_E_FIELD_MAX_VALUE = (long)(Math.pow(2,32) - 1);
	/**
	 * The maximum value of the sqrtA field.
	 */
	private final static long GPS_SQRTA_FIELD_MAX_VALUE = (long)(Math.pow(2,32) - 1);

	/**
	 * The minimum value of the omegadot field.
	 * This is the minimum 2's complement value by the scale factor for the cuc field
	 */
	private final static int GPS_OMEGADOT_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,23) * Math.pow(2,-43);

	/**
	 * The maximum value of omegadot field.
	 * This is the maximum 2's complement value by the scale factor for the cuc field
	 */
	private final static int GPS_OMEGADOT_FIELD_MAX_VALUE = (int)(Math.pow(2,24) - 1);//(Math.pow(2,23) - 1) * Math.pow(2,-43);


	/**
	 * The minimum value of the IDOT  field.
	 * This is the minimum 2's complement value by the scale factor for the cuc field
	 */
	private final static short GPS_IDOT_FIELD_MIN_VALUE = 0;// -1.0 * Math.pow(2,13) * Math.pow(2,-43);

	/**
	 * The maximum value of the IDOT  field.
	 * This is the maximum 2's complement value by the scale factor for the cuc field
	 */
	private final static short GPS_IDOT_FIELD_MAX_VALUE = (short)(Math.pow(2,14) - 1);//(Math.pow(2,13) - 1) * Math.pow(2,-43);


	/** The conversion for semi circles to radians. */
	public final static double SCALE_SEMI_CIRCLES_TO_RADIANS = SatelliteEphemeris.PI;

	/** The conversion for radians to semi circles. */
	public final static double SCALE_RADIANS_TO_SEMI_CIRCLES = 1.0 /  SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;

	/**
	 * The constant F.
	 */
	private final static double F = -2 * Math.sqrt(3.986005E+14) / Math.pow(SatelliteEphemeris.C_SPEED_OF_LIGHT, 2);

	/**
	 * The maximum number of iterations to go around when solving Kepler's equation.
	 */
	private final static int MAXIMUM_NUMBER_OF_ITERATIONS_SOLVING_KEPLERS_EQUATION = 16;

	/**
	 * Maximum value change in eccentric anomoly from one step to the next when solving
	 * Kepler's equation.
	 */
	private final static double MAXIMUM_VALUE_CHANGE_IN_ECCENTRIC_ANOMOLY = 1.0E-15;

	/**
	 * Sanity check threshold for solving Kepler's equation.
	 */
	private final static double SANITY_CHECK_THRESHOLD_KEPLERS_EQUATION = 1.0E-13;

	/** The number of different methods to solve Keplers. */
	public final static short NUMBER_OF_METHODS_TO_SOLVE_KEPLERS_EQUATION = 2;

	/** Use Wegsteins accelerator to solve Keplers equation for the eccentric anomoly. */
	public final static short USE_WEGSTEINS_ACCELERATOR_TO_SOLVE_KEPLERS_EQUATION = 0;

	/** Use Newtons method to solve Keplers equation for the eccentric anomoly. */
	public final static short USE_NEWTONS_METHOD_TO_SOLVE_KEPLERS_EQUATION = 1;

	/** A boolean to indicate whether this satellite is healthy or not    */
	private boolean satelliteHealthyBoolean;

	/** The PRN of the SV in discrete units. */
	private short PRN;

	/** sat id is PRN-1 */
	private short satID;

	private double n0_computedMeanMotionRadiansPerSecond;
	/**
	 * The corrected mean motion in radians per second is a derived field
	 * calculated using the formula in the GPS Signal Specification.
	 */
	private double n_correctedMeanMotionRadiansPerSecond;
	/**
	 * This is the predicted User Range Accuracy (URA) for the satellite in Metres. This is
	 * a statistical indicator of the ranging accuracies obtainable with a specific
	 * satellite.
	 */
	private double URA_predictedUserRangeAccuracyMetres;



//	Satellite clock and health data

	/**
	 * The GPS week number in weeks from midnight Jan 5 1980 / morning of Jan 6
	 * 1980. It is sourced as a 10 bit number that rolls back to 0. The week
	 * 0 started on January 6 1980. 0 <= gpsWeekNumber <= 1023. It rolled over at
	 * midnight on 22 August 1999.
	 * <b>Note that this is the true week number since Jan 5 1980 and
	 * hence will be larger than 1023</B>
	 * <p>
	 * Note that this field is not used when calculating the location of the satellite
	 * and will be set to GPS_WEEK_NUMBER_NOT_SUPPLIED in the case of the GL warn as
	 * it is not available.
	 */
	private int gpsWeekNumber;

	private short gpsWeekNumberShort;

	/**
	 * This is the predicted User Range Accuracy (URA) code for the satellite. This is
	 * a statistical indicator of the ranging accuracies obtainable with a specific
	 * satellite. It is a 4 bit number and hence is in the range of 0 to 15. It is
	 * converted into an accuracy number (X) in metres by the Triangulator using the
	 * formula in the GPS Signal specification:
	 * <p>
	 * <code>
	 * If N is 6 or less, X = 2 ^ (1 + URA/2) <p>
	 * If N is 6 or more but less than 15, X = 2 ^ (URA + 2) <p>
	 * If N is 15 then use the satellite at your own risk <p>
	 * <code>
	 *
	 */
	private byte ura_predictedUserRangeAccuracyCode;

	/**
	 * This indicates the health of the signal components from the satellite. If this
	 * is 0 then all of the navigation data is OK. If it is 1 then some or all of the
	 * navigation data is bad. This is a 6 bit number. The MSB is:
	 * <p>
	 * 0 = All data is ok.
	 * <p>
	 * 1 = Some data is bad.
	 * <p>
	 * The 5 LSBs indicate the error code in the range of 0 to 31. They details are in
	 * Table 20-VIII of the ICD. A string representation of the codes is in
	 * field satelliteHealthDescriptions. Not all of the error codes are relevant to
	 * the triangulator since it is designed for using C codes on the L1 Channel. The
	 * field satelliteHealthCodesOKForL1SinglePointPositioning is used to determine whether
	 * the health code affects the current location fix.
	 */
	private int satelliteHealth;

	/**
	 * This is a correction term to account for the effect of satellite group
	 * delay differential. It is a value in seconds.
	 */
	private short tgd_estimatedGroupDelayDifferentialSeconds;


	/**
	 * Issue of Data, Clock (iodc_issuedOfDataClock) indicates the issue number of the data set and
	 * combined the user with the means of conveniently detecting any change in the
	 * correction parameters,
	 */
	private int iodc_issuedOfDataClock;

	/**
	 * This is the reference time for the clock (secs of GPS week).
	 */
	private int toc_clockDataReferenceTimeSecondsOfGPSWeek;

	/** Satellite clock drift rate in seconds/(second^2). */
	private short af2_satelliteClockDriftRateSecondsPerSquareSecond;

	/** Satellite clock drift in seconds/second. */
	private int af1_satelliteClockDriftSecondsPerSecond;

	/** Satellite clock bias in seconds.*/
	private int af0_satelliteClockBiasSeconds;

	/**
	 * Issue of Data, Ephemeris (IODE) is a number that indicates the issue
	 * of ephemeris associates with this collection of Satellite data. The
	 * IODE changes every hour or so, as the new information is uploaded to
	 * the space segment from the tracking stations. The IODE is an 8 bit
	 * number.
	 */
	private short IODE;

	/**
	 * Amplitude of the Sine Harmonic Correction term to the Orbit Radius
	 * in metres.
	 */
	private int Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres;
	/** Mean Motion difference from Computed Value in radians/sec. */
	private int DeltaN_meanMotionDifferenceFromComputedValueRadiansPerSecond;

	private int DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond;


	/** Mean Anomaly at Reference Time in radians. */
	private double M0_meanAnomoloyAtReferenceTimeRadians;

	private long M0_meanAnomoloyAtReferenceTimeSemiCircles;

	/** Amplitude of the Cosine Harmonic Correction Term to the Argument of Latitude in radians. */
	private int Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians;
	/** Eccentricity. */
	private long e_eccentricity;
	/** Amplitude of the Sine Harmonic Correction Term to the Argument of Latitude in radians. */
	private int Cus_amplitudeSineHarmonicCorrectionLatitudeRadians;

	/** Square root of the semi major axis in square root meters. */
	private long sqrtA_squareRootOfSemiMajorAxisMetres;

	private double A_semiMajorAxisMetres;

	/** Reference time ephemeris in seconds of GPS week. */
	private int toe_timeOfEphemerisSecondsOfGPSWeek;

	/** 1 bit FIT Interval Flag from Subframe 2 of the ephemeris. */
	private short FIT_intervalFlag;
	/** 5 bit Age of Data Offset parameter from Subframe 2 of the ephemeris. */
	private short AODO_ageOfDataOffset;

	/** Amplitude of the Cosine Harmonic Correction Term to the Angle of inclination in radians. */
	private int Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians;

	/** Longitude of Ascending Node of Orbit Plane at Weekly Epoch in radians. */
	private double OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneRadians;

	private long OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles;

	/** Amplitude of the Sine Harmonic Correction Term to the Angle of inclination in radians. */
	private int Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians;

	/** Inclination Angle at reference time in radians. */
	private double i0_inclinationAngleAtReferenceTimeRadians;

	private long i0_inclinationAngleAtReferenceTimeSemiCircles;

	/** Amplitude of the Cosine Harmonic Correction Term to the Orbit Radius in metres. */
	private int Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres;

	/** Argument of gpsTimeForVelocityCalculation in radians. */
	private double omega_argumentOfPerigeeRadians;

	private long omega_argumentOfPerigeeSemiCircles;


	/** Rate of Right Ascension in radians/sec. */
	private double OMEGADOT_rightAscensionRadiansPerSecond;

	private int OMEGADOT_rightAscensionSemiCirclesPerSecond;

	/** Rate of Inclination Angle in radians/sec. */
	private double IDOT_rateOfInclinationAngleRadiansPerSecond;

	private short IDOT_rateOfInclinationAngleSemiCirclesPerSecond;

	/**
	 * A 2 bit number which Indicates which codes are commanded on for the L2 Channel.<p>
	 * 00 = Reserved<p>
	 * 01 = P code ON,<p>
	 * 10 = C/A code on.
	 * <p>
	 * NOTE that this parameter is not used by the triangulator.
	 */
	private byte codesOnL2Channel;

	/**
	 * A 1 bit number that indicates that the nav stream was commanded OFF on the P-code
	 * of the L2 channel.<p>
	 * NOTE that this parameter is not used by the triangulator.
	 */
	private byte L2PDataFlag;

//	Subframe 1 reserved data

	private int sf1ReservedData23Bit;

	private	int sf1ReservedData24ABit;

	private	int sf1ReservedData24BBit;

	private int sf1ReservedData16Bit;

	private double nmctTime;

//	TLM Word

	short tlmMessage;

	byte tlmReservedBits;

//	HOW Word

	int towCountMessage;

	byte alertFlag;

	byte antiSpoofFlag; 

	//raw subframe data

	/** Subframe 1-3 in hex with parity removed */

	String rawSubframeDataString;


	/**
	 * @param PRN
	 * @param GPSWeekNumberShort
	 * @param ura_predictedUserRangeAccuracyCode
	 * @param satelliteHealth
	 * @param tgd_estimatedGroupDelayDifferentialSeconds
	 * @param iodc_issuedOfDataClock
	 * @param toc_clockDataReferenceTimeSecondsOfGPSWeek
	 * @param af2_satelliteClockDriftRateSecondsPerSquareSecond
	 * @param af1_satelliteClockDriftSecondsPerSecond
	 * @param af0_satelliteClockBiasSeconds
	 * @param IODE
	 * @param Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres
	 * @param DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond
	 * @param M0_meanAnomoloyAtReferenceTimeSemiCircles
	 * @param Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians
	 * @param e_eccentricity
	 * @param Cus_amplitudeSineHarmonicCorrectionLatitudeRadians
	 * @param sqrtA_squareRootOfSemiMajorAxisMetres
	 * @param toe_timeOfEphemerisSecondsOfGPSWeek
	 * @param FIT_intervalFlag
	 * @param AODO_ageOfDataOffset
	 * @param Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians
	 * @param OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles
	 * @param Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians
	 * @param i0_inclinationAngleAtReferenceTimeSemiCircles
	 * @param Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres
	 * @param omega_argumentOfPerigeeSemiCircles
	 * @param OMEGADOT_rightAscensionSemiCirclesPerSecond
	 * @param IDOT_rateOfInclinationAngleSemiCirclesPerSecond
	 * @param codesOnL2Channel
	 * @param L2PDataFlag
	 * @param sf1ReservedData23Bit;
	 * @param sf1ReservedData24ABit;
	 * @param sf1ReservedData24BBit;
	 * @param sf1ReservedData16Bit;
	 * @param nmct time
	 * @param tlmMessage
	 * @param tlmReservedBits
	 * @param towCountMessage
	 * @param alertFlag
	 * @param antiSpoofFlag
	 * @throws IllegalArgumentException
	 */

	public SatelliteEphemeris
	(
			short PRN,
			short satID,

			// Satellite Health and Clock information

			short GPSWeekNumberShort,
			byte ura_predictedUserRangeAccuracyCode,
			int satelliteHealth,
			short tgd_estimatedGroupDelayDifferentialSeconds,
			int iodc_issuedOfDataClock,
			int toc_clockDataReferenceTimeSecondsOfGPSWeek,
			short af2_satelliteClockDriftRateSecondsPerSquareSecond,
			int af1_satelliteClockDriftSecondsPerSecond,
			int af0_satelliteClockBiasSeconds,

			// Satellite Ephemeris data

			short IODE,
			int Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres,
			int DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond,
			long M0_meanAnomoloyAtReferenceTimeSemiCircles,
			int Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians,
			long e_eccentricity,
			int Cus_amplitudeSineHarmonicCorrectionLatitudeRadians,
			long sqrtA_squareRootOfSemiMajorAxisMetres,
			int toe_timeOfEphemerisSecondsOfGPSWeek,
			byte FIT_intervalFlag,
			short AODO_ageOfDataOffset,
			int Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians,
			long OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles,
			int Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians,
			long i0_inclinationAngleAtReferenceTimeSemiCircles,
			int Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres,
			long omega_argumentOfPerigeeSemiCircles,
			int OMEGADOT_rightAscensionSemiCirclesPerSecond,
			short IDOT_rateOfInclinationAngleSemiCirclesPerSecond,

			int codesOnL2Channel,
			byte L2PDataFlag,
//			int logSequence,
			int sf1ReservedData23Bit,
			int sf1ReservedData24ABit,
			int sf1ReservedData24BBit,
			int sf1ReservedData16Bit,
			double nmctTime,
			short tlmMessage,
			byte tlmReservedBits,
			int towCountMessage,
			byte alertFlag,
			byte antiSpoofFlag,
			Date satelliteEphemerisTimestamp,
			String rawSubframeDataString

	) throws IllegalArgumentException
	{


//		setLogger(Logger.getLogger(thisClass));
//		this.logger.setLevel(DebugLogger.getOutputVerbosity());
//		DebugLogger.recordLogToFile(this.logger, ".PRN." +PRN);

//		this.logger.entering(thisClass, "Constructor");

//		this.logger.finer("SatelliteEphemeris.ctor entered method parameters: " +
//		"((PRN short)" + PRN +
//		", (GPSWeekNumber short)" + GPSWeekNumberShort +
//		", (URA byte)" + ura_predictedUserRangeAccuracyCode +
//		", Sat health " + satelliteHealth + ", " +
//		"  TGD seconds" + tgd_estimatedGroupDelayDifferentialSeconds +
//		", (IODC short)" + iodc_issuedOfDataClock +
//		", TOC " + toc_clockDataReferenceTimeSecondsOfGPSWeek +
//		", AF2 " + af2_satelliteClockDriftRateSecondsPerSquareSecond +
//		", AF1 " + af1_satelliteClockDriftSecondsPerSecond + ", " + af0_satelliteClockBiasSeconds +
//		", ( IODE short)" + IODE + ", " + Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres +
//		", deltaN(RADIANS/SEC)" + DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond * SCALE_SEMI_CIRCLES_TO_RADIANS +
//		", m0(RADIANS/SEC) " + M0_meanAnomoloyAtReferenceTimeSemiCircles  * SCALE_SEMI_CIRCLES_TO_RADIANS +
//		", Cuc " + Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians +
//		", eccentricity " + e_eccentricity +
//		", Cus" + Cus_amplitudeSineHarmonicCorrectionLatitudeRadians +
//		", sqrtA " + sqrtA_squareRootOfSemiMajorAxisMetres +
//		", toe " + toe_timeOfEphemerisSecondsOfGPSWeek +
//		", (FIT interval flag byte)" + FIT_intervalFlag +
//		", (AODO short)" + AODO_ageOfDataOffset +
//		", cic " + Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians +
//		", omega0 " + OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles +
//		", Cis " + Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians +
//		", i0 " + i0_inclinationAngleAtReferenceTimeSemiCircles +
//		", Crc " + Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres +
//		", omega_argumentofPerigee" + omega_argumentOfPerigeeSemiCircles +
//		", omegaDot " + OMEGADOT_rightAscensionSemiCirclesPerSecond +
//		", IDOT " + IDOT_rateOfInclinationAngleSemiCirclesPerSecond +
//		", (CODES ON L2 byte)" + codesOnL2Channel +
//		", (L2PDataFlag byte)" + L2PDataFlag +
//		", " + ")");

		this.PRN = PRN;
		this.satID = satID;
		this.gpsWeekNumberShort = GPSWeekNumberShort;
		this.ura_predictedUserRangeAccuracyCode = ura_predictedUserRangeAccuracyCode;
		this.satelliteHealth = satelliteHealth;
		this.tgd_estimatedGroupDelayDifferentialSeconds = tgd_estimatedGroupDelayDifferentialSeconds;
		this.iodc_issuedOfDataClock = iodc_issuedOfDataClock;
		this.toc_clockDataReferenceTimeSecondsOfGPSWeek = toc_clockDataReferenceTimeSecondsOfGPSWeek;
		this.af2_satelliteClockDriftRateSecondsPerSquareSecond = af2_satelliteClockDriftRateSecondsPerSquareSecond;
		this.af1_satelliteClockDriftSecondsPerSecond = af1_satelliteClockDriftSecondsPerSecond;
		this.af0_satelliteClockBiasSeconds = af0_satelliteClockBiasSeconds;
		this.IODE = IODE;
		this.Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres = Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres;

		this.DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond = DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond;

		this.M0_meanAnomoloyAtReferenceTimeSemiCircles= M0_meanAnomoloyAtReferenceTimeSemiCircles;

		this.Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians = Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians;
		this.e_eccentricity = e_eccentricity;
		this.Cus_amplitudeSineHarmonicCorrectionLatitudeRadians = Cus_amplitudeSineHarmonicCorrectionLatitudeRadians;

		this.sqrtA_squareRootOfSemiMajorAxisMetres = sqrtA_squareRootOfSemiMajorAxisMetres;

		this.toe_timeOfEphemerisSecondsOfGPSWeek = toe_timeOfEphemerisSecondsOfGPSWeek;
		this.FIT_intervalFlag = FIT_intervalFlag;
		this.AODO_ageOfDataOffset = AODO_ageOfDataOffset;
		this.Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians = Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians;

		this.OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles = OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles;

		this.Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians = Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians;

		this.i0_inclinationAngleAtReferenceTimeSemiCircles = i0_inclinationAngleAtReferenceTimeSemiCircles;

		this.Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres = Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres;

		this.omega_argumentOfPerigeeSemiCircles = omega_argumentOfPerigeeSemiCircles;

		this.OMEGADOT_rightAscensionSemiCirclesPerSecond = OMEGADOT_rightAscensionSemiCirclesPerSecond;

		this.IDOT_rateOfInclinationAngleSemiCirclesPerSecond = IDOT_rateOfInclinationAngleSemiCirclesPerSecond;

		this.sf1ReservedData23Bit = sf1ReservedData23Bit;

		this.sf1ReservedData24ABit = sf1ReservedData24ABit;

		this.sf1ReservedData24BBit = sf1ReservedData24BBit;

		this.sf1ReservedData16Bit = sf1ReservedData16Bit;

		this.nmctTime = nmctTime;

		this.tlmMessage = tlmMessage;

		this.tlmReservedBits = tlmReservedBits;

		this.towCountMessage = towCountMessage;

		this.satelliteEphemerisTimestamp = satelliteEphemerisTimestamp;

		this.rawSubframeDataString = rawSubframeDataString;

		checkRange();

	}


	public SatelliteEphemeris()
	{
		this.logger.setLevel(Level.SEVERE);
		this.logger.severe("SatelliteEphemeris.Constructor: DO NOT USE THIS CONSTRUCTOR");
	}

	/**
	 * This method extracts the ephemeris parameters from a rawgpsephem novatelLog
	 * @param novatelLog the novatel log
	 * @return this satellite's ephemeris
	 */

	/* Novatel OEM 4 raw subframe data structure (RAWEPHEMA log)::::
	 * 240bits (60 hex characters) 6x10 parity bits removed from original 300bit/subframe
	 *
	 * SUBFRAME1
	 * |BIT(s)|:              |PARAM:|
	 *
	 *          ----------- TLM  -----------
	 * 1-8(8)                 TLM PREAMBLE(HARDCODED)
	 * 9-22(14)               TLM MESSAGE
	 * 23-24(2)               Reserved bits
	 *          ----------- TLM  -----------
	 *
	 * 25-46(22)              HOW  x
	 * 47-48(2)               t
	 * 49-58(10)              WEEKNUMBER x
	 * 59-60(2)               C/A OR P ON L2
	 * 61-64(4)               URA INDEX
	 * 65-70(6)               SV HEALTH      MSB INDICATES THE HEALTH 0 = ALL NAV DATA OK 1 = SOME OR ALL NAV DATA DEAD
	 * 71-72(2)               IODC 2 MSBs
	 * 73(1)                  L2 P data flag
	 * 74-96(23)              23 Reserved Bits
	 * 97-120(24)             24 Reserved Bits
	 * 121-144(24)            24 Reserved Bits
	 * 145-160(16)            16 Reserved Bits
	 * 161-168(8)             TGD
	 * 169-176(8)             IODC 8 LSBs
	 * 177-192                toc
	 * 193-200(16)            AF2
	 * 201-216(16)            AF1
	 * 217-238(22)            AF0
	 * 239-240(2)             t
	 */


	/* (non-Javadoc)
	 * @see OSGRS.dataType.DataType#checkRange()
	 */
	public void checkRange() throws IllegalArgumentException
	{
		if (this.PRN < 0 || this.PRN > SatelliteEphemeris.PRN_MAXIMUM_VALUE)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The PRN number is invalid. 0 < PRN < " + SatelliteEphemeris.PRN_MAXIMUM_VALUE + " PRN: " + PRN);
		}

		// Satellite Health and Clock information

		if (this.gpsWeekNumberShort < SatelliteEphemeris.GPS_WEEK_NUMBER_NOT_SUPPLIED || this.gpsWeekNumberShort > SatelliteEphemeris.GPS_WEEK_NUMBER_MAX_VALUE)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The GPS week number is invalid. 0 <= Week No <= " + GPS_WEEK_NUMBER_MAX_VALUE + " gpsWeekNumber: " + gpsWeekNumber);
		}
		this.gpsWeekNumber = this.gpsWeekNumberShort;

		//If the week number is less than the maximum value then it is likely to have rolled over.
		//Lets set it to be 1000 so that we can use test data from 1999
		/*
        if (this.gpsWeekNumber < 1000 && this.gpsWeekNumber >= 0)
        {
            this.gpsWeekNumber += (SatelliteEphemeris.GPS_WEEK_NUMBER_MAX_VALUE + 1);
        }
		 */

		if (
				this.ura_predictedUserRangeAccuracyCode < 0 ||
				this.ura_predictedUserRangeAccuracyCode > SatelliteEphemeris.URA_CODE_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The URA code is invalid. ura_predictedUserRangeAccuracyCode: " + ura_predictedUserRangeAccuracyCode);
		}


		if (this.satelliteHealth < 0 || this.satelliteHealth > SatelliteEphemeris.SATELLITE_HEALTH_MAX_VALUE)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The Satellite health value is invalid. satelliteHealth: " + satelliteHealth);
		}

		if
		(
				this.tgd_estimatedGroupDelayDifferentialSeconds < GPS_TGD_FIELD_MIN_VALUE
				||
				this.tgd_estimatedGroupDelayDifferentialSeconds > GPS_TGD_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The Estimated group delay differential value is invalid. " + GPS_TGD_FIELD_MIN_VALUE + " < tgd_estimatedGroupDelayDifferentialSeconds < " + GPS_TGD_FIELD_MAX_VALUE + " tgd_estimatedGroupDelayDifferentialSeconds: " + tgd_estimatedGroupDelayDifferentialSeconds);
		}


		if (this.iodc_issuedOfDataClock < 0 || this.iodc_issuedOfDataClock > SatelliteEphemeris.IODC_MAXIMUM_VALUE)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The IODC value is invalid. iodc_issuedOfDataClock: " + iodc_issuedOfDataClock);
		}


		if (this.toc_clockDataReferenceTimeSecondsOfGPSWeek < 0 || this.toc_clockDataReferenceTimeSecondsOfGPSWeek > SatelliteEphemeris.GPS_TIME_MAXIMUM_VALUE)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The toc code is invalid. toc_clockDataReferenceTimeSecondsOfGPSWeek: " + this.toc_clockDataReferenceTimeSecondsOfGPSWeek);
		}


		if (
				this.af2_satelliteClockDriftRateSecondsPerSquareSecond < GPS_AF2_FIELD_MIN_VALUE ||
				this.af2_satelliteClockDriftRateSecondsPerSquareSecond > GPS_AF2_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set af2 is invalid. " + GPS_AF2_FIELD_MIN_VALUE + " <= af2 <= " + GPS_AF2_FIELD_MAX_VALUE + " af2: " + af2_satelliteClockDriftRateSecondsPerSquareSecond);
		}


		if (
				this.af1_satelliteClockDriftSecondsPerSecond < GPS_AF1_FIELD_MIN_VALUE ||
				this.af1_satelliteClockDriftSecondsPerSecond > GPS_AF1_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set af1 is invalid. " + GPS_AF1_FIELD_MIN_VALUE + " <= af1 <= " + GPS_AF1_FIELD_MAX_VALUE + " af1: " + af1_satelliteClockDriftSecondsPerSecond);
		}


		if (
				this.af0_satelliteClockBiasSeconds < GPS_AF0_FIELD_MIN_VALUE ||
				this.af0_satelliteClockBiasSeconds > GPS_AF0_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set af0 is invalid. " + GPS_AF0_FIELD_MIN_VALUE + " <= af0 <= " + GPS_AF0_FIELD_MAX_VALUE + " af0: " + af0_satelliteClockBiasSeconds);
		}


		// Satellite Ephemeris data

		if (this.IODE < SatelliteEphemeris.IODE_MINIMUM_VALUE || this.IODE > SatelliteEphemeris.IODE_MAXIMUM_VALUE)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The IODE is invalid. IODE: " + IODE);
		}


		if (
				this.Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres < GPS_CR_FIELD_MIN_VALUE ||
				this.Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres > GPS_CR_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set crs is invalid. " + GPS_CR_FIELD_MIN_VALUE + " <= crs <= " + GPS_CR_FIELD_MAX_VALUE + " crs: " + Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres);
		}


		if (
				this.DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond < GPS_DELTAN_FIELD_MIN_VALUE ||
				this.DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond > GPS_DELTAN_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set deltaN is invalid. " + GPS_DELTAN_FIELD_MIN_VALUE + " <= deltaN <= " + GPS_DELTAN_FIELD_MAX_VALUE + " deltaN: " + DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond);
		}
//		this.DeltaN_meanMotionDifferenceFromComputedValueRadiansPerSecond = this.DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond *
		//SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;

		if (
				this.M0_meanAnomoloyAtReferenceTimeSemiCircles < GPS_32BIT_31SF__FIELD_MIN_VALUE ||
				this.M0_meanAnomoloyAtReferenceTimeSemiCircles > GPS_32BIT_31SF__FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set m0 is invalid. " + GPS_32BIT_31SF__FIELD_MIN_VALUE + " <= m0 <= " + GPS_32BIT_31SF__FIELD_MAX_VALUE + " m0: " + M0_meanAnomoloyAtReferenceTimeSemiCircles);
		}
		this.M0_meanAnomoloyAtReferenceTimeRadians = this.M0_meanAnomoloyAtReferenceTimeSemiCircles *
		SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;

		if (
				this.Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians < GPS_C_FIELD_MIN_VALUE ||
				this.Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians > GPS_C_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set cuc is invalid. " + GPS_C_FIELD_MIN_VALUE + " <= cuc <= " + GPS_C_FIELD_MAX_VALUE + " cuc: " + Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians);
		}

		if (
				this.e_eccentricity < 0 ||
				this.e_eccentricity > GPS_E_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set e is invalid. 0 <= e <= " + GPS_E_FIELD_MAX_VALUE + " e: " + e_eccentricity);
		}

		if (
				this.Cus_amplitudeSineHarmonicCorrectionLatitudeRadians < GPS_C_FIELD_MIN_VALUE ||
				this.Cus_amplitudeSineHarmonicCorrectionLatitudeRadians > GPS_C_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set cus is invalid. " + GPS_C_FIELD_MIN_VALUE + " <= cus <= " + GPS_C_FIELD_MAX_VALUE + " cus: " + Cus_amplitudeSineHarmonicCorrectionLatitudeRadians);
		}

		if (
				this.sqrtA_squareRootOfSemiMajorAxisMetres < 0 ||
				this.sqrtA_squareRootOfSemiMajorAxisMetres > GPS_SQRTA_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set sqrtA is invalid. 0 <= sqrtA <= " + GPS_SQRTA_FIELD_MAX_VALUE + " sqrtA: " + sqrtA_squareRootOfSemiMajorAxisMetres);
		}

		if (this.toe_timeOfEphemerisSecondsOfGPSWeek > SatelliteEphemeris.GPS_TIME_MAXIMUM_VALUE)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The toe code is invalid. toe_timeOfEphemerisSecondsOfGPSWeek: " + toe_timeOfEphemerisSecondsOfGPSWeek);
		}
		if (
				this.Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians < GPS_C_FIELD_MIN_VALUE ||
				this.Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians > GPS_C_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set cic is invalid. " + GPS_C_FIELD_MIN_VALUE + " <= cic <= " + GPS_C_FIELD_MAX_VALUE + " cic: " + Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians);
		}

		if (
				this.OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles < GPS_32BIT_31SF__FIELD_MIN_VALUE ||
				this.OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles > GPS_32BIT_31SF__FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set Omega0 is invalid. " + GPS_32BIT_31SF__FIELD_MIN_VALUE + " <= Omega0 <= " + GPS_32BIT_31SF__FIELD_MAX_VALUE + " Omega0: " + OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles);
		}
		this.OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneRadians = this.OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles *
		SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;

		if (
				this.Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians < GPS_C_FIELD_MIN_VALUE ||
				this.Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians > GPS_C_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set cis is invalid. " + GPS_C_FIELD_MIN_VALUE + " <= cis <= " + GPS_C_FIELD_MAX_VALUE + " cis: " + Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians);
		}



		if (
				this.i0_inclinationAngleAtReferenceTimeSemiCircles < GPS_32BIT_31SF__FIELD_MIN_VALUE ||
				this.i0_inclinationAngleAtReferenceTimeSemiCircles > GPS_32BIT_31SF__FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set i0 is invalid. " + GPS_32BIT_31SF__FIELD_MIN_VALUE + " <= i0 <= " + GPS_32BIT_31SF__FIELD_MAX_VALUE + " i0: " + i0_inclinationAngleAtReferenceTimeSemiCircles);
		}
		this.i0_inclinationAngleAtReferenceTimeRadians = this.i0_inclinationAngleAtReferenceTimeSemiCircles *
		SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;


		if (
				this.Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres < GPS_CR_FIELD_MIN_VALUE ||
				this.Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres > GPS_CR_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set crc is invalid. " + GPS_CR_FIELD_MIN_VALUE + " <= crc <= " + GPS_CR_FIELD_MAX_VALUE + " crc: " + Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres);
		}

		if (
				this.omega_argumentOfPerigeeSemiCircles < GPS_32BIT_31SF__FIELD_MIN_VALUE ||
				this.omega_argumentOfPerigeeSemiCircles > GPS_32BIT_31SF__FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set omega is invalid. " + GPS_32BIT_31SF__FIELD_MIN_VALUE + " <= omega <= " + GPS_32BIT_31SF__FIELD_MAX_VALUE + " omega: " + omega_argumentOfPerigeeSemiCircles);
		}
		this.omega_argumentOfPerigeeRadians = this.omega_argumentOfPerigeeSemiCircles *
		SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;


		if (
				this.OMEGADOT_rightAscensionSemiCirclesPerSecond < GPS_OMEGADOT_FIELD_MIN_VALUE ||
				this.OMEGADOT_rightAscensionSemiCirclesPerSecond > GPS_OMEGADOT_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set omegaDot is invalid. " + GPS_OMEGADOT_FIELD_MIN_VALUE + " <= omegaDot <= " + GPS_OMEGADOT_FIELD_MAX_VALUE + " omegaDot: " + OMEGADOT_rightAscensionSemiCirclesPerSecond);
		}
		this.OMEGADOT_rightAscensionRadiansPerSecond = this.OMEGADOT_rightAscensionSemiCirclesPerSecond *
		SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;

		if (
				this.IDOT_rateOfInclinationAngleSemiCirclesPerSecond < GPS_IDOT_FIELD_MIN_VALUE ||
				this.IDOT_rateOfInclinationAngleSemiCirclesPerSecond > GPS_IDOT_FIELD_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set IDOT is invalid. " + GPS_IDOT_FIELD_MIN_VALUE + " <= IDOT <= " + GPS_IDOT_FIELD_MAX_VALUE + " IDOT: " + IDOT_rateOfInclinationAngleSemiCirclesPerSecond);
		}
		this.IDOT_rateOfInclinationAngleRadiansPerSecond = this.IDOT_rateOfInclinationAngleSemiCirclesPerSecond *
		SatelliteEphemeris.SCALE_SEMI_CIRCLES_TO_RADIANS;


		if (
				this.codesOnL2Channel < SatelliteEphemeris.CODES_ON_L2_MIN_VALUE ||
				this.codesOnL2Channel > SatelliteEphemeris.CODES_ON_L2_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The codesOnL2Channel code is invalid. codesOnL2Channel: " + codesOnL2Channel);
		}

		if (
				this.L2PDataFlag < SatelliteEphemeris.L2P_FLAG_MIN_VALUE ||
				this.L2PDataFlag > SatelliteEphemeris.L2P_FLAG_MAX_VALUE
		)
		{
			throw new IllegalArgumentException("SatelliteEphemeris.set The L2PDataFlag code is invalid. L2PDataFlag: " + L2PDataFlag);
		}


		// Derived fields

		this.satelliteHealthyBoolean = (this.satelliteHealth == 0);

		this.A_semiMajorAxisMetres = this.sqrtA_squareRootOfSemiMajorAxisMetres * this.sqrtA_squareRootOfSemiMajorAxisMetres;

		// The Computed mean motion in radians per second using the formula in the GPS signal spec
		this.n0_computedMeanMotionRadiansPerSecond = Math.sqrt
		(
				3.986005E+14
				/
				(this.A_semiMajorAxisMetres * this.A_semiMajorAxisMetres * this.A_semiMajorAxisMetres)
		);




//		// The Corrected mean motion in radians per second using the formula in the GPS signal spec
		this.n_correctedMeanMotionRadiansPerSecond = this.n0_computedMeanMotionRadiansPerSecond +
		this.DeltaN_meanMotionDifferenceFromComputedValueRadiansPerSecond;



		// User Range Accuracy (URA) code to metres
		this.URA_predictedUserRangeAccuracyMetres = SatelliteEphemeris.URA_CODE_TO_ACCURACY_METRES[this.ura_predictedUserRangeAccuracyCode];
	}

	/**
	 * Calculates the integer equivalent of a twos complement number
	 * @param twosComplementNumber the twos complement number
	 * @param numberOfBits the number of bits in the above number format
	 *
	 * @return the integer equivalent
	 */
	public static int twosComplementToInteger(long twosComplementNumber, int numberOfBits)
	{
		long bitMask = 0x01 << (numberOfBits - 1);
		long complementMask = (long)Math.pow(2, numberOfBits) - 1;


		int intValue = 0;
		if ((twosComplementNumber & bitMask) != 0)
		{
			//intValue = (int)((twosComplementNumber ^ complementMask) - 1) * -1; 
			intValue = (int)((twosComplementNumber ^ complementMask) + 1) * -1; //changed to plus
		}
		else
		{
			intValue = (int)twosComplementNumber;
		}


		return intValue;
	}

	public static long takeTwosComplement(long binaryNumber,int numberOfBits)
	{
		long workingLong;
		//System.out.println("binaryNumber: " + Long.toBinaryString(binaryNumber));
		workingLong = (~binaryNumber) & (long)(Math.pow(2, numberOfBits) - 1);
		//System.out.println("workingLong: " + Long.toBinaryString(workingLong));
		workingLong += 1;
		//System.out.println("workingLong: " + Long.toBinaryString(workingLong));

		return workingLong;
	}

	public static long putIntoTwosComplementForm(long number, int numberOfBits)
	{
		if(number >= 0)
		{
			//System.out.println("jhggh");

			return number;
		} else
		{
			return takeTwosComplement(Math.abs(number), numberOfBits);
		}
	}

	//========================================================================================================//

	//GET ACCESSORS

	/**
	 * This method returns whether or not the satellite is healthy. This field
	 * is initialised in the constructor depending on the value of the
	 * satelliteHealth parameter.
	 *
	 * @return whether or not the satellite is healthy.
	 */
	public boolean isSatelliteHealthy()
	{
		return this.satelliteHealthyBoolean;
		//return false;
	}

	/** Accessor for the af2_satelliteClockDriftRateSecondsPerSquareSecond. */
	public short getAf2()
	{
		return this.af2_satelliteClockDriftRateSecondsPerSquareSecond;
	}

	/** Accessor for the af1_satelliteClockDriftSecondsPerSecond. */
	public int getAf1()
	{
		return this.af1_satelliteClockDriftSecondsPerSecond;
	}

	/** Accessor for the af0_satelliteClockBiasSeconds. */
	public int getAf0()
	{
		return this.af0_satelliteClockBiasSeconds;
	}

	public short getPRN()
	{
		return this.PRN;
	}

	/** Accessor for the iodc_issuedOfDataClock. */
	public int getIODC()
	{
		return this.iodc_issuedOfDataClock;
	}

	/** Accessor for the toc_clockDataReferenceTimeSecondsOfGPSWeek. */
	public int getToc()
	{
		return this.toc_clockDataReferenceTimeSecondsOfGPSWeek;
	}

	/** Accessor for the TGD */
	public short getTGD()
	{
		return this.tgd_estimatedGroupDelayDifferentialSeconds;
	}

	/** Accessor for the Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres. */
	public int getCrs()
	{
		return this.Crs_amplitudeSineHarmonicCorrectionOrbitRadiusMetres;
	}

	public int getgetDeltaNSemiCicleRaw()
	{
		return this.DeltaN_meanMotionDifferenceFromComputedValueRadiansPerSecond;
	}


	/*
    /** Accessor for the DeltaN_meanMotionDifferenceFromComputedValueRadiansPerSecond. */
	/*    public double getDeltaNSemiCiclePerSec()
    {
        return this.DeltaN_meanMotionDifferenceFromComputedValueRadiansPerSecond * SCALE_RADIANS_TO_SEMI_CIRCLES;
    }*/

	/** Accessor for the M0 in semi circles. */
	public double getM0SemiCircles()
	{
		return this.M0_meanAnomoloyAtReferenceTimeRadians * SCALE_RADIANS_TO_SEMI_CIRCLES;
	}

	/** Accessor for the Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians. */
	public int getCucUnscaled()
	{
		return this.Cuc_amplitudeCosineHarmonicCorrectionLatitudeRadians;
	}

	/** Accessor for the e_eccentricity. */
	public long getE()
	{
		return this.e_eccentricity;
	}

	/** Accessor for the Cus_amplitudeSineHarmonicCorrectionLatitudeRadians. */
	public int getCus()
	{
		return this.Cus_amplitudeSineHarmonicCorrectionLatitudeRadians;
	}

	/** Accessor for the sqrtA_squareRootOfSemiMajorAxisMetres. */
	public long getSqrtA()
	{
		return this.sqrtA_squareRootOfSemiMajorAxisMetres;
	}

	/** Accessor for the toe_timeOfEphemerisSecondsOfGPSWeek. */
	public int getToe()
	{
		return this.toe_timeOfEphemerisSecondsOfGPSWeek;
	}

	/** Accessor for the FIT_intervalFlag. */
	public short getFIT()
	{
		return this.FIT_intervalFlag;
	}

	/** Acessor for the AODO_ageOfDataOffset. */
	public short getAODO()
	{
		return this.AODO_ageOfDataOffset;
	}

	/** Accessor for the Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians. */
	public int getCic()
	{
		return this.Cic_amplitudeCosineHarmonicCorrectionAngleInclinationRadians;
	}

	/** Accessor for the OMEGA0 in semi circles. */
	public long getOMEGA0SemiCirclesUnscaled()
	{
		return this.OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneSemiCircles;//OMEGA0_longitudeAscendingNodeOrbitPlaneAtWeeklyOrbitPlaneRadians * SCALE_RADIANS_TO_SEMI_CIRCLES;
	}

	/** Accessor for the Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians. */
	public int
	getCis()
	{
		return this.Cis_amplitudeSineHarmonicCorrectionAngleInclinationRadians;
	}

	/** Accessor for the I0 in semi circles. */
	public long getI0SemiCircles()
	{
		return this.i0_inclinationAngleAtReferenceTimeSemiCircles;//.i0_inclinationAngleAtReferenceTimeRadians * SCALE_RADIANS_TO_SEMI_CIRCLES;
	}

	/** Accessor for the Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres. */
	public int getCrc()
	{
		return this.Crc_amplitudeCosineHamonicCorrectionTermOrbitRadiusMetres;
	}

	/** Accessor for the omega in semi circles. */
	public long getOmegaSemiCircles()
	{
		return this.omega_argumentOfPerigeeSemiCircles;
	}

	/** Accessor for the OMEGADot in semi circles/sec. */
	public int getOMEGADotSemiCirclesUnscaled()
	{
		return this.OMEGADOT_rightAscensionSemiCirclesPerSecond;//.OMEGADOT_rightAscensionRadiansPerSecond * SCALE_RADIANS_TO_SEMI_CIRCLES;
	}

	/** Accessor for the idot in semi circles/sec. */
	public short getIdotSemiCircles()
	{
		return this.IDOT_rateOfInclinationAngleSemiCirclesPerSecond;//IDOT_rateOfInclinationAngleRadiansPerSecond * SCALE_RADIANS_TO_SEMI_CIRCLES;
	}

	/** Accessor for the codesOnL2Channel. */
	public byte getCodesOnL2Channel()
	{
		return this.codesOnL2Channel;
	}

	/** Accessor for the L2PDataFlag. */
	public byte getL2DataFlag()
	{
		return this.L2PDataFlag;
	}

	/**
	 * Accessor for the reference time of clock toc.
	 *
	 * @return toc_clockDataReferenceTimeSecondsOfGPSWeek.
	 */
	public double getReferenceTimeOfClock()
	{
		return this.toc_clockDataReferenceTimeSecondsOfGPSWeek;
	}


	/**
	 * @return the sqrtA_squareRootOfSemiMajorAxisMetres
	 */
	public long getSqrtA_squareRootOfSemiMajorAxisMetres()
	{
		return this.sqrtA_squareRootOfSemiMajorAxisMetres;
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

	/** Accessor for the URA Code. */
	public byte getURACode()
	{
		return this.ura_predictedUserRangeAccuracyCode;
	}


	/**
	 * @return the nmctTime
	 */
	public final double getNmctTime()
	{
		return this.nmctTime;
	}

	public Date getSatelliteEphemerisTimestamp()
	{
		return this.satelliteEphemerisTimestamp;
	}


	public short getSatID() 
	{
		return this.satID;
	}


	public String getRawSubframeDataString() 
	{
		return this.rawSubframeDataString;
	}


	public long getM0_meanAnomoloyAtReferenceTimeSemiCircles() 
	{
		return this.M0_meanAnomoloyAtReferenceTimeSemiCircles;
	}


	public int getDeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond() 
	{
		return this.DeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond;
	}

}
