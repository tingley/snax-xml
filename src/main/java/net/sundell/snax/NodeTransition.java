package net.sundell.snax;

class NodeTransition<T> {

    private ElementConstraint test;
    private NodeState<T> target;
    
    NodeTransition(ElementConstraint test, NodeState<T> target) {
        this.test = test;
        this.target = target;
    }

    ElementConstraint getTest() {
        return test;
    }

    void setTest(ElementConstraint test) {
        this.test = test;
    }

    NodeState<T> getTarget() {
        return target;
    }

    void setTarget(NodeState<T> target) {
        this.target = target;
    }

}
