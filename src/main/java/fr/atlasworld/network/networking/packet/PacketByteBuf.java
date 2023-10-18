package fr.atlasworld.network.networking.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PacketByteBuf extends ByteBuf {
    private final ByteBuf parent;

    public PacketByteBuf(ByteBuf parent) {
        this.parent = parent;
    }
    
    @Override
    public int capacity() {
        return this.parent.capacity();
    }

    @Override
    public PacketByteBuf capacity(int newCapacity) {
        this.parent.capacity(newCapacity);
        return this;
    }

    @Override
    public int maxCapacity() {
        return this.parent.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.parent.alloc();
    }

    @Override
    @Deprecated
    public ByteOrder order() {
        return this.parent.order();
    }

    @Override
    @Deprecated
    public PacketByteBuf order(ByteOrder endianness) {
        this.parent.order(endianness);
        return this;
    }

    @Override
    public PacketByteBuf unwrap() {
        this.parent.unwrap();
        return this;
    }

    @Override
    public boolean isDirect() {
        return this.parent.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return this.parent.isReadOnly();
    }

    @Override
    public PacketByteBuf asReadOnly() {
        return new PacketByteBuf(this.parent.asReadOnly());
    }

    @Override
    public int readerIndex() {
        return this.parent.readerIndex();
    }

    @Override
    public PacketByteBuf readerIndex(int readerIndex) {
        this.parent.readerIndex(readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.parent.writerIndex();
    }

    @Override
    public PacketByteBuf writerIndex(int writerIndex) {
        this.parent.writerIndex(writerIndex);
        return this;
    }

    @Override
    public PacketByteBuf setIndex(int readerIndex, int writerIndex) {
        this.parent.setIndex(readerIndex, writerIndex);
        return this;
    }

    @Override
    public int readableBytes() {
        return this.parent.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.parent.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.parent.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.parent.isReadable();
    }

    @Override
    public boolean isReadable(int size) {
        return this.parent.isReadable(size);
    }

    @Override
    public boolean isWritable() {
        return this.parent.isWritable();
    }

    @Override
    public boolean isWritable(int size) {
        return this.parent.isWritable(size);
    }

    @Override
    public PacketByteBuf clear() {
        this.parent.clear();
        return this;
    }

    @Override
    public PacketByteBuf markReaderIndex() {
        this.parent.markReaderIndex();
        return this;
    }

    @Override
    public PacketByteBuf resetReaderIndex() {
        this.parent.resetReaderIndex();
        return this;
    }

    @Override
    public PacketByteBuf markWriterIndex() {
        this.parent.markWriterIndex();
        return this;
    }

    @Override
    public PacketByteBuf resetWriterIndex() {
        this.parent.resetWriterIndex();
        return this;
    }

    @Override
    public PacketByteBuf discardReadBytes() {
        this.parent.discardReadBytes();
        return this;
    }

    @Override
    public PacketByteBuf discardSomeReadBytes() {
        this.parent.discardSomeReadBytes();
        return this;
    }

    @Override
    public PacketByteBuf ensureWritable(int minWritableBytes) {
        this.parent.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public int ensureWritable(int minWritableBytes, boolean force) {
        return this.parent.ensureWritable(minWritableBytes, force);
    }

    @Override
    public boolean getBoolean(int index) {
        return this.parent.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return this.parent.getByte(index);
    }

    @Override
    public short getUnsignedByte(int index) {
        return this.parent.getUnsignedByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.parent.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return this.parent.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int index) {
        return this.parent.getUnsignedShort(index);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.parent.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int index) {
        return this.parent.getMedium(index);
    }

    @Override
    public int getMediumLE(int index) {
        return this.parent.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.parent.getUnsignedMedium(index);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.parent.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int index) {
        return this.parent.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return this.parent.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.parent.getUnsignedInt(index);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return this.parent.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int index) {
        return this.parent.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return this.parent.getLongLE(index);
    }

    @Override
    public char getChar(int index) {
        return this.parent.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return this.parent.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return this.parent.getDouble(index);
    }

    @Override
    public PacketByteBuf getBytes(int index, ByteBuf dst) {
        this.parent.getBytes(index, dst);
        return this;
    }

    @Override
    public PacketByteBuf getBytes(int index, ByteBuf dst, int length) {
        this.parent.getBytes(index, dst, length);
        return this;
    }

    @Override
    public PacketByteBuf getBytes(int index, ByteBuf dst, int dstIndex, int length) {
        this.parent.getBytes(index,dst, dstIndex, length);
        return this;
    }

    public PacketByteBuf getBytes(int index, PacketByteBuf dst) {
        return this.getBytes(index, dst.asByteBuf());
    }

    public PacketByteBuf getBytes(int index, PacketByteBuf dst, int length) {
        return this.getBytes(index, dst.asByteBuf(), length);
    }

    public PacketByteBuf getBytes(int index, PacketByteBuf dst, int dstIndex, int length) {
        return this.getBytes(index, dst.asByteBuf(), dstIndex, length);
    }

    @Override
    public PacketByteBuf getBytes(int index, byte[] dst) {
        this.parent.getBytes(index, dst);
        return this;
    }

    @Override
    public PacketByteBuf getBytes(int index, byte[] dst, int dstIndex, int length) {
        this.parent.getBytes(index, dst, dstIndex, length);
        return this;
    }

    @Override
    public PacketByteBuf getBytes(int index, ByteBuffer dst) {
        this.parent.getBytes(index, dst);
        return this;
    }

    @Override
    public PacketByteBuf getBytes(int index, OutputStream out, int length) throws IOException {
        this.parent.getBytes(index, out, length);
        return this;
    }

    @Override
    public int getBytes(int index, GatheringByteChannel out, int length) throws IOException {
        return this.parent.getBytes(index, out, length);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.parent.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.parent.getCharSequence(index, length, charset);
    }

    public String getString(int index, Charset charset) {
        int length = this.getInt(index);
        return (String) this.getCharSequence((index + Integer.SIZE), length, charset);
    }

    public String getString(int index) {
        return this.getString(index, StandardCharsets.UTF_8);
    }

    @Override
    public PacketByteBuf setBoolean(int index, boolean value) {
        return null;
    }

    @Override
    public PacketByteBuf setByte(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setShort(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setShortLE(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setMedium(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setMediumLE(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setInt(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setIntLE(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setLong(int index, long value) {
        return null;
    }

    @Override
    public PacketByteBuf setLongLE(int index, long value) {
        return null;
    }

    @Override
    public PacketByteBuf setChar(int index, int value) {
        return null;
    }

    @Override
    public PacketByteBuf setFloat(int index, float value) {
        return null;
    }

    @Override
    public PacketByteBuf setDouble(int index, double value) {
        return null;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuf src) {
        return null;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuf src, int length) {
        return null;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        return null;
    }

    @Override
    public PacketByteBuf setBytes(int index, byte[] src) {
        return null;
    }

    @Override
    public PacketByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        return null;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuffer src) {
        return null;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return 0;
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return 0;
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return 0;
    }

    @Override
    public PacketByteBuf setZero(int index, int length) {
        return null;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return 0;
    }

    @Override
    public boolean readBoolean() {
        return false;
    }

    @Override
    public byte readByte() {
        return 0;
    }

    @Override
    public short readUnsignedByte() {
        return 0;
    }

    @Override
    public short readShort() {
        return 0;
    }

    @Override
    public short readShortLE() {
        return 0;
    }

    @Override
    public int readUnsignedShort() {
        return 0;
    }

    @Override
    public int readUnsignedShortLE() {
        return 0;
    }

    @Override
    public int readMedium() {
        return 0;
    }

    @Override
    public int readMediumLE() {
        return 0;
    }

    @Override
    public int readUnsignedMedium() {
        return 0;
    }

    @Override
    public int readUnsignedMediumLE() {
        return 0;
    }

    @Override
    public int readInt() {
        return 0;
    }

    @Override
    public int readIntLE() {
        return 0;
    }

    @Override
    public long readUnsignedInt() {
        return 0;
    }

    @Override
    public long readUnsignedIntLE() {
        return 0;
    }

    @Override
    public long readLong() {
        return 0;
    }

    @Override
    public long readLongLE() {
        return 0;
    }

    @Override
    public char readChar() {
        return 0;
    }

    @Override
    public float readFloat() {
        return 0;
    }

    @Override
    public double readDouble() {
        return 0;
    }

    @Override
    public PacketByteBuf readBytes(int length) {
        return null;
    }

    @Override
    public PacketByteBuf readSlice(int length) {
        return null;
    }

    @Override
    public PacketByteBuf readRetainedSlice(int length) {
        return null;
    }

    @Override
    public PacketByteBuf readBytes(ByteBuf dst) {
        return null;
    }

    @Override
    public PacketByteBuf readBytes(ByteBuf dst, int length) {
        return null;
    }

    @Override
    public PacketByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        return null;
    }

    @Override
    public PacketByteBuf readBytes(byte[] dst) {
        return null;
    }

    @Override
    public PacketByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        return null;
    }

    @Override
    public PacketByteBuf readBytes(ByteBuffer dst) {
        return null;
    }

    @Override
    public PacketByteBuf readBytes(OutputStream out, int length) throws IOException {
        return null;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return 0;
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return null;
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return 0;
    }

    @Override
    public PacketByteBuf skipBytes(int length) {
        return null;
    }

    @Override
    public PacketByteBuf writeBoolean(boolean value) {
        return null;
    }

    @Override
    public PacketByteBuf writeByte(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeShort(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeShortLE(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeMedium(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeMediumLE(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeInt(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeIntLE(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeLong(long value) {
        return null;
    }

    @Override
    public PacketByteBuf writeLongLE(long value) {
        return null;
    }

    @Override
    public PacketByteBuf writeChar(int value) {
        return null;
    }

    @Override
    public PacketByteBuf writeFloat(float value) {
        return null;
    }

    @Override
    public PacketByteBuf writeDouble(double value) {
        return null;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuf src) {
        return null;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuf src, int length) {
        return null;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        return null;
    }

    @Override
    public PacketByteBuf writeBytes(byte[] src) {
        return null;
    }

    @Override
    public PacketByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        return null;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuffer src) {
        return null;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return 0;
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return 0;
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return 0;
    }

    @Override
    public PacketByteBuf writeZero(int length) {
        return null;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return 0;
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return 0;
    }

    @Override
    public int bytesBefore(byte value) {
        return 0;
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return 0;
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return 0;
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return 0;
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return 0;
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return 0;
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return 0;
    }

    @Override
    public PacketByteBuf copy() {
        return null;
    }

    @Override
    public PacketByteBuf copy(int index, int length) {
        return null;
    }

    @Override
    public PacketByteBuf slice() {
        return null;
    }

    @Override
    public PacketByteBuf retainedSlice() {
        return null;
    }

    @Override
    public PacketByteBuf slice(int index, int length) {
        return null;
    }

    @Override
    public PacketByteBuf retainedSlice(int index, int length) {
        return null;
    }

    @Override
    public PacketByteBuf duplicate() {
        return null;
    }

    @Override
    public PacketByteBuf retainedDuplicate() {
        return null;
    }

    @Override
    public int nioBufferCount() {
        return 0;
    }

    @Override
    public ByteBuffer nioBuffer() {
        return null;
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return null;
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return null;
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return new ByteBuffer[0];
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return new ByteBuffer[0];
    }

    @Override
    public boolean hasArray() {
        return false;
    }

    @Override
    public byte[] array() {
        return new byte[0];
    }

    @Override
    public int arrayOffset() {
        return 0;
    }

    @Override
    public boolean hasMemoryAddress() {
        return false;
    }

    @Override
    public long memoryAddress() {
        return 0;
    }

    @Override
    public String toString(Charset charset) {
        return null;
    }

    @NotNull
    @Override
    public String toString(int index, int length, Charset charset) {
        return null;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return 0;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public PacketByteBuf retain(int increment) {
        return null;
    }

    @Override
    public int refCnt() {
        return 0;
    }

    @Override
    public PacketByteBuf retain() {
        return null;
    }

    @Override
    public PacketByteBuf touch() {
        return null;
    }

    @Override
    public PacketByteBuf touch(Object hint) {
        return null;
    }

    @Override
    public boolean release() {
        return false;
    }

    @Override
    public boolean release(int decrement) {
        return false;
    }
}
