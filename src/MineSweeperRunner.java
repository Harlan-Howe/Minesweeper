import javax.swing.JFrame;
import java.awt.FlowLayout;

public class MineSweeperRunner extends JFrame
{
	public static void main(String[] args)
	{
		MineSweeperRunner app = new MineSweeperRunner();
		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public MineSweeperRunner()
	{
		super("MineSweeper");
		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(new MinePanel());
		setSize(MineSquare.size*MinePanel.NUM_CELLS_ACROSS,MineSquare.size*MinePanel.NUM_CELLS_DOWN +32);
		setVisible(true);
		setResizable(false);
	}
}
