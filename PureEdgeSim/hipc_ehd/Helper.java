package hipc_ehd;

import com.mechalikh.pureedgesim.datacentersmanager.ComputingNode;
import com.mechalikh.pureedgesim.datacentersmanager.DataCenter;
import com.mechalikh.pureedgesim.scenariomanager.SimulationParameters;
import com.mechalikh.pureedgesim.taskgenerator.Task;
import hipc_ehd.dag.TaskNode;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Helper {
    public static Integer getRandomInteger(Integer min, Integer max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public static double generateRandomDouble(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }

        Random random = new Random();
        return min + random.nextDouble() * (max - min);
    }

    public static Double calculateDistance(DataCenter dc1, DataCenter dc2) {
        double x1 = dc1.getLocation().getXPos();
        double y1 = dc1.getLocation().getYPos();
        double x2 = dc2.getLocation().getXPos();
        double y2 = dc2.getLocation().getYPos();
        double distance = Math.sqrt(Math.pow((x2 - x1), 2) + Math.pow((y2 - y1), 2));
        return distance;
    }

    public static double getDataRate(double bw) {
        //SimulationParameters.MAN_BANDWIDTH_BITS_PER_SECOND
        float leftLimit = 0.2F;
        float rightLimit = 0.7F;
        float generatedFloat = leftLimit + new Random().nextFloat() * (rightLimit - leftLimit);
        return 2 * bw * generatedFloat;
    }

    public static double calculatePropagationDelay(double distance) {
        return distance * 10 / 300000000;
    }

    public static double getManEdgeTransmissionLatency(double bits) {
        double dataRate = Helper.getDataRate(SimulationParameters.MAN_BANDWIDTH_BITS_PER_SECOND);
        return bits / dataRate;
    }

    public static double getManCloudTransmissionLatency(double bits) {
        double dataRate = Helper.getDataRate(SimulationParameters.WAN_BANDWIDTH_BITS_PER_SECOND);
        return bits / dataRate;
    }

    public static double getWirelessTransmissionLatency(double bits) {
        double dataRate = Helper.getDataRate(SimulationParameters.WIFI_BANDWIDTH_BITS_PER_SECOND);
        return bits / dataRate;
    }

    public static double dynamicEnergyConsumption(double taskLength, double mipsCapacity, double mipsRequirement) {
        double latency = taskLength / mipsCapacity;
        double cpuEnergyConsumption = (0.01 * (mipsRequirement * mipsRequirement) / (mipsCapacity * mipsCapacity) * latency) / 3600; //alpha = 0.01
        return cpuEnergyConsumption;
    }

    public static double calculateTransmissionLatency(Task task, ComputingNode computingNode) {
        double distance = task.getEdgeDevice().getMobilityModel().distanceTo(computingNode);
        double upload_latency = Helper.getWirelessTransmissionLatency(task.getFileSize()) + Helper.calculatePropagationDelay(distance);
        ;
        double download_latency = Helper.getWirelessTransmissionLatency(task.getOutputSize()) + Helper.calculatePropagationDelay(distance);
        return upload_latency + download_latency;
    }

    public static double calculateAverageLatency(Task task, ComputingNode local, ComputingNode mec, ComputingNode cloud) {
        double local_ex_time = Helper.calculateExecutionTime(local, task);
        double mec_ex_time = Helper.calculateExecutionTime(mec, task);
        double cloud_ex_time = Helper.calculateExecutionTime(cloud, task);

        double mec_trans_time = Helper.calculateTransmissionLatency(task, mec);
        double cloud_trans_time = Helper.calculateTransmissionLatency(task, mec);

        return (local_ex_time + (mec_ex_time + mec_trans_time) + (cloud_ex_time + cloud_trans_time)) / 3.0;
    }

    public static double calculateAverageRemoteLatency(Task task, ComputingNode mec, ComputingNode cloud) {
        double mec_ex_time = Helper.calculateExecutionTime(mec, task);
        double cloud_ex_time = Helper.calculateExecutionTime(cloud, task);

        double mec_trans_time = Helper.calculateTransmissionLatency(task, mec);
        double cloud_trans_time = Helper.calculateTransmissionLatency(task, mec);

        return ((mec_ex_time + mec_trans_time) + (cloud_ex_time + cloud_trans_time)) / 2.0;
    }

    public static double calculateLocalLatency(Task task) {
        return Helper.calculateExecutionTime(task.getEdgeDevice(), task);
    }

    public static double calculateRemoteEnergyConsumption(TaskNode task){
        /**
         if ("cellular".equals(connectivity)) {
         transmissionEnergyPerBits = SimulationParameters.CELLULAR_DEVICE_TRANSMISSION_WATTHOUR_PER_BIT;
         receptionEnergyPerBits = SimulationParameters.CELLULAR_DEVICE_RECEPTION_WATTHOUR_PER_BIT;
         } else if ("wifi".equals(connectivity)) {
         transmissionEnergyPerBits = SimulationParameters.WIFI_DEVICE_TRANSMISSION_WATTHOUR_PER_BIT;
         receptionEnergyPerBits = SimulationParameters.WIFI_DEVICE_RECEPTION_WATTHOUR_PER_BIT;
         } else {
         transmissionEnergyPerBits = SimulationParameters.ETHERNET_WATTHOUR_PER_BIT / 2;
         receptionEnergyPerBits = SimulationParameters.ETHERNET_WATTHOUR_PER_BIT / 2;
         }
         */
        return task.getFileSize()*SimulationParameters.WIFI_DEVICE_TRANSMISSION_WATTHOUR_PER_BIT;
    }

    public static double calculateExecutionTime(ComputingNode computingNode, Task task){
        double io_time = 0;
        double cpu_time = 0;

        cpu_time = task.getLength() / computingNode.getMipsPerCore();

        if (computingNode.getReadBandwidth() > 0 && computingNode.getWriteBandwidth() > 0) {
            io_time = task.getStorageNeed() * 60 / (100 * computingNode.getReadBandwidth()) //READ operation, 60% read
                        + task.getStorageNeed() * 40 / (100 * computingNode.getWriteBandwidth()); //WRITE operation, 40% write;
        }

        return cpu_time + io_time ;
    }

        public static int[] generateRandomArray(int size, int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Max must be greater than min");
        }
        if (size > (max - min + 1)) {
            throw new IllegalArgumentException("Size must be less than or equal to the range");
        }

        Random random = new Random();
        Set<Integer> generatedNumbers = new HashSet<>();
        int[] randomArray = new int[size];

        for (int i = 0; i < size; i++) {
            int number;
            do {
                number = random.nextInt(max - min + 1) + min;
            } while (generatedNumbers.contains(number));
            generatedNumbers.add(number);
            randomArray[i] = number;
        }

        return randomArray;
    }

    public static double getCharge(double taskLength){
        double cpu = 0.005;
        return cpu*taskLength;
    }
}
