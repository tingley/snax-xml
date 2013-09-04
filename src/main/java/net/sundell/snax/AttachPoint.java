package net.sundell.snax;

public class AttachPoint<T> {
    private NodeState<T> nodeState;
    
    AttachPoint(NodeState<T> nodeState) {
        this.nodeState = nodeState;
    }
    
    NodeState<T> getNodeState() {
        return nodeState;
    }
}
