package org.streamreasoning.rsp4j.gwin.content;

import org.apache.jena.graph.Graph;
import org.streamreasoning.rsp4j.api.RDFUtils;
import org.streamreasoning.rsp4j.api.secret.content.Content;
import org.streamreasoning.rsp4j.api.secret.content.ContentFactory;
import org.streamreasoning.rsp4j.api.secret.time.Time;
import org.streamreasoning.rsp4j.yasper.content.EmptyContent;

public class JenaGraphContentFactory implements ContentFactory<Graph, Graph> {

    Time time;

    public JenaGraphContentFactory(Time time) {
        this.time = time;
    }


    @Override
    public Content<Graph, Graph> createEmpty() {
        return new EmptyContent(RDFUtils.createGraph());
    }

    @Override
    public Content<Graph, Graph> create() {
        return new JenaContentGraph(time);
    }
}
