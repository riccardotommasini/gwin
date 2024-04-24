package org.streamreasoning.rsp4j.gwin.querying.operators.r2r;

import org.apache.commons.rdf.api.RDFTerm;
import org.apache.log4j.Logger;
import org.streamreasoning.rsp4j.api.operators.r2r.RelationToRelationOperator;
import org.streamreasoning.rsp4j.api.operators.r2r.Var;
import org.streamreasoning.rsp4j.api.querying.result.SolutionMapping;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;
import org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding;
import org.streamreasoning.rsp4j.yasper.querying.operators.r2r.BindingImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Projection implements RelationToRelationOperator<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding, org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding>, Function<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding, org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> {

    private final boolean star;
    private final Var[] vars;
    private final Function<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding, org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> f;
    private final Collection<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> solutions;
    private Stream<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> tvb;
    private static final Logger log = Logger.getLogger(Projection.class);

    public Projection(Stream<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> tvb, Var... vars) {
        this.tvb = tvb;
        this.vars = vars;
        this.star = vars == null;
        this.solutions = new ArrayList<>();
    this.f =
        binding -> {
          if (!star) {
            org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding b = new org.streamreasoning.rsp4j.yasper.querying.operators.r2r.BindingImpl();
            for (Var v : vars) {
              RDFTerm value = binding.value(v);
              if (value != null) b.add(v, value);
              else {
                  log.warn("Projection Variable not found: " + v.name());
                  b.add(v, null);
              }
            }
            return b;
          }
          return binding;
        };
    }

    public Projection(TimeVarying<Collection<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding>> tvb, Var... vars) {
        this.vars = vars;
        this.star = vars == null;
        this.solutions = tvb.get();
        this.f = binding -> {
            if (!star) {
                org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding b = new BindingImpl();
                for (Var v : vars) {
                    RDFTerm value = binding.value(v);
                    if (value != null)
                        b.add(v, value);
                    else
                        return null;
                }
                return b;
            }
            return binding;
        };
    }

    @Override
    public Stream<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> eval(Stream<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> sds) {
        return sds.map(f);
    }

    @Override
    public TimeVarying<Collection<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding>> apply(SDS<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> sds) {
        return new TimeVarying<Collection<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding>>() {
            @Override
            public void materialize(long ts) {
                Stream<org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding> collect = tvb != null ? eval(tvb) : eval(solutions.stream());
                solutions.clear();
                solutions.addAll(collect.collect(Collectors.toList()));
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
        throw new UnsupportedOperationException();
    }

    @Override
    public org.streamreasoning.rsp4j.yasper.querying.operators.r2r.Binding apply(Binding binding) {
        return f.apply(binding);
    }
}
