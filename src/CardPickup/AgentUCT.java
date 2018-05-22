package CardPickup;

import java.util.ArrayList;

public class AgentUCT extends Player {
	protected final String newName = "AgentUCT"; //Overwrite this variable in your player subclass

	/**Do not alter this constructor as nothing has been initialized yet. Please use initialize() instead*/
	public AgentUCT() {
		super();
        playerName = newName;
	}
	
	public void initialize() {

	}

    protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c){
    	oppNode = opponentNode;
    	if(opponentPickedUp)
    		oppLastCard = c;
    	else
    		oppLastCard = null;
    }

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
    	float currentRank = 0;
    	int currentBest = 0;
    	HandEvaluator evaluator = new HandEvaluator();
        for(int i = 0; i < sums.length; i++){
            ArrayList<Card> possible = nodes[currentNode].getNeighbor(i).getPossibleCards();
            if(possible != null){
                for(int j = 0; j < possible.size(); j++) {
                	Node[] cpGraph = nodes.clone();
                	Card test = cpGraph[currentNode].getNeighbor(i).getCard();
                	hand.addHoleCard(test);
                	if(currentRank < evaluator.rankHand(hand)) {
                		currentRank = evaluator.rankHand(hand);
                		currentBest = i;
                	}
                	hand.remove(test);
                }                	
            }
        }
        int neighbor = nodes[currentNode].getNeighbor(currentBest).getNodeID();
        return new Action(ActionType.PICKUP, neighbor);
	}
}

