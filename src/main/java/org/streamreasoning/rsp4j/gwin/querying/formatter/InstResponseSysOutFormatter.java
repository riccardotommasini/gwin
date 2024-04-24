package org.streamreasoning.rsp4j.gwin.querying.formatter;

import org.streamreasoning.rsp4j.api.format.QueryResultFormatter;

import java.util.Observable;

/**
 * Created by riccardo on 03/07/2017.
 */

public class InstResponseSysOutFormatter<O> extends QueryResultFormatter<O> {

    public InstResponseSysOutFormatter(String format, boolean distinct) {
        super(format, distinct);
    }

    @Override
    public void notify(O t, long ts) {
        System.out.println(t);
    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
