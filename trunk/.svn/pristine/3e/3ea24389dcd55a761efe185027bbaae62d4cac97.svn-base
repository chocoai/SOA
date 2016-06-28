<%@ page language="java" contentType="text/xml; charset=utf-8" pageEncoding="UTF-8" import="java.util.*, java.io.*"%>
<%!
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
	//设置页面不缓存
	response.setHeader("Pragma","No-cache");
	response.setHeader("Cache-Control","no-cache");
	response.setDateHeader("Expires", 0);
	
	ime.security.LoginSession.setHttpSession(session);
	
	String ATTACHEMENT_PATH = "attachments";
	Map<String, String> fileMap = new HashMap<String, String>();
	Map<String, String> _fileMap = new HashMap<String, String>();
	try {
        com.oreilly.servlet.multipart.MultipartParser mp = new com.oreilly.servlet.multipart.MultipartParser(request, 100*1024*1024, false, false, "utf-8");
		String rootPath = ime.document.DocumentStorage.getAbsolutePath("", ATTACHEMENT_PATH);
		File rootDir = new File(rootPath);
		if( !rootDir.exists() )
			rootDir.mkdirs();
        
		com.oreilly.servlet.multipart.Part part = null;
        String path = null, repath = null, fileName = null, fileExt = null, fileuid = null, deleteFile = null;
        com.oreilly.servlet.multipart.FilePart filePart = null;
		StringBuilder sb = new StringBuilder();
		
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
				    
				    File $temp = File.createTempFile("pattern", "." + fileExt);
				    String $tempPath = $temp.getAbsolutePath();
				    createThumbnail(tempPath, $tempPath, 480, 800);
				    temp.delete();
				    
				    _fileMap.put(fileName, $tempPath);
				}
			}
		}
        Iterator<Map.Entry<String, String>> it = _fileMap.entrySet().iterator();
        while(it.hasNext()){
        	Map.Entry<String, String> entry = it.next();
        	fileName = entry.getKey();
        	String temp = entry.getValue();
        	repath = ime.document.DocumentStorage.getFilePath(fileName);
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
		    source.delete();
		    
		    sb.setLength(0);
	        sb.append(repath).append("/").append(fileuid);
	        if( fileExt != null )
		    	sb.append(".").append(fileExt);
	        fileMap.put(fileName, sb.toString());
        }
 	} catch (Exception e) {
 		e.printStackTrace();
    }
%>
<uploaded>
<% 
	Iterator<Map.Entry<String, String>> it = fileMap.entrySet().iterator();
	while(it.hasNext()){
		Map.Entry<String, String> entry = it.next();
%>
	<file name="<%=entry.getKey() %>" url="<%=entry.getValue() %>" />
<% 	} %>
</uploaded>
