package hipc_ehd;

import com.mechalikh.pureedgesim.simulationmanager.Simulation;
import hipc_ehd.dag.DependentTaskGenerator;

public class EhdHipc {
    private static String settingsPath = "PureEdgeSim/hipc_ehd/GSAgri/settings/";
    private static String outputPath = "PureEdgeSim/hipc_ehd/GSAgri/output/";

//    private static String settingsPath = "PureEdgeSim/hipc_ehd/sco/settings/";
//    private static String outputPath = "PureEdgeSim/hipc_ehd/sco/output/";

//    private static String settingsPath = "PureEdgeSim/hipc_ehd/oco/settings/";
//    private static String outputPath = "PureEdgeSim/hipc_ehd/oco/output/";

//    private static String settingsPath = "PureEdgeSim/hipc_ehd/random-greedy/settings/";
//    private static String outputPath = "PureEdgeSim/hipc_ehd/random-greedy/output/";

    public EhdHipc(){
        Simulation sim = new Simulation();
        sim.setCustomOutputFolder(outputPath);
        sim.setCustomSettingsFolder(settingsPath);
        sim.setCustomTaskGenerator(DependentTaskGenerator.class);
        sim.setCustomEdgeOrchestrator(CustomOrchestrator.class);
        sim.launchSimulation();
    }

    public static void main(String args[]){
        new EhdHipc();
    }
}
