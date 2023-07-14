package fr.atlasworld.network.networking;

import io.netty.buffer.ByteBuf;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class PacketByteBuf {
    private final ByteBuf parent;

    public PacketByteBuf(ByteBuf parent) {
        this.parent = parent;
    }

    public PacketByteBuf writeShort(short value) {
        this.parent.writeShort(value);
        return this;
    }

    public short readShort() {
        return this.parent.readShort();
    }

    public PacketByteBuf writeShortArray(short[] value) {
        this.parent.writeInt(value.length);
        for (short s : value) {
            this.writeShort(s);
        }
        return this;
    }

    public short[] readShortArray() {
        short[] ret = new short[this.readInt()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.readShort();
        }
        return ret;
    }

    public PacketByteBuf writeInt(int value) {
        this.parent.writeInt(value);
        return this;
    }

    public int readInt() {
        return this.parent.readInt();
    }

    public PacketByteBuf writeIntArray(int[] value) {
        this.parent.writeInt(value.length);
        for (int i : value) {
            this.writeInt(i);
        }
        return this;
    }

    public int[] readIntArray() {
        int[] ret = new int[this.readInt()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.readInt();
        }
        return ret;
    }

    public PacketByteBuf writeLong(long value) {
        this.parent.writeLong(value);
        return this;
    }

    public long readLong() {
        return this.parent.readLong();
    }

    public PacketByteBuf writeLongArray(long[] value) {
        this.parent.writeInt(value.length);
        for (long l : value) {
            this.writeLong(l);
        }
        return this;
    }

    public long[] readLongArray() {
        long[] ret = new long[this.readInt()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.readLong();
        }
        return ret;
    }

    public PacketByteBuf writeFloat(float value) {
        this.parent.writeFloat(value);
        return this;
    }

    public float readFloat() {
        return this.parent.readFloat();
    }

    public PacketByteBuf writeFloatArray(float[] value) {
        this.writeInt(value.length);
        for (float f : value) {
            this.writeFloat(f);
        }
        return this;
    }

    public float[] readFloatArray() {
        float[] ret = new float[this.readInt()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.readFloat();
        }
        return ret;
    }

    public PacketByteBuf writeDouble(double value) {
        this.parent.writeDouble(value);
        return this;
    }

    public double readDouble() {
        return this.parent.readDouble();
    }

    public PacketByteBuf writeDoubleArray(double[] value) {
        this.parent.writeInt(value.length);
        for (double d : value) {
            this.writeDouble(d);
        }
        return this;
    }

    public double[] readDoubleArray() {
        double[] ret = new double[this.readInt()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.readDouble();
        }
        return ret;
    }

    public PacketByteBuf writeChar(char value) {
        this.parent.writeChar(value);
        return this;
    }

    public char readChar() {
        return this.parent.readChar();
    }

    public PacketByteBuf writeCharArray(char[] value) {
        this.parent.writeInt(value.length);
        for (char c : value) {
            this.writeChar(c);
        }
        return this;
    }

    public char[] readCharArray() {
        char[] ret = new char[this.readInt()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.readChar();
        }
        return ret;
    }

    public PacketByteBuf writeBoolean(boolean value) {
        this.parent.writeBoolean(value);
        return this;
    }

    public boolean readBoolean() {
        return this.parent.readBoolean();
    }

    public PacketByteBuf writeBooleanArray(boolean[] value) {
        this.parent.writeInt(value.length);
        for (boolean b : value) {
            this.writeBoolean(b);
        }
        return this;
    }

    public boolean[] readBooleanArray() {
        boolean[] ret = new boolean[this.readInt()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = this.readBoolean();
        }
        return ret;
    }

    public PacketByteBuf writeString(String value) {
        return this.writeString(value, StandardCharsets.UTF_8);
    }

    public PacketByteBuf writeString(String value, Charset charset) {
        this.parent.writeInt(value.length());
        this.parent.writeCharSequence(value, charset);
        return this;
    }

    public String readString() {
        return this.readString(StandardCharsets.UTF_8);
    }

    public String readString(Charset charset) {
        return this.parent.readCharSequence(this.parent.readInt(), charset).toString();
    }

    public PacketByteBuf writeUuid(UUID value) {
        return this.writeString(value.toString());
    }

    public UUID readUuid() {
        return UUID.fromString(this.readString());
    }

    public PacketByteBuf writeByteBuf(ByteBuf buf) {
        this.parent.writeBytes(buf);
        return this;
    }

    public PacketByteBuf writeByteBuf(PacketByteBuf buf) {
        this.parent.writeBytes(buf.getParent());
        return this;
    }

    public void release() {
        this.parent.release();
    }

    public int maxCapacity() {
        return this.parent.maxCapacity();
    }

    public int capacity() {
        return this.parent.capacity();
    }

    public PacketByteBuf capacity(int capacity) {
        this.parent.capacity(capacity);
        return this;
    }

    public int readerIndex() {
        return this.parent.readerIndex();
    }

    public PacketByteBuf readerIndex(int index) {
        this.parent.readerIndex(index);
        return this;
    }

    public int writerIndex() {
        return this.parent.writerIndex();
    }

    public PacketByteBuf writerIndex(int index) {
        this.parent.writerIndex(index);
        return this;
    }

    public ByteBuf getParent() {
        return parent;
    }
}
