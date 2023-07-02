namespace Connectx.Protocols.RTCM
{
  /// <summary>
  /// RINEX observation code for navigation system frequences
  /// </summary>
  internal enum RinexObsCode : uint
  {
		/// <summary>
		/// Gallileo E1
		/// </summary>
		L1B = 1_575_420_000,

		/// <summary>
		/// GPS L1, GLONASS G1, SBAS L1, QZSS L1
		/// </summary>
		L1C = 1_575_420_000,

		/// <summary>
		/// GLONASS G2
		/// </summary>
		L2C = 1_246_000_000,

		/// <summary>
		/// BeiDou B1
		/// </summary>
		L2I = 1_561_098_000,

		/// <summary>
		/// GPS L2
		/// </summary>
		L2P = 1_227_600_000,

		/// <summary>
		/// QZSS L2
		/// </summary>
		L2S = 1_227_600_000,

		/// <summary>
		/// NavIC L5
		/// </summary>
		L5A = 1_176_450_000,

		/// <summary>
		/// GPS L5, Gallileo E5A, SBAS L5, QZSS L5
		/// </summary>
		L5I = 1_176_450_000,

		/// <summary>
		/// Gallileo E6
		/// </summary>
		L6B = 1_278_750_000,

		/// <summary>
		/// BeiDou B3
		/// </summary>
		L6I = 1_268_520_000,

		/// <summary>
		/// Gallileo E5B
		/// </summary>
		L7I = 1_207_140_000,

		/// <summary>
		/// Gallileo E5AB
		/// </summary>
		L8I = 1_191_795_000
	}
}
