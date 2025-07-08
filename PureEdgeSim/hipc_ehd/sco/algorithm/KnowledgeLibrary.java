package hipc_ehd.sco.algorithm;

import java.util.List;

public class KnowledgeLibrary {
    List<KnowledgePoint> knowledgePointList;
    public KnowledgeLibrary(List<KnowledgePoint> knowledgePoints){
        knowledgePointList = knowledgePoints;
    }
    public List<KnowledgePoint> getKnowledgePointList() {
        return knowledgePointList;
    }
}
