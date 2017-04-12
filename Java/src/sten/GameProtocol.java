package sten;

/**
 *	Avgör outputs baserade på inputs och spelets regler.
 *
 *  @author Mia Persson (förmodad)
 * 	@author Philip Ekholm
 *  @author Aron Polner
 * 	2017-03-26
 */

public class GameProtocol{
    private final int SELECTED_ROCK = 0;
    private final int SELECTED_SCISSORS = 1;
    private final int SELECTED_BAG = 2;
    private final int AWAIT_OPPONENT = 3;
    private final int CLIENT_WIN = 4;
    private final int SERVER_WIN = 5;
    private final int EQUAL = 6;
    private final int GAME_OVER = 7;   

    private boolean serverMadeSelection = false;
    private int serverSelection = -1; 
    private int serverScore = 0;
    private int clientScore = 0;

    /*
    *   Kolla först om någon har nått 10 poäng, skicka isf game-over.
    *   Annars kolla om servern har gjort ett drag, annars vänta
    *   Returnera från jämförelsen som utförs i en annan metod.
    */

    public int processClientMessage(int message){
    	
    	if(serverScore >= 10 || clientScore >= 10){
    		return GAME_OVER;
    	}
    	
    	if(serverMadeSelection){
    		int response = comparePlayerSelection(message);
    		
    		return response;
    	}
    	else{
    		return AWAIT_OPPONENT;
    	}
    }

    public void setServerSelection(int selection){
        this.serverMadeSelection = true;
        this.serverSelection = selection;
    }

    public void resetServerSelection(){
        this.serverMadeSelection = false;
        this.serverSelection = -1;
    } 

    /*
    *   Jämför selektion. Vi körde ingen array-struktur då vi tyckte
    *   det var överflödigt, går att hantera bra med switch/if-cases.
    */

    public int comparePlayerSelection(int clientSelection){
    	switch(clientSelection){
		case SELECTED_ROCK:
			if(serverSelection == SELECTED_BAG){
				serverScore++;
				return SERVER_WIN;
			}
			else if(serverSelection == SELECTED_SCISSORS){
				clientScore++;
				return CLIENT_WIN;
			}
			return EQUAL;
		case SELECTED_SCISSORS:
			if(serverSelection == SELECTED_ROCK){
				serverScore++;
				return SERVER_WIN;
			}
			else if(serverSelection == SELECTED_BAG){
				clientScore++;
				return CLIENT_WIN;
			}
			
			return EQUAL;
		case SELECTED_BAG:
			if(serverSelection == SELECTED_SCISSORS){
				serverScore++;
				return SERVER_WIN;
			}
			else if(serverSelection == SELECTED_ROCK){
				clientScore++;
				return CLIENT_WIN;
			}
			return EQUAL;
		default:
			System.out.println("Något gick fel när poäng skulle bestämmas");
		}
    	
    	return GAME_OVER;
    }
  
    public String getScore()
    {
        return "Client: " + clientScore + ", Server: " + serverScore;
    }

    public boolean getScoreIsNotZero()
    {
        return !((clientScore == 0) && (serverScore == 0));
    }
}
