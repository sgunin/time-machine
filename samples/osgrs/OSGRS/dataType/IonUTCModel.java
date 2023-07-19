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

import java.util.logging.*;
import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;
import java.util.Date;

/* (non-Javadoc)
 * @see OSGRS.dataType.DataType#checkRange()
 */

/**
 * This class is the GNSS UTC data.
 * It is used to decode the UTC data from the receiver and encode it into GNSS format.
 *	@author Manosh Fernando	and Nam Hoang
 */

public class IonUTCModel implements DataType
{

	private Date ionUTCTimestamp;

	/** The logger used for debug for this IonUTCModel instance */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/**
	 * The minimum value of the A0 field.
	 * This is the minimum 2's complement value by the scale factor for the A0 field
	 */
	private final static double GPS_A0_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,32);

	/**
	 * The maximum value of the A0 field.
	 * This is the maximum 2's complement value by the scale factor for the A0 field
	 */
	private final static double GPS_A0_FIELD_MAX_VALUE = (Math.pow(2,32) - 1);

	/**
	 * The minimum value of the A1 field.
	 * This is the minimum 2's complement value by the scale factor for the A1 field
	 */
	private final static double GPS_A1_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,24);

	/**
	 * The maximum value of the A1 field.
	 * This is the maximum 2's complement value by the scale factor for the A1 field
	 */
	private final static double GPS_A1_FIELD_MAX_VALUE = (Math.pow(2,24) - 1);

	/** The maxiumum value for range checking for the 8 bit fields, dtLS, WNt,WNlsf. */
	private final static short GPS_8_BIT_FIELD_MAX_VALUE = (short)(Math.pow(2,8) - 1);

	/** The maxiumum value for range checking for the tot field (reference time for UTC data). */
	private final static short GPS_TOT_FIELD_MAX_VALUE = (short)(602112.0/Math.pow(2,12));

	/** The maxiumum value for range checking for the DN field. */
	private final static short GPS_DN_FIELD_MIN_VALUE = 1;

	/** The maxiumum value for range checking for the DN field. */
	private final static short GPS_DN_FIELD_MAX_VALUE = 7;

	/** The number of seconds in a day. */
	private final static int NUMBER_OF_SECONDS_IN_A_DAY = 60 * 60 * 24;

	/** The number of seconds in 3/4 of a day. */
	private final static int THREE_QUARTERS_OF_A_DAY = (int)(3.0 / 4 * NUMBER_OF_SECONDS_IN_A_DAY);

	/** The number of seconds in 5/4 of a day. */
	private final static int FIVE_QUARTERS_OF_A_DAY = (int)(5.0 / 4 * NUMBER_OF_SECONDS_IN_A_DAY);


	//TODO: make the limtis and stuff for the rest of the Ionoshere

	/** The alpha coefficient alpha0 in seconds. The range is approximately + - 120E-09. */
	private short alpha0_constant_term;

	/** The alpha coefficient alpha1 in seconds/semicircle. The range is approximately + - 954E-09. */
	private short alpha1_first_order_term;

	/** The alpha coefficient alpha2 in seconds/semicircles<sup>2</sup>. The range is approximately + - 7.6E-06. */
	private short alpha2_second_order_term;

	/** The alpha coefficient alpha3 in seconds/semicircles<sup>3</sup>. The range is approximately + - 7.6E-06. */
	private short alpha3_third_order_term;

	/** The beta coefficient beta0 in seconds. The range is approximately + - 260000. */
	private short beta0_constant_term;

	/** The beta coefficient beta1 in seconds/semicircles. The range is approximately + - 2100000. */
	private short beta1_first_order_term;

	/** The beta coefficient beta2 in seconds/semicircles<sup>2</sup>. The range is approximately + - 8400000. */
	private short beta2_second_order_term;

	/** The beta coefficient beta3 in seconds/semicircles<sup>3</sup>. The range is approximately + - 8400000. */
	private short beta3_third_order_term;

	//### UTC PARAMS STUFF #########

	/** A0 in seconds. This is one of the first order terms of the polymonial. */
	private long A0_second_alpha_constant_term;

	/** A1 in seconds/second. This is one of the first order terms of the polymonial. */
	private int A1_second_alpha_first_order_term;

	/** dtLS in seconds. */
	private short dtLS;

	/** tot in seconds. This is the reference time for the UTC data. */
	private short tot;

	/** WNt in weeks. This is the UTC reference week number in weeks. */
	private short WNt;

	/**
	 * WNlsf this is the eight LBSs of the full week number when the
	 * leap second becomes effective in conjunction with the DN.
	 */
	private short WNlsf;

	/**
	 * DN is the day number after which the leap seconds becomes
	 * effective in conjunction with the WNlsf.
	 */
	private short DN;

	/**
	 * dtLSF in seconds is the value of the delta time due to leap seconds.
	 */
	private short dtLSF;


	/**
	 * @param a0
	 * @param a1
	 * @param dtLS
	 * @param tot
	 * @param WNt
	 * @param WNlsf
	 * @param DN
	 * @param dtLSF
	 */
	public IonUTCModel(
			//IONOSPHERIC PARAMS

			short alpha0_constant_term,
			short alpha1_first_order_term,
			short alpha2_second_order_term,
			short alpha3_third_order_term,
			short beta0_constant_term,
			short beta1_first_order_term,
			short beta2_second_order_term,
			short beta3_third_order_term,

			//UTC PARAMS
			short WNt,
			short tot,
			long A0_second_alpha_constant_term,
			int A1_second_alpha_first_order_term,
			short WNlsf,
			short DN,
			short dtLS,
			short dtLSF,
			Date ionUTCTimestamp)

	{


		setLogger(Logger.getLogger(this.thisClass + "@" + System.currentTimeMillis()));
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		//DebugLogger.recordLogToFile(this.logger); 

		this.logger.entering(thisClass, "Constructor");

		this.alpha0_constant_term = alpha0_constant_term;
		this.alpha1_first_order_term = alpha1_first_order_term;
		this.alpha2_second_order_term = alpha2_second_order_term;
		this.alpha3_third_order_term = alpha3_third_order_term;
		this.beta0_constant_term = beta0_constant_term;
		this.beta1_first_order_term = beta1_first_order_term;
		this.beta2_second_order_term = beta2_second_order_term;
		this.beta3_third_order_term = beta3_third_order_term;

		this.A0_second_alpha_constant_term = A0_second_alpha_constant_term;
		this.A1_second_alpha_first_order_term = A1_second_alpha_first_order_term;
		this.dtLS = dtLS;
		this.tot = tot;
		this.WNt = WNt;
		this.WNlsf = WNlsf;
		this.DN = DN;
		this.dtLSF = dtLSF;
		this.ionUTCTimestamp = ionUTCTimestamp;

		checkRange();

		this.logger.exiting(thisClass, "Constructor");

	}

	/**
	 *  Dummy constructor for UTCModel
	 */
	public IonUTCModel()
	{
		//do nothing
	}


	public void checkRange() throws IllegalArgumentException
	{
		if(this.alpha0_constant_term < 0 || this.alpha0_constant_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("alpha0_constant_term is outside acceptable range of values."
					+ " alpha0_constant_term: " + this.alpha0_constant_term);
		}

		if(this.alpha1_first_order_term < 0 || this.alpha1_first_order_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("alpha1_first_order_term is outside acceptable range of values."
					+ " alpha1_first_order_term: " + this.alpha1_first_order_term);
		}

		if(this.alpha2_second_order_term < 0 || this.alpha2_second_order_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("alpha2_second_order_term is outside acceptable range of values."
					+ " alpha2_second_order_term: " + this.alpha2_second_order_term);
		}

		if(this.alpha3_third_order_term < 0 || this.alpha3_third_order_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("alpha3_third_order_term is outside acceptable range of values."
					+ " alpha3_third_order_term: " + this.alpha3_third_order_term);
		}

		if(this.beta0_constant_term < 0 || this.beta0_constant_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("beta0_constant_term is outside acceptable range of values."
					+ " beta0_constant_term: " + this.beta0_constant_term);
		}

		if(this.beta1_first_order_term < 0 || this.beta1_first_order_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("beta1_first_order_term is outside acceptable range of values."
					+ " beta1_first_order_term: " + this.beta1_first_order_term);
		}

		if(this.beta2_second_order_term < 0 || this.beta2_second_order_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("beta2_second_order_term is outside acceptable range of values."
					+ " beta2_second_order_term: " + this.beta2_second_order_term);
		}

		if(this.beta3_third_order_term < 0 || this.beta3_third_order_term > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("beta3_third_order_term is outside acceptable range of values."
					+ " beta3_third_order_term: " + this.beta3_third_order_term);
		}

		if (this.A0_second_alpha_constant_term < 0 || this.A0_second_alpha_constant_term > GPS_A0_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set a0 is outside the range of valid values " 
					+ GPS_A0_FIELD_MIN_VALUE + " <= a0 <= " + GPS_A0_FIELD_MAX_VALUE + ". a0: " 
					+ this.A0_second_alpha_constant_term);
		}

		if (this.A1_second_alpha_first_order_term < 0 || this.A1_second_alpha_first_order_term> GPS_A1_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set a1 is outside the range of valid values " 
					+ GPS_A1_FIELD_MIN_VALUE + " <= a1 <= " + GPS_A1_FIELD_MAX_VALUE + ". a1: " 
					+ this.A1_second_alpha_first_order_term);
		}

		if (this.dtLS < 0 || this.dtLS > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set dtLS is outside the range of valid values 0 <= dtLS <= " 
					+ GPS_8_BIT_FIELD_MAX_VALUE + ". dtLS: " + this.dtLS);
		}

		if (this.tot < 0 || this.tot > GPS_TOT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set tot is outside the range of valid values 0 <= tot <= " 
					+ GPS_TOT_FIELD_MAX_VALUE + ". tot: " + tot);
		}

		if (this.WNt < 0 || this.WNt > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set WNt is outside the range of valid values 0 <= WNt <= " 
					+ GPS_8_BIT_FIELD_MAX_VALUE + ". WNt: " + WNt);
		}

		if (this.WNlsf < 0 || this.WNlsf > GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set WNlsf is outside the range of valid values 0 <= WNlsf <= " 
					+ GPS_8_BIT_FIELD_MAX_VALUE + ". WNlsf: " + WNlsf);
		}

		if (this.DN < GPS_DN_FIELD_MIN_VALUE || this.DN > GPS_DN_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set DN is outside the range of valid values 1 <= DN <= " 
					+ GPS_DN_FIELD_MAX_VALUE + ". DN: " + DN);
		}

		if (this.dtLSF < 0 || this.dtLSF >GPS_8_BIT_FIELD_MAX_VALUE)
		{
			throw new IllegalArgumentException("UTCModel.set dtLSF is outside the range of valid values 0 <= dtLSF <= " 
					+ this.GPS_8_BIT_FIELD_MAX_VALUE + ". dtLSF: " + dtLSF);
		}

		if(this.ionUTCTimestamp == null)
		{
			throw new IllegalArgumentException("date object passed is null");
		}

	}

	/**
	 * This method is used to calculate the current UTC offset
	 * in seconds from GPS time. To convert to GPS time, subtract
	 * the value returned by this method from UTC time.
	 * <p>
	 * @param dateTime this is the GPS date time.
	 *
	 * @return the number of seconds that the UTC time scale is
	 *          <b>ahead</b> of the GPS time scale.
	 */
	public double getUTCOffset(GPSDateTime dateTime)
	{
		//assert DebugLog.verbose("UTCModel.getUTCOffset(dateTime) entered dateTime: " + dateTime.toString());

		return this.getUTCOffset(dateTime.getGpsWeekNumber(), dateTime.getGpsSecondsOfWeek());
	}



	/**
	 * This method is used to calculate the current UTC offset
	 * in seconds from GPS time. To convert to GPS time, subtract
	 * the value returned by this method from UTC time.
	 * <p>
	 * @param gpsDateTimeInMilliseconds the approximate gps date time
	 *               in milliseconds.
	 *
	 * @return the number of seconds that the UTC time scale is
	 *          <b>ahead</b> of the GPS time scale.
	 */
	public double getUTCOffset(long gpsDateTimeInMilliseconds)
	{
		//assert DebugLog.verbose("UTCModel.getUTCOffset(gpsDateTimeInMilliseconds) entered gpsDateTimeInMilliseconds: " + gpsDateTimeInMilliseconds);
		return this.getUTCOffset(
				GPSDateTime.calculateGPSWeekNumber(gpsDateTimeInMilliseconds),
				GPSDateTime.calculateGPSSecondsOfWeek((double)gpsDateTimeInMilliseconds)
		);
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

	public double getUTCOffset(int gpsWeekNumber, double gpsTimeOfWeekSeconds)
	{
//		Logger logger = Logger.getLogger("getUTCOffset method. for ionmodel" );

//		logger.setLevel(Level.FINE);
//		//System.out.println(logger.getLevel());
//		DebugLogger.recordLogToFile(logger);



		this.logger.entering(thisClass, "getUTCOffset");


		//reverse DN to usuable form

		short leftJustifiedDN = (short)(Integer.reverse(this.DN)>>>24);

		this.logger.fine("leftJustifiedDN: " + leftJustifiedDN);


		int DNinSeconds = (this.DN - 1) * NUMBER_OF_SECONDS_IN_A_DAY;

		//Calculate the complete UTC week fields. This is the WN field plus the MSBs of the gps week
		//See section 2.5.6 of the GPSSS.
		int upperBitsOfGpsWeekNumber = gpsWeekNumber - (gpsWeekNumber & 0xFF);

		//The UTC reference week
		int fullUTCreferenceWeek = upperBitsOfGpsWeekNumber + this.WNt;

		//The full WN lsf
		int fullWNlsf = upperBitsOfGpsWeekNumber + this.WNlsf;
		this.logger.fine("UTCModel.getUTCOffset(wk,secs) fullWNlsf: " + fullWNlsf);

		double dtUTC;

		//Condition a. from the GPSSpec
		//When the leap second effectivity time is not in the past AND
		//the users present time is not between DN + 3/4 and DN + 5/4
		if (
				(fullWNlsf >= gpsWeekNumber && DNinSeconds >= gpsTimeOfWeekSeconds) &&
				(gpsTimeOfWeekSeconds < (DNinSeconds + THREE_QUARTERS_OF_A_DAY) || gpsTimeOfWeekSeconds > (DNinSeconds + FIVE_QUARTERS_OF_A_DAY))
		)
		{
			this.logger.fine("UTCModel.getUTCOffset(wk,secs) condition a, LS is in the future and not between 3/4 and 5/4");
			dtUTC = GNSSUtil.twosComplementToInteger(this.dtLS, 8) + 
			(GNSSUtil.twosComplementToInteger(this.A0_second_alpha_constant_term, 32)*Math.pow(2, -30)) 
			+ (GNSSUtil.twosComplementToInteger(this.A1_second_alpha_first_order_term, 24)*Math.pow(2, -50)) 
			* (gpsTimeOfWeekSeconds - (this.tot * Math.pow(2, 12)) + 604800 * (gpsWeekNumber - fullUTCreferenceWeek));
		}
		else
		{
			//Condition b, from the GPSSpec
			//When the current time falls between the period of DN + 3/4 and
			//DN + 5/4 then we need to accommodate the leap second roll over
			if(
					fullWNlsf == gpsWeekNumber &&
					gpsTimeOfWeekSeconds >= (DNinSeconds + THREE_QUARTERS_OF_A_DAY) &&
					gpsTimeOfWeekSeconds <= (DNinSeconds + FIVE_QUARTERS_OF_A_DAY)
			)
			{
				dtUTC = GNSSUtil.twosComplementToInteger(this.dtLS,8) + (GNSSUtil.twosComplementToInteger(this.A0_second_alpha_constant_term, 32)*Math.pow(2, -30)) 
				+ (GNSSUtil.twosComplementToInteger(this.A1_second_alpha_first_order_term, 24)*Math.pow(2, -50)) 
				* (gpsTimeOfWeekSeconds - (this.tot * Math.pow(2, 12)) + 604800 * (gpsWeekNumber - fullUTCreferenceWeek));
				this.logger.fine("UTCModel.getUTCOffset(wk,secs) dtUTC: " + dtUTC);

				double W = this.doubleMod(gpsTimeOfWeekSeconds - dtUTC - 43200, 86400) + 43200;
				this.logger.fine("UTCModel.getUTCOffset(wk,secs) W: " + W);
				double utc = this.doubleMod(W, 86400 + GNSSUtil.takeTwosComplement(this.dtLSF,8) 
						- GNSSUtil.twosComplementToInteger(this.dtLS,8));
				this.logger.fine("UTCModel.getUTCOffset(wk,secs) utc: " + utc);

				dtUTC = this.doubleMod(gpsTimeOfWeekSeconds - utc, NUMBER_OF_SECONDS_IN_A_DAY);
			}
			else
			{
				//Condition c. from the GPSSpec
				//When the time of the effectivity of the LS is in the past then we use the same
				//equation as in condition a but substitude dtLsf for dtls
				dtUTC = (GNSSUtil.twosComplementToInteger(this.dtLSF, 8)) + (GNSSUtil.twosComplementToInteger(this.A0_second_alpha_constant_term, 32) * Math.pow(2, -30)) 
				+ (GNSSUtil.twosComplementToInteger(this.A1_second_alpha_first_order_term, 24) * Math.pow(2, -50)) 
				* (gpsTimeOfWeekSeconds - (this.tot * Math.pow(2, 12)) + 604800 * (gpsWeekNumber - fullUTCreferenceWeek));
			}

		}

		this.logger.fine("UTCModel.getUTCOffset(wk,secs) dtUTC: " + dtUTC);

		this.logger.exiting(thisClass, "getUTCOffset");

		return dtUTC;

//		return 0.0;

	}

	/**
	 * Outputs information about this class in String format.
	 *
	 * @return a string that represents this instance of an
	 *              UTCModel.
	 */
	public String toString()
	{
		return "a0: " + this.A0_second_alpha_constant_term +
		" a1: " + this.A1_second_alpha_first_order_term +
		" dtLS: " + this.dtLS +
		" tot: " + this.tot +
		" WNt: " + this.WNt +
		" WNlsf: " + this.WNlsf +
		" DN: " + this.DN +
		" dtLSF: " + this.dtLSF;
	}

	public String getParametersString()
	{
		String parametersString = "Ionospere Model - " + "alpha_0: " + this.alpha0_constant_term + " alpha_1: " 
		+ this.alpha1_first_order_term + " alpha_2: " + this.alpha2_second_order_term + " alpha_3: " 
		+ this.alpha3_third_order_term + " beta_0: " + this.beta0_constant_term + " beta_1: " 
		+ this.beta1_first_order_term + " beta_2: " + this.beta2_second_order_term + " beta_3: " 
		+ this.beta3_third_order_term + " UTC Model - " + "A1: " + this.A1_second_alpha_first_order_term + " A0: "
		+ this.A0_second_alpha_constant_term + " t_ot: " + this.tot + " WN_t: " + this.WNt + " dt_LS: "+ this.dtLS 
		+ " WN_LSF: " + this.WNlsf + " DN: " + this.DN + " dt_LSF: " + this.dtLSF;

		return parametersString;
	}


	//get accessors

	/** accessor for getAlpha0_constant_term */
	public short getAlpha0_constant_term()
	{
		return this.alpha0_constant_term;
	}

	/** accessor for alpha_first_order_term */
	public short getAlpha1_first_order_term()
	{
		return this.alpha1_first_order_term;
	}

	/**acsessor for alpha2_second_order_term */
	public short getAlpha2_second_order_term()
	{
		return this.alpha2_second_order_term;
	}

	/**Accessor for alpha3_third_order_term */
	public short getAlpha3_third_order_term()
	{
		return this.alpha3_third_order_term;
	}

	/**Accessor for beta0_constant_term */
	public short getBeta0_constant_term()
	{
		return this.beta0_constant_term;
	}

	/**Accessor for beta1_first_order_term */
	public short getBeta1_first_order_term()
	{
		return this.beta1_first_order_term;
	}

	/**Accessor for beta2_second_order_term */
	public short getBeta2_first_order_term()
	{
		return this.beta2_second_order_term;
	}

	/**Accessor for beta3_third_order_term */
	public short getBeta3_third_order_term()
	{
		return this.beta3_third_order_term;
	}

	/**Accessor for A0_second_alpha_constant_term */
	public long getA0_second_alpha_term()
	{
		return this.A0_second_alpha_constant_term;
	}

	/**Accessor for A1_second_alpha_first_order_term */
	public long getA1_second_alpha_first_order_term()
	{
		return this.A1_second_alpha_first_order_term;
	}

	/**Accessor for dtLS */
	public short getDtLS()
	{
		return this.dtLS;
	}

	/**Accessor for tot */
	public short getTot()
	{
		return this.tot;
	}

	/** Accessor for WNt */
	public short getWNt()
	{
		return this.WNt;
	}

	/** Accessor for WNlsf */
	public short getWNlsf()
	{
		return this.WNlsf;
	}

	/** Accessor for DN */
	public short getDN()
	{
		return this.DN;
	}

	/** Accessor for dtLSF */
	public short getdtLSF()
	{
		return this.dtLSF;
	}

	/**
	 * same as mod operator but designed to handle floating point inputs
	 * @param value
	 * @param mod
	 * @return
	 */

	private double doubleMod(double value, double mod)
	{
		int numberOfFullValues = (int)(value / mod);
		return (value - (mod * numberOfFullValues));
	}

	public Date getIonUTCTimestamp()
	{
		return this.ionUTCTimestamp;
	}

}
