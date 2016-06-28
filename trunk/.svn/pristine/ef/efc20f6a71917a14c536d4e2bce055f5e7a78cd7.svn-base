<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*, ime.web.xpt.*, ime.web.xpt.template.*, org.json.*, org.hibernate.*" %>
<%!
public class GoodsView extends HtmlControl{
	public GoodsView(){
		
	}
	public List<LinkFile> getHeadFiles(){
		if( headFiles.size() == 0 ){
			headFiles.add(new LinkFile("xxx.js", LinkFile.JS));
		}
		return headFiles;
	}
	/**
	 * 获取CSS样式字符串
	 */
	public String getCSS(){
		return "";
	}
	/**
	 * 获取javascript脚本字符串
	 */
	public String getScript(){
		return "";
	}
	/**
	 * 处理AJAX请求
	 * @param service 请求服务名称
	 * @param args 请求服务参数
	 * @return 请求处理结果
	 */
	public JSONObject doAjaxRequest(String service, JSONObject args){
		JSONObject result = new JSONObject();
		try{
			result.put("say", args.get("say"));
		} catch(Exception e ){
			
		}
		
		return result;
	}
	
	/**
	 * 在生成页面前执行的数据处理方法，在此处获取（或提交）相关数据
	 * @param hsession Hibernate Session对象
	 * @param request Http请求对象
	 */
	public void dataProcess(Session hsession, HttpServletRequest request) throws Exception{
		//查询数据并加入到context中，以便模板生成
		/*
		Query query = hsession.createQuery("select a.id, b.name from DRP_WebPortal a, DRP_CatalogGoods b where id = :id");
		query.setParameter("id", 1L);
		query.setFirstResult(100);
		query.setMaxResults(20);
		
		List<Object[]> list = query.list();
		for( Object item : list ){
			Map<String, Object> entity = (Map<String, Object>)item;
			
		}
		*/
		context.put("data", new JSONObject("{say:'oh hello xpt world!!!'}"));
	}
}
%>
<%
	//注册部件
	HtmlPartFactory.registerPart("ime.drp.GoodsView", GoodsView.class);
%>