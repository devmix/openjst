/*
 * Copyright (C) 2013 OpenJST Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openjst.server.mobile.model;

import org.openjst.commons.rpc.RPCMessageFormat;
import org.openjst.server.commons.model.types.MessageDeliveryState;

import javax.annotation.Nullable;
import javax.persistence.*;
import java.util.Date;

/**
 * @author Sergey Grachev
 */
@NamedQueries({
        @NamedQuery(name = Queries.RPCMessage.DELIVERY_SUCCESS,
                query = "update RPCMessage e set e.state = :state where e.id = :msgId"),

        @NamedQuery(name = Queries.RPCMessage.DELIVERY_FAIL,
                query = "update RPCMessage e set e.state = :state, e.deliveryMessage = :msg where e.id = :msgId")
})
@Entity
@Table(name = RPCMessage.TABLE)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.INTEGER)
public abstract class RPCMessage extends AbstractAccountEntity {

    public static final String TABLE = "rpc_message";
    public static final String COLUMN_CLIENT_ID = "client_id";
    public static final String COLUMN_CREATED = "created";
    public static final String COLUMN_FORMAT = "format";
    public static final String COLUMN_DATA = "data";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_DELIVERY_MESSAGE = "delivery_message";

    @JoinColumn(name = COLUMN_CLIENT_ID, nullable = false)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Client client;

    @Column(name = COLUMN_CREATED, nullable = false)
    private Date created = new Date();

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_FORMAT, nullable = false)
    private RPCMessageFormat format;

    @Lob
    @Column(name = COLUMN_DATA, nullable = false)
    private byte[] data;

    @Enumerated(EnumType.STRING)
    @Column(name = COLUMN_STATE, length = 255)
    private MessageDeliveryState state;

    @Column(name = COLUMN_DELIVERY_MESSAGE, length = Short.MAX_VALUE)
    private String deliveryMessage;

    public Client getClient() {
        return client;
    }

    public void setClient(final Client client) {
        this.client = client;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(final Date created) {
        this.created = created;
    }

    public RPCMessageFormat getFormat() {
        return format;
    }

    public void setFormat(final RPCMessageFormat format) {
        this.format = format;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(final byte[] data) {
        this.data = data;
    }

    @Nullable
    public MessageDeliveryState getState() {
        return state;
    }

    public void setState(final MessageDeliveryState state) {
        this.state = state;
    }

    @Nullable
    public String getDeliveryMessage() {
        return deliveryMessage;
    }

    public void setDeliveryMessage(final String deliveryMessage) {
        this.deliveryMessage = deliveryMessage;
    }

}
