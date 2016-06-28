<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" import="java.util.*, ime.webui.template.*, org.apache.velocity.*, org.apache.velocity.context.*, apf.framework.Framework, ime.security.*,ime.core.*,java.io.*,ime.core.entity.*,ime.document.*,ime.export.tool.*"%>
<%
	String data = request.getParameter("data");
	String file = request.getParameter("file");
	
	if( file == null || file.length() == 0 )
		file = "report.xls";
	if( file.endsWith(".doc") ){
		String charset = "utf-8";
		StringWriter writer = null;
		try{
			String pageWidth  = request.getParameter("pageWidth");
			String pageHeight = request.getParameter("pageHeight");
			
			if( pageWidth == null || pageWidth.length() == 0 )
				pageWidth = "595.3";
			if( pageHeight == null || pageHeight.length() == 0 )
				pageHeight = "841.9";
			
			TemplateEngine engine = TemplateEngineFactory.getTemplateEngine("root");
			Template template = engine.getTemplate("/pages/core/export_word.vm", charset);
			Context context = engine.newContext();
			context.put("width", pageWidth);
			context.put("height", pageHeight);
			context.put("doc", data);
			
			writer = new StringWriter();
			template.merge(context, writer);
			
			String html = writer.toString();
			writer.flush();
			writer.close();
			
			String path = DocumentStorage.getAbsolutePath("../", "export");
			File pFile = new File(path);
			if(!pFile.exists())
				pFile.mkdirs();
			path = path + "/" + UUID.randomUUID().toString() + ".mht";
			Html2MHTCompiler h2t = new Html2MHTCompiler(html, charset, path);
			boolean c = h2t.compile();
			if(c){
				BufferedReader reader = null;
				try {
					StringBuilder sb = new StringBuilder();
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), charset));
					String lineStr = null;
					while ((lineStr = reader.readLine()) != null) {
						sb.append(lineStr);
						sb.append("\r\n");
					}
					reader.close();
					data = sb.toString();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (Exception e) {
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(writer != null)
				writer.close();
		}
	}
	response.setHeader("Content-Disposition", "attachment; filename=" + file);
	response.setCharacterEncoding("UTF-8");
	response.getWriter().print(data);
%>