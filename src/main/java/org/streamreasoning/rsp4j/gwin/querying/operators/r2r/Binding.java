package org.streamreasoning.rsp4j.gwin.querying.operators.r2r;

import org.apache.commons.rdf.api.RDFTerm;
import org.streamreasoning.rsp4j.api.operators.r2r.Var;
import org.streamreasoning.rsp4j.yasper.querying.formatter.Differentiable;

import java.util.Set;

public interface Binding extends Differentiable<Binding, Binding>, Cloneable {

    Set<Var> variables();

    RDFTerm value(Var v);

    RDFTerm value(String v);

    boolean compatible(Binding b);

    default Binding union(Binding b) {
        Set<Var> res = this.variables();
        Binding r = new BindingImpl(b.variables().size() + this.variables().size());
        res.forEach(v -> r.add(v, this.value(v)));
        b.variables().forEach(v -> r.add(v, b.value(v)));
        return r;
    }

    default Binding difference(Binding b) {
        Binding clone = (Binding) this.copy();
        Set<Var> res = clone.variables();
        res.removeAll(b.variables());
        BindingImpl r = new BindingImpl();
        res.forEach(v -> r.add(v, clone.value(v)));
        return r;
    }

    default Binding intersection(Binding b) {
        Binding clone = (Binding) this.copy();
        Set<Var> res = clone.variables();
        res.retainAll(b.variables());
        BindingImpl r = new BindingImpl();
        res.forEach(v -> r.add(v, clone.value(v)));
        return r;
    }

    default Binding copy() {
        BindingImpl b = new BindingImpl();
        this.variables().forEach(v -> b.add(v, this.value(v)));
        return b;
    }

    boolean add(Var s, RDFTerm bind);

    int size();
}
