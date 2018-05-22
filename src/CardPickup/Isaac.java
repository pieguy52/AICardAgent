package CardPickup;

import java.util.ArrayList;
import java.util.Random;

public class Isaac extends Player {

    protected final String newName = "Isaac";
    HandEvaluator hE;
    Random rand;

    public Isaac() {
        super();
        playerName = newName;
    }

    @Override
    public void initialize() {
        hE = new HandEvaluator();
        rand = new Random();
    }

    @Override
    public Action makeAction() {
        double t = budget;
        if(hand.getNumHole() == 5)
            return new Action(); //test for burn theory
        int dest = nodes[currentNode].getNodeID();
        ArrayList<Node> neighbors = nodes[currentNode].getNeighborList();
        Hand temp = new Hand();//temp hand to hold onto cards for hand eval
        for(int i = 0; i < hand.size(); i++)
            temp.addHoleCard(hand.getHoleCard(i));
        if(neighbors.size() == 1)
            dest = neighbors.get(0).getNodeID();
        else if(!neighbors.isEmpty()){
            for(Node neighbor: neighbors){
                float currRank = hE.rankHand(temp);
                for(Card c: neighbor.getPossibleCards()){
                    temp.addHoleCard(c);
                    if(currRank < hE.rankHand(temp)) {
                        currRank = hE.rankHand(temp);
                        dest = neighbor.getNodeID();
                    }
                    temp.remove(c);
                }
            }
        }
        float dE = hE.rankHand(hand) - hE.rankHand(temp);
        if(dE > 1) {
            double prob = Math.pow(Math.E, (dE / t));
            double counter = rand.nextDouble();
            if (counter < prob) {
                int bound = rand.nextInt(nodes[currentNode].getNeighborAmount() - 1);
                dest = nodes[currentNode].getNeighbor(bound).getNodeID();
                return new Action(ActionType.MOVE, dest);
            }
        }
        if(!nodes[dest].getPossibleCards().isEmpty())
            return new Action(ActionType.PICKUP, dest);
        else {
            int prob = rand.nextInt(nodes[currentNode].getNeighborAmount());
            dest = nodes[currentNode].getNeighbor(prob).getNodeID();
            return new Action(ActionType.MOVE, dest);
        }
    }

    @Override
    protected void actionResult(int currentNode, Card c){
        this.currentNode = currentNode;
        if(c!=null) {
            addCardToHand(c);
            nodes[currentNode].clearPossibleCards();
        }
    }

    @Override
    protected void opponentAction(int opponentNode, boolean opponentPickedUp, Card c){
        oppNode = opponentNode;
        if(opponentPickedUp) {
            oppLastCard = c;
            nodes[opponentNode].clearPossibleCards();
        }
        else
            oppLastCard = null;
    }
}
