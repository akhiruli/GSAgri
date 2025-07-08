package hipc_ehd.GSAgri.algorithm;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import hipc_ehd.Helper;
import hipc_ehd.dag.TaskNode;

import java.util.Map;

public class TaskCriticality {
    static double ENERGY_THRESHOLD = 0.8;
    static double OUT_DEGREE_THRESHOLD = 0.3;
    static double LATENCY_DEADLINE_RATIO_THRESHOLD = 0.8;
    private SimulationManager simulationManager;
    public TaskCriticality(SimulationManager simManager){
        this.simulationManager = simManager;
    }
    public void assignTaskCriticality(Map<Integer, TaskNode> job){
        double total_energy = 0.0;
        double total_out_degree = 0;
        int critical_task = 0;
        DataCenter cloudDc = simulationManager.getDataCentersManager().getCloudDatacentersList().get(0);
        DataCenter edgeDC = simulationManager.getDataCentersManager().getEdgeDatacenterList().get(0);

        for (Map.Entry<Integer, TaskNode> taskInfo : job.entrySet()) {
            TaskNode task = taskInfo.getValue();
            total_out_degree += task.successors.size();

            total_energy += Helper.dynamicEnergyConsumption(task.getLength(),
                    task.getComputingNode().getMipsPerCore(), task.getComputingNode().getMipsPerCore());
        }

        for (Map.Entry<Integer, TaskNode> taskInfo : job.entrySet()) {
            TaskNode task = taskInfo.getValue();

            double energy = Helper.dynamicEnergyConsumption(task.getLength(),
                    task.getComputingNode().getMipsPerCore(), task.getComputingNode().getMipsPerCore());
            if(energy/total_energy > ENERGY_THRESHOLD){
                task.setCritical(true);
                critical_task++;
                //System.out.println("Criticality: energy: "+ energy +":"+total_energy+":"+energy/total_energy);
                continue;
            }

            if((double) task.successors.size()/total_out_degree > OUT_DEGREE_THRESHOLD){
                task.setCritical(true);
                critical_task++;
                //System.out.println("Criticality: degree: "+ task.successors.size() +":"+total_out_degree+":"+task.successors.size()/total_out_degree);
                continue;
            }
            double avgLatency = Helper.calculateAverageLatency(task, task.getEdgeDevice(), edgeDC.nodeList.get(0), cloudDc.nodeList.get(0));
            if(avgLatency/task.getMaxLatency() > LATENCY_DEADLINE_RATIO_THRESHOLD){
                critical_task++;
                task.setCritical(true);
                //System.out.println("Criticality: latency: "+ avgLatency +":"+task.getMaxLatency()+":"+avgLatency/task.getMaxLatency());
            }
        }

        //System.out.println("Total tasks: "+job.size()+ " Crtical tasks: "+critical_task);
    }
}
