package com.example.tictactoe_app;
import java.util.Random;

public class TicTacToeGame {

    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert}
    // Current difficulty level
    public DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;

    public static final int BOARD_SIZE = 9;
    public int humanWins;
    public int ties;
    public int androidWins;
    public int win;

    public static final char HUMAN_PLAYER = 'X';
    public static final char COMPUTER_PLAYER = 'O';
    public static final char OPEN_SPOT = ' ';
    public static final char OPEN_SPOT_NE = '.';
    public char mBoard[] = {OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT,OPEN_SPOT};
    public char turn;
    public char startTurn;
    public boolean gameOver;
    public boolean gameStarted;

    private Random mRand; //dime si alguien tiene dudas de que por aca nos pasamos el juego nanana


    public TicTacToeGame() {
        // Seed the random number generator
        mRand = new Random();

        startTurn = HUMAN_PLAYER; // Human starts first
        turn = HUMAN_PLAYER;
        gameOver=false;
        gameStarted=false;

        win = 0;                // Set to 1, 2, or 3 when game is over
        humanWins= 0;
        ties= 0;
        androidWins= 0;
    }


    // Check for a winner.  Return
    //  0 if no winner or tie yet
    //  1 if it's a tie
    //  2 if X won
    //  3 if O won

    public int checkForWinner() {
        // Check horizontal wins
        for (int i = 0; i <= 6; i += 3)	{
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+1] == HUMAN_PLAYER &&
                    mBoard[i+2]== HUMAN_PLAYER){
                win = 2;
                return 2;}
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+1]== COMPUTER_PLAYER &&
                    mBoard[i+2] == COMPUTER_PLAYER){
                win = 3;
                return 3;}
        }
        // Check vertical wins
        for (int i = 0; i <= 2; i++) {
            if (mBoard[i] == HUMAN_PLAYER &&
                    mBoard[i+3] == HUMAN_PLAYER &&
                    mBoard[i+6]== HUMAN_PLAYER){
                win = 2;
                return 2;}
            if (mBoard[i] == COMPUTER_PLAYER &&
                    mBoard[i+3] == COMPUTER_PLAYER &&
                    mBoard[i+6]== COMPUTER_PLAYER){
                win = 3;
                return 3;}
        }
        // Check for diagonal wins
        if ((mBoard[0] == HUMAN_PLAYER &&
                mBoard[4] == HUMAN_PLAYER &&
                mBoard[8] == HUMAN_PLAYER) ||
                (mBoard[2] == HUMAN_PLAYER &&
                        mBoard[4] == HUMAN_PLAYER &&
                        mBoard[6] == HUMAN_PLAYER)){
            win = 2;
            return 2;}
        if ((mBoard[0] == COMPUTER_PLAYER &&
                mBoard[4] == COMPUTER_PLAYER &&
                mBoard[8] == COMPUTER_PLAYER) ||
                (mBoard[2] == COMPUTER_PLAYER &&
                        mBoard[4] == COMPUTER_PLAYER &&
                        mBoard[6] == COMPUTER_PLAYER)){
            win = 3;
            return 3;}
        // Check for tie
        for (int i = 0; i < BOARD_SIZE; i++) {
            // If we find a number, then no one has won yet
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER){
                win = 0;
                return 0;}
        }
        // If we make it through the previous loop, all places are taken, so it's a tie
        win = 1;
        return 1;
    }


    public int getComputerMove() {
        int move = -1;
        if (mDifficultyLevel == DifficultyLevel.Easy) {
            move = getRandomMove();
        }else if (mDifficultyLevel == DifficultyLevel.Harder) {
            move = getWinningMove();
            if (move == -1)
                move = getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Expert) {
            // Try to win, but if that's not possible, block.
            // If that's not possible, move anywhere.
            move = getWinningMove();
            if (move == -1)
                move = getBlockingMove();
            if (move == -1)
                move = getRandomMove();
        }
        return move;
    }


    public int getRandomMove(){
        int move;
        do
        {
            move = mRand.nextInt(BOARD_SIZE);
        } while (mBoard[move] == HUMAN_PLAYER || mBoard[move] == COMPUTER_PLAYER);

        mBoard[move] = COMPUTER_PLAYER;
        return move;
    }


    public int getWinningMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                mBoard[i] = COMPUTER_PLAYER;
                if (checkForWinner() == 3) {
                    return i;
                }else{
                    mBoard[i] = OPEN_SPOT;
                }
            }
        }
        return -1;
    }


    public int getBlockingMove(){
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (mBoard[i] != HUMAN_PLAYER && mBoard[i] != COMPUTER_PLAYER) {
                mBoard[i] = HUMAN_PLAYER;
                if (checkForWinner() == 2) {
                    mBoard[i] = COMPUTER_PLAYER;
                    return i;
                }else{
                    mBoard[i] = OPEN_SPOT;
                }
            }
        }
        return -1;
    }


    /** Clear the board of all X's and O's by setting all spots to OPEN_SPOT. */
    public void clearBoard(){
        for(int i=0; i < BOARD_SIZE; i++){
            mBoard[i]=OPEN_SPOT;
        }
        win=0;
        gameOver=false;
    }
    /** Set the given player at the given location on the game board.
     * The location must be available, or the board will not be changed.
     *
     * @param player - The HUMAN_PLAYER or COMPUTER_PLAYER
     * @param location - The location (0-8) to place the move
     */
    public boolean setMove(char player, int location){
        if(mBoard[location]==OPEN_SPOT) {
            mBoard[location]=player;
            return true;
        }
        else return false;
    }


    public DifficultyLevel getDifficultyLevel() {
        return mDifficultyLevel;
    }
    public void setDifficultyLevel(DifficultyLevel difficultyLevel) {
        mDifficultyLevel = difficultyLevel;
    }


    public void counter(int playerWin) {
        if(playerWin==1) ties++;
        else if(playerWin==2) humanWins++;
        else if(playerWin==3) androidWins++;
        else return;
    }

    public void restartCount(){
        humanWins= 0;
        ties= 0;
        androidWins= 0;
    }

    public char getBoardOccupant(int i){
        return mBoard[i];
    }

    public boolean gameOver(){
        if(win!=0) return true;
        else return false;
    }

    public char[] getBoardState(){
        return mBoard;
    }

    public void setStartedGame(){
        if(gameStarted==true){
            gameStarted=false;
        }else gameStarted=true;
    }

    public boolean getStartedGame(){
        return gameStarted;
    }

    public void toChangeTurn(){
        if (this.turn==HUMAN_PLAYER)this.turn=COMPUTER_PLAYER;
        else if (this.turn==COMPUTER_PLAYER)this.turn=HUMAN_PLAYER;
    }

    public void toChangeStartTurn(){
        if (this.startTurn==HUMAN_PLAYER)this.startTurn=COMPUTER_PLAYER;
        else if (this.startTurn==COMPUTER_PLAYER)this.startTurn=HUMAN_PLAYER;
    }

    public void setTurn(char turn){
        this.turn=turn;
    }

}
