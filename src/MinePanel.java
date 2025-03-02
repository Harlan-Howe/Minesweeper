

import javax.swing.*;
import java.util.Random;
import java.awt.*;
import java.awt.event.*;

public class MinePanel extends JPanel {

	public static final int NUM_COLS = 20;
	public static final int NUM_ROWS = 15;
	public static final int NUM_MINES = 65;
	private MineSquare[][] mySquares;
	private MineSquare pressedSquare;
	private boolean firstClick;
	/**
	 * Creates the mine panel, including a grid of (NUM_ROWS x NUM_COLS) MineSquares.
	 *
	 */
	public MinePanel()
	{
		super(new GridLayout(NUM_ROWS, NUM_COLS));
		mySquares = new MineSquare[NUM_ROWS][NUM_COLS];
		for (int row = 0; row< NUM_ROWS; row++)
			for (int col = 0; col< NUM_COLS; col++)
			{
				mySquares[row][col] = new MineSquare();
				add(mySquares[row][col]);
			}
		setPreferredSize(new Dimension(NUM_COLS *MineSquare.size, NUM_ROWS *MineSquare.size));
		setRandomMines();
		doNeighborCount();
		addMouseListener(new clickListener());
		firstClick = true;
	}
	
	/**
	 * precondition: all the cells are cleared - no mines!
	 * postcondition: randomly distributes exactly numMines mines around the grid. 
	 */
	public void setRandomMines()
	{
		Random generator = new Random();
		int col, row;
		boolean placed;
		for (int n = 0; n< NUM_MINES; n++)
		{
			placed = false;
			do
			{
				col = generator.nextInt(NUM_COLS);
				row = generator.nextInt(NUM_ROWS);
				if (!mySquares[row][col].hasAMine())
				{
					mySquares[row][col].setMine(true);
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
		for (int col = 0; col< NUM_COLS; col++)
			for (int row = 0; row< NUM_ROWS; row++)
				countMyNeighbors(row,col);
	}
	
	/**
	 * A "safe" way to check whether there is a mine at the given location, (row, col).
	 * Precondition: The cells in the array exist, but row and col do not need to be
	 * in the range of the grid.
	 * @return true if there is a mine at this location; false if there is no mine or if (row, col) is out of bounds.
	 */
	private boolean locationHasMine(int row, int col)
	{
		if ((col>=0)&&(col< NUM_COLS)&&(row>=0)&&(row< NUM_ROWS))
			return mySquares[row][col].hasAMine();
		return false;
	}
	
	/**
	 * Precondition: the cell at (row, col) exists
	 * Postcondition: the cell now knows how many mines [0...8] are in its 
	 * immediate neighborhood. 
	 */
	private void countMyNeighbors(int row, int col)
	{
		int count = 0;
		for (int i=-1;i<2; i++)
			for (int j=-1;j<2;j++)
				if (locationHasMine(row+i,col+j))
					count++;
		if (locationHasMine(row,col))
			count--;
		mySquares[row][col].setNeighboringMines(count);
	}	
	
	/**
	 * precondition: the cells all exist.
	 * postcondition: any cell with a mine in it has its appearance changed: 
	 * if it has a flag, it shows the bomb, but if it doesn't, it shows an explosion.
	 */
	public void revealAllMines()
	{
		for (int col = 0; col< NUM_COLS; col++)
			for (int row = 0; row< NUM_ROWS; row++)
				if (mySquares[row][col].hasAMine())
					if (mySquares[row][col].getMyStatus()==MineStatus.FLAGGED)
						mySquares[row][col].setMyStatus(MineStatus.BOMB_REVEALED);
					else
						mySquares[row][col].setMyStatus(MineStatus.EXPLODED);
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
		for (int col = 0; col< NUM_COLS; col++)
			for (int row = 0; row< NUM_ROWS; row++)
			{	mySquares[row][col].setMyStatus(MineStatus.ORIGINAL);
				mySquares[row][col].setMine(false);
			}
		setRandomMines();
		doNeighborCount();
		pressedSquare=null;
		firstClick = true;
		repaint();
	}
	/**
	 * precondition: None
	 * postcondition: if this cell has zero mines in its neighborhood, it reveals
	 * all its neighbors. Of course, if any of them have zero mines, they reveal 
	 * their neighbors, too.
	 */
	public void checkForZeroes(int row, int col)
	{
		//TODO: this is the recursive method you need to write!

		// Hint: I can think of three base cases that you should consider for an (x,y) pair.
		//     The order you consider them __will__ matter.

		// at some point, you may need to change the status of the cell.

		// suggestion: call all 8 neighbors, even if they might have non-zero neighbors
		// or other issues. Let the base-cases at the start of the method decide whether to
		// quickly return or to do more.

	}

	/**
	 * determines whether the user has successfully identified all the cells.
	 * @return - whether the user has met the condition to win.
	 */
	public boolean checkForWin()
	{
		int count_of_revealed = 0;
		int count_of_flagged = 0;
		for (int row = 0; row< NUM_ROWS; row++)
			for (int col = 0; col< NUM_COLS; col++)
			{
				if (mySquares[row][col].getMyStatus() == MineStatus.NUMBER_REVEALED)
					count_of_revealed++;
				if (mySquares[row][col].getMyStatus() == MineStatus.FLAGGED)
					count_of_flagged++;
			}

		return (count_of_revealed == NUM_COLS * NUM_ROWS - NUM_MINES) &&
				(count_of_flagged == NUM_MINES);
	}

	public class clickListener extends MouseAdapter
	{
		/**
		 * postcondition: the variable pressedSquare is set to the cell where the
		 * button was pressed.
		 */
		public void mousePressed(MouseEvent mEvt)
		{
			int whichCol = mEvt.getX()/MineSquare.size;
			int whichRow = mEvt.getY()/MineSquare.size;
			if (whichCol<0 || whichRow<0 || whichCol>= NUM_COLS || whichRow>= NUM_ROWS)
				return;
			pressedSquare = mySquares[whichRow][whichCol];
		}
		/**
		 * postcondition: if this is the same cell as when the button was pressed,
		 * it will handle the action of clicking this cell.
		 */
		public void mouseReleased(MouseEvent mEvt)
		{
			//System.out.println("Clicked.");
			int whichCol = mEvt.getX()/MineSquare.size;
			int whichRow = mEvt.getY()/MineSquare.size;
			if (whichCol<0 || whichRow<0 || whichCol>= NUM_COLS || whichRow>= NUM_ROWS)
				return;
			MineSquare clickedSquare = mySquares[whichRow][whichCol];
			if (clickedSquare != pressedSquare)
			{
				pressedSquare = null;
				return;
			}
			// if this is a shift-click or right mouse button, toggle flag
			if (((mEvt.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)==MouseEvent.SHIFT_DOWN_MASK) ||
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
				if (firstClick && clickedSquare.hasAMine())
				{
					while (clickedSquare.hasAMine())
						reset();
				}
				if (clickedSquare.hasAMine())
				{
					revealAllMines();
					JOptionPane.showMessageDialog(null, "Play Again?");
					reset();
				}
				else
				{
					checkForZeroes(whichCol,whichRow);
					clickedSquare.setMyStatus(MineStatus.NUMBER_REVEALED);
					firstClick = false;
					repaint();

				}
			}
			pressedSquare = null;
			if (checkForWin())
			{
				JOptionPane.showMessageDialog(null, "You win!", "Congratulations", JOptionPane.INFORMATION_MESSAGE);
				reset();
			}
		}
	}
}
