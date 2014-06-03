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

package org.openjst.commons.rpc.formats.xml;

import net.n3.nanoxml.IXMLParser;
import net.n3.nanoxml.StdXMLReader;
import net.n3.nanoxml.XMLException;
import net.n3.nanoxml.XMLParserFactory;
import org.openjst.commons.rpc.RPCFormat;
import org.openjst.commons.rpc.RPCMessage;
import org.openjst.commons.rpc.RPCRequest;
import org.openjst.commons.rpc.RPCResponse;
import org.openjst.commons.rpc.exceptions.RPCException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Sergey Grachev
 */
public final class XmlRpc implements RPCFormat {

    private final Object lock = new Object();
    private final boolean useExtensions;
    private IXMLParser parser;

    public XmlRpc(final boolean useExtensions) {
        this.useExtensions = useExtensions;
    }

    private IXMLParser initParser() throws RPCException {
        synchronized (lock) {
            if (parser == null) {
                try {
                    parser = XMLParserFactory.createDefaultXMLParser();
                } catch (Exception e) {
                    throw RPCException.newNested(e);
                }
            }
            return parser;
        }
    }

    public <T extends RPCMessage> T read(final byte[] data) throws RPCException {
        initParser();
        try {
            parser.setReader(StdXMLReader.stringReader(new String(data, "UTF-8")));
            parser.setBuilder(new XmlRpcReader(useExtensions));
            //noinspection unchecked
            return (T) parser.parse();
        } catch (XMLException e) {
            throw RPCException.newNested(e);
        } catch (UnsupportedEncodingException e) {
            throw RPCException.newNested(e);
        }
    }

    public byte[] write(final RPCRequest request) throws RPCException {
        try {
            return new XmlRpcWriter(useExtensions).write(request);
        } catch (IOException e) {
            throw RPCException.newNested(e);
        }
    }

    public byte[] write(final RPCResponse response) throws RPCException {
        try {
            return new XmlRpcWriter(useExtensions).write(response);
        } catch (IOException e) {
            throw RPCException.newNested(e);
        }
    }
}
