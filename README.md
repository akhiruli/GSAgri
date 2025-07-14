# The simulation framework for paper titled "GSAgri: Green and Secure Agriculture through efficient task offloading and scheduling under IoT-enabled energy-harvesting multi-access edge computing framework"

## 📝 Please Cite It As:
BQProfit: Budget and QoS aware task offloading and resource allocation for Profit maximization under MEC platform

Bibtex:

```groovy
@article{islam2025gsagri,
  title={GSAgri: Green and Secure Agriculture through efficient task offloading and scheduling under IoT-enabled energy-harvesting multi-access edge computing framework},
  author={Islam, Akhirul and Ghose, Manojit},
  journal={Expert Systems with Applications},
  volume={284},
  pages={127814},
  year={2025},
  publisher={Elsevier}
}
```

Steps:
1. Clone the source code to local directory.
2. Use a proper IDE such as IntelliJ IDEA
3. The main class is EhdHipc (EhdHipc.java)
4. We need to uncomment coresponding the strategy to test and comment the rest.
5. Dataset is loaded by class DataProcessor (DataProcessor.java)
   a. Need to download the DAG dataset from zenodo (https://zenodo.org/records/6373666) and provide the correct path in the class DataProcessor.
   b. For testing with real dataset, just need to enable the flag isReal in the class DataProcessor
   c. For number of applications change the variable num_apps in the class DataProcessor as per the requirement.
   d. For real network dataset enable flag realNetwork in class NetworkLink (NetworkLink.java)
