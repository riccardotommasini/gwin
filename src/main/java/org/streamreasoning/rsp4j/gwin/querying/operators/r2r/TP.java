package org.streamreasoning.rsp4j.gwin.querying.operators.r2r;

import org.apache.commons.rdf.api.Graph;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMappingBase;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;
import org.streamreasoning.rsp4j.yasper.querying.PrefixMap;
import org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding;
import org.streamreasoning.rsp4j.yasper.querying.operators.r2r.BindingImpl;
import org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TP implements RelationToRelationOperator<Graph, org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> {

    private final org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm s;
    private final org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm p;
    private final org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm o;
    private List<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> solutions = new ArrayList<>();

    public TP(org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm s, org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm p, org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm o) {
        this.s = s;
        this.p = p;
        this.o = o;
    }
    public TP(String s, String p, String o) {
        this.s = org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm.create(s);
        this.p = org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm.create(p);
        this.o = org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm.create(o);
    }
    public TP(String s, String p, String o, PrefixMap prefixes) {
        this.s = org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm.create(prefixes.expandIfPrefixed(s));
        this.p = org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm.create(prefixes.expandIfPrefixed(p));
        this.o = org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm.create(prefixes.expandIfPrefixed(o));
    }

    @Override
    //it returns a stream of variable bindings, which is a sequence
    public Stream<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> eval(Stream<Graph> sds) {
        return sds.flatMap(Graph::stream)
                .map(t -> {

                    org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding b = new BindingImpl();

                    boolean sb = this.s.bind(b, t.getSubject());

                    boolean ob = o.bind(b, t.getObject());

                    boolean pb = p.bind(b, t.getPredicate());

                    if (!sb || !ob || !pb) {
                        return null;
                    }

                    return b;
                }).filter(Objects::nonNull);
    }

    @Override
    public TimeVarying<Collection<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding>> apply(SDS<Graph> sds) {
        //TODO this should return an SDS
        return new TimeVarying<Collection<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding>>() {
            @Override
            public void materialize(long ts) {
                //time should not be important
                solutions.clear();
                solutions.addAll(eval(sds.toStream()).collect(Collectors.toList()));
            }

            @Override
            public Collection<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> get() {
                return solutions;
            }

            @Override
            public String iri() {
                return null;
            }
        };
    }

    @Override
    public SolutionMapping<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> createSolutionMapping(org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding result) {
        return new SolutionMappingBase<Binding>(result, System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof TP)) {
            return false;
        }
        TP tp = (TP) o;
        return tp.s.equals(this.s) && tp.p.equals(this.p) && tp.o.equals(this.o);
    }

    public org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm getSubject() {
        return s;
    }

    public org.streamreasoning.rsp4j.yasper.querying.operators.r2r.VarOrTerm getProperty() {
        return p;
    }

    public VarOrTerm getObject() {
        return o;
    }
}
