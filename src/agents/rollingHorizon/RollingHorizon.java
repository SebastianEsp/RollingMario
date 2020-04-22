package agents.rollingHorizon;

import engine.core.MarioForwardModel;
import engine.helper.GameStatus;
import engine.helper.MarioActions;

import javax.xml.transform.Result;
import java.util.*;

public class RollingHorizon {
    MarioForwardModel model;
    int actionSequenceLength = 10;
    int childrenEachGeneration = 25;
    float timeEachFrameMs = 40;
    double mutationRate = 0.2;
    int mutationCounter = 0;
    int tickNumber = 0;
    boolean useShiftBuffer = true;

    private boolean action[];
    ArrayList<ArrayList<String>> actionsList = new ArrayList<>();
    //private boolean actions[][];
    //private ArrayList<ArrayList<String>> actionsQueue = new ArrayList<>();
    private Actions actionList;

    RollingHorizon(MarioForwardModel model) {
        this.model = model.clone();
        this.actionList = new Actions();
    }

    /**
     *
     * @return A list of MarioActions
     */
    public boolean[] getAction(MarioForwardModel model){
        RollingHorizon(model);
        //System.out.print(actionsList);
        //System.out.print("\n");
        ArrayList<String> nextAction = actionsList.get(0);
        System.out.print(nextAction);
        actionsList.remove(0);
        ArrayList<ArrayList<String>> actions = new ArrayList<>();
        //ResultPair result = RollingHorizon(model);
        //If no actions is found take a random action
        /*if (result.actions.size() < 1){
            Random randomGenerator = new Random();
            actions.add(actionList.sample());
            var formattedActions = formatActions(actions);
            return formattedActions;
        }*/

        boolean[] formattedAction = formatAction(nextAction);
        //System.out.print(model.getCompletionPercentage());
        //System.out.print("\n");

        System.out.print("[");
        for (boolean bool : formattedAction){
            System.out.print(bool);
            System.out.print(", ");
        }
        System.out.print("]\n");

        return formattedAction;
    }

    /**
     * Generates a list of randomized MarioActions
     * @return A list of randomized MarioActions
     */
    public ArrayList<ArrayList<String>> GenerateRandomActions(){
        ArrayList<ArrayList<String>> actions = new ArrayList<>();
        for (int i = 0; i < actionSequenceLength; i++) {
                actions.add(actionList.sample());
        }
        return actions;
    }

    public void FillInActions(){
        while (actionsList.size() < actionSequenceLength) {
            actionsList.add(actionList.getRightSpeed());
        }
    }

    /**
     * Mutates actions to allow for some degree of randomization
     * @param actions A boolean list of actions to mutate
     * @return A boolean list of mutated actions
     */
    public ArrayList<ArrayList<String>> MutateAction(ArrayList<ArrayList<String>> actions){
        Random randomGenerator = new Random();
        ArrayList<ArrayList<String>> newActions = new ArrayList<>(actions);
        int randomInt = randomGenerator.nextInt(actionSequenceLength);
        newActions.set(randomInt, actionList.sample());

        for (int i = 0; i < actionSequenceLength; i++) {
            randomInt = randomGenerator.nextInt(100) + 1;
            if ((float)randomInt / 100 < mutationRate){
                newActions.set(i, actionList.sample());
                mutationCounter+=1;
            }
        }
        return newActions;
    }

    /**
     * Gets the reward of a simulation
     * @param newModel A MarioForwardModel containing the state of the game
     * @param newActions A boolean list of actions used to generate the simulation
     * @return The completion percentage from the MarioForwardModel used as reward for the simulation
     */
    public double GetRewardsFromSequence(MarioForwardModel model, ArrayList<ArrayList<String>> newActions, boolean printStuff){
        double reward = -1000000;
        MarioForwardModel newModel = model.clone();
        //System.out.println("BEGIN ADVANCE");

        for ( ArrayList<String> action : newActions) {
            //System.out.println("Advance by action: " + action);
            boolean[] formattedActions = formatActions2(action);
            newModel.advance(formattedActions);
            reward = newModel.getCompletionPercentage();
            if (printStuff){
                System.out.print(reward);
                System.out.print("\n");
            }
            if(newModel.getGameStatus() == GameStatus.LOSE){
                //System.out.println("DEATH");
                reward = newModel.getCompletionPercentage() - 10000;
                return reward;
            }
            else if(newModel.getGameStatus() == GameStatus.WIN){
                reward = newModel.getCompletionPercentage() + 100000;
                return reward;
            }
        }
        return reward;
    }

    public boolean[] formatActions2( ArrayList<String> actions){
        boolean[] formattedActions = new boolean[MarioActions.numberOfActions()];

        for (String action : actions) {
            switch(action) {
                case "left":
                    formattedActions[MarioActions.LEFT.getValue()] = true;
                    break;
                case "Right":
                    formattedActions[MarioActions.RIGHT.getValue()] = true;
                    break;
                case "Down":
                    formattedActions[MarioActions.DOWN.getValue()] = true;
                    break;
                case "Speed":
                    formattedActions[MarioActions.SPEED.getValue()] = true;
                    break;
                case "Jump":
                    formattedActions[MarioActions.JUMP.getValue()] = true;
                    break;
                default:
                    // code block
            }
        }

        return formattedActions;
    }

    public boolean[] formatActions( ArrayList<ArrayList<String>> actions){

        boolean[] formattedActions = new boolean[MarioActions.numberOfActions()];

        for (ArrayList<String> action : actions) {
            if(action.size() == 1){
                if(action.get(0) == "Left"){
                    formattedActions[MarioActions.LEFT.getValue()] = true;
                }else if(action.get(0) == "Right"){
                    formattedActions[MarioActions.RIGHT.getValue()] = true;
                }else if(action.get(0) == "Down"){
                    formattedActions[MarioActions.DOWN.getValue()] = true;
                }else if(action.get(0) == "Speed"){
                    formattedActions[MarioActions.SPEED.getValue()] = true;
                }else if(action.get(0) == "Jump"){
                    formattedActions[MarioActions.JUMP.getValue()] = true;
                }
            }else if(action.size() == 2){
                if(action.get(0) == "Left" && action.get(1) == "Speed"){
                    formattedActions[MarioActions.LEFT.getValue()] = true;
                    formattedActions[MarioActions.SPEED.getValue()] = true;
                }else if(action.get(0) == "Left" && action.get(1) == "Jump"){
                    formattedActions[MarioActions.LEFT.getValue()] = true;
                    formattedActions[MarioActions.JUMP.getValue()] = true;
                }else if(action.get(0) == "Right" && action.get(1) == "Speed"){
                    formattedActions[MarioActions.RIGHT.getValue()] = true;
                    formattedActions[MarioActions.SPEED.getValue()] = true;
                }else if(action.get(0) == "Right" && action.get(1) == "Jump"){
                    formattedActions[MarioActions.RIGHT.getValue()] = true;
                    formattedActions[MarioActions.JUMP.getValue()] = true;
                }else if(action.get(0) == "Jump" && action.get(1) == "Speed"){
                    formattedActions[MarioActions.JUMP.getValue()] = true;
                    formattedActions[MarioActions.SPEED.getValue()] = true;
                }
            }else if(action.size() == 3){
                if(action.get(0) == "Right" && action.get(1) == "Jump" && action.get(2) == "Speed"){
                    formattedActions[MarioActions.RIGHT.getValue()] = true;
                    formattedActions[MarioActions.SPEED.getValue()] = true;
                    formattedActions[MarioActions.JUMP.getValue()] = true;
                }else if(action.get(0) == "Left" && action.get(1) == "Jump" && action.get(2) == "Speed"){
                    formattedActions[MarioActions.LEFT.getValue()] = true;
                    formattedActions[MarioActions.SPEED.getValue()] = true;
                    formattedActions[MarioActions.JUMP.getValue()] = true;
                }
            }
        }

        return formattedActions;
    }

    public boolean[] formatAction( ArrayList<String> action){

        boolean[] formattedActions = new boolean[MarioActions.numberOfActions()];

        if(action.size() == 1){
            if(action.get(0) == "Left"){
                formattedActions[MarioActions.LEFT.getValue()] = true;
            }else if(action.get(0) == "Right"){
                formattedActions[MarioActions.RIGHT.getValue()] = true;
            }else if(action.get(0) == "Down"){
                formattedActions[MarioActions.DOWN.getValue()] = true;
            }else if(action.get(0) == "Speed"){
                formattedActions[MarioActions.SPEED.getValue()] = true;
            }else if(action.get(0) == "Jump"){
                formattedActions[MarioActions.JUMP.getValue()] = true;
            }
        }else if(action.size() == 2){
            if(action.get(0) == "Left" && action.get(1) == "Speed"){
                formattedActions[MarioActions.LEFT.getValue()] = true;
                formattedActions[MarioActions.SPEED.getValue()] = true;
            }else if(action.get(0) == "Left" && action.get(1) == "Jump"){
                formattedActions[MarioActions.LEFT.getValue()] = true;
                formattedActions[MarioActions.JUMP.getValue()] = true;
            }else if(action.get(0) == "Right" && action.get(1) == "Speed"){
                formattedActions[MarioActions.RIGHT.getValue()] = true;
                formattedActions[MarioActions.SPEED.getValue()] = true;
            }else if(action.get(0) == "Right" && action.get(1) == "Jump"){
                formattedActions[MarioActions.RIGHT.getValue()] = true;
                formattedActions[MarioActions.JUMP.getValue()] = true;
            }else if(action.get(0) == "Jump" && action.get(1) == "Speed"){
                formattedActions[MarioActions.JUMP.getValue()] = true;
                formattedActions[MarioActions.SPEED.getValue()] = true;
            }
        }else if(action.size() == 3){
            if(action.get(0) == "Right" && action.get(1) == "Jump" && action.get(2) == "Speed"){
                formattedActions[MarioActions.RIGHT.getValue()] = true;
                formattedActions[MarioActions.SPEED.getValue()] = true;
                formattedActions[MarioActions.JUMP.getValue()] = true;
            }else if(action.get(0) == "Left" && action.get(1) == "Jump" && action.get(2) == "Speed"){
                formattedActions[MarioActions.LEFT.getValue()] = true;
                formattedActions[MarioActions.SPEED.getValue()] = true;
                formattedActions[MarioActions.JUMP.getValue()] = true;
            }
        }
        return formattedActions;
    }


    /**
     * Simulates a new generation based on given actions
     * @param actions A boolean list of possible user inputs. Each input is set to either true or false
     * @return A ResultPair containing the best action and the associated reward
     */
    public ResultPair SimulateGeneration(ArrayList<ArrayList<String>> actions, double oldReward, MarioForwardModel model){
        tickNumber += 1;
        ArrayList<ArrayList<String>> bestActions = new ArrayList<>(actions);
        double bestReward = oldReward;
        //System.out.print(bestReward);
        for (int i = 0; i < childrenEachGeneration; i++) {
            //ArrayList<ArrayList<String>> actionsCopy = new ArrayList<>(actions);
            ArrayList<ArrayList<String>> newActions = MutateAction(actions);
            double newReward = GetRewardsFromSequence(model, newActions, false);
            if (newReward > bestReward) {
                bestReward = newReward;
                bestActions = newActions;
                /*
                System.out.print(tickNumber);
                System.out.print(": YAY\n");
                System.out.print(oldReward);
                System.out.print(newReward);
                System.out.print(bestActions);
                */
            }
        }
        return new ResultPair(bestActions, bestReward);
    }

    /**
     * Main loop of the Rolling Horizon algorithm
     * @return A ResultPair containing an action and the associated reward
     */
    public void RollingHorizon(MarioForwardModel model){

        for (int i = 0; i < model.getEnemiesFloatPos().length; i++) {
            var tmp = model.getEnemiesFloatPos()[i];
            //System.out.println(tmp);
        }

        if (!useShiftBuffer){
            actionsList = new ArrayList<>();
        }

        //Fills up the actionsList
        FillInActions();

        //Initializes some variables
        double reward = GetRewardsFromSequence(model, actionsList, false);
        ResultPair simulationResult = null;
        int numberOfGenerations = 0;
        double timeSpent = 0;
        float maxTime = timeEachFrameMs;
        float averageGenTime = 0;
        boolean runMoreGens = true;

        /*
        System.out.print("Before: ");
        System.out.print(actionsList);
        System.out.print("\n");
        */

        //While there's still time left, run a generation
        while (runMoreGens) {
            long startTime = System.currentTimeMillis();

            simulationResult = SimulateGeneration(actionsList, reward, model);

            /*
            System.out.print("Gen ");
            System.out.print(numberOfGenerations);
            //System.out.print(": Reward: ");
            //System.out.print(simulationResult.reward);
            System.out.print(": Actions: ");
            System.out.print(simulationResult.actions);
            System.out.print("\n");
            */

            /*
            System.out.print(actionsList);
            System.out.print(" : ");
            System.out.print(simulationResult.actions);
            System.out.print("\n");
            */

            actionsList = new ArrayList<>(simulationResult.actions);

            reward = simulationResult.reward;


            //Update variables to see if we should run another generation
            long endTime = System.currentTimeMillis();
            long timeThisGen = endTime - startTime;

            averageGenTime = ((averageGenTime * numberOfGenerations) + timeThisGen) / (numberOfGenerations + 1);
            numberOfGenerations += 1;
            timeSpent += timeThisGen;

            runMoreGens = timeSpent + averageGenTime < maxTime;
        }
        /*
        System.out.print("After: ");
        System.out.print(actionsList);
        System.out.print("\n");
        */
        mutationCounter = 0;
    }

    //Inner class to encompass an actions and it's associated reward
    final class ResultPair {
        private final ArrayList<ArrayList<String>> actions;
        private final double reward;

        public ResultPair(ArrayList<ArrayList<String>> actions, double reward) {
            this.actions = actions;
            this.reward = reward;
        }

        public ArrayList<ArrayList<String>> actions() {
            return actions;
        }

        public double reward() {
            return reward;
        }
    }

    final class Actions{

        private final ArrayList<ArrayList<String>> actions = new ArrayList<ArrayList<String>>();

        Actions(){
            ArrayList<String> action1 = new ArrayList<>(Arrays.asList("Left"));
            ArrayList<String> action2 = new ArrayList<>(Arrays.asList("Left", "Jump"));
            ArrayList<String> action3 = new ArrayList<>(Arrays.asList("Left", "Speed"));
            ArrayList<String> action4 = new ArrayList<>(Arrays.asList("Left", "Jump", "Speed"));

            ArrayList<String> action5 = new ArrayList<>(Arrays.asList("Right"));
            ArrayList<String> action6 = new ArrayList<>(Arrays.asList("Right", "Jump"));
            ArrayList<String> action7 = new ArrayList<>(Arrays.asList("Right", "Speed"));
            ArrayList<String> action8 = new ArrayList<>(Arrays.asList("Right", "Jump", "Speed"));

            ArrayList<String> action9 = new ArrayList<>(Arrays.asList("Down"));
            ArrayList<String> action10 = new ArrayList<>(Arrays.asList("Jump"));
            ArrayList<String> action11 = new ArrayList<>(Arrays.asList("Jump", "Speed"));

            actions.add(action1);
            actions.add(action2);
            actions.add(action3);
            actions.add(action4);
            actions.add(action5);
            actions.add(action6);
            actions.add(action7);
            actions.add(action8);
            //actions.add(action9);
            actions.add(action10);
            actions.add(action11);
        }

        public ArrayList<ArrayList<String>> getActionList(){
            return actions;
        }

        public ArrayList<String> sample(){
            Random rand = new Random();
            return actions.get(rand.nextInt(actions.size()));
        }

        public ArrayList<String> getRightSpeed() {
            return new ArrayList<>(Arrays.asList("Right", "Speed"));
        }

        public ArrayList<String> getRightJumpSpeed() {
            return new ArrayList<>(Arrays.asList("Right", "Jump", "Speed"));
        }
    }

}
