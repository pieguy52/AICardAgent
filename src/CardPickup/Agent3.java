package CardPickup;

import java.util.ArrayList;

public class Agent3 extends Player {

	protected final String newName = "Agent3"; //Overwrite this variable in your player subclass

	/**Do not alter this constructor as nothing has been initialized yet. Please use initialize() instead*/
	public Agent3() {
		super();
        playerName = newName;
        
	}
	
	public void initialize() {
		//WRITE ANY INITIALIZATION COMPUTATIONS HERE
		//burnt = false;
	}
	
	/**
     * THIS METHOD SHOULD BE OVERRIDDEN if you wish to make computations off of the opponent's moves. 
     * GameMaster will call this to update your player on the opponent's actions. This method is called
     * after the opponent has made a move.
     * 
     * @param opponentNode Opponent's current location
     * @param opponentPickedUp Notifies if the opponent picked up a card last turn
     * @param c The card that the opponent picked up, if any (null if the opponent did not pick up a card)
     */
    protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c){
    	oppNode = opponentNode;
    	if(opponentPickedUp)
    		oppLastCard = c;
    	else
    		oppLastCard = null;
    }

    /**
     * THIS METHOD SHOULD BE OVERRIDDEN if you wish to make computations off of your results.
     * GameMaster will call this to update you on your actions.
     *
     * @param currentNode Opponent's current location
     * @param c The card that you picked up, if any (null if you did not pick up a card)
     */
    protected void actionResult(int currentNode, Card c){
        this.currentNode = currentNode;
        if(c!=null)
            addCardToHand(c);
    }

	
	public Action makeAction() {
		if(hand.size()==5)
			return new Action();//end
		Card max_card = hand.getHoleCard(0);
		for(int i = 1; i < hand.size(); i++) {
			if(max_card.compareTo(hand.getHoleCard(i)) < 0)
				max_card = hand.getHoleCard(i);
		}
		int[] sums = new int[nodes[currentNode].getNeighborAmount()];
        for(int i = 0; i < sums.length; i++){
            ArrayList<Card> possible = nodes[currentNode].getNeighbor(i).getPossibleCards();
            if(possible != null){
                for(int j = 0; j < possible.size(); j++) {
                	if(max_card.getSuit() == possible.get(j).getSuit() &&
                			((max_card.getRank() == (possible.get(j).getRank()+1) || 
        					max_card.getRank() == (possible.get(j).getRank()-1)))) {
        		        return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());}
                	else if(max_card.getRank() == possible.get(j).getRank()) {
                		return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());}
                	else if(max_card.getSuit() == possible.get(j).getSuit()){
                		return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());}
                	else if(max_card.getSuit() != possible.get(j).getSuit() &&
                			((max_card.getRank() == (possible.get(j).getRank()+1) || 
        					max_card.getRank() == (possible.get(j).getRank()-1)))){
                		return new Action(ActionType.PICKUP, nodes[currentNode].getNeighbor(i).getNodeID());
                	}
                	else {
                		    sums[i] += possible.get(j).getRank();
                     
                	}
                }                	
            }
        }
        int maxIndex = 0;
        for(int k = 1; k < sums.length; k++)
            if(sums[maxIndex] < sums[k])
                maxIndex = k;
        int neighbor = nodes[currentNode].getNeighbor(maxIndex).getNodeID();
        return new Action(ActionType.PICKUP, neighbor);
	}
}

