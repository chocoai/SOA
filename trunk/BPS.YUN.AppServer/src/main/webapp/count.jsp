<%@ page contentType="text/html;charset=gb2312"%>
<%@ page language="java"
	import="java.io.*,java.awt.*,java.awt.image.*,java.util.*,javax.imageio.*"%>
<%!//ͬ�����¼�����
	synchronized void counter() {
		ServletContext application = getServletContext(); //����application����
		String szPath = application.getRealPath("/"); //�õ���ǰ·��
		szPath = szPath + "hits.txt"; //�������ļ�
		String szRecord = ""; //���� String
		long nRecord = 0; //���� s
		try {
			BufferedReader file = new BufferedReader(new FileReader(szPath));
			szRecord = file.readLine(); //��ȡ�������ļ�
			file.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		if (szRecord == null) {
			szRecord = "0";
		}

		nRecord = Long.parseLong(szRecord.toString()) + 1;
		//������+1
		try {
			File f = new File(szPath);
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			pw.print(nRecord); //д�ļ�
			pw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}%>
<%
	//��ʾ������
	if (session.isNew()) { //������»Ự
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

	//����ҳ�治����
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
	
	// ���ڴ��д���ͼ��
	int width  = fm.stringWidth(szRecord);
	int height = fm.getHeight();
	BufferedImage image = new BufferedImage(width, height,
			BufferedImage.TYPE_INT_RGB);

	// ��ȡͼ��������
	Graphics2D g = image.createGraphics();
	image = g.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	g.dispose();
	g = image.createGraphics();
	
//	g.fillRect(0, 0, width, height);
	
	//�趨����
	g.setFont(font);

	
	g.setColor(new Color(color));
	g.drawString(szRecord, 0, fm.getHeight() - fm.getDescent());

	//ͼ����Ч
	g.dispose();

	//���ͼ��ҳ��
	ImageIO.write(image, "png", response.getOutputStream());
	out.clear();
	out = pageContext.pushBody();
%>
