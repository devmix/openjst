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

package org.openjst.commons.io;

import org.openjst.commons.io.buffer.ArrayDataBuffer;
import org.openjst.commons.io.buffer.ArrayDataInputBuffer;
import org.openjst.commons.io.buffer.ArrayDataOutputBuffer;
import org.openjst.commons.io.buffer.DataBufferException;
import org.testng.annotations.Test;

import java.io.Serializable;

import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Sergey Grachev
 */
public class DataOutputBufferTest {

    @Test(groups = "unit")
    public void test() throws DataBufferException {
        final SerializedObject writeObject = new SerializedObject(1, 2.2, "test");

        final ArrayDataOutputBuffer out = new ArrayDataOutputBuffer();
        out.writeInt8((byte) 0x00);
        out.writeInt8((byte) 0x01);
        out.writeInt8((byte) 0x7F);
        out.writeInt8((byte) 0x80);
        out.writeInt8((byte) 0xFF);
        out.writeInt16((short) 0x0000);
        out.writeInt16((short) 0x0001);
        out.writeInt16((short) 0x7FFF);
        out.writeInt16((short) 0x8000);
        out.writeInt16((short) 0xFFFF);
        out.writeInt32(0x000000);
        out.writeInt32(0x000001);
        out.writeInt32(0x7FFFFF);
        out.writeInt32(0x800000);
        out.writeInt32(0xFFFFFF);
        out.writeInt64(0x000000000000L);
        out.writeInt64(0x000000000001L);
        out.writeInt64(0x7FFFFFFFFFFFL);
        out.writeInt64(0x800000000000L);
        out.writeInt64(0xFFFFFFFFFFFFL);
        out.writeFloat32(Float.MIN_VALUE);
        out.writeFloat32(Float.MIN_VALUE / 2);
        out.writeFloat32(Float.MAX_VALUE / 2);
        out.writeFloat32(Float.MAX_VALUE);
        out.writeFloat64(Double.MIN_VALUE);
        out.writeFloat64(Double.MIN_VALUE / 2);
        out.writeFloat64(Double.MAX_VALUE / 2);
        out.writeFloat64(Double.MAX_VALUE);
        out.writeBoolean8(true);
        out.writeBoolean8(false);
        out.writeBytes(new byte[]{0x00, 0x01, 0x7F, (byte) 0x80, (byte) 0xFF});
        out.writeUtf8("test тест");
        out.writeObject(writeObject);
        out.writeVLQInt32(0x000000);
        out.writeVLQInt32(0x000001);
        out.writeVLQInt32(0x7FFFFF);
        out.writeVLQInt32(0x800000);
        out.writeVLQInt32(0xFFFFFF);
        out.writeVLQInt64(0x000000000000L);
        out.writeVLQInt64(0x000000000001L);
        out.writeVLQInt64(0x7FFFFFFFFFFFL);
        out.writeVLQInt64(0x800000000000L);
        out.writeVLQInt64(0xFFFFFFFFFFFFL);
        out.writeVLQFloat32(Float.MIN_VALUE);
        out.writeVLQFloat32(Float.MIN_VALUE / 2);
        out.writeVLQFloat32(Float.MAX_VALUE / 2);
        out.writeVLQFloat32(Float.MAX_VALUE);
        out.writeVLQFloat64(Double.MIN_VALUE);
        out.writeVLQFloat64(Double.MIN_VALUE / 2);
        out.writeVLQFloat64(Double.MAX_VALUE / 2);
        out.writeVLQFloat64(Double.MAX_VALUE);

        final ArrayDataInputBuffer in = new ArrayDataInputBuffer(out.toByteArray(), 0, out.toByteArray().length);
        assertThat(in.readInt8()).isEqualTo((byte) 0x00);
        assertThat(in.readInt8()).isEqualTo((byte) 0x01);
        assertThat(in.readInt8()).isEqualTo((byte) 0x7F);
        assertThat(in.readInt8()).isEqualTo((byte) 0x80);
        assertThat(in.readInt8()).isEqualTo((byte) 0xFF);
        assertThat(in.readInt16()).isEqualTo((short) 0x0000);
        assertThat(in.readInt16()).isEqualTo((short) 0x0001);
        assertThat(in.readInt16()).isEqualTo((short) 0x7FFF);
        assertThat(in.readInt16()).isEqualTo((short) 0x8000);
        assertThat(in.readInt16()).isEqualTo((short) 0xFFFF);
        assertThat(in.readInt32()).isEqualTo(0x000000);
        assertThat(in.readInt32()).isEqualTo(0x000001);
        assertThat(in.readInt32()).isEqualTo(0x7FFFFF);
        assertThat(in.readInt32()).isEqualTo(0x800000);
        assertThat(in.readInt32()).isEqualTo(0xFFFFFF);
        assertThat(in.readInt64()).isEqualTo(0x000000000000L);
        assertThat(in.readInt64()).isEqualTo(0x000000000001L);
        assertThat(in.readInt64()).isEqualTo(0x7FFFFFFFFFFFL);
        assertThat(in.readInt64()).isEqualTo(0x800000000000L);
        assertThat(in.readInt64()).isEqualTo(0xFFFFFFFFFFFFL);
        assertThat(in.readFloat32()).isEqualTo(Float.MIN_VALUE);
        assertThat(in.readFloat32()).isEqualTo(Float.MIN_VALUE / 2);
        assertThat(in.readFloat32()).isEqualTo(Float.MAX_VALUE / 2);
        assertThat(in.readFloat32()).isEqualTo(Float.MAX_VALUE);
        assertThat(in.readFloat64()).isEqualTo(Double.MIN_VALUE);
        assertThat(in.readFloat64()).isEqualTo(Double.MIN_VALUE / 2);
        assertThat(in.readFloat64()).isEqualTo(Double.MAX_VALUE / 2);
        assertThat(in.readFloat64()).isEqualTo(Double.MAX_VALUE);
        assertThat(in.readBoolean8()).isTrue();
        assertThat(in.readBoolean8()).isFalse();
        assertThat(in.readBytes(5)).isEqualTo(new byte[]{0x00, 0x01, 0x7F, (byte) 0x80, (byte) 0xFF});
        assertThat(in.readUtf8()).isEqualTo("test тест");
        assertThat(in.readObject()).isEqualTo(writeObject);
        assertThat(in.readVLQInt32()).isEqualTo(0x000000);
        assertThat(in.readVLQInt32()).isEqualTo(0x000001);
        assertThat(in.readVLQInt32()).isEqualTo(0x7FFFFF);
        assertThat(in.readVLQInt32()).isEqualTo(0x800000);
        assertThat(in.readVLQInt32()).isEqualTo(0xFFFFFF);
        assertThat(in.readVLQInt64()).isEqualTo(0x000000000000L);
        assertThat(in.readVLQInt64()).isEqualTo(0x000000000001L);
        assertThat(in.readVLQInt64()).isEqualTo(0x7FFFFFFFFFFFL);
        assertThat(in.readVLQInt64()).isEqualTo(0x800000000000L);
        assertThat(in.readVLQInt64()).isEqualTo(0xFFFFFFFFFFFFL);
        assertThat(in.readVLQFloat32()).isEqualTo(Float.MIN_VALUE);
        assertThat(in.readVLQFloat32()).isEqualTo(Float.MIN_VALUE / 2);
        assertThat(in.readVLQFloat32()).isEqualTo(Float.MAX_VALUE / 2);
        assertThat(in.readVLQFloat32()).isEqualTo(Float.MAX_VALUE);
        assertThat(in.readVLQFloat64()).isEqualTo(Double.MIN_VALUE);
        assertThat(in.readVLQFloat64()).isEqualTo(Double.MIN_VALUE / 2);
        assertThat(in.readVLQFloat64()).isEqualTo(Double.MAX_VALUE / 2);
        assertThat(in.readVLQFloat64()).isEqualTo(Double.MAX_VALUE);
    }

    @Test(groups = "unit")
    public void testRandomAccess() throws DataBufferException {
        final SerializedObject writeObject = new SerializedObject(1, 2.2, "test");

        final ArrayDataBuffer b = new ArrayDataBuffer();
        b.writeInt8((byte) 0x00);
        b.writeInt8((byte) 0x01);
        b.writeInt8((byte) 0x7F);
        b.writeInt8((byte) 0x80);
        b.writeInt8((byte) 0xFF);
        b.writeInt16((short) 0x0000);
        b.writeInt16((short) 0x0001);
        b.writeInt16((short) 0x7FFF);
        b.writeInt16((short) 0x8000);
        b.writeInt16((short) 0xFFFF);
        b.writeInt32(0x000000);
        b.writeInt32(0x000001);
        b.writeInt32(0x7FFFFF);
        b.writeInt32(0x800000);
        b.writeInt32(0xFFFFFF);
        System.out.println(b.getOffset());
        b.writeInt64(0x000000000000L);
        b.writeInt64(0x000000000001L);
        b.writeInt64(0x7FFFFFFFFFFFL);
        b.writeInt64(0x800000000000L);
        b.writeInt64(0xFFFFFFFFFFFFL);
        b.writeFloat32(Float.MIN_VALUE);
        b.writeFloat32(Float.MIN_VALUE / 2);
        b.writeFloat32(Float.MAX_VALUE / 2);
        b.writeFloat32(Float.MAX_VALUE);
        b.writeFloat64(Double.MIN_VALUE);
        b.writeFloat64(Double.MIN_VALUE / 2);
        b.writeFloat64(Double.MAX_VALUE / 2);
        b.writeFloat64(Double.MAX_VALUE);
        b.writeBoolean8(true);
        b.writeBoolean8(false);
        b.writeBytes(new byte[]{0x00, 0x01, 0x7F, (byte) 0x80, (byte) 0xFF});
        b.writeUtf8("test тест");
        b.writeObject(writeObject);

        assertThat(b.getSize()).isEqualTo(284);

        b.seek(0);
        assertThat(b.readInt8()).isEqualTo((byte) 0x00);
        assertThat(b.readInt8()).isEqualTo((byte) 0x01);
        assertThat(b.readInt8()).isEqualTo((byte) 0x7F);
        assertThat(b.readInt8()).isEqualTo((byte) 0x80);
        assertThat(b.readInt8()).isEqualTo((byte) 0xFF);
        assertThat(b.readInt16()).isEqualTo((short) 0x0000);
        assertThat(b.readInt16()).isEqualTo((short) 0x0001);
        assertThat(b.readInt16()).isEqualTo((short) 0x7FFF);
        assertThat(b.readInt16()).isEqualTo((short) 0x8000);
        assertThat(b.readInt16()).isEqualTo((short) 0xFFFF);
        assertThat(b.readInt32()).isEqualTo(0x000000);
        assertThat(b.readInt32()).isEqualTo(0x000001);
        assertThat(b.readInt32()).isEqualTo(0x7FFFFF);
        assertThat(b.readInt32()).isEqualTo(0x800000);
        assertThat(b.readInt32()).isEqualTo(0xFFFFFF);
        assertThat(b.readInt64()).isEqualTo(0x000000000000L);
        assertThat(b.readInt64()).isEqualTo(0x000000000001L);
        assertThat(b.readInt64()).isEqualTo(0x7FFFFFFFFFFFL);
        assertThat(b.readInt64()).isEqualTo(0x800000000000L);
        assertThat(b.readInt64()).isEqualTo(0xFFFFFFFFFFFFL);
        assertThat(b.readFloat32()).isEqualTo(Float.MIN_VALUE);
        assertThat(b.readFloat32()).isEqualTo(Float.MIN_VALUE / 2);
        assertThat(b.readFloat32()).isEqualTo(Float.MAX_VALUE / 2);
        assertThat(b.readFloat32()).isEqualTo(Float.MAX_VALUE);
        assertThat(b.readFloat64()).isEqualTo(Double.MIN_VALUE);
        assertThat(b.readFloat64()).isEqualTo(Double.MIN_VALUE / 2);
        assertThat(b.readFloat64()).isEqualTo(Double.MAX_VALUE / 2);
        assertThat(b.readFloat64()).isEqualTo(Double.MAX_VALUE);
        assertThat(b.readBoolean8()).isTrue();
        assertThat(b.readBoolean8()).isFalse();
        assertThat(b.readBytes(5)).isEqualTo(new byte[]{0x00, 0x01, 0x7F, (byte) 0x80, (byte) 0xFF});
        assertThat(b.readUtf8()).isEqualTo("test тест");
        assertThat(b.readObject()).isEqualTo(writeObject);

        b.seek(0);
        assertThat(b.readInt8()).isEqualTo((byte) 0x00);
        b.seek(5);
        assertThat(b.readInt16()).isEqualTo((short) 0x0000);
        b.seek(5 + 10);
        assertThat(b.readInt32()).isEqualTo(0x000000);
        b.seek(5 + 10 + 20);
        assertThat(b.readInt64()).isEqualTo(0x000000000000L);

        assertThat(b.getSize()).isEqualTo(284);
    }

    @Test(groups = "unit")
    public void testEmptyString() throws DataBufferException {
        final ArrayDataBuffer b = new ArrayDataBuffer();
        b.writeUtf8("");

        assertThat(b.getSize()).isEqualTo(4);

        b.seek(0);
        assertThat(b.readUtf8()).isEqualTo("");
    }

    private static final class SerializedObject implements Serializable {
        private static final long serialVersionUID = 7559848924922780643L;
        private final int i;
        private final double d;
        private final String s;

        private SerializedObject(final int i, final double d, final String s) {
            this.i = i;
            this.d = d;
            this.s = s;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            final SerializedObject that = (SerializedObject) o;
            return Double.compare(that.d, d) == 0 && i == that.i && !(s != null ? !s.equals(that.s) : that.s != null);
        }

        @Override
        public int hashCode() {
            int result = i;
            final long temp = d != +0.0d ? Double.doubleToLongBits(d) : 0L;
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (s != null ? s.hashCode() : 0);
            return result;
        }
    }
}
