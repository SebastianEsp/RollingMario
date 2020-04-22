package agents.rollingHorizon;

import engine.core.MarioAgent;
import engine.core.MarioForwardModel;
import engine.core.MarioTimer;
import engine.helper.MarioActions;

import java.util.ArrayList;

public class Agent implements MarioAgent {
    private boolean tmp[];
    private boolean[] action;
    private RollingHorizon rh;

    @Override
    public void initialize(MarioForwardModel model, MarioTimer timer) {
        this.rh = new RollingHorizon(model);
    }

    @Override
    public boolean[] getActions(MarioForwardModel model, MarioTimer timer) {
        action = this.rh.getAction(model);
        //System.out.println(action);
        //tmp = new boolean[MarioActions.numberOfActions()];
        //tmp[MarioActions.RIGHT.getValue()] = true;
        for (boolean a:action) {
            //System.out.println(a);
        }

        return action;
    }

    @Override
    public String getAgentName() {
	return "RollingHorizonAgent";
    }
}
