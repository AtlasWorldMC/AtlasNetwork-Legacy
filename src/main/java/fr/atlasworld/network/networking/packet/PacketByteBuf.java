package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.networking.processor.ForEachByteProcessor;
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
import java.util.function.Function;

public class PacketByteBuf extends ByteBuf {
    private final ByteBuf parent;

    public PacketByteBuf(ByteBuf parent) {
        super();
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
        return (String) this.getCharSequence((index + Integer.BYTES), length, charset);
    }

    public String getString(int index) {
        return this.getString(index, StandardCharsets.UTF_8);
    }

    @Override
    public PacketByteBuf setBoolean(int index, boolean value) {
        this.parent.setBoolean(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setByte(int index, int value) {
        this.parent.setByte(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setShort(int index, int value) {
        this.parent.setShort(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setShortLE(int index, int value) {
        this.parent.setShortLE(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setMedium(int index, int value) {
        this.parent.setMedium(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setMediumLE(int index, int value) {
        this.parent.setMediumLE(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setInt(int index, int value) {
        this.parent.setInt(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setIntLE(int index, int value) {
        this.parent.setIntLE(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setLong(int index, long value) {
        this.parent.setLong(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setLongLE(int index, long value) {
        this.parent.setLongLE(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setChar(int index, int value) {
        this.parent.setChar(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setFloat(int index, float value) {
        this.parent.setFloat(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setDouble(int index, double value) {
        this.parent.setDouble(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuf src) {
        this.parent.setBytes(index, src);
        return this;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuf src, int length) {
        this.parent.setBytes(index, src, length);
        return this;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuf src, int srcIndex, int length) {
        this.parent.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public PacketByteBuf setBytes(int index, byte[] src) {
        this.parent.setBytes(index, src);
        return this;
    }

    @Override
    public PacketByteBuf setBytes(int index, byte[] src, int srcIndex, int length) {
        this.parent.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public PacketByteBuf setBytes(int index, ByteBuffer src) {
        this.parent.setBytes(index, src);
        return this;
    }

    @Override
    public int setBytes(int index, InputStream in, int length) throws IOException {
        return this.parent.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, ScatteringByteChannel in, int length) throws IOException {
        return this.parent.setBytes(index, in, length);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.parent.setBytes(index, in, position, length);
    }

    @Override
    public PacketByteBuf setZero(int index, int length) {
        this.parent.setZero(index, length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.parent.setCharSequence(index, sequence, charset);
    }

    public PacketByteBuf setString(int index, String value, Charset charset) {
        this.setInt(index, value.length());
        this.setCharSequence(index + Integer.BYTES, value, charset);
        return this;
    }

    public PacketByteBuf setString(int index, String value) {
        return this.setString(index, value, StandardCharsets.UTF_8);
    }

    @Override
    public boolean readBoolean() {
        return this.parent.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.parent.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.parent.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.parent.readShort();
    }

    @Override
    public short readShortLE() {
        return this.parent.readShortLE();
    }

    @Override
    public int readUnsignedShort() {
        return this.parent.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.parent.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.parent.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.parent.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.parent.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.parent.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.parent.readInt();
    }

    @Override
    public int readIntLE() {
        return this.parent.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.parent.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.parent.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.parent.readLong();
    }

    @Override
    public long readLongLE() {
        return this.parent.readLongLE();
    }

    @Override
    public char readChar() {
        return this.parent.readChar();
    }

    @Override
    public float readFloat() {
        return this.parent.readFloat();
    }

    @Override
    public double readDouble() {
        return this.parent.readDouble();
    }

    @Override
    public PacketByteBuf readBytes(int length) {
        return new PacketByteBuf(this.parent.readBytes(length));
    }

    @Override
    public PacketByteBuf readSlice(int length) {
        return new PacketByteBuf(this.parent.readSlice(length));
    }

    @Override
    public PacketByteBuf readRetainedSlice(int length) {
        return new PacketByteBuf(this.parent.readRetainedSlice(length));
    }

    @Override
    public PacketByteBuf readBytes(ByteBuf dst) {
        this.parent.readBytes(dst);
        return this;
    }

    @Override
    public PacketByteBuf readBytes(ByteBuf dst, int length) {
        this.parent.readBytes(dst, length);
        return this;
    }

    @Override
    public PacketByteBuf readBytes(ByteBuf dst, int dstIndex, int length) {
        this.parent.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public PacketByteBuf readBytes(byte[] dst) {
        this.parent.readBytes(dst);
        return this;
    }

    @Override
    public PacketByteBuf readBytes(byte[] dst, int dstIndex, int length) {
        this.parent.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public PacketByteBuf readBytes(ByteBuffer dst) {
        this.parent.readBytes(dst);
        return this;
    }

    @Override
    public PacketByteBuf readBytes(OutputStream out, int length) throws IOException {
        this.parent.readBytes(out, length);
        return this;
    }

    @Override
    public int readBytes(GatheringByteChannel out, int length) throws IOException {
        return this.parent.readBytes(out, length);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return this.parent.readBytes(out, position, length);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return this.parent.readCharSequence(length, charset);
    }

    public String readString(Charset charset) {
        return (String) this.readCharSequence(this.readInt(), charset);
    }

    public String readString() {
        return this.readString(StandardCharsets.UTF_8);
    }

    @Override
    public PacketByteBuf skipBytes(int length) {
        this.parent.skipBytes(length);
        return this;
    }

    @Override
    public PacketByteBuf writeBoolean(boolean value) {
        this.parent.writeBoolean(value);
        return this;
    }

    @Override
    public PacketByteBuf writeByte(int value) {
        this.parent.writeByte(value);
        return this;
    }

    @Override
    public PacketByteBuf writeShort(int value) {
        this.parent.writeShort(value);
        return this;
    }

    @Override
    public PacketByteBuf writeShortLE(int value) {
        this.parent.writeShortLE(value);
        return this;
    }

    @Override
    public PacketByteBuf writeMedium(int value) {
        this.parent.writeMedium(value);
        return this;
    }

    @Override
    public PacketByteBuf writeMediumLE(int value) {
        this.parent.writeMediumLE(value);
        return this;
    }

    @Override
    public PacketByteBuf writeInt(int value) {
        this.parent.writeInt(value);
        return this;
    }

    @Override
    public PacketByteBuf writeIntLE(int value) {
        this.parent.writeIntLE(value);
        return this;
    }

    @Override
    public PacketByteBuf writeLong(long value) {
        this.parent.writeLong(value);
        return this;
    }

    @Override
    public PacketByteBuf writeLongLE(long value) {
        this.parent.writeLongLE(value);
        return this;
    }

    @Override
    public PacketByteBuf writeChar(int value) {
        this.parent.writeChar(value);
        return this;
    }

    @Override
    public PacketByteBuf writeFloat(float value) {
        this.parent.writeFloat(value);
        return this;
    }

    @Override
    public PacketByteBuf writeDouble(double value) {
        this.parent.writeDouble(value);
        return this;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuf src) {
        this.parent.writeBytes(src);
        return this;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuf src, int length) {
        this.parent.writeBytes(src, length);
        return this;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuf src, int srcIndex, int length) {
        this.parent.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public PacketByteBuf writeBytes(byte[] src) {
        this.parent.writeBytes(src);
        return this;
    }

    @Override
    public PacketByteBuf writeBytes(byte[] src, int srcIndex, int length) {
        this.parent.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public PacketByteBuf writeBytes(ByteBuffer src) {
        this.parent.writeBytes(src);
        return this;
    }

    @Override
    public int writeBytes(InputStream in, int length) throws IOException {
        return this.parent.writeBytes(in, length);
    }

    @Override
    public int writeBytes(ScatteringByteChannel in, int length) throws IOException {
        return this.parent.writeBytes(in, length);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return this.parent.writeBytes(in, position, length);
    }

    @Override
    public PacketByteBuf writeZero(int length) {
        this.parent.writeZero(length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.parent.writeCharSequence(sequence, charset);
    }

    public PacketByteBuf writeString(String value, Charset charset) {
        this.writeInt(value.length());
        this.writeCharSequence(value, charset);
        return this;
    }

    @Override
    public int indexOf(int fromIndex, int toIndex, byte value) {
        return this.parent.indexOf(fromIndex, toIndex, value);
    }

    @Override
    public int bytesBefore(byte value) {
        return this.parent.bytesBefore(value);
    }

    @Override
    public int bytesBefore(int length, byte value) {
        return this.parent.bytesBefore(length, value);
    }

    @Override
    public int bytesBefore(int index, int length, byte value) {
        return this.parent.bytesBefore(index, length, value);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return this.parent.forEachByte(processor);
    }

    public int forEachByte(Function<Byte, Boolean> func) {
        return this.forEachByte(new ForEachByteProcessor(func));
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.parent.forEachByte(index, length, processor);
    }

    public int forEachByte(int index, int length, Function<Byte, Boolean> func) {
        return this.forEachByte(index, length, new ForEachByteProcessor(func));
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return this.parent.forEachByteDesc(processor);
    }

    public int forEachByteDesc(Function<Byte, Boolean> func) {
        return this.forEachByteDesc(new ForEachByteProcessor(func));
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.parent.forEachByteDesc(index, length, processor);
    }

    public int forEachByteDesc(int index, int length, Function<Byte, Boolean> func) {
        return this.forEachByteDesc(index, length, new ForEachByteProcessor(func));
    }

    @Override
    public PacketByteBuf copy() {
        return new PacketByteBuf(this.parent.copy());
    }

    @Override
    public PacketByteBuf copy(int index, int length) {
        return new PacketByteBuf(this.parent.copy(index, length));
    }

    @Override
    public PacketByteBuf slice() {
        return new PacketByteBuf(this.parent.slice());
    }

    @Override
    public PacketByteBuf retainedSlice() {
        return new PacketByteBuf(this.parent.retainedSlice());
    }

    @Override
    public PacketByteBuf slice(int index, int length) {
        return new PacketByteBuf(this.parent.slice(index, length));
    }

    @Override
    public PacketByteBuf retainedSlice(int index, int length) {
        return new PacketByteBuf(this.parent.retainedSlice(index, length));
    }

    @Override
    public PacketByteBuf duplicate() {
        return new PacketByteBuf(this.parent.duplicate());
    }

    @Override
    public PacketByteBuf retainedDuplicate() {
        return new PacketByteBuf(this.parent.retainedDuplicate());
    }

    @Override
    public int nioBufferCount() {
        return this.parent.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.parent.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int index, int length) {
        return this.parent.nioBuffer(index, length);
    }

    @Override
    public ByteBuffer internalNioBuffer(int index, int length) {
        return this.parent.internalNioBuffer(index, length);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.parent.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int index, int length) {
        return this.parent.nioBuffers(index, length);
    }

    @Override
    public boolean hasArray() {
        return this.parent.hasArray();
    }

    @Override
    public byte[] array() {
        return this.parent.array();
    }

    @Override
    public int arrayOffset() {
        return this.parent.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.parent.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.parent.memoryAddress();
    }

    @Override
    public String toString(Charset charset) {
        return this.parent.toString(charset);
    }

    @NotNull
    @Override
    public String toString(int index, int length, Charset charset) {
        return this.parent.toString(index, length, charset);
    }

    @Override
    public int hashCode() {
        return this.parent.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PacketByteBuf pBuf) {
            return this.parent.equals(pBuf.parent);
        }

        return this.parent.equals(obj);
    }

    @Override
    public int compareTo(ByteBuf buffer) {
        return this.parent.compareTo(buffer);
    }

    @Override
    public String toString() {
        return this.parent.toString();
    }

    @Override
    public PacketByteBuf retain(int increment) {
        this.parent.retain(increment);
        return this;
    }

    @Override
    public PacketByteBuf retain() {
        this.parent.retain();
        return this;
    }

    @Override
    public PacketByteBuf touch() {
        this.parent.touch();
        return this;
    }

    @Override
    public PacketByteBuf touch(Object hint) {
        this.parent.touch(hint);
        return this;
    }

    @Override
    public boolean release() {
        return this.parent.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.parent.release(decrement);
    }

    @Override
    public int refCnt() {
        return this.parent.refCnt();
    }

    @Override
    public ByteBuf asByteBuf() {
        return this.parent;
    }


}
