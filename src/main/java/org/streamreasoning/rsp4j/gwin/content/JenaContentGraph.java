package org.streamreasoning.rsp4j.gwin.content;

import org.apache.jena.graph.Graph;
import org.apache.jena.sparql.graph.GraphFactory;
import org.streamreasoning.rsp4j.api.secret.content.Content;
import org.streamreasoning.rsp4j.api.secret.time.Time;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class JenaContentGraph implements Content<Graph, Graph> {
    Time instance;
    private Set<Graph> elements;
    private long last_timestamp_changed;

    public JenaContentGraph(Time instance) {
        this.instance = instance;
        this.elements = new HashSet<>();
    }

    @Override
    public int size() {
        return elements.size();
    }

    @Override
    public void add(Graph e) {
        elements.add(e);

        this.last_timestamp_changed = instance.getAppTime();
    }

    @Override
    public Long getTimeStampLastUpdate() {
        return last_timestamp_changed;
    }


    @Override
    public String toString() {
        return elements.toString();
    }


    @Override
    public Graph coalesce() {
        if (elements.size() == 1)
            return elements.stream().findFirst().orElseGet(GraphFactory::createGraphMem);
        else {
            Graph g = GraphFactory.createGraphMem();
            elements.stream().flatMap(Graph::stream).forEach(g::add);
            return g;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JenaContentGraph that = (JenaContentGraph) o;
        return last_timestamp_changed == that.last_timestamp_changed &&
               Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements, last_timestamp_changed);
    }
}
