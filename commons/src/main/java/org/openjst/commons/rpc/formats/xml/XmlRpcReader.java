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

import net.n3.nanoxml.IXMLBuilder;
import org.jetbrains.annotations.Nullable;
import org.openjst.commons.dto.tuples.Pair;
import org.openjst.commons.encodings.Base64;
import org.openjst.commons.rpc.RPCParameters;
import org.openjst.commons.rpc.exceptions.RPCException;
import org.openjst.commons.rpc.objects.RPCObjectsFactory;
import org.openjst.commons.utils.DateTimeUtils;
import org.openjst.commons.utils.RPCUtils;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;
import java.util.Stack;

/**
 * @author Sergey Grachev
 */
final class XmlRpcReader implements IXMLBuilder {

    private final boolean useExtensions;
    private boolean finished;
    private Stack<XmlRpcUtils.Element> stack;
    private XmlRpcUtils.State state = XmlRpcUtils.State.ROOT;
    private String objectName;
    private String methodName;
    private RPCParameters parameters;
    private XmlRpcUtils.DataType type = null;
    private int errorCode;
    private String errorString;
    private Object faultCustom;
    private boolean fault;
    private int level;

    public XmlRpcReader(final boolean useExtensions) {
        this.useExtensions = useExtensions;
    }

    private void addParameter(final Object value) {
        if (parameters == null) {
            parameters = RPCObjectsFactory.newParameters();
        }

        parameters.add(value);
    }

    public void startBuilding(final String systemID, final int lineNr) throws RPCException {
        stack = new Stack<XmlRpcUtils.Element>();
        fault = false;
        level = 0;
        finished = false;
    }

    public void newProcessingInstruction(final String target, final Reader reader) throws RPCException {
        // ignore
    }

    public void addAttribute(final String key, final String nsPrefix, final String nsSystemID, final String value, final String type) throws RPCException {
        // ignore
    }

    public void elementAttributesProcessed(final String name, final String nsPrefix, final String nsSystemID) throws RPCException {
        // ignore
    }

    public void startElement(final String name, final String nsPrefix, final String nsSystemID, final String systemID, final int lineNr) throws RPCException {
        final XmlRpcUtils.State previousState = state;
        switch (state) {
            case ROOT: {
                if (XmlRpcUtils.TAG_METHOD_CALL.equals(name)) {
                    state = XmlRpcUtils.State.METHOD_CALL;
                    type = XmlRpcUtils.DataType.REQUEST;
                } else if (XmlRpcUtils.TAG_METHOD_RESPONSE.equals(name)) {
                    state = XmlRpcUtils.State.METHOD_RESPONSE;
                    type = XmlRpcUtils.DataType.RESPONSE;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case METHOD_CALL: {
                if (XmlRpcUtils.TAG_METHOD_NAME.equals(name)) {
                    state = XmlRpcUtils.State.METHOD_NAME;
                } else if (XmlRpcUtils.TAG_PARAMS.equals(name)) {
                    state = XmlRpcUtils.State.PARAMS;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case METHOD_RESPONSE: {
                if (XmlRpcUtils.TAG_PARAMS.equals(name)) {
                    state = XmlRpcUtils.State.PARAMS;
                } else if (XmlRpcUtils.TAG_FAULT.equals(name)) {
                    state = XmlRpcUtils.State.FAULT;
                    fault = true;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case FAULT: {
                if (XmlRpcUtils.TAG_VALUE.equals(name)) {
                    state = XmlRpcUtils.State.VALUE;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case PARAMS: {
                if (XmlRpcUtils.TAG_PARAM.equals(name)) {
                    state = XmlRpcUtils.State.PARAM;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case PARAM: {
                if (XmlRpcUtils.TAG_VALUE.equals(name)) {
                    state = XmlRpcUtils.State.VALUE;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case VALUE: {
                if (XmlRpcUtils.TAG_ARRAY.equals(name)) {
                    state = XmlRpcUtils.State.ARRAY;
                } else if (XmlRpcUtils.TAG_BASE64.equals(name)) {
                    state = XmlRpcUtils.State.BASE64;
                } else if (XmlRpcUtils.TAG_BOOLEAN.equals(name)) {
                    state = XmlRpcUtils.State.BOOLEAN;
                } else if (XmlRpcUtils.TAG_DATETIME_ISO8601.equals(name)) {
                    state = XmlRpcUtils.State.DATETIME;
                } else if (XmlRpcUtils.TAG_DOUBLE.equals(name)) {
                    state = XmlRpcUtils.State.DOUBLE;
                } else if (XmlRpcUtils.TAG_I4.equals(name)) {
                    state = XmlRpcUtils.State.INT;
                } else if (XmlRpcUtils.TAG_INT.equals(name)) {
                    state = XmlRpcUtils.State.INT;
                } else if (XmlRpcUtils.TAG_STRING.equals(name)) {
                    state = XmlRpcUtils.State.STRING;
                } else if (XmlRpcUtils.TAG_NIL.equals(name)) {
                    state = XmlRpcUtils.State.NIL;
                } else if (XmlRpcUtils.TAG_STRUCT.equals(name)) {
                    state = XmlRpcUtils.State.STRUCT;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case ARRAY: {
                if (XmlRpcUtils.TAG_DATA.equals(name)) {
                    state = XmlRpcUtils.State.DATA;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case DATA: {
                if (XmlRpcUtils.TAG_VALUE.equals(name)) {
                    state = XmlRpcUtils.State.VALUE;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case STRUCT: {
                if (XmlRpcUtils.TAG_MEMBER.equals(name)) {
                    state = XmlRpcUtils.State.MEMBER;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            case MEMBER: {
                if (XmlRpcUtils.TAG_NAME.equals(name)) {
                    state = XmlRpcUtils.State.NAME;
                } else if (XmlRpcUtils.TAG_VALUE.equals(name)) {
                    state = XmlRpcUtils.State.VALUE;
                } else {
                    throw RPCException.newUnexpectedXmlTag(name, lineNr);
                }
                break;
            }
            default: {
                throw RPCException.newUnexpectedXmlTag(name, lineNr);
            }
        }

        stack.push(previousState.newStackElement());
        level++;
    }

    public void endElement(final String name, final String nsPrefix, final String nsSystemID) throws RPCException {
        final XmlRpcUtils.Element currentElement = stack.pop();
        switch (state) {
            // value types
            case BASE64:
            case BOOLEAN:
            case DATETIME:
            case DOUBLE:
            case INT:
            case STRING:
            case NIL:
            case ARRAY:
            case DATA:
            case STRUCT: {
                // copy to parent
                stack.peek().value = currentElement.value;
                break;
            }
            case NAME: {
                stack.peek().name = currentElement.name;
                break;
            }
            // value
            case VALUE: {
                switch (currentElement.parentState) {
                    case PARAM: {
                        addParameter(currentElement.value);
                        break;
                    }
                    case DATA: {
                        stack.peek().addToArray(currentElement.value);
                        break;
                    }
                    case MEMBER: {
                        stack.peek().value = currentElement.value;
                        break;
                    }
                }
                break;
            }
            case MEMBER: {
                if (XmlRpcUtils.DataType.RESPONSE.equals(type) && fault && level == 5) {
                    if (XmlRpcUtils.VALUE_FAULT_CODE.equals(currentElement.name)) {
                        this.errorCode = ((Number) currentElement.value).intValue();
                    } else if (XmlRpcUtils.VALUE_FAULT_STRING.equals(currentElement.name)) {
                        this.errorString = (String) currentElement.value;
                    } else if (XmlRpcUtils.VALUE_FAULT_CUSTOM.equals(currentElement.name)) {
                        this.faultCustom = currentElement.value;
                    }
                } else {
                    stack.peek().addObjectField(currentElement.name, currentElement.value);
                }
                break;
            }
            case METHOD_CALL:
            case METHOD_RESPONSE: {
                finished = true;
                break;
            }
        }

        state = currentElement.parentState;
        level--;
    }

    public void addPCData(final Reader reader, final String systemID, final int lineNr) throws RPCException {
        final XmlRpcUtils.Element element = stack.peek();
        final StringBuilder value;
        try {
            value = XmlRpcUtils.readString(reader);
        } catch (IOException e) {
            throw RPCException.newNested(e);
        }
        switch (state) {
            case METHOD_NAME: {
                final String name = value.toString().trim();
                final Pair<String, String> method = RPCUtils.parseMethod(name);
                if (method == null) {
                    throw RPCException.newIncorrectMethodName(name);
                }
                objectName = method.first();
                methodName = method.second();
                break;
            }
            case BASE64: {
                element.value = Base64.decode(value.toString());
                break;
            }
            case BOOLEAN: {
                element.value = Boolean.valueOf(value.toString());
                break;
            }
            case DATETIME: {
                try {
                    element.value = DateTimeUtils.parseXmlRpcIso8601(value.toString());
                } catch (ParseException e) {
                    throw RPCException.newNested(e);
                }
                break;
            }
            case DOUBLE: {
                element.value = Double.parseDouble(value.toString());
                break;
            }
            case INT: {
                element.value = smallestInteger(value.toString());
                break;
            }
            case STRING: {
                element.value = value.toString();
                break;
            }
            case NAME: {
                element.name = value.toString();
                break;
            }
            default: {
                throw RPCException.newUnexpectedXmlPCData(value.toString(), lineNr);
            }
        }
    }

    @Nullable
    public Object getResult() throws RPCException {
        if (!finished) {
            return null;
        }

        this.stack.clear();
        this.stack = null;

        if (XmlRpcUtils.DataType.REQUEST.equals(type)) {

            return RPCObjectsFactory.newRequest(null, objectName, methodName, parameters);

        } else if (XmlRpcUtils.DataType.RESPONSE.equals(type)) {

            if (fault) {
                if (faultCustom != null) {
                    return RPCObjectsFactory.newResponse(null, errorCode, errorString, faultCustom);
                }
                return RPCObjectsFactory.newResponse(null, errorCode, errorString);
            }
            return RPCObjectsFactory.newResponse(null, parameters);

        }

        throw RPCException.newUnknownMessageFormat();
    }

    private static Object smallestInteger(final String value) {
        final long number = Long.parseLong(value);
        if (number >= Byte.MIN_VALUE && number <= Byte.MAX_VALUE) {
            return (byte) number;
        } else if (number >= Short.MIN_VALUE && number <= Short.MAX_VALUE) {
            return (short) number;
        } else if (number >= Integer.MIN_VALUE && number <= Integer.MAX_VALUE) {
            return (int) number;
        }
        return number;
    }
}
