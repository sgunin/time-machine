namespace Connectx.Protocols.RTCM
{
	/// <summary>
	/// RTCM v3.3 constants
	/// </summary>
	internal class Constants
  {
		#region GPS
		/// <summary>
		/// The GPS L1 pseudorange in meter
		/// </summary>
		public const double GPS_L1PSEUDORANGE = 299_792.458;

		#endregion

		#region GLONASS

		/// <summary>
		/// GLONASS L1 pseudorange in meter
		/// </summary>
		public const double GLONASS_L1PSEUDORANGE = 599_584.920;

		#endregion

		#region Other physics constants

		/// <summary>
		/// Pi value
		/// </summary>
		public const double PI = 3.14159265358979323846;

		/// <summary>
		/// E value
		/// </summary>
		public const double E = 2.7182818284590452354;

		/// <summary>
		/// Light speed, meter per second
		/// </summary>
		public const double LIGHT_SPEED = 299_792_458.0;

		#endregion
	}
}
