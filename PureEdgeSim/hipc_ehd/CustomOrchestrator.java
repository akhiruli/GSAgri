package hipc_ehd;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import com.mechalikh.pureedgesim.taskorchestrator.Orchestrator;
import data_processor.DataProcessor;
import data_processor.Job;
import hipc_ehd.dag.TaskNode;
import hipc_ehd.oco.algorithm.OcoOffloadingDecision;
import hipc_ehd.sco.algorithm.Offer;

import java.util.*;

import static com.mechalikh.pureedgesim.scenariomanager.SimulationParameters.TYPES.EDGE_DEVICE;
import static com.mechalikh.pureedgesim.taskgenerator.Task.FailureReason.FAILED_BECAUSE_DEVICE_DEAD;
import static com.mechalikh.pureedgesim.taskgenerator.Task.FailureReason.FAILED_DUE_TO_LATENCY;
import static hipc_ehd.GSAgri.prediction.EnergyPrediction.MAX_ENERGY;

public class CustomOrchestrator extends Orchestrator {
    protected Map<Integer, Integer> historyMap = new HashMap<>();
    private SimulationManager simManager;
    private Integer taskFiledDueToEnergy = 0;

    public CustomOrchestrator(SimulationManager simulationManager) {
        super(simulationManager);
        simManager = simulationManager;
        // Initialize the history map
        for (int i = 0; i < nodeList.size(); i++)
            historyMap.put(i, 0);
    }

    protected ComputingNode findComputingNode(String[] architecture, Task task) {
        TaskNode taskNode = (TaskNode) task;

        if(taskNode.isStartTask()){
            taskNode.setStartTime(simManager.getSimulation().clock());
        }
        if(task.getStatus() == Task.Status.FAILED){
            //this is the case where device could not send task for offloading decision
            return null;
        }
        if ("ROUND_ROBIN".equals(algorithm)) {
            return roundRobin(architecture, task);
        } else if ("TRADE_OFF".equals(algorithm)) {
            return tradeOff(architecture, task);
        } else if ("GS_AGRI".equals(algorithm)) {
            ComputingNode computingNode = gsAgriPolicy(architecture, task);
            return computingNode;
            //return gsAgriPolicy(task);
        }else if("SCOPE".equals(algorithm)){
            ComputingNode computingNode = scopePolicy(task);
            //System.out.println("AKHIRUL-Job: "+task.getApplicationID()+" Task: "+task.getId()+" : "+computingNode);
            return computingNode;
        } else if ("RG".equals(algorithm)) {
            return rgPolicy(task);
        } else if ("OCO".equals(algorithm)) {
            ComputingNode computingNode = ocoPolicy(task);
            return computingNode;
        } else {
            throw new IllegalArgumentException(getClass().getSimpleName() + " - Unknown orchestration algorithm '"
                    + algorithm + "', please check the simulation parameters file...");
        }
    }

    @Override
    public void resultsReturned(Task task) {
        TaskNode taskNode = (TaskNode) task;
        taskNode.setTaskDone(true);
        CustomSimMananger customSimMananger = (CustomSimMananger) simManager;
        double currentTime = simManager.getSimulation().clock()/60.0; //converted to min
        double startTime = taskNode.getEdgeDevice().getStartTime();
        boolean energyUpdated = false;
        taskNode.setEndTime(simManager.getSimulation().clock());
        if(task.getStatus() == Task.Status.FAILED) {
            double latency = task.getActualNetworkTime() + task.getActualCpuTime()+ task.getWatingTime();
            System.out.println("Task " + task.getId() + " failed for job " + task.getApplicationID() +
                    " CPU: " + task.getLength() + " node type: " + task.getComputingNode().getType() +
                    " ID:" + task.getComputingNode().getId() + " Reason : " + task.getFailureReason() +
                    " energy level: "+task.getEdgeDevice().getEnergyModel().getBatteryLevel() +
                    " latency: "+latency + " Deadline: "+ task.getMaxLatency());
            if(task.getFailureReason() == FAILED_DUE_TO_LATENCY){
                System.out.println("Network Time: "+task.getActualNetworkTime() + " CPU time: "+ task.getActualCpuTime()+
                        " Waiting time: "+ task.getWatingTime());
            }

            if(task.getFailureReason() ==  FAILED_BECAUSE_DEVICE_DEAD || task.getEdgeDevice().getEnergyModel().getBatteryLevel() <= 0){
                //System.out.println("Task Failed due to energy");
                taskFiledDueToEnergy++;
                updateBattery(taskNode, currentTime);
                energyUpdated = true;
            }
        }

        if(simManager.getScenario().getStringOrchAlgorithm().equals("SCOPE")) {
            double extra_transmission_latency = 0.0;
            if(task.getComputingNode() != null){
                extra_transmission_latency = Helper.calculateTransmissionLatency(task, task.getComputingNode());
            }

            task.addActualNetworkTime(extra_transmission_latency);
        }

        if(!energyUpdated && (currentTime - startTime) > 60){ //Every 60 mins we are predicting the energy
            updateBattery(taskNode, currentTime);
        }

        if(taskNode.isEndTask()) {
            Job app = DataProcessor.scheduledJob.get(taskNode.getApplicationID());
            app.setStatus(true);
            if (customSimMananger.isAllDagCompleted()){
                simLog.setTaskFiledDueToEnergy(taskFiledDueToEnergy);
                customSimMananger.genReport();
                System.out.println("Number of task failed due to energy: "+taskFiledDueToEnergy);
            }
        } else if(!taskNode.isEndTask()) {
            customSimMananger.scheduleSuccessors(taskNode.successors);
        }
    }

    private ComputingNode rgPolicy(Task task){
        TaskNode taskNode = (TaskNode) task;
        if(taskNode.getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
            updateSecStats(true, null, null, taskNode);
            return task.getEdgeDevice();
        }

        ComputingNode computingNode = null;
        DataCenter nearestDC = findNearestEdgeDC(taskNode);
        computingNode = this.getBestServer(nearestDC.nodeList, taskNode);
        if(taskNode.getSecurityLevel() == TaskNode.SecurityLevel.HIGH){
            if(computingNode != null) {
                updateSecStats(false, computingNode, nearestDC, taskNode);
                return computingNode;
            }
        }

        List<ComputingNode> serverList = getAllServers(simManager.getDataCentersManager().getEdgeDatacenterList());
        computingNode = this.getBestServer(serverList, taskNode);
        if(computingNode == null){
            computingNode = simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0);
        }
        updateSecStats(false, computingNode, nearestDC, taskNode);
        return computingNode;
    }

    private ComputingNode ocoPolicy(Task task){
        TaskNode taskNode = (TaskNode) task;
        OcoOffloadingDecision offloadingDecision = OcoOffloadingDecision.getInstance();
        offloadingDecision.getOffloadingDecision(simManager, taskNode);
        if(taskNode.getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
            updateSecStats(true, null, null, taskNode);
            return task.getEdgeDevice();
        }

        ComputingNode computingNode = null;
        DataCenter nearestDC = findNearestEdgeDC(taskNode);
        if(taskNode.getTaskDecision() == TaskNode.TaskDecision.CLOUD_ONLY){
            computingNode = simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0);
        } else{
            computingNode = simManager.getDataCentersManager().getEdgeDatacenterList().
                    get(taskNode.getDcIndex()).nodeList.get(taskNode.getServerIndex());
        }

        updateSecStats(false, computingNode, nearestDC, taskNode);
        return computingNode;
    }

    private ComputingNode scopePolicy(Task task){
        TaskNode taskNode = (TaskNode) task;
        if(taskNode.getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
            updateSecStats(true, null, null, taskNode);
            return task.getEdgeDevice();
        } else{
            DataCenter nearestDC = findNearestEdgeDC(taskNode);
            Offer offer = taskNode.getOffer();
            int dc_index = offer.getDcIndex();
            int server_index = offer.getServerIndex();
            updateSecStats(false, simManager.getDataCentersManager().getEdgeDatacenterList().get(dc_index).nodeList.get(server_index), nearestDC, taskNode);
            return simManager.getDataCentersManager().getEdgeDatacenterList().get(dc_index).nodeList.get(server_index);
        }
    }

    private ComputingNode gsAgriPolicy(String[] architecture, Task task){
        ComputingNode computingNode = null;
        TaskNode taskNode = (TaskNode) task;
        if(!task.getEdgeDevice().isSensor()) {
            if (taskNode.isDummyTask() ||
                    taskNode.getTaskDecision() == TaskNode.TaskDecision.UE_ONLY){
                updateSecStats(true, null, null, taskNode);
                return task.getEdgeDevice();
            }
        } else{
            if(task.getEdgeDevice().isSensor())
                System.out.println("######SENSOR#####");
        }

        DataCenter nearestDC = findNearestEdgeDC(taskNode);
        //System.out.println("DC: "+nearestDC.getName()+" computing: "+nearestDC.nodeList.get(0).getName());

        if(taskNode.getSecurityLevel() == TaskNode.SecurityLevel.HIGH){
            //computingNode = getLeastLoadedServer(nearestDC.nodeList, taskNode);
            computingNode = this.getLeastLoadedServer_new(nearestDC.nodeList, taskNode);
            if(computingNode != null) {
                updateSecStats(false, computingNode, nearestDC, taskNode);
                nearestDC.incrTaskCount();
                return computingNode;
            }
        }

        Collections.sort(simManager.getDataCentersManager().getEdgeDatacenterList(), Comparator.comparingInt(DataCenter::getNumTasks));
        //List<ComputingNode> serverList = getAllServers(simManager.getDataCentersManager().getEdgeDatacenterList());
        //computingNode = this.getLeastLoadedServer_new(serverList, taskNode);
        DataCenter leastTaskCountDC = simManager.getDataCentersManager().getEdgeDatacenterList().get(0);
        computingNode = this.getLeastLoadedServer_new(leastTaskCountDC.nodeList, taskNode);
        if(computingNode == null){
            computingNode = simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0);
        } else{
            leastTaskCountDC.incrTaskCount();
        }

        updateSecStats(false, computingNode, nearestDC, taskNode);
        return computingNode;
    }



    List<ComputingNode> getAllServers(List<DataCenter> dataCenterList){
        List<ComputingNode> nodeList = new ArrayList<>();
        //List<ComputingNode> nodeList = new ArrayList<>();
        for(DataCenter dc : dataCenterList){
            for(ComputingNode computingNode : dc.nodeList) {
                nodeList.add(computingNode);
            }
        }
        return nodeList;
    };

    private void updateSecStats(boolean isUE, ComputingNode computingNode, DataCenter nearestDC, TaskNode taskNode){
        if(taskNode.getSecurityLevel() == TaskNode.SecurityLevel.HIGH ||
                taskNode.getSecurityLevel() == TaskNode.SecurityLevel.NORMAL){
            if(isUE){
                simManager.getSimulationLogger().incrNumHighSecInUE();
            } else{
                if(computingNode.getDcName().equals(nearestDC.getName())){
                    simManager.getSimulationLogger().incrNumHighSecInNearestEdge();
                } else{
                    simManager.getSimulationLogger().inrNumHighSecInOther();
                }
            }
        }
    }

    protected ComputingNode tradeOff(String[] architecture, Task task) {
        int selected = -1;
        double min = -1;
        double new_min;// the computing node with minimum weight;
        ComputingNode node;
        // get best computing node for this task
        for (int i = 0; i < nodeList.size(); i++) {
            node = nodeList.get(i);
            if(task.getId() == 0 && task.getEdgeDevice() == node){
                selected = i;
                break;
            }
            if (offloadingIsPossible(task, node, architecture)) {
                // the weight below represent the priority, the less it is, the more it is
                // suitable for offlaoding, you can change it as you want
                double weight = 1.2; // this is an edge server 'cloudlet', the latency is slightly high then edge
                // devices
                if (node.getType() == SimulationParameters.TYPES.CLOUD) {
                    weight = 1.8; // this is the cloud, it consumes more energy and results in high latency, so
                    // better to avoid it
                } else if (node.getType() == EDGE_DEVICE) {
                    weight = 1.3;// this is an edge device, it results in an extremely low latency, but may
                    // consume more energy.
                }
                new_min = (historyMap.get(i) + 1) * weight * task.getLength() / node.getMipsPerCore();
                if (min == -1 || min > new_min) { // if it is the first iteration, or if this computing node has more
                    // cpu mips and
                    // less waiting tasks
                    min = new_min;
                    // set the first computing node as the best one
                    selected = i;
                }
            }
        }
        if (selected != -1) {
            historyMap.put(selected, historyMap.get(selected) + 1);
            node = nodeList.get(selected);
        } else{
            node = null;
        }
        // assign the tasks to the selected computing node

        return node;
    }

    protected ComputingNode roundRobin(String[] architecture, Task task) {
        int selected = -1;
        int minTasksCount = -1; // Computing node with minimum assigned tasks.
        ComputingNode node = null;
        for (int i = 0; i < nodeList.size(); i++) {
            if (offloadingIsPossible(task, nodeList.get(i), architecture)
                    && (minTasksCount == -1 || minTasksCount > historyMap.get(i))) {
                minTasksCount = historyMap.get(i);
                // if this is the first time,
                // or new min found, so we choose it as the best computing node.
                selected = i;
            }
        }
        // Assign the tasks to the obtained computing node.
        historyMap.put(selected, minTasksCount + 1);
        if(selected != -1){
            node = nodeList.get(selected);
        }
        return node;
    }

    private void updateBattery(TaskNode taskNode, double currentTime){
        //System.out.println("Updating energy for: "+taskNode.getEdgeDevice().getId());
        double currBatteryLevel = taskNode.getEdgeDevice().getEnergyModel().getBatteryLevel();
        taskNode.getEdgeDevice().setStartTime(currentTime);
        double harvestedEnergy = taskNode.getEnergyPredictionModel().getEnergy();
        double total_energy = currBatteryLevel + harvestedEnergy;
        if(total_energy >= MAX_ENERGY) {
            taskNode.getEdgeDevice().getEnergyModel().setBatteryCapacity(MAX_ENERGY);
        } else{
            taskNode.getEdgeDevice().getEnergyModel().setBatteryCapacity(total_energy);
        }

        simLog.addLatencyPenalty(Helper.generateRandomDouble(1.5, 5.0));
    }

    private DataCenter findNearestEdgeDC(Task task){
        DataCenter dataCenter = null;
        List<DataCenter> edgeDcs = simManager.getDataCentersManager().getEdgeDatacenterList();
        for(DataCenter dc : edgeDcs){
            if(sameLocation(dc.nodeList.get(0), task.getEdgeDevice(), SimulationParameters.EDGE_DEVICES_RANGE)
            || (SimulationParameters.ENABLE_ORCHESTRATORS && sameLocation(dc.nodeList.get(0),
                    task.getOrchestrator(), SimulationParameters.EDGE_DEVICES_RANGE))){
                dataCenter = dc;
            }
        }

        return dataCenter;
    }

    private List<DcDistanceIndexPair> sortDcWithDistance(DataCenter nearestDc){
        List<DataCenter> edgeDcs = simManager.getDataCentersManager().getEdgeDatacenterList();
        List<DcDistanceIndexPair> indexDistList = new ArrayList<>();
        for(Integer i = 0; i < edgeDcs.size(); i++){
            Double distance = Helper.calculateDistance(nearestDc, edgeDcs.get(i));
            DcDistanceIndexPair indexDist = new DcDistanceIndexPair();
            indexDist.setDistance(distance);
            indexDist.setIndex(i);
            indexDistList.add(indexDist);
        }

        Collections.sort(indexDistList, new DcDistanceIndexComparator());

        return indexDistList;
    }

    private Integer nearestDC(List<DataCenter> edgeDcs, DataCenter nearestDc){
        double min_dist = Double.MAX_VALUE;
        Integer index = -1;
        for (int i=0; i < edgeDcs.size(); i++) {
            Double distance = Helper.calculateDistance(nearestDc, edgeDcs.get(i));
            if(distance < min_dist){
                min_dist = distance;
                index = i;
            }
        }
        return index;
    }

    private ComputingNode getBestServer(List<ComputingNode> computingNodes, TaskNode task){ //null return means not possible
        ComputingNode schedule_node = null;
        Double min_latency = Double.MAX_VALUE;
        double cpuUtil = 0.0;
        for(ComputingNode computingNode : computingNodes){
            cpuUtil += computingNode.getCurrentCpuUtilization();
        }
        double avgCpuUtil = cpuUtil/computingNodes.size();
        //System.out.println("Average avgCpuUtil: "+avgCpuUtil);
        for(ComputingNode computingNode : computingNodes){
            Double computationTime = Helper.calculateExecutionTime(computingNode, task);
            Double transmissionTime = this.calculateTransmissionLatency(task, computingNode);
            Double latency = computationTime + transmissionTime + 0.012*transmissionTime;
            //System.out.println("getCurrentCpuUtilization: "+computingNode.getCurrentCpuUtilization()+ " latency: "+latency+" max latency: "+ task.getMaxLatency());
            if(computingNode.getCurrentCpuUtilization() <= avgCpuUtil && latency < task.getMaxLatency()
                    && latency < min_latency) {
                min_latency = latency;
                schedule_node = computingNode;
            }
        }

        return schedule_node;
    }


    private ComputingNode getLeastLoadedServer_new(List<ComputingNode> computingNodes, TaskNode task){
        //double leastCpuUtil = Double.MAX_VALUE;
        ComputingNode schedule_node = null;
        Collections.sort(computingNodes, (d1, d2) -> (int) (d1.getAvgCpuUtilization() - d2.getAvgCpuUtilization()));
        double totalCpuUtil = 0.0;
        double min_latency = Double.MAX_VALUE;
        int counter = 0;

        for(ComputingNode computingNode : computingNodes){
            totalCpuUtil += computingNode.getCurrentCpuUtilization();
            //System.out.print("CPU-Utitl: "+computingNode.getAvgCpuUtilization()+":");
        }

        double avgCpuUtil = totalCpuUtil/computingNodes.size();
        for(ComputingNode computingNode : computingNodes){
            if(computingNode.getAvgCpuUtilization() <= avgCpuUtil){
                Double computationTime = Helper.calculateExecutionTime(computingNode, task);
                Double transmissionTime = this.calculateTransmissionLatency(task, computingNode);
                Double latency = computationTime + transmissionTime;// + 0.012*transmissionTime;
                if(computingNode.getAvailableCores() > 0 && latency <= task.getMaxLatency() && min_latency > latency){
                    schedule_node = computingNode;
                    min_latency = latency;
                }
                counter++;
            }
        }

        counter++;
        if(schedule_node == null){
            for(int i=counter; i<computingNodes.size(); i++){
                ComputingNode computingNode = computingNodes.get(i);
                Double computationTime = Helper.calculateExecutionTime(computingNode, task);
                Double transmissionTime = this.calculateTransmissionLatency(task, computingNode);
                Double latency = computationTime + transmissionTime + 0.012*transmissionTime;
                //Double latency = computationTime + transmissionTime;
                if(latency < task.getMaxLatency()){
                    schedule_node = computingNode;
                    break;
                }
            }
        }
        return schedule_node;
    }

    private ComputingNode getLeastLoadedServer(List<ComputingNode> computingNodes, TaskNode task){
        //double leastCpuUtil = Double.MAX_VALUE;
        ComputingNode schedule_node = null;
        Collections.sort(computingNodes, (d1, d2) -> (int) (d2.getAvgCpuUtilization() - d1.getAvgCpuUtilization()));
        double v = computingNodes.size()/1.5;
        int counter = (int) v;
        double min_latency = Double.MAX_VALUE;
        for(int i=0; i<counter; i++){
            ComputingNode computingNode = computingNodes.get(i);
            Double computationTime = Helper.calculateExecutionTime(computingNode, task);
            Double transmissionTime = this.calculateTransmissionLatency(task, computingNode);
            Double latency = computationTime + transmissionTime + 0.012*transmissionTime;
            if(latency < task.getMaxLatency() && min_latency > latency){
                schedule_node = computingNode;
                min_latency = latency;
            }
        }

        if(schedule_node == null){
            for(int i=counter; i<computingNodes.size(); i++){
                ComputingNode computingNode = computingNodes.get(i);
                Double computationTime = Helper.calculateExecutionTime(computingNode, task);
                Double transmissionTime = this.calculateTransmissionLatency(task, computingNode);
                Double latency = computationTime + transmissionTime + 0.012*transmissionTime;
                //Double latency = computationTime + transmissionTime;
                if(latency < task.getMaxLatency()){
                    schedule_node = computingNode;
                    break;
                }
            }
        }
        return schedule_node;
    }

    private double calculateTransmissionLatency(Task task, ComputingNode computingNode){
        double distance = task.getEdgeDevice().getMobilityModel().distanceTo(computingNode);
        double upload_latency = Helper.getWirelessTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);
        double download_latency = Helper.getWirelessTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        return upload_latency + download_latency;
    }

    private void updateTransmissionLatency(Task task, Integer type, double distance){ //0-nearest edge, 1-neighbour edge, 2- cloud
        double upload_latency = 0;
        double download_latency = 0;
        double ul_wir = Helper.getWirelessTransmissionLatency(task.getFileSize());
        double dl_wir = Helper.getWirelessTransmissionLatency(task.getOutputSize());
        if(type == 0){
            upload_latency = ul_wir + Helper.calculatePropagationDelay(distance);
            download_latency = dl_wir + Helper.calculatePropagationDelay(distance);
        } else if(type == 1){
            upload_latency = ul_wir + Helper.getManEdgeTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);
            download_latency = dl_wir + Helper.getManEdgeTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        } else if(type == 2){
            upload_latency = ul_wir + Helper.getManCloudTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);
            download_latency = dl_wir + Helper.getManCloudTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        } else{
            System.out.println("Invalid Type");
        }

        task.setDownloadTransmissionLatency(download_latency);
        task.setUploadTransmissionLatency(upload_latency);
    }

}
