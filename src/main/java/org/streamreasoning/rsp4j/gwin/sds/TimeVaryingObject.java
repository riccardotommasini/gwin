package org.streamreasoning.rsp4j.gwin.sds;

import org.apache.commons.rdf.api.IRI;
import org.streamreasoning.rsp4j.api.operators.s2r.execution.assigner.StreamToRelationOp;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

public class TimeVaryingObject<E> implements TimeVarying<E> {

    private final StreamToRelationOp<?, E> op;
    private final IRI name;
    private E graph;

    public TimeVaryingObject(StreamToRelationOp<?, E> op, IRI name) {
        this.op = op;
        this.name = name;
    }

    /**
     * The setTimestamp function merges the element
     * in the org.streamreasoning.rsp4j.gwin.content into a single graph
     * and adds it to the current dataset.
     **/
    @Override
    public void materialize(long ts) {
        graph = op.content(ts).coalesce();
    }

    @Override
    public E get() {
        return graph;
    }

    @Override
    public String iri() {
        return name.getIRIString();
    }


}
