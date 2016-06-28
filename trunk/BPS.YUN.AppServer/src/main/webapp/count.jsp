<%@ page contentType="text/html;charset=gb2312"%>
<%@ page language="java"
	import="java.io.*,java.awt.*,java.awt.image.*,java.util.*,javax.imageio.*"%>
<%!//同步更新计数器
	synchronized void counter() {
		ServletContext application = getServletContext(); //构造application对象
		String szPath = application.getRealPath("/"); //得到当前路径
		szPath = szPath + "hits.txt"; //计数器文件
		String szRecord = ""; //记数 String
		long nRecord = 0; //记数 s
		try {
			BufferedReader file = new BufferedReader(new FileReader(szPath));
			szRecord = file.readLine(); //读取计数器文件
			file.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		if (szRecord == null) {
			szRecord = "0";
		}

		nRecord = Long.parseLong(szRecord.toString()) + 1;
		//计数器+1
		try {
			File f = new File(szPath);
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			pw.print(nRecord); //写文件
			pw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}%>
<%
	//显示计数器
	if (session.isNew()) { //如果是新会话
		counter();
	}
	String Path = application.getRealPath("/");
	String szPath = Path + "hits.txt";
	String szRecord = "";
	BufferedReader file = new BufferedReader(new FileReader(szPath));
	try {
		szRecord = file.readLine();
		file.close();
	} catch (Exception e) {
		System.out.println(e);
	}

	//设置页面不缓存
	response.setHeader("Pragma", "No-cache");
	response.setHeader("Cache-Control", "no-cache");
	response.setDateHeader("Expires", 0);

	String fontFamily = request.getParameter("font");
	String fontSize   = request.getParameter("fontSize");
	String rgb        = request.getParameter("color");
	int color = 0;
	if( fontFamily == null )
		fontFamily = "Times New Roman";
	if( fontSize == null )
		fontSize = "16";
	if( rgb != null )
		color = Integer.parseInt(rgb);
	
	int size = Integer.parseInt(fontSize);
	Font font = new Font(fontFamily, Font.PLAIN, size);
	FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
	
	// 在内存中创建图象
	int width  = fm.stringWidth(szRecord);
	int height = fm.getHeight();
	BufferedImage image = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);

	// 获取图形上下文
	Graphics2D g = image.createGraphics();
	image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	g.dispose();
	g = image.createGraphics();
	
//	g.fillRect(0, 0, width, height);
	
	//设定字体
	g.setFont(font);

	
	g.setColor(new Color(color));
	g.drawString(szRecord, 0, fm.getHeight() - fm.getDescent());

	//图象生效
	g.dispose();

	//输出图象到页面
	ImageIO.write(image, "png", response.getOutputStream());
	out.clear();
	out = pageContext.pushBody();
%>
