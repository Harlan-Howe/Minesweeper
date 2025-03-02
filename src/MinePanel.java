

import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class MinePanel extends JPanel {

	public static final int NUM_CELLS_ACROSS = 20;
	public static final int NUM_CELLS_DOWN = 15;
	public static final int NUM_MINES = 65;
	private MineSquare[][] mySquares;
	private MineSquare pressedSquare;
	/**
	 * Creates the mine panel, including a grid of (numCellsAcross x numCellsDown) MineSquares.
	 *
	 */
	public MinePanel()
	{
		super(new GridLayout(NUM_CELLS_DOWN, NUM_CELLS_ACROSS));
		mySquares = new MineSquare[NUM_CELLS_ACROSS][NUM_CELLS_DOWN];
		for (int i = 0; i< NUM_CELLS_DOWN; i++)
			for (int j = 0; j< NUM_CELLS_ACROSS; j++)
			{
				mySquares[j][i] = new MineSquare();
				add(mySquares[j][i]);
			}
		setPreferredSize(new Dimension(NUM_CELLS_ACROSS *MineSquare.size, NUM_CELLS_DOWN *MineSquare.size));
		setRandomMines();
		doNeighborCount();
		addMouseListener(new clickListener());
	}
	
	/**
	 * precondition: all the cells are cleared - no mines!
	 * postcondition: randomly distributes exactly numMines mines around the grid. 
	 */
	public void setRandomMines()
	{
		Random generator = new Random();
		int x, y;
		boolean placed;
		for (int n = 0; n< NUM_MINES; n++)
		{
			placed = false;
			do
			{
				x = generator.nextInt(NUM_CELLS_ACROSS);
				y = generator.nextInt(NUM_CELLS_DOWN);
				if (!mySquares[x][y].hasAMine())
				{
					mySquares[x][y].setMine(true);
					placed = true;
				}
				//System.out.println("("+x+", "+y+")");
			}while (!placed);
		}
	}
	/**
	 * precondition: the cells in the grid already exist. Presumably, there are some mines out there.
	 * postcondition: each cell in the area now knows how many bombs [0...8] there
	 * are in its neighborhood.
	 *
	 */
	public void doNeighborCount()
	{
		for (int i = 0; i< NUM_CELLS_ACROSS; i++)
			for (int j = 0; j< NUM_CELLS_DOWN; j++)
				countMyNeighbors(i,j);
	}
	
	/**
	 * A "safe" way to check whether there is a mine at the given location, (x,y).
	 * Precondition: The cells in the array exist, but x and y do not need to be
	 * in the range of the grid.
	 * @return true if there is a mine at this location; false if there is no mine or if (x,y) is out of bounds.
	 */
	private boolean locationHasMine(int x, int y)
	{
		if ((x>=0)&&(x< NUM_CELLS_ACROSS)&&(y>=0)&&(y< NUM_CELLS_DOWN))
			return mySquares[x][y].hasAMine();
		return false;
	}
	
	/**
	 * Precondition: the cell at (x,y) exists
	 * Postcondition: the cell now knows how many mines [0...8] are in its 
	 * immediate neighborhood. 
	 */
	private void countMyNeighbors(int x, int y)
	{
		int count = 0;
		for (int i=-1;i<2; i++)
			for (int j=-1;j<2;j++)
				if (locationHasMine(x+i,y+j))
					count++;
		if (locationHasMine(x,y))
			count--;
		mySquares[x][y].setNeighboringMines(count);
	}	
	
	/**
	 * precondition: the cells all exist.
	 * postcondition: any cell with a mine in it has its appearance changed: 
	 * if it has a flag, it shows the bomb, but if it doesn't, it shows an explosion.
	 */
	public void revealAllMines()
	{
		for (int i = 0; i< NUM_CELLS_ACROSS; i++)
			for (int j = 0; j< NUM_CELLS_DOWN; j++)
				if (mySquares[i][j].hasAMine())
					if (mySquares[i][j].getMyStatus()==MineStatus.FLAGGED)
						mySquares[i][j].setMyStatus(MineStatus.BOMB_REVEALED);
					else
						mySquares[i][j].setMyStatus(MineStatus.EXPLODED);
		repaint();
	}
	/**
	 * precondition: all the cells exist.
	 * postcondition: the cells are cleared of mines, new mines are distributed,
	 * the neighboring cells are counted, and the appearance of all the cells
	 * are reset.
	 */
	public void reset()
	{
		for (int i = 0; i< NUM_CELLS_ACROSS; i++)
			for (int j = 0; j< NUM_CELLS_DOWN; j++)
			{	mySquares[i][j].setMyStatus(MineStatus.ORIGINAL);
				mySquares[i][j].setMine(false);
			}
		setRandomMines();
		doNeighborCount();
		pressedSquare=null;
		repaint();
	}
	/**
	 * precondition: None
	 * postcondition: if this cell has zero mines in its neighborhood, it reveals
	 * all its neighbors. Of course, if any of them have zero mines, they reveal 
	 * their neighbors, too.
	 */
	public void checkForZeroes(int x, int y)
	{
		//TODO: this is the recursive method you need to write!

		// Hint: I can think of three base cases that you should consider for an (x,y) pair.
		//     The order you consider them __will__ matter.

		// at some point, you may need to change the status of the cell.

		// suggestion: call all 8 neighbors, even if they might have non-zero neighbors
		// or other issues. Let the base-cases at the start of the method decide whether to
		// quickly return or to do more.

	}
	public class clickListener extends MouseAdapter
	{
		/**
		 * postcondition: the variable pressedSquare is set to the cell where the
		 * button was pressed.
		 */
		public void mousePressed(MouseEvent mEvt)
		{
			int whichX = mEvt.getX()/MineSquare.size;
			int whichY = mEvt.getY()/MineSquare.size;
			if (whichX<0 || whichY<0 || whichX>= NUM_CELLS_ACROSS || whichY>= NUM_CELLS_DOWN)
				return;
			pressedSquare = mySquares[whichX][whichY];
		}
		/**
		 * postcondition: if this is the same cell as when the button was pressed,
		 * it will handle the action of clicking this cell.
		 */
		public void mouseReleased(MouseEvent mEvt)
		{
			//System.out.println("Clicked.");
			int whichX = mEvt.getX()/MineSquare.size;
			int whichY = mEvt.getY()/MineSquare.size;
			if (whichX<0 || whichY<0 || whichX>= NUM_CELLS_ACROSS || whichY>= NUM_CELLS_DOWN)
				return;
			MineSquare clickedSquare = mySquares[whichX][whichY];
			if (clickedSquare != pressedSquare)
			{
				pressedSquare = null;
				return;
			}
			// if this is a shift-click or right mouse button, toggle flag
			if (((mEvt.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_MASK) ||
					SwingUtilities.isRightMouseButton(mEvt))
			{
				if (clickedSquare.getMyStatus()==MineStatus.ORIGINAL)
					clickedSquare.setMyStatus(MineStatus.FLAGGED);
				else if (clickedSquare.getMyStatus()==MineStatus.FLAGGED)
					clickedSquare.setMyStatus(MineStatus.ORIGINAL);
				repaint();
			}
			else // normal (left) click
			{
				if (clickedSquare.hasAMine())
				{
					revealAllMines();
					JOptionPane.showMessageDialog(null, "Play Again?");
					reset();
				}
				else
				{
					checkForZeroes(whichX,whichY);
					clickedSquare.setMyStatus(MineStatus.NUMBER_REVEALED);
					repaint();
				}
			}
			pressedSquare = null;
		}
	}
}
