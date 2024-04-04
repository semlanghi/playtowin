/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.application.inkstream.annotation;

import com.example.application.inkstream.record.EventBean;
import org.apache.commons.rdf.api.IRI;
import org.streamreasoning.rsp4j.api.sds.SDS;
import org.streamreasoning.rsp4j.api.sds.timevarying.TimeVarying;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;


final public class SDSEventImpl<V> implements SDS<Relation<EventBean<V>>> {

    private final Set<TimeVarying<Relation<EventBean<V>>>> defs = new HashSet<>();

    @Override
    public Collection<TimeVarying<Relation<EventBean<V>>>> asTimeVaryingEs() {
        return defs;
    }

    @Override
    public void add(IRI iri, TimeVarying<Relation<EventBean<V>>> tvg) {
        this.defs.add(tvg);
    }

    @Override
    public void add(TimeVarying<Relation<EventBean<V>>> tvg) {
        this.defs.add(tvg);
    }

    @Override
    public void materialized() {

    }

    @Override
    public Stream<Relation<EventBean<V>>> toStream() {
        return defs.stream().map(new Function<TimeVarying<Relation<EventBean<V>>>, Relation<EventBean<V>>>() {
            @Override
            public Relation<EventBean<V>> apply(TimeVarying<Relation<EventBean<V>>> relationTimeVarying) {
                return relationTimeVarying.get();
            }
        });
    }

}
