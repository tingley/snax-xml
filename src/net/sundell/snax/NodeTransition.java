package net.sundell.snax;

class NodeTransition<T> {

    private NodeTest<T> test;
    private NodeState<T> target;
    
    NodeTransition(NodeTest<T> test, NodeState<T> target) {
        this.test = test;
        this.target = target;
    }

    NodeTest<T> getTest() {
        return test;
    }

    void setTest(NodeTest<T> test) {
        this.test = test;
    }

    NodeState<T> getTarget() {
        return target;
    }

    void setTarget(NodeState<T> target) {
        this.target = target;
    }

}
