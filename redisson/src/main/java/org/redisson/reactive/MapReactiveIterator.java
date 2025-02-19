/**
 * Copyright (c) 2013-2022 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.redisson.reactive;

import org.redisson.RedissonMap;
import org.redisson.ScanResult;
import org.redisson.api.RFuture;
import org.redisson.client.RedisClient;
import reactor.core.publisher.FluxSink;

import java.util.AbstractMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * 
 * @author Nikita Koksharov
 *
 * @param <K> key type
 * @param <V> value type
 * @param <M> entry type
 */
public class MapReactiveIterator<K, V, M> implements Consumer<FluxSink<M>> {

    private final RedissonMap<K, V> map;
    private final String pattern;
    private final int count;

    public MapReactiveIterator(RedissonMap<K, V> map, String pattern, int count) {
        this.map = map;
        this.pattern = pattern;
        this.count = count;
    }
    
    @Override
    public void accept(FluxSink<M> emitter) {
        emitter.onRequest(new IteratorConsumer<M>(emitter) {
            @Override
            protected boolean tryAgain() {
                return MapReactiveIterator.this.tryAgain();
            }

            @Override
            protected Object transformValue(Object value) {
                return getValue((Entry<Object, Object>) value);
            }

            @Override
            protected RFuture<ScanResult<Object>> scanIterator(RedisClient client, String nextIterPos) {
                return MapReactiveIterator.this.scanIterator(client, nextIterPos);
            }
        });
    }

    protected boolean tryAgain() {
        return false;
    }

    M getValue(Entry<Object, Object> entry) {
        return (M) new AbstractMap.SimpleEntry<K, V>((K) entry.getKey(), (V) entry.getValue()) {

            @Override
            public V setValue(V value) {
                return map.put((K) entry.getKey(), value);
            }

        };
    }

    public RFuture<ScanResult<Object>> scanIterator(RedisClient client, String nextIterPos) {
        return (RFuture<ScanResult<Object>>) (Object) map.scanIteratorAsync(map.getRawName(), client, nextIterPos, pattern, count);
    }

}
