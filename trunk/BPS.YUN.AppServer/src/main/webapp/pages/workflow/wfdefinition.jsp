<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8" import="ime.core.*,ime.core.entity.*,ime.document.*,ime.workflow.*,ime.workflow.services.*,ime.workflow.definition.entity.*"%>
<%
	Domain domain = Environment.instance().getDomain(Reserved.MAIN_DOMAIN_ID);
	
	String processId      = request.getParameter("processId");
	String definitionUid  = request.getParameter("definitionUid");
	
	if( (definitionUid == null || definitionUid.length() == 0 ) && ( processId != null && processId.length() > 0)){
		WorkflowService service = new WorkflowService();
		ProcessDefinition def = service.getDefinition(Long.parseLong(processId));
		if( def != null )
			definitionUid = def.getUid();
	}
	StringBuilder path = new StringBuilder();
	path.append("../../")
	    .append(DocumentStorage.DOC_ROOT)
	    .append(domain.getStorageName())
	    .append("/")
	    .append(Workflow.DEFINITION_PATH)
	    .append("/")
	    .append(definitionUid)
	    .append(".xml");

%>
<jsp:forward page="<%=path.toString()%>" />