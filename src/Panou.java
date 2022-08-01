import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Panou extends JPanel implements ActionListener{

	
	private ImageIcon capDreapta;
	private ImageIcon capStanga;
	private ImageIcon capSus;
	private ImageIcon capJos;
	private ImageIcon mar;
	
	static final int SCREEN_WIDTH=600;
	static final int SCREEN_HEIGHT=600;
	static final int UNIT_SIZE=25;
	static final int GAME_UNITS=(SCREEN_WIDTH*SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY=100;
	final int x[]=new int[GAME_UNITS];
	final int y[]=new int[GAME_UNITS];
	int partileCorpului=6;
	int marMancat;
	int marX;
	int marY;
	char directie='D';
	boolean merge=false;
	Timer timp;
	Random random;
	
	private static String url = "jdbc:mysql://localhost:3306/ok?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static String pass = "1994";
	private static String name = "razvan";
	private static Connection con;
	
	Panou(){
	
		random=new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH+25,SCREEN_HEIGHT+25));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		start();
		
	}
	
	
	public static void afisare() {
		String query = "select * from scor"; 		
	
		try {
		
			Statement sql = con.createStatement(); 
			ResultSet rs = sql.executeQuery(query);
			
			while(rs.next())
				System.out.println("Name: " + rs.getString("name") + "\nScore: " + rs.getString("score") + "\n");
			
			
			sql.close();
			rs.close();
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void insert(String name, String score) {
		PreparedStatement myStm;

		String insert = "insert into scor (name, score) values (?, ?)";

		try {
			
			myStm = con.prepareStatement(insert);
			myStm.setString(1, name);
			myStm.setString(2, score);
			
			int count = myStm.executeUpdate();
			
			if(count > 0)
				System.out.println("Inserare reusita");
			
			myStm.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	public void start() {
		marNou();
		merge=true;
		timp=new Timer(DELAY, this);
		timp.start();
	}	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		desen(g);
		
	}
	
	public void desen(Graphics g) {
	if(merge) {	
//		for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE; i++)
//			{
//				g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
//				g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
//			}
		
		mar = new ImageIcon("mar.png");
		mar.paintIcon(this, g, marX, marY);
		
		for (int i=0;i<partileCorpului;i++)
		{
			if(i==0) {
			if(directie=='D'){
				capDreapta = new ImageIcon("capDreapta.png");
				capDreapta.paintIcon(this, g, x[0], y[0]);
			}
			if(directie=='S')
			{
				capStanga = new ImageIcon("capStanga.png");
				capStanga.paintIcon(this, g, x[0], y[0]);
			}
			if(directie=='Î')
			{
				capSus = new ImageIcon("capSus.png");
				capSus.paintIcon(this, g, x[0], y[0]);
			}
			if(directie=='J')
			{
				capJos = new ImageIcon("capJos.png");
				capJos.paintIcon(this, g, x[0], y[0]);
			}
			}
			
			else {
				g.setColor(new Color(127,221,76));
				g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
			}
		}
		g.setColor(Color.white);
		g.setFont(new Font ("INK FREE", Font.BOLD, 40));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString("SCOR: "+marMancat, (SCREEN_WIDTH - metrics.stringWidth("SCOR: "+marMancat))/2, g.getFont().getSize());
		
	}
	else
		GameOver(g);
	
	}
	
	public void marNou() {
		marX=random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		marY=random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;	
	}

	public void miscare() {
		
	for(int i=partileCorpului; i>0;i--)
	{
		x[i]=x[i-1];
		y[i]=y[i-1];
			
	}
	
	switch(directie) {
	case 'Î':
		y[0]=y[0]-UNIT_SIZE;
		break;
		
	case 'J':
		y[0]=y[0]+UNIT_SIZE;
		break;
	
	case 'S':
		x[0]=x[0]-UNIT_SIZE;
		break;
	
	case 'D':
		x[0]=x[0]+UNIT_SIZE;
		break;
	
	
	}
		
	}
	
	public void verificareMar() {
		
		if((x[0] == marX)&&(y[0] == marY)){
			partileCorpului++;
			marMancat++;
			marNou();
		}
			
		
	}
	
	public void verificareCiocniri() {
		for (int i=partileCorpului;i>0;i--) {
			if((x[0]== x[i])&& (y[0]== y[i])) {
				merge=false;
			}
		}
		if(x[0]<0)
			merge=false;
		if(x[0]>SCREEN_WIDTH)
			merge=false;
		if(y[0]<0)
			merge=false;
		if(y[0]>SCREEN_HEIGHT)
			merge=false;
		
		if(!merge)
			timp.stop();
	}
	
	public void GameOver(Graphics g) {
		try {
			con = DriverManager.getConnection(url, name ,pass);
			insert("Razvan", String.valueOf(marMancat));
			afisare();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		g.setColor(Color.red);
		g.setFont(new Font ("INK FREE", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("SCOR: "+marMancat, (SCREEN_WIDTH - metrics1.stringWidth("SCOR: "+marMancat))/2, g.getFont().getSize());
		
		
		g.setColor(Color.red);
		g.setFont(new Font ("INK FREE", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("GAME OVER", (SCREEN_WIDTH - metrics2.stringWidth("Game Over"))/2, SCREEN_HEIGHT/2);
		
		g.setColor(Color.red);
		g.setFont(new Font ("INK FREE", Font.BOLD, 40));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press R to RESTART",(SCREEN_WIDTH - metrics3.stringWidth("Press R to RESTART: "))/2,400);
	
		
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(merge) {
			miscare();
			verificareMar();
			verificareCiocniri();
		}
		repaint();
	
		
	}

	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			
				case KeyEvent.VK_R:{
					merge=true;
					marMancat=0;
					partileCorpului=6;
					repaint();
					directie='D';
					for (int i=0;i<partileCorpului;i++)
					{
						x[i]=0;
						y[i]=0;
					}
						
					start();
				
					
				}
					
				case KeyEvent.VK_LEFT:
					if(directie !='D') {
						directie ='S';
					}
					break;
			
				case KeyEvent.VK_RIGHT:
					if(directie !='S') {
						directie ='D';
					}
					break;
					
				case KeyEvent.VK_UP:
					if(directie !='J') {
						directie ='Î';
						
					}
					break;
			
				case KeyEvent.VK_DOWN:
					if(directie !='Î') {
						directie ='J';
					}
					break;
			}
		}
	}
}
