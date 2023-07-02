namespace Connectx.Protocols.RTCM
{
	/// <summary>
	/// Navigation system reference frequence
	/// RTCM 10403.3 Table 3.5-73
	/// </summary>
	internal enum ReferenceFrequence : uint
  {
		#region GPS

		/// <summary>
		/// GPS L1 frequence
		/// </summary>
		GPS_L1 = RinexObsCode.L1C,

		/// <summary>
		/// GPS L2 frequence
		/// </summary>
		GPS_L2 = RinexObsCode.L2P,

		/// <summary>
		/// GPS L5 frequence
		/// </summary>
		GPS_L5 = RinexObsCode.L5I,

		#endregion

		#region GLONASS

		/// <summary>
		/// GLONASS G1 frequence
		/// 1602000000 + 1000000*k*9/16, where k = [-7;6]
		/// </summary>
		GPS_G1 = 1_602_000_000,

		/// <summary>
		/// GLONASS G2 frequence
		/// 1246000000 + 1000000*k*7/16, where k = [-7;6]
		/// </summary>
		GPS_G2 = 1_246_000_000,

		#endregion

		#region Gallileo

		/// <summary>
		/// Gallileo E1 frequence
		/// </summary>
		GALLILEO_E1 = RinexObsCode.L1B,

		/// <summary>
		/// Gallileo E5A frequence
		/// </summary>
		GALLILEO_E5A = RinexObsCode.L5I,

		/// <summary>
		/// Gallileo E5B frequence
		/// </summary>
		GALLILEO_E5B = RinexObsCode.L7I,

		/// <summary>
		/// Gallileo E5AB frequence
		/// </summary>
		GALLILEO_E5AB = RinexObsCode.L8I,

		/// <summary>
		/// Gallileo E6 frequence
		/// </summary>
		GALLILEO_E6 = RinexObsCode.L6B,

		#endregion

		#region SBAS

		/// <summary>
		/// SBAS L1 frequence
		/// </summary>
		SBAS_L1 = RinexObsCode.L1C,

		/// <summary>
		/// SBAS L5 frequence
		/// </summary>
		SBAS_L5 = RinexObsCode.L5I,

		#endregion

		#region QZSS

		/// <summary>
		/// QZSS L1 frequence
		/// </summary>
		QZSS_L1 = RinexObsCode.L1C,

		/// <summary>
		/// QZSS L2 frequence
		/// </summary>
		QZSS_L2 = RinexObsCode.L2S,

		/// <summary>
		/// QZSS L5 frequence
		/// </summary>
		QZSS_L5 = RinexObsCode.L5I,

		#endregion

		#region BeiDou

		/// <summary>
		/// BeiDou B1 frequence
		/// </summary>
		BEIDOU_B1 = RinexObsCode.L2I,

		/// <summary>
		/// BeiDou B2 frequence
		/// </summary>
		BEIDOU_B2 = RinexObsCode.L7I,

		/// <summary>
		/// BeiDou B3 frequence
		/// </summary>
		BEIDOU_B3 = RinexObsCode.L6I,

		#endregion

		#region NavIC/IRNSS

		/// <summary>
		/// NavIC L5 frequence
		/// </summary>
		NAVIC_L5 = RinexObsCode.L5A

		#endregion
	}
}
