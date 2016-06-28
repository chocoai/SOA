package bps.common;



public class MD5Util {

	public final static String MD5(String str) throws Exception{
		java.security.MessageDigest alga;
		try {
			alga = java.security.MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			return str;
		}
		alga.update(str.getBytes("UTF-8"));
		byte[] digesta = alga.digest();
		return byte2hex(digesta);
	}
	
	protected static String byte2hex(byte bytes[]) {
		StringBuffer retString = new StringBuffer();
		for (int i = 0; i < bytes.length; ++i) {
			retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF))
					.substring(1).toUpperCase());
		}
		return retString.toString();
	}

	protected static byte[] hex2byte(String hex) {
		byte[] bts = new byte[hex.length() / 2];
		for (int i = 0; i < bts.length; i++) {
			bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2),
					16);
		}
		return bts;
	}  
}
