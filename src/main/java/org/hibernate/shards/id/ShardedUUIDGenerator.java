/**
 * Copyright (C) 2007 Google Inc.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */

package org.hibernate.shards.id;

import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.UUIDHexGenerator;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.session.ShardedSessionImpl;
import org.hibernate.shards.util.Preconditions;
import org.hibernate.type.Type;
import org.hibernate.util.PropertiesHelper;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Properties;

/**
 * Supports generation of either 32-character hex String UUID or 128 bit
 * BigInteger UUID that encodes the shard.
 *
 * @author Tomislav Nad
 */
public class ShardedUUIDGenerator extends UUIDHexGenerator implements ShardEncodingIdentifierGenerator {

    private IdType idType;

    private static final String ZERO_STRING = "00000000000000000000000000000000";
    private static final String ID_TYPE_PROPERTY = "sharded-uuid-type";

    private static enum IdType {STRING, INTEGER}

    private int getShardId() {
        final ShardId shardId = ShardedSessionImpl.getCurrentSubgraphShardId();
        Preconditions.checkState(shardId != null);
        return shardId.getId();
    }

    public ShardId extractShardId(final Serializable identifier) {
        Preconditions.checkNotNull(identifier);
        String hexId;

        switch (idType) {
            case STRING:
                hexId = (String) identifier;
                return new ShardId(Integer.decode("0x" + hexId.substring(0, 4)));

            case INTEGER:
                String strippedHexId;
                if (identifier instanceof BigInteger) {
                    strippedHexId = ((BigInteger) identifier).toString(16);
                } else if (identifier instanceof Long) {
                    strippedHexId = BigInteger.valueOf((Long) identifier).toString(16);
                } else {
                    throw new UnsupportedOperationException("Unable to extract shard id");
                }

                hexId = ZERO_STRING.substring(0, 32 - strippedHexId.length()) + strippedHexId;
                return new ShardId(Integer.decode("0x" + hexId.substring(0, hexId.length() - 28)));

            default:
                // should never get here
                throw new IllegalStateException("ShardedUUIDGenerator was not configured properly");
        }
    }

    @Override
    public Serializable generate(final SessionImplementor session, final Object object) {

        final String id = format((short) getShardId()) +
                format(getIP()) +
                format((short) (getJVM() >>> 16)) +
                format(getHiTime()) +
                format(getLoTime()) +
                format(getCount());

        switch (idType) {
            case STRING:
                return id;

            case INTEGER:
                return new BigInteger(id, 16);

            default:
                // should never get here
                throw new IllegalStateException("ShardedUUIDGenerator was not configured properly");
        }
    }

    @Override
    public void configure(final Type type, final Properties params, final Dialect d) {
        this.idType = IdType.valueOf(PropertiesHelper.getString(ID_TYPE_PROPERTY, params, "INTEGER"));
    }
}
