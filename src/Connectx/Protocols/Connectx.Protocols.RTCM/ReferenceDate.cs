namespace Connectx.Protocols.RTCM
{
	/// <summary>
	/// Time References in GNSS
	/// https://gssc.esa.int/navipedia/index.php/Time_References_in_GNSS
	/// GNSS and time https://www.shalen.dev/blog/gnss-time
	/// </summary>
	internal static class ReferenceDate
  {
		/// <summary>
		/// GPS Time (GPST) is a continuous time scale (no leap seconds) defined by the GPS Control segment on the basis of a set of atomic clocks at the Monitor Stations and onboard the satellites.
		/// It starts at 0h UTC (midnight) of January 5th to 6th 1980 (6.d0). At that epoch, the difference TAI−UTC was 19 seconds, thence GPS−UTC=n − 19s.
		/// GPS time is synchronised with the UTC(USNO) at 1 microsecond level (modulo one second), but actually is kept within 25 ns.
		/// Stanart time format: UTC(USNO)
		/// UTC = GPS +/- leap seconds
		/// </summary>
		public static DateTime GPSReferenceDate = new(1980, 1, 6, 0, 0, 0, DateTimeKind.Utc);

		/// <summary>
		/// Galileo System Time (GST) is a continuous time scale maintained by the Galileo Central Segment and synchronised with TAI with a nominal offset below 50 ns.
		/// The GST start epoch, GST(T0), is defined 13 seconds before 0:00:00 UTC on Sunday, 22 August 1999 (midnight between 21 and 22 August)
		/// Stanart time format: TAI
		/// UTC = GST + dSeconds
		/// </summary>
		public static DateTime GalileoReferenceDate = new(1999, 8, 22, 0, 0, 0, DateTimeKind.Utc);

		/// <summary>
		/// BeiDou Time (BDT) is a continuous time scale starting at 0h UTC on January 1st, 2006 and is synchronised with UTC within 100 ns< (modulo one second).
		/// Stanart time format: UTC(NTSC)
		/// BDT = UTC (NTSC)
		/// </summary>
		public static DateTime BeiDouReferenceDate = new(2006, 1, 1, 0, 0, 0, DateTimeKind.Utc);

		/// <summary>
		/// GLONASS Time (GLONASST) is generated by the GLONASS Central Synchroniser and the difference between the UTC(SU) and GLONASST should not exceed 1 millisecond plus three hours
		/// (i.e.,GLONASST=UTC(SU)+3h−τ, where |τ|<1milisec.), but τ is typically better than 1 microsecond.
		/// Note: Unlike GPS, Galileo or BeiDou, GLONASS time scale implements leap seconds, like UTC.
		/// Maximum offset (modulo 1s) from reference timescale: 4 ns (probability 0.95)
		/// Stanart time format: UTC(SU)
		/// GLONASS time = UTC(SU) + 3h
		/// </summary>
		public static DateTime GLONASSReferenceDate = new(0, DateTimeKind.Utc);

		/// <summary>
		/// Leap seconds for datetime => 2017-01-01
		/// https://cdf.gsfc.nasa.gov/html/CDFLeapSeconds.txt
		/// </summary>
		public static byte CURRENT_LEAP_SECONDS = 37;

		/// <summary>
		/// Convert UTC to GPS (UTC(USNO)) datetime
		/// </summary>
		/// <param name="utcDateTime">UTC datetime</param>
		/// <returns>GPS (UTC(USNO)) datetime</returns>
		/// <exception cref="ArgumentException">Exception, if UTC datetime < 2017-01-01</exception>
		public static DateTime ConvertUtcToGps(DateTime utcDateTime)
		{
			// Check for current leap second use
			if (utcDateTime < new DateTime(2017, 1, 1))
				throw new ArgumentException("utcDateTime must be > 2017-01-01");

			return utcDateTime.AddSeconds(CURRENT_LEAP_SECONDS);
		}

		/// <summary>
		/// Convert GPS (UTC(USNO)) to UTC datetime
		/// </summary>
		/// <param name="gpsDateTime">GPS (UTC(USNO)) datetime</param>
		/// <returns>UTC datetime</returns>
		/// <exception cref="ArgumentException">Exception, if UTC datetime < 2017-01-01</exception>
		public static DateTime ConvertGpstoUtc(DateTime gpsDateTime)
		{
			// Check for current leap second use
			if (gpsDateTime < new DateTime(2017, 1, 1))
				throw new ArgumentException("gpsDateTime must be > 2017-01-01");

			return gpsDateTime.AddSeconds(-CURRENT_LEAP_SECONDS);
		}


	}
}
