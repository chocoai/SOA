<%@	page contentType="image/jpeg" pageEncoding="UTF-8" import="com.google.zxing.common.BitMatrix, com.google.zxing.*,com.google.zxing.qrcode.decoder.*,
java.awt.image.*,java.util.*,javax.imageio.*,java.io.*,org.jbarcode.JBarcode,org.jbarcode.encode.*,org.jbarcode.paint.*,org.jbarcode.util.*"	%>
<%!
int	BLACK =	0xFF000000;
int	WHITE =	0xFFFFFFFF;

BufferedImage toBufferedImage(BitMatrix	matrix)	{
	int width = matrix.getWidth();
	int height	= matrix.getHeight();
	BufferedImage image = new BufferedImage(width,	height,	BufferedImage.TYPE_INT_RGB);
	for (int x	= 0; x < width;	x++) {
		for (int y = 0; y < height; y++)	{
			image.setRGB(x, y,	matrix.get(x, y) ? BLACK : WHITE);
		}
	}
	return	image;
}


void writeBitMatrix(BitMatrix matrix, String logo, OutputStream stream)
	throws IOException {
	BufferedImage image = toBufferedImage(matrix);
	ImageIO.write(image, "JPEG", stream);
}

void writeEAN13(String content, OutputStream stream){
	try
    {
		JBarcode jbarcode = new JBarcode(EAN13Encoder.getInstance(), WidthCodedPainter.getInstance(), EAN13TextPainter.getInstance());
		BufferedImage image = jbarcode.createBarcode(content);
		ImageIO.write(image, "JPEG", stream);
    }
    catch (Exception localException)
    {
    }
}
void writeEAN8(String content, OutputStream stream){
	try
    {
		JBarcode jbarcode = new JBarcode(EAN8Encoder.getInstance(), WidthCodedPainter.getInstance(), EAN8TextPainter.getInstance());
		BufferedImage image = jbarcode.createBarcode(content);
		ImageIO.write(image, "JPEG", stream);
    }
    catch (Exception localException)
    {
    }
}
void writeCode39(String content, OutputStream stream){
	try
    {
		JBarcode jbarcode = new JBarcode(Code39Encoder.getInstance(), WideRatioCodedPainter.getInstance(), BaseLineTextPainter.getInstance());
		jbarcode.setShowCheckDigit(false);
		
		BufferedImage image = jbarcode.createBarcode(content);
		ImageIO.write(image, "JPEG", stream);
    }
    catch (Exception localException)
    {
    }
}
void writeCode93(String content, OutputStream stream){
	try
    {
		JBarcode jbarcode = new JBarcode(Code93Encoder.getInstance(), WideRatioCodedPainter.getInstance(), BaseLineTextPainter.getInstance());
		jbarcode.setShowCheckDigit(false);
		
		BufferedImage image = jbarcode.createBarcode(content);
		ImageIO.write(image, "JPEG", stream);
    }
    catch (Exception localException)
    {
    }
}
void writeCode128(String content, OutputStream stream){
	try
    {
		JBarcode jbarcode = new JBarcode(Code128Encoder.getInstance(), WideRatioCodedPainter.getInstance(), BaseLineTextPainter.getInstance());
		jbarcode.setShowCheckDigit(false);
		
		BufferedImage image = jbarcode.createBarcode(content);
		ImageIO.write(image, "JPEG", stream);
    }
    catch (Exception localException)
    {
    }
}
%>
<%
	//设置页面不缓存
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	
	String format  = request.getParameter("format");
	String content = request.getParameter("content");
	String logo    = request.getParameter("qr_logo");
	String size    = request.getParameter("qr_size");
	String ecl     = request.getParameter("ecl");
	Object errorCorrectionLevel = ErrorCorrectionLevel.L;
	if( "H".equals(ecl) )
		errorCorrectionLevel = ErrorCorrectionLevel.H;
	
	if( "EAN13".equals(format) ){
		writeEAN13(content, response.getOutputStream());
	}
	else if( "EAN8".equals(format) ){
		writeEAN8(content, response.getOutputStream());
	}
	else if( "Code39".equals(format) ){
		writeCode39(content, response.getOutputStream());
	}
	else if( "Code93".equals(format) ){
		writeCode93(content, response.getOutputStream());
	}
	else if( "Code128".equals(format) ){
		writeCode128(content, response.getOutputStream());
	}
	else if( "PDF417".equals(format) ){
		int qr_size = 400;
		if( size != null && size.length() > 0 ){
			try{
				qr_size = Integer.valueOf(size);
			}
			catch(Exception e){
			}
		}
		Map	hints =	new	HashMap();
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
		hints.put(EncodeHintType.MARGIN, 1);
		MultiFormatWriter multiFormatWriter	= new MultiFormatWriter();
		BitMatrix bitMatrix	= multiFormatWriter.encode(content,	BarcodeFormat.PDF_417, qr_size,	qr_size, hints);
		
		// 输出图象到页面
		writeBitMatrix(bitMatrix, logo, response.getOutputStream());
	}
	else {
		int qr_size = 400;
		if( size != null && size.length() > 0 ){
			try{
				qr_size = Integer.valueOf(size);
			}
			catch(Exception e){
			}
		}
		Map	hints =	new	HashMap();
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
		//hints.put(EncodeHintType.CHARACTER_SET,	"UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);
		MultiFormatWriter multiFormatWriter	= new MultiFormatWriter();
		BitMatrix bitMatrix	= multiFormatWriter.encode(content,	BarcodeFormat.QR_CODE, qr_size,	qr_size, hints);
		
		// 输出图象到页面
		writeBitMatrix(bitMatrix, logo, response.getOutputStream());
	}
	
	out.clear();
	out	= pageContext.pushBody();
%>