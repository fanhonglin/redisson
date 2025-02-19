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
package org.redisson.client.protocol.decoder;

import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.client.handler.State;
import org.redisson.client.protocol.Decoder;

import java.util.List;

/**
 * 
 * @author Nikita Koksharov
 *
 */
public class ScoredSortedSetScanReplayDecoder implements MultiDecoder<ListScanResult<Object>> {

    @Override
    public Decoder<Object> getDecoder(Codec codec, int paramNum, State state) {
        return StringCodec.INSTANCE.getValueDecoder();
    }
    
    @Override
    public ListScanResult<Object> decode(List<Object> parts, State state) {
        List<Object> values = (List<Object>) parts.get(1);
        for (int i = 1; i < values.size(); i++) {
            values.remove(i);
        }
        return new ListScanResult<>((String) parts.get(0), values);
    }

}
