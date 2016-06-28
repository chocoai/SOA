<%@ page language="java" contentType="text/xml; charset=utf-8" pageEncoding="UTF-8" import="java.awt.image.*,javax.imageio.*,org.imgscalr.*,ime.security.*,ime.core.event.*,ime.document.*,java.util.*,java.io.*,com.oreilly.servlet.multipart.*"%>
<%!
private static final int[] RGB_MASKS = {0xFF0000, 0xFF00, 0xFF};
private static final ColorModel RGB_OPAQUE = new DirectColorModel(32, RGB_MASKS[0], RGB_MASKS[1], RGB_MASKS[2]);

BufferedImage getImage(File file) throws Exception{
	java.awt.Image img = java.awt.Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());

	PixelGrabber pg = new PixelGrabber(img, 0, 0, -1, -1, true);
	pg.grabPixels();
	int width = pg.getWidth(), height = pg.getHeight();

	DataBuffer buffer = new DataBufferInt((int[]) pg.getPixels(), pg.getWidth() * pg.getHeight());
	WritableRaster raster = Raster.createPackedRaster(buffer, width, height, width, RGB_MASKS, null);
	return new BufferedImage(RGB_OPAQUE, raster, false, null);
}
BufferedImage scaleImage(File file, Integer width, Integer height, boolean stretch){
	BufferedImage img = null;
	try{
	    img = getImage(file);
	}catch(Exception e){
		e.printStackTrace();
	}
	
	double x		= 0;
	double y		= 0;
	double _height 			= img.getHeight();
	double _width 			= img.getWidth();
	double i_pre = 1d;
	if(width == null && height != null){
		i_pre = _height / height;
	}else if(height == null && width != null){
		i_pre = _width / width;
	}else{
		i_pre = Math.min(_height / height, _width / width);
	}
	if(width == null && height != null){
		if(i_pre > 1)
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_HEIGHT, height);
		else{
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_HEIGHT, (int)_height);
		}
	}else if(height == null && width != null){
		if(i_pre > 1)
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_WIDTH, width);
		else{
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_WIDTH, (int)_width);
		}
	}else if(!stretch){
		double initRate = _width / _height;
		double templateRate = (double)width / (double)height;
		if(templateRate > initRate){//宽为标准进行裁剪
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_WIDTH, width);
			_height = img.getHeight();
			y = (int)((_height - height) / 2);
			x = 0;
			img = Scalr.crop(img, (int)x, (int)y, width, height);
		}else{
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_HEIGHT, height);
			_width = img.getWidth();
			x = (int)((_width - width) / 2);
			y = 0;
			img = Scalr.crop(img, (int)x, (int)y, width, height);
		}
	}else if(stretch){
		double initRate = _width / _height;
		double templateRate = (double)width / (double)height;
		if(templateRate > initRate){//宽为标准进行拉伸
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_WIDTH, width);
		}else{
			img = Scalr.resize(img, Scalr.Mode.FIT_TO_HEIGHT, height);
		}
	}
	return img;
}

/**
 * 创建图片缩略图(等比缩放)
 * 
 * @param src
 *            源图片文件完整路径
 * @param dist
 *            目标图片文件完整路径
 * @param width
 *            缩放的宽度
 * @param height
 *            缩放的高度
 * @throws Exception 
 */
String createThumbnail(String src, String dist, float width, float height) throws Exception {
    try {
    	float w = width;
    	float h = height;
        java.io.File srcfile = new java.io.File(src);
        if (!srcfile.exists()) {
            throw new Exception("文件不存在");
        }
        
        java.awt.image.BufferedImage image = javax.imageio.ImageIO.read(srcfile);
        if(image.getHeight() > image.getWidth()){
        	w = height;
        	h = width;
        }
        // 获得缩放的比例
        double ratio = 1.0;
        // 判断如果高、宽都不大于设定值，则不处理
        if (image.getHeight() > h || image.getWidth() > w) {
            if (image.getHeight() > image.getWidth()) {
                ratio = h / image.getHeight();
            } else {
                ratio = w / image.getWidth();
            }
        }
        // 计算新的图面宽度和高度
        int newWidth = (int) (image.getWidth() * ratio);
        int newHeight = (int) (image.getHeight() * ratio);
        java.io.FileOutputStream os = null;
        java.awt.image.BufferedImage bfImage = null;
        try{
        	bfImage = new java.awt.image.BufferedImage(newWidth, newHeight, java.awt.image.BufferedImage.TYPE_INT_RGB);
            bfImage.getGraphics().drawImage(image.getScaledInstance(newWidth, newHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            bfImage.flush();
            os = new java.io.FileOutputStream(dist);
            com.sun.image.codec.jpeg.JPEGImageEncoder encoder = com.sun.image.codec.jpeg.JPEGCodec.createJPEGEncoder(os);
            encoder.encode(bfImage);
            os.close();
        }catch (Exception e) {
        	throw e;
		}finally{
			if(bfImage != null)
				bfImage.flush();
			if(os != null)
				os.close();
		}
        return dist;
    } catch (Exception e) {
        throw e;
    }
}
%>

<%
	/*
	   参数
	   width:宽度
	   height:高度
	   stretch:是否拉伸
	 */
	//设置页面不缓存
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	
	String ATTACHEMENT_PATH = "attachments";
	Map<String, String> fileMap = new HashMap<String, String>();
	Map<String, String> _fileMap = new HashMap<String, String>();
	List<String> delMap = new ArrayList<String>();
	try {
		Integer width = null, height = null;
		try{
			width = Integer.parseInt(request.getParameter("width"));
		}catch(Exception e){
			width = null;
		}
		try{
			height = Integer.parseInt(request.getParameter("height"));
		}catch(Exception e){
			height = null;
		}
		Boolean stretch = false;
		try{
			stretch = Boolean.parseBoolean(request.getParameter("stretch"));
		}catch(Exception e){
			stretch = false;
		}
        com.oreilly.servlet.multipart.MultipartParser mp = new com.oreilly.servlet.multipart.MultipartParser(request, 10*1024*1024, false, false, "utf-8");
		LoginSession.setHttpSession(request.getSession());
		String rootPath = DocumentStorage.getAbsolutePath("", ATTACHEMENT_PATH);
		File rootDir = new File(rootPath);
		if( !rootDir.exists() )
			rootDir.mkdirs();
        
		com.oreilly.servlet.multipart.Part part = null;
        String path = null, repath = null;
        com.oreilly.servlet.multipart.FilePart filePart = null;
        String fileName = null, fileExt = null;
		StringBuilder sb = new StringBuilder();
		String fileuid = null;
		String deleteFile = null;
		
        while ((part = mp.readNextPart()) != null) {
			if (part.isFile()) {
				filePart = (com.oreilly.servlet.multipart.FilePart) part;
				fileName = filePart.getFileName();
				if (fileName != null) {
					int index = fileName.lastIndexOf(".");
					if( index > 0 )
						fileExt = fileName.substring(index + 1);
				    File temp = File.createTempFile("pattern", "." + fileExt);
				    String tempPath = temp.getAbsolutePath();
				    OutputStream output = null;
				    try{
					    output = new FileOutputStream(temp);
					    filePart.writeTo(output);
					    output.flush();
					    output.close();
				    }finally{
				    	if(output != null)
				    		output.close();
				    }
				    
				    if(width != null || height != null){
					  	//在内存中创建图象
					  	BufferedImage image = null;
					  	try{
							image = scaleImage(temp, width, height, stretch);
							ImageIO.write(image, fileExt, temp);
							image.flush();
					  	}finally{
					  		if(image != null)
					  			image.flush();
					  	}
					  	_fileMap.put(fileName, tempPath);
				    }else{//等比例缩放
				    	File $temp = File.createTempFile("pattern", "." + fileExt);
					    String $tempPath = $temp.getAbsolutePath();
					    createThumbnail(tempPath, $tempPath, 480, 800);
					    temp.delete();
					    
					    _fileMap.put(fileName, $tempPath);
				    }
				}
			}
			else if( part.isParam() ){
				String name = ((com.oreilly.servlet.multipart.ParamPart)part).getName();
				String value = ((com.oreilly.servlet.multipart.ParamPart)part).getStringValue();
				if( "deleteFile".equals(name) )
					deleteFile = value;
			}
		}
        Iterator<Map.Entry<String, String>> it = _fileMap.entrySet().iterator();
        while(it.hasNext()){
        	Map.Entry<String, String> entry = it.next();
        	fileName = entry.getKey();
        	String temp = entry.getValue();
        	
        	repath = DocumentStorage.getFilePath(fileName);
			sb.setLength(0);
		    sb.append(rootPath).append(repath);
		    path = sb.toString();
		    File dir = new File(path);
		    if( !dir.exists() )
		    	dir.mkdirs();
		    sb.setLength(0);
		    sb.append(Long.toHexString(System.currentTimeMillis()))
			  .append(Long.toHexString(Math.round(Math.random() * 100)));
		    fileuid = sb.toString();
		    sb.setLength(0);
		    sb.append(path).append("/").append(fileuid);
		    if( fileExt != null )
		    	sb.append(".").append(fileExt);
		    //移动文件
		    File source = new File(temp);
		    File target = new File(sb.toString());
		    source.renameTo(target);
		    
		    EventManager.instance().fireEvent(new ime.core.event.Event(EventType.FILE_EVENT, new File(sb.toString()).getAbsolutePath(), "create"));
		    sb.setLength(0);
	        sb.append(repath).append("/").append(fileuid);
	        if( fileExt != null )
		    	sb.append(".").append(fileExt);
	        fileMap.put(fileName, sb.toString());
        }
        
        if( deleteFile != null && deleteFile.length() > 0 ){
			String[] deleteFiles = deleteFile.split(":");
			for( int i = 0; i < deleteFiles.length; i++ ){
				sb.setLength(0);
				sb.append(ATTACHEMENT_PATH).append("/").append(deleteFiles[i]);
				path = DocumentStorage.getAbsolutePath("", sb.toString());
				if( path != null ){
					File file = new File(path);
					file.delete();
					EventManager.instance().fireEvent(new ime.core.event.Event(EventType.FILE_EVENT, file.getAbsolutePath(), "delete"));
				}
			}
        }
 	} catch (Exception e) {
 		response.getWriter().write(e.getMessage());
    }
%>
<%
	String method = request.getParameter("method");
	if( "iframe".equals(method)){
		response.setContentType("text/html; charset=utf-8");
%>
<html>
<head></head>
<body>
<%}%>
<uploaded>
<% 
	Iterator<Map.Entry<String, String>> it = fileMap.entrySet().iterator();
	while(it.hasNext()){
		Map.Entry<String, String> entry = it.next();
%>
	<file name="<%=entry.getKey() %>" url="<%=entry.getValue() %>" />
<% } %>
</uploaded>
<%if( "iframe".equals(method)){ %>
</body>
</html>
<%}%>