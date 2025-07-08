package hipc_ehd.oco.algorithm;

import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import hipc_ehd.CustomSimMananger;
import hipc_ehd.Helper;
import hipc_ehd.dag.TaskNode;


import java.util.ArrayList;
import java.util.List;

public class OcoOffloadingDecision {
    private static final double HARVESTABLE_ENERGY_MEAN = 5;
    private static final double HARVESTABLE_ENERGY_SCALE = 4e-7;
    private static final double TASK_LENGTH = 1000;
    private static final double CPU_CYCLES = 1000;
    private static final double ENERGY_QUEUE_STABILIZATION_THRESHOLD = 2e-4;
    private static final double COST_PENALTY = 2e-9;
    private static final double V = 1e-6; // Trade-off parameter between energy and cost
    private static volatile OcoOffloadingDecision instance;
    private OcoOffloadingDecision(){
    }
    public static OcoOffloadingDecision getInstance() {
        if (instance == null) {
            synchronized (OcoOffloadingDecision.class) {
                if (instance == null) {
                    instance = new OcoOffloadingDecision();
                }
            }
        }
        return instance;
    }

    public void getOffloadingDecision(SimulationManager simMananger, TaskNode taskNode){
        double localExecutionEnergyCost = Helper.dynamicEnergyConsumption(taskNode.getLength(),
                taskNode.getEdgeDevice().getMipsPerCore(), taskNode.getEdgeDevice().getMipsPerCore());

        List<DataCenter>  dcList= simMananger.getDataCentersManager().getEdgeDatacenterList();
        List<List<Double>> offloadingEnergyCosts = new ArrayList<>();
        for(int i=0; i<dcList.size(); i++) {
            List<Double> serverCostList = new ArrayList<>();
            for(int s = 0; s < dcList.get(i).nodeList.size(); s++) {
                Double offloadingEnergyCost = Helper.calculateRemoteEnergyConsumption(taskNode) * Helper.generateRandomDouble(0.4, .6);
                //Double offloadingEnergyCost = Helper.calculateRemoteEnergyConsumption(taskNode)* Helper.generateRandomDouble(0.0038, 0.08);
                //Double offloadingEnergyCost = Helper.calculateRemoteEnergyConsumption(taskNode)* Helper.generateRandomDouble(150, 200);
                //Double offloadingEnergyCost = Helper.calculateRemoteEnergyConsumption(taskNode)* Helper.generateRandomDouble(150, 200);
                serverCostList.add(offloadingEnergyCost);
            }
            offloadingEnergyCosts.add(serverCostList);
        }
        // Calculate the cost penalty of dropping the task
        double droppingCostPenalty = COST_PENALTY * taskNode.getLength();

        double minCost = Math.min(localExecutionEnergyCost, droppingCostPenalty);
        for(int i=0; i<dcList.size(); i++) {
            for(int s = 0; s < dcList.get(i).nodeList.size(); s++) {
                minCost = Math.min(minCost, offloadingEnergyCosts.get(i).get(s));
            }
        }

        // Make decision based on the minimum cost
        if (minCost == localExecutionEnergyCost) {
            // Execute the task locally
            taskNode.setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
        } else if (minCost == droppingCostPenalty) {
            // Drop the task
            System.out.println("drops the task in time slot ");
            taskNode.setTaskDecision(TaskNode.TaskDecision.CLOUD_ONLY);
        } else {
            // Offload the task to the server with the minimum offloading cost
            int r = Helper.getRandomInteger(0, 14);
            if(r == 0){
                taskNode.setTaskDecision(TaskNode.TaskDecision.CLOUD_ONLY);
            } else {
                int bestServer = 0;
                int bestDc = 0;
                for (int i = 0; i < dcList.size(); i++) {
                    for (int s = 0; s < dcList.get(i).nodeList.size(); s++) {
                        if (offloadingEnergyCosts.get(i).get(s) == minCost) {
                            bestDc = i;
                            bestServer = s;
                            break;
                        }
                    }
                }

                taskNode.setDcIndex(bestDc);
                taskNode.setServerIndex(bestServer);
                taskNode.setTaskDecision(TaskNode.TaskDecision.MEC_ONLY);
            }
        }
    }
}