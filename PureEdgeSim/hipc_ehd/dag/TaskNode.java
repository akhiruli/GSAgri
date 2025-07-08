package hipc_ehd.dag;

import com.mechalikh.pureedgesim.taskgenerator.Task;
import data_processor.Job;
import hipc_ehd.GSAgri.prediction.EnergyPrediction;
import hipc_ehd.sco.algorithm.Offer;

import java.util.ArrayList;
import java.util.List;


public class TaskNode extends Task{
    public enum TaskType {
        NONE,
        NORMAL,
        IO_INTENSIVE,
        CPU_INTENSIVE,
        MEM_INTENSIVE
    };

    public enum TaskDecision{
        UE_ONLY,
        MEC_ONLY,
        OPEN,
        CLOUD_ONLY
    };

    public enum SecurityLevel{
        HIGH,
        NORMAL,
        LOW
    }
    public List<TaskNode>   predecessors;
    public List<TaskNode>   successors;
    List<Integer> predecessorsId;
    List<Integer> successorsId;
    private boolean taskDone;
    private Integer level;
    private boolean startTask;
    private boolean endTask;
    private TaskType taskType;
    private boolean isDummyTask;
    private boolean isCritical;
    private double localCost;
    private double remoteCost;
    private boolean isPrefTask;
    private EnergyPrediction energyPredictionModel;
    private Offer offer;
    private EnergyPrediction energyPrediction;
    private int dcIndex;
    private int serverIndex;
    private double startTime;
    private double endTime;

    public List<TaskNode> getSuccessors() {
        return successors;
    }

    public void setSuccessors(List<TaskNode> successors) {
        this.successors = successors;
    }

    public List<TaskNode> getPredecessors() {
        return predecessors;
    }

    public void setPredecessors(List<TaskNode> predecessors) {
        this.predecessors = predecessors;
    }
    public TaskNode(int id, long length){
        super(id, length);
        predecessors = new ArrayList<>();
        successors = new ArrayList<>();
        successorsId = new ArrayList<>();
        predecessorsId = new ArrayList<>();
        taskDone = false;
        level = 0;
        startTask = false;
        endTask = false;
        taskType = TaskType.NORMAL;
        isDummyTask = false;
        taskDecision = TaskDecision.OPEN;
    }

    private SecurityLevel securityLevel;

    public Double getSubDeadline() {
        return subDeadline;
    }

    public void setSubDeadline(Double subDeadline) {
        this.subDeadline = subDeadline;
    }

    private Double subDeadline;

    public double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(double executionTime) {
        this.executionTime = executionTime;
    }

    private double executionTime;

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    private Integer rank;
    private TaskDecision taskDecision;

    public TaskDecision getTaskDecision() {
        return taskDecision;
    }

    public void setTaskDecision(TaskDecision taskDecision) {
        this.taskDecision = taskDecision;
    }

    public boolean isDummyTask() {
        return isDummyTask;
    }

    public void setDummyTask(boolean dummyTask) {
        isDummyTask = dummyTask;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public boolean isStartTask() {
        return startTask;
    }

    public void setStartTask(boolean startTask) {
        this.startTask = startTask;
    }

    public boolean isEndTask() {
        return endTask;
    }

    public void setEndTask(boolean endTask) {
        this.endTask = endTask;
    }

    public boolean isTaskDone() {
        return taskDone;
    }

    public void setTaskDone(boolean taskDone) {
        this.taskDone = taskDone;
    }
    public List<Integer> getPredecessorsId() {
        return predecessorsId;
    }

    public void setPredecessorsId(List<Integer> predecessorsId) {
        this.predecessorsId = predecessorsId;
    }

    public List<Integer> getSuccessorsId() {
        return successorsId;
    }

    public void setSuccessorsId(List<Integer> successorsId) {
        this.successorsId = successorsId;
    }
    public boolean isCritical() {
        return isCritical;
    }

    public void setCritical(boolean critical) {
        isCritical = critical;
    }
    public SecurityLevel getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(SecurityLevel securityLevel) {
        this.securityLevel = securityLevel;
    }
    public double getLocalCost() {
        return localCost;
    }

    public void setLocalCost(double localCost) {
        this.localCost = localCost;
    }

    public double getRemoteCost() {
        return remoteCost;
    }

    public void setRemoteCost(double remoteCost) {
        this.remoteCost = remoteCost;
    }
    public boolean isPrefTask() {
        return isPrefTask;
    }

    public void setPrefTask(boolean prefTask) {
        isPrefTask = prefTask;
    }

    public EnergyPrediction getEnergyPredictionModel() {
        return energyPredictionModel;
    }

    public void setEnergyPredictionModel(EnergyPrediction energyPredictionModel) {
        this.energyPredictionModel = energyPredictionModel;
    }
    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public EnergyPrediction getEnergyPrediction() {
        return energyPrediction;
    }

    public void setEnergyPrediction(EnergyPrediction energyPrediction) {
        this.energyPrediction = energyPrediction;
    }
    public int getServerIndex() {
        return serverIndex;
    }

    public void setServerIndex(int serverIndex) {
        this.serverIndex = serverIndex;
    }

    public int getDcIndex() {
        return dcIndex;
    }

    public void setDcIndex(int dcIndex) {
        this.dcIndex = dcIndex;
    }
    public double getEndTime() {
        return endTime;
    }

    public void setEndTime(double endTime) {
        this.endTime = endTime;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }
}
