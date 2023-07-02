namespace Connectx.Protocols.RTCM
{
	/// <summary>
	/// OBS Code
	/// </summary>
	internal enum OBSCode : byte
  {
		/// <summary>
		/// None or unknown
		/// </summary>
		None = 0,

		/// <summary>
		/// L1C/A,G1C/A,E1C (GPS,GLO,GAL,QZS,SBS)
		/// </summary>
		L1C = 1,

		/// <summary>
		/// L1P,G1P,B1P (GPS,GLO,BDS)
		/// </summary>
		L1P = 2,

		/// <summary>
		/// L2C/A,G1C/A (GPS,GLO)
		/// </summary>
		L2C = 14,

		/// <summary>
		/// L2 L1C/A-(P2-P1) (GPS)
		/// </summary>
		L2D = 15,

		/// <summary>
		/// L2C(M+L),B1_2I+Q (GPS,QZS,BDS)
		/// </summary>
		L2X = 18,

		/// <summary>
		/// L2P,G2P (GPS,GLO)
		/// </summary>
		L2P = 19,

		/// <summary>
		/// L2 Z-track (GPS)
		/// </summary>
		L2W = 20,

		/// <summary>
		/// Maximum number of OBS code
		/// </summary>
		MaxCode = 68
  }
}
