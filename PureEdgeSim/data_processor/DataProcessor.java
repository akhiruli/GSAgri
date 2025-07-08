package data_processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.simulationmanager.SimulationManager;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import hipc_ehd.GSAgri.algorithm.GraphPartition;
import hipc_ehd.GSAgri.algorithm.TaskCriticality;
import hipc_ehd.GSAgri.prediction.EnergyPrediction;
import hipc_ehd.Helper;
import hipc_ehd.dag.TaskNode;
import hipc_ehd.networkdataset.NetworkDataset;
import hipc_ehd.sco.algorithm.ScoAlgorithm;
import org.jgrapht.alg.util.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static hipc_ehd.GSAgri.prediction.EnergyPrediction.MAX_ENERGY;

public class DataProcessor {
    public static boolean isReal = false;
    public static Map<Integer, Job> jobsMap;
    public static Map<Integer, Job> scheduledJob;
    public static Map<Integer, Map<Integer, TaskNode>> tasksMap;
    private List<? extends ComputingNode> devices;
    SimulationManager simManager;
    private Integer ueDeviceIndex;
    // Download TaskDetails.txt and JobDetails.json from zenodo and provide the file location below
    static final String task_dataset_path = "TaskDetails.txt";
    static final String job_dataset_path = "JobDetails.json";
    String realAppCsvFile = "PureEdgeSim/real_dataset/tomato_bacterial_disease.csv";
    static Integer totalCpuIntensiveTask = 0;
    EnergyPrediction energyPrediction = null;
    private long algoTime = 0;
    public DataProcessor(SimulationManager simulationManager,
                         List<? extends ComputingNode> devicesList){
        jobsMap = new HashMap();
        tasksMap = new HashMap<>();
        scheduledJob = new HashMap<>();
        simManager = simulationManager;
        devices = devicesList;
        ueDeviceIndex = 0;
        energyPrediction = new EnergyPrediction(simulationManager);
        energyPrediction.loadEnergyProduction();

        if(isReal){
            loadRealApps();
            //generateRealApps(150);
        } else {
            this.loadJobs();
            this.loadTasks();
        }

        NetworkDataset obj = NetworkDataset.getInstance();
        //System.out.println("DataProcessor: " + jobsMap.size() + "-" + tasksMap.size());
    }

    public Map<Integer, Job> getJobsMap() {
        return jobsMap;
    }

    public Map<Integer, Map<Integer, TaskNode>> getTasksMap() {
        return tasksMap;
    }
    public List<Task> getTaskList(){
        TaskCriticality taskCriticality = new TaskCriticality(simManager);
        GraphPartition graphPartition = new GraphPartition(simManager);
        ScoAlgorithm scoAlgorithm = new ScoAlgorithm(simManager);
        List<Task> taskList = new ArrayList<>();
        this.assignDependencies();
        this.assignUEDevice();
        this.assignStartAndEndTask(); //if required
        //this.updateData(3);
        Integer job_count = 0;
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            if(!isValidDag(job)){
                System.out.println("DAG with ap_id: " + entry.getKey() + " is not valid");
                continue;
            }

//            if(job.size() > 20) { //small
//                continue;
//            }

//            if(job.size() < 20 && job.size() > 50) { //medium
//                continue;
//            }
//            if(job.size() < 50){
//                continue;
//            }

            taskCriticality.assignTaskCriticality(job);
            setSecurityLevel(job);
            if(simManager.getScenario().getStringOrchAlgorithm().equals("GS_AGRI")) {
                double startTime = System.currentTimeMillis();
                Pair<List<TaskNode>, List<TaskNode>> partition = graphPartition.partition(job);
                graphPartition.assignOffloadingDecision(partition);
                setHighPrefTasks(job);
                double endTime = System.currentTimeMillis();
                algoTime += (endTime-startTime);
            } else if(simManager.getScenario().getStringOrchAlgorithm().equals("SCOPE")){
                double budget = allocateBudgetToJob(job);
                scoAlgorithm.partition_new(job, budget);
            } else if (simManager.getScenario().getStringOrchAlgorithm().equals("RG")) {
                partition(job);
            }

            job_count++;
//            if(job_count == 25)
//                continue;
            List<TaskNode> tempTaskList = new ArrayList<>();
            for (Map.Entry<Integer, TaskNode> task : job.entrySet()) {
                //task.getValue().setContainerSize(jobsMap.get(entry.getKey()).getAvgTaskLength());
                taskList.add(task.getValue());
                tempTaskList.add(task.getValue());
            }

            scheduledJob.put(entry.getKey(), jobsMap.get(entry.getKey()));

            if(job_count >=60)
               break;
        }
        return taskList;
    }

    private boolean isValidForSec(int[] indexes, int counter){
        for(int value : indexes){
            if(counter == value){
                return true;
            }
        }

        return false;
    }

    private void setSecurityLevel(Map<Integer, TaskNode> job) {
        Integer numTask = (job.size()*Helper.getRandomInteger(5, 10))/100;
        int[] indexes = Helper.generateRandomArray(numTask, 0, job.size());
        int counter = 0;
        for (Map.Entry<Integer, TaskNode> taskInfo : job.entrySet()) {
            TaskNode task = taskInfo.getValue();
            boolean isSecSet = false;
            if(isValidForSec(indexes, counter)){
                if(counter%2 == 0){
                    task.setSecurityLevel(TaskNode.SecurityLevel.HIGH);
                } else{
                    task.setSecurityLevel(TaskNode.SecurityLevel.NORMAL);
                }
                isSecSet = true;
            }
            counter++;
        }
    }

    private void partition(Map<Integer, TaskNode> job){
        for (Map.Entry<Integer, TaskNode> taskInfo : job.entrySet()) {
            TaskNode task = taskInfo.getValue();
            //Integer random_num = Helper.getRandomInteger(0,2);
            double r = Helper.generateRandomDouble(0, 3.7);
            if(r < 1.0){
                task.setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
            } else{
                task.setTaskDecision(TaskNode.TaskDecision.OPEN);
            }
           /* if(random_num == 0){
                task.setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
            } else{
                task.setTaskDecision(TaskNode.TaskDecision.OPEN);
            }*/
        }
    }

    private void assignDecision(Pair<List<TaskNode>, List<TaskNode>> partition){
        List<TaskNode> local = partition.getFirst();
        for(TaskNode taskNode : local){
            taskNode.setTaskDecision(TaskNode.TaskDecision.UE_ONLY);
        }
    }

    public static Comparator<TaskNode> getCompByCPUNeed()
    {
        Comparator comp = new Comparator<TaskNode>(){
            @Override
            public int compare(TaskNode t1, TaskNode t2)
            {
                if (t1.getLength() == t2.getLength())
                    return 0;
                else if(t1.getLength() < t2.getLength())
                    return -1;
                else
                    return 1;
            }
        };
        return comp;
    }


    void printDag(Map<Integer, TaskNode> job){
        for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
            TaskNode taskNode = task.getValue();
            String pred_str = "";
            String succ_str = "";
            for(TaskNode t : taskNode.successors){
                if(succ_str.isEmpty()){
                    succ_str += t.getId();
                } else{
                    succ_str += "->" + t.getId();
                }
            }

            for(TaskNode t : taskNode.predecessors){
                if(pred_str.isEmpty()){
                    pred_str += t.getId();
                } else{
                    pred_str += "->" + t.getId();
                }
            }

            System.out.println("Task: " + taskNode.getId() + " predecessors: " + pred_str + " Successors: " + succ_str);
        }
    }

    boolean isValidDag(Map<Integer, TaskNode> job){
        boolean result = true;
        for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
            TaskNode taskNode = task.getValue();
            if (taskNode.isStartTask()) {
                if (taskNode.predecessors.size() != 0 || taskNode.successors.size() == 0) {
                    result = false;
                    break;
                }
            } else if (taskNode.isEndTask()) {
                if (taskNode.predecessors.size() == 0 || taskNode.successors.size() != 0) {
                    result = false;
                    break;
                }
            } else {
                if (taskNode.predecessors.size() == 0 || taskNode.successors.size() == 0) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }
    void assignUEDevice(){
        if(devices.size() == 0) {
            System.out.println("No UE devices available");
            return;
        }

        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()){
                TaskNode taskNode = task.getValue();
                double currentTime = simManager.getSimulation().clock();
                if(devices.get(ueDeviceIndex).getMipsPerCore() == 0) {
                    System.out.println("AKHIRUL-MIPS zero");
                }
                taskNode.setEdgeDevice(devices.get(ueDeviceIndex));
                taskNode.getEdgeDevice().setStartTime(currentTime);
                if(!taskNode.getEdgeDevice().getEnergyModel().isBatteryPowered()){
                    taskNode.getEdgeDevice().getEnergyModel().setBatteryCapacity(18.5);
                }
                //double energyHarevested = energyPrediction.getEnergy();
                //System.out.println("AKHIRUL: energyHarevested-"+energyHarevested);
                //taskNode.getEdgeDevice().getEnergyModel().setBatteryCapacity(energyHarevested > MAX_ENERGY?MAX_ENERGY:energyHarevested);
            }

            ueDeviceIndex++;
            if(ueDeviceIndex >= devices.size()){
                ueDeviceIndex = 0;
            }
        }
    }

    void assignDependencies(){
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()){
                TaskNode taskNode = task.getValue();
                for(Integer taskId : taskNode.getSuccessorsId()){
                    TaskNode dTask = job.get(taskId);
                    if(dTask != null)
                        taskNode.successors.add(dTask);
                }

                for(Integer taskId : taskNode.getPredecessorsId()){
                    TaskNode dTask = job.get(taskId);
                    if(dTask != null)
                        taskNode.predecessors.add(dTask);
                }

                if(taskNode.successors.size() == 0){
                    taskNode.setEndTask(true);
                }

                if(taskNode.predecessors.size() == 0){
                    taskNode.setStartTask(true);
                }

                //rectifying the dependency
                List<TaskNode> successors = taskNode.successors;
                for(TaskNode tNode : successors){
                    boolean pred_check = false;
                    for(TaskNode succNode: tNode.predecessors){
                        if(succNode.getId() == taskNode.getId()){
                            pred_check = true;
                            break;
                        }
                    }
                    if(!pred_check) {
                        tNode.predecessors.add(taskNode);
                    }
                }

                List<TaskNode> predecessors = taskNode.predecessors;
                for(TaskNode tNode : predecessors){
                    boolean succ_check = false;
                    for(TaskNode succNode: tNode.successors){
                        if(succNode.getId() == taskNode.getId()){
                            succ_check = true;
                            break;
                        }
                    }
                    if(!succ_check) {
                        tNode.successors.add(taskNode);
                    }
                }
            }

        }
    }

    void assignStartAndEndTask(){
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()){
            Map<Integer, TaskNode> job = entry.getValue();
            List<TaskNode> startTasks = new ArrayList<>();
            List<TaskNode> endTasks = new ArrayList<>();
            ComputingNode ueDevice = null;
            for(Map.Entry<Integer, TaskNode> task :  job.entrySet()) {
                TaskNode taskNode = task.getValue();
                ueDevice = taskNode.getEdgeDevice();
                if(taskNode.predecessors.size() == 0){
                    startTasks.add(taskNode);
                }

                if(taskNode.successors.size() == 0){
                    endTasks.add(taskNode);
                }
            }

            if(startTasks.size() > 1){
                Integer dummy_task_id = this.getDummyTaskId(job);
                TaskNode taskNode = this.createDummyTask(dummy_task_id, Integer.valueOf(entry.getKey()), ueDevice);
                taskNode.setStartTask(true);
                taskNode.successors.addAll(startTasks);
                for(TaskNode task : startTasks){
                    task.setStartTask(false);
                    task.predecessors.add(taskNode);
                }
                job.put(dummy_task_id, taskNode);
            }

            if(endTasks.size() > 1){
                Integer dummy_task_id = this.getDummyTaskId(job);
                TaskNode taskNode = this.createDummyTask(dummy_task_id, Integer.valueOf(entry.getKey()), ueDevice);
                taskNode.predecessors.addAll(endTasks);
                taskNode.setEndTask(true);
                for(TaskNode task : endTasks){
                    task.setEndTask(false);
                    task.successors.add(taskNode);
                }
                job.put(dummy_task_id, taskNode);
            }
        }

    }

    Integer getDummyTaskId(Map<Integer, TaskNode> job){
       Integer task_id = Integer.MIN_VALUE;
       for(Map.Entry<Integer, TaskNode> task :  job.entrySet()){
           if(task.getKey() > task_id){
               task_id = task.getKey();
           }
       }

       return task_id + 1;
    }

    TaskNode createDummyTask(Integer id, Integer app_id, ComputingNode ueDevice){
        TaskNode taskNode = new TaskNode(0, 0);
        taskNode.setApplicationID(app_id);
        taskNode.setFileSize(0).setOutputSize(0);
        taskNode.setContainerSize(0);
        taskNode.setRegistry(simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0));
        taskNode.setId(id);
        taskNode.setMaxLatency(0);
        taskNode.setMemoryNeed(0.0);
        taskNode.setStorageNeed(0.0);
        taskNode.setContainerSize(0);
        taskNode.setEdgeDevice(ueDevice);
        taskNode.setDummyTask(true);
        return taskNode;
    }

    public void loadJobs(){
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get(job_dataset_path));
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNodeRoot = objectMapper.readTree(jsonData);
            if(jsonNodeRoot.isObject()) {
                ArrayNode jobs = (ArrayNode) jsonNodeRoot.get("Jobs");
                if(jobs.isArray()){
                    for(JsonNode objNode : jobs){
                        Job job = new Job();
                        job.setJobID_InDB(objNode.get("JobID_InDB").asText());
                        job.setJobID(objNode.get("JobID").asText());
                        job.setTimeDeadLinePrefered(objNode.get("TimeDeadLinePrefered").asText());
                        job.setListOfTasks(objNode.get("ListOfTasks").asText());
                        job.setMinStorageNeeded(objNode.get("MinStorageNeeded").asText());
                        job.setTasksWhichCanRunInParallel(objNode.get("TasksWhichCanRunInParallel").asText());
                        job.setMinTaskSize(objNode.get("MinTaskSize").asText());
                        job.setMaxTaskSize(objNode.get("MaxTaskSize").asText());
                        job.setAvgTaskLength();
                        jobsMap.put(Integer.valueOf(job.getJobID()), job);
                    }
                }
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    public void generateRealApps(int num){
        int jobId = 1;
        String csvFile = "/Users/akhirul.islam/Hipc_edgeSim_intellij/PureEdgeSim/hipc_ehd/GSAgri/tomato_bacterial_disease.csv";
        try (FileWriter writer = new FileWriter(csvFile)) {
            for (int i = 0; i < num; i++) {
                String line = "";
                Job job = new Job();
                job.setJobID(String.valueOf(jobId));
                for (int j = 1; j <= 7; j++) {
                    int task_id = j;
                    line = String.valueOf(jobId);
                /*0. Capture frame
                  1. Save the image to disk
                  2. convert image to pixels and load into a matrix
                  3. transpose the matrix
                  4. Load ML model
                  5. predict
                  6. getLabels and resuls
                * */
                    long cpu = 0; //number of instructions in MI (task length)
                    switch (j) {
                        case 1:
                            cpu = 100 * Helper.getRandomInteger(510, 600);
                            break;
                        case 2:
                            cpu = 100 * Helper.getRandomInteger(170, 200);
                            break;
                        case 3:
                            cpu = 100 * Helper.getRandomInteger(30, 50);
                            break;
                        case 4:
                            cpu = 100 * Helper.getRandomInteger(10, 20);
                            break;
                        case 5:
                            cpu = 100 * Helper.getRandomInteger(170, 200);
                            break;
                        case 6:
                            cpu = 100 * Helper.getRandomInteger(20, 30);
                            break;
                        case 7:
                            cpu = 100 * Helper.getRandomInteger(7, 10);
                            break;
                        default:
                            System.out.println("Wrong task- " + j);
                    }
                    //jobid, taskid, cpu, ipfilesize, opfilesize, memory, storage, deadline, succesor, predecessor
                    double deadline = cpu / 2000 + Helper.generateRandomDouble(0.3, 0.5);

                    TaskNode taskNode = new TaskNode(task_id, cpu);

                    line += "," + task_id;
                    line += "," + cpu;
                    //System.out.println("AKHIRUL-");

                    taskNode.setApplicationID(jobId);
                    long inputFileSize = Helper.getRandomInteger(130, 150); //Kb
                    long outputFileSize = Helper.getRandomInteger(20, 100);
                    line += "," + inputFileSize;
                    line += "," + outputFileSize;
                    taskNode.setFileSize(inputFileSize).setOutputSize(outputFileSize);
                    taskNode.setContainerSize(2500);
                    taskNode.setRegistry(simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0));
                    taskNode.setId(task_id);
                    taskNode.setMaxLatency(deadline);
                    double memory = Helper.generateRandomDouble(1000, 1500);
                    line += "," + memory;
                    taskNode.setMemoryNeed(memory);
                    double storage = Helper.generateRandomDouble(500, 1000);
                    line += "," + storage;
                    taskNode.setStorageNeed(storage);
                    taskNode.setEnergyPredictionModel(energyPrediction);
                    line += "," + deadline;

                    //predecessor
                    String pred = "";
                    String succ = "";
                    int id = j;
                    id--;
                    while (id > 0) {
                        taskNode.getPredecessorsId().add(id);
                        if (pred.isEmpty()) {
                            pred = String.valueOf(id);
                        } else {
                            pred += "-" + id;
                        }
                        id--;
                    }

                    //successor
                    id = j;
                    id++;
                    while (id <= 7) {
                        taskNode.getSuccessorsId().add(id);
                        if (succ.isEmpty()) {
                            succ = String.valueOf(id);
                        } else {
                            succ += "-" + id;
                        }
                        id++;
                    }

                    line += "," + pred;
                    line += "," + succ;
                    if (tasksMap.containsKey(jobId)) {
                        tasksMap.get(jobId).put(task_id, taskNode);
                    } else {
                        Map<Integer, TaskNode> tMap = new HashMap<>();
                        tMap.put(task_id, taskNode);
                        tasksMap.put(jobId, tMap);
                    }
                    task_id++;
                    line += "\n";
                    writer.append(line);
                    //System.out.println(line);
                }

                jobsMap.put(jobId, job);
                jobId++;
            }
        } catch (IOException e) {
            System.err.println("An error occurred while creating the CSV file.");
            e.printStackTrace();
        }
    }

    public void loadRealApps(){
        String line;
        String delimiter = ",";
        try (BufferedReader br = new BufferedReader(new FileReader(realAppCsvFile))) {
            while ((line = br.readLine()) != null) {
                //jobid, taskid, cpu, ipfilesize, opfilesize, memory, storage, deadline, succesor, predecessor
                String[] values = line.split(delimiter);
                String jobId_str = values[0];
                Integer jobId = Integer.valueOf(jobId_str);
                Integer taskId = Integer.valueOf(values[1]);
                long cpu = Long.parseLong(values[2]);
                long ipfilesize = Long.parseLong(values[3]);
                long opfilesize = Long.parseLong(values[4]);
                Double memory = Double.valueOf(values[5]);
                Double storage = Double.valueOf(values[6]);
                Double deadline = Double.valueOf(values[7]) + 1.5;
                String predecessor = values[8];
                String succesor = "";
                if(values.length == 10) {
                    //If successor is empty the data format is 150,7,100,2971927,3255522,10975.224802152414,5558.47599780476,11.26,6-5-4-3-2-1,
                    //In this case we get length less than 1
                    succesor = values[9];
                }

                if(!jobsMap.containsKey(jobId)){
                    Job job = new Job();
                    job.setJobID(jobId_str);
                    jobsMap.put(jobId, job);
                }

                TaskNode taskNode = new TaskNode(taskId, cpu);
                taskNode.setApplicationID(jobId);
                taskNode.setFileSize(ipfilesize).setOutputSize(opfilesize);
                taskNode.setContainerSize(25000);
                taskNode.setRegistry(simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0));
                taskNode.setId(taskId);
                taskNode.setMaxLatency(deadline);
                taskNode.setMemoryNeed(memory);
                taskNode.setStorageNeed(storage);
                taskNode.setEnergyPredictionModel(energyPrediction);

                if(!predecessor.isEmpty()) {
                    String[] predIds = predecessor.split("-");
                    for (int i = 0; i < predIds.length; i++) {
                        taskNode.getPredecessorsId().add(Integer.valueOf(predIds[i]));
                    }
                }

                if(!succesor.isEmpty()) {
                    String[] succIds = succesor.split("-");
                    for (int i = 0; i < succIds.length; i++) {
                        taskNode.getSuccessorsId().add(Integer.valueOf(succIds[i]));
                    }
                }

                if(tasksMap.containsKey(jobId)) {
                    tasksMap.get(jobId).put(taskId, taskNode);
                } else{
                    Map<Integer, TaskNode> tMap = new HashMap<>();
                    tMap.put(taskId, taskNode);
                    tasksMap.put(jobId, tMap);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file.");
            e.printStackTrace();
        }
    }

    public void loadTasks(){
        BufferedReader reader;
        long lineCount = 0;
        FileInputStream inputStream = null;
        Scanner sc = null;
        Integer taskCount = 0;
        Integer dupTaskCount = 0;
        Long max_cpu = Long.MIN_VALUE;
        Long min_cpu = Long.MAX_VALUE;

        Double max_storage = Double.MIN_VALUE;
        Double min_storage = Double.MAX_VALUE;

        Double max_mem = Double.MIN_VALUE;
        Double min_mem = Double.MAX_VALUE;

        Double max_deadline = Double.MIN_VALUE;
        Double min_deadline = Double.MAX_VALUE;
        try {
            inputStream = new FileInputStream(task_dataset_path);
            sc = new Scanner(inputStream, "unicode");
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                lineCount++;
                if(lineCount > 1) {
                    String fields[] = line.split("\\[");

                    String other_fields[] = fields[0].split(",");
                    String taskId = other_fields[1];
                    if(!taskId.isEmpty()) {
                        Integer jobId = Integer.valueOf(other_fields[2]);
                        Long cpu = Long.parseLong(other_fields[3])/1000+1;
                        Double memory = Double.parseDouble(other_fields[4]);
                        Double storage = Double.parseDouble(other_fields[5]);
                        Double deadline = Double.parseDouble(other_fields[8])/50+1;
                        Integer task_id = Integer.parseInt(taskId);

                        if(cpu < min_cpu){
                            min_cpu = cpu;
                        }

                        if(cpu > max_cpu){
                            max_cpu = cpu;
                        }

                        if(memory < min_mem){
                            min_mem = memory;
                        }

                        if(memory > max_mem){
                            max_mem = memory;
                        }

                        if(deadline < min_deadline){
                            min_deadline = deadline;
                        }

                        if(deadline > max_deadline){
                            max_deadline = deadline;
                        }

                        if(storage < min_storage){
                            min_storage = storage;
                        }

                        if(storage > max_storage){
                            max_storage = storage;
                        }
//24
                        TaskNode taskNode = new TaskNode(task_id, cpu);
                        taskNode.setApplicationID(Integer.valueOf(jobId));
                        taskNode.setFileSize(Helper.getRandomInteger(80000, 400000)).setOutputSize(Helper.getRandomInteger(8000, 400000));
                        taskNode.setContainerSize(25000);
                        taskNode.setRegistry(simManager.getDataCentersManager().getCloudDatacentersList().get(0).nodeList.get(0));
                        taskNode.setId(task_id);
                        taskNode.setMaxLatency(deadline);
                        taskNode.setMemoryNeed(memory*Helper.getRandomInteger(1000, 1500));
                        taskNode.setStorageNeed(storage*Helper.getRandomInteger(20, 50));
                        taskNode.setParallelTaskPct(Helper.getRandomInteger(70, 90));
                        taskNode.setEnergyPredictionModel(energyPrediction);

                        String successors_str = fields[1];
                        if(successors_str.length() > 1) {
                            String successors = fields[1].split("\\]")[0];
                            String[] succList = successors.split(",");
                            for(String succ : succList){
                                if(!succ.isEmpty()) {
                                    taskNode.getSuccessorsId().add(Integer.parseInt(succ));
                                }
                            }
                        }
                        String predecessors_str = fields[3];

                        if(predecessors_str.length() > 1){
                            String predecessors = predecessors_str.split("\\]")[0];
                            String[] predList = predecessors.split(",");
                            for(String pred : predList){
                                if(!pred.isEmpty()) {
                                    taskNode.getPredecessorsId().add(Integer.parseInt(pred));
                                }
                            }
                        }

                        if(tasksMap.containsKey(jobId)) {
                            if(tasksMap.get(jobId).containsKey(task_id)){
                                dupTaskCount++;
                            } else {
                                tasksMap.get(jobId).put(task_id, taskNode);
                            }
                        } else{
                            Map<Integer, TaskNode> tMap = new HashMap<>();
                            tMap.put(task_id, taskNode);
                            tasksMap.put(jobId, tMap);
                        }
                        taskCount++;
                    }
                }
            }
            sc.close();
        } catch (IOException ioException){
            ioException.printStackTrace();
        }

//        System.out.println("Total tasks loaded: " + taskCount + " duplicate: " + dupTaskCount);
        System.out.println("MAX CPU: " + max_cpu + " MIN CPU: " + min_cpu);
        System.out.println("MAX storage: " + max_storage + " MIN storage: " + min_storage);
        System.out.println("MAX memory: " + max_mem + " MIN memory: " + min_mem);
        System.out.println("MAX deadline: " + max_deadline + " MIN deadline: " + min_deadline);
    }

    private void markPredecessorHigPref(List<TaskNode> predecessors){
        for(TaskNode taskNode : predecessors){
            taskNode.setPrefTask(true);
            markPredecessorHigPref(taskNode.predecessors);
        }
    }

    private void setHighPrefTasks(Map<Integer, TaskNode> job){
        for (Map.Entry<Integer, TaskNode> task : job.entrySet()) {
            TaskNode taskNode = task.getValue();
            if(taskNode.isCritical()){
                taskNode.setPrefTask(true);
                markPredecessorHigPref(taskNode.predecessors);
            }
        }
    }

    private void updateData(int type){ //1-cpu, 2-io, 3-memory
        int job_no = 0;
        for(Map.Entry<Integer, Map<Integer, TaskNode>> entry : tasksMap.entrySet()) {
            Map<Integer, TaskNode> job = entry.getValue();
            job_no++;
            int intensive_task = (int) Math.ceil(job.size()/2) + Helper.getRandomInteger(2, 5);
            int task_assigned = 0;
            List<DataCenter> dclist = simManager.getDataCentersManager().getEdgeDatacenterList();
            ComputingNode node = dclist.get(0).nodeList.get(0);
            final Double MIPS = node.getMipsPerCore();
            for (Map.Entry<Integer, TaskNode> task : job.entrySet()) {
                TaskNode taskNode = task.getValue();
                if(taskNode.isDummyTask())
                    continue;
                TaskNode.TaskType taskType = TaskNode.TaskType.NORMAL;

                Double cpuTime = taskNode.getLength() / MIPS;
                Double ioTime = taskNode.getStorageNeed() * 60 / (100 * node.getReadBandwidth()) //READ operation, 60% read
                        + taskNode.getStorageNeed() * 40 / (100 * node.getWriteBandwidth()); //WRITE operation, 40% write;;
                Double memoryTransferTime = taskNode.getMemoryNeed()/node.getDataBusBandwidth();

                Double totalTime = cpuTime + ioTime + memoryTransferTime;

                if(type == 1) {
                    if(cpuTime ==0)
                        continue;
                    if (cpuTime / totalTime > 0.5) {
                        task_assigned++;
                        continue;
                    }

                    Double required_cpu_time = memoryTransferTime + ioTime + Helper.getRandomInteger(10, 50);
                    int task_length = (int) Math.ceil((taskNode.getLength() * required_cpu_time) / (cpuTime));
                    taskNode.setLength(task_length);
                } else if(type == 2){
                    if(ioTime <= 0)
                        continue;
                    if(ioTime/totalTime > 0.5){
                        task_assigned++;
                        continue;
                    }

                    Double required_io_time = memoryTransferTime + cpuTime + Helper.getRandomInteger(10, 50);
                    int task_length = (int) Math.ceil((taskNode.getLength() * required_io_time) / (ioTime));
                    taskNode.setLength(task_length);
                } else { //type=3
                    if(memoryTransferTime <= 0)
                        continue;
                    if(memoryTransferTime/totalTime > 0.5){
                        task_assigned++;
                        continue;
                    }

                    Double required_mem_time = ioTime + cpuTime + Helper.getRandomInteger(10, 50);
                    int task_length = (int) Math.ceil((taskNode.getLength() * required_mem_time) / (memoryTransferTime));
                    taskNode.setLength(task_length);
                }

                if (task_assigned >= intensive_task)
                    break;
            }

            if(job_no >= 300)
                break;

        }
    }


    private double getMECComputingTime(TaskNode taskNode){ //only for multi-user scheduling algo
        List<DataCenter> dclist = simManager.getDataCentersManager().getEdgeDatacenterList();
        ComputingNode node = dclist.get(0).nodeList.get(0);
        Double mips = node.getMipsPerCore();

        //System.out.println(mips + " " + node.getWriteBandwidth() + " " + " " + node.getReadBandwidth());
        double cpuTime = taskNode.getLength()/mips;

        double io_time = taskNode.getStorageNeed() * 60 / (100 * node.getReadBandwidth()) //READ operation, 60% read
                + taskNode.getStorageNeed() * 40 / (100 * node.getWriteBandwidth()); //WRITE operation, 40% write;
        double mem_tr_time = taskNode.getMemoryNeed() / node.getDataBusBandwidth();

        return cpuTime + io_time + mem_tr_time;
    }


    private TaskNode getRootTask(List<TaskNode> tempTaskList){
        TaskNode rootNode = null;
        for(TaskNode taskNode : tempTaskList) {
            if(taskNode.isStartTask()){
                rootNode = taskNode;
                break;
            }
        }

        return  rootNode;
    }

    private double calculateTransmissionLatency(Task task, ComputingNode computingNode){
        double distance = task.getEdgeDevice().getMobilityModel().distanceTo(computingNode);
        double upload_latency = Helper.getWirelessTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);;
        double download_latency = Helper.getWirelessTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        return upload_latency + download_latency;
    }

    private boolean isAllpredInPrevLabel(TaskNode taskNode,Set<TaskNode> prevLabel){
        boolean ret = true;
        for(TaskNode task : taskNode.predecessors){
            boolean flag = false;
            for(TaskNode prevLabelTask : prevLabel){
                if(task.getId() == prevLabelTask.getId()){
                    flag = true;
                }
            }
            if(!flag){
                ret = false;
                break;
            }
        }
        return ret;
    }

    private double allocateBudgetToJob(Map<Integer, TaskNode> job) {
        Double min_budget = Double.MAX_VALUE;
        Double max_budget = Double.MIN_VALUE;
        Double total_charge = 0.0;
        for (Map.Entry<Integer, TaskNode> job_entry : job.entrySet()) {
            TaskNode taskNode = job_entry.getValue();
            total_charge += Helper.getCharge(taskNode.getLength());
        }
        Integer budget_pct = Math.toIntExact(Helper.getRandomInteger(60, 70));
        return (total_charge*budget_pct)/100;
    }
}

