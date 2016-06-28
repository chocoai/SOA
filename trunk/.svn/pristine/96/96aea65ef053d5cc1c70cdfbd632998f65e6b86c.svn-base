<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page language="java"
	import="java.io.*,java.util.*,apf.framework.*,ime.web.util.*,ime.web.xpt.*"%>
<%!
	/**
	 * 注册Html部件
	 * @param dir 部件所在目录
	 */
	private void registerPart(HttpServletRequest request, HttpServletResponse response, File dir){
		StringBuilder sb = new StringBuilder();
		sb.append(dir.getAbsolutePath()).append("/").append(dir.getName()).append(".jsp");
		File regFile = new File(sb.toString());
		if( regFile.exists() ){
			sb.setLength(0);
			sb.append("/xpt/part/").append(dir.getParentFile().getName())
			  .append("/").append(dir.getName()).append("/").append(dir.getName()).append(".jsp");
			try{
				WrapperResponse wrapperResponse = new WrapperResponse(response); 
				request.getRequestDispatcher(sb.toString()).include(request, wrapperResponse);
				
				sb.setLength(0);
				sb.append("<span >")
				  .append(dir.getParentFile().getName()).append(".").append(dir.getName())
				  .append("已装入</span><br />");
			}
			catch(Exception e){
				sb.setLength(0);
				sb.append("<span class=\"error\">部件装入失败")
				  .append(dir.getParentFile().getName()).append(".").append(dir.getName())
				  .append("：").append(e.getMessage()).append("</span><br />");
			}
			try{
				response.getWriter().write(sb.toString());
			}
			catch(IOException e){
			}
		}
	}
	/**
	 * 注册名称空间下所有的Html部件
	 * @param nsdir 名称空间目录
	 */
	private void registerNSPart(HttpServletRequest request, HttpServletResponse response, File nsdir){
		File[] files = nsdir.listFiles();
		if( files == null )
			return;
		for( int i = 0; i < files.length; i++ ){
			registerPart(request, response, files[i]);
		}
	}
	/**
	 * 注册所有的Html部件
	 */
	public void registerAllPart(HttpServletRequest request, HttpServletResponse response){
		String path = Framework.instance().getAbsolutePath("/xpt/part");
		File dir = new File(path);
		File[] files = dir.listFiles();
		for( int i = 0; i < files.length; i++ ){
			registerNSPart(request, response, files[i]);
		}
	}
%>

<%
	registerAllPart(request, response);
	XPTEngine.clearAllCache();
%>
