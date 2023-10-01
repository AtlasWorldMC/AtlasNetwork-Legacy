package fr.atlasworld.network.networking.packet;

import fr.atlasworld.network.api.networking.packet.PacketByteBuf;
import fr.atlasworld.network.networking.utilities.ForEachByteProcessor;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufConvertible;
import io.netty.util.ReferenceCounted;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class PacketByteBufImpl implements ByteBufConvertible, PacketByteBuf, ReferenceCounted {
    private final ByteBuf parent;

    public PacketByteBufImpl(ByteBuf parent) {
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
    public boolean isReadOnly() {
        return this.parent.isReadOnly();
    }

    @Override
    public PacketByteBuf asReadOnly() {
        return new PacketByteBufImpl(this.parent.asReadOnly());
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
        return this.parent.isReadable(size);
    }

    @Override
    public PacketByteBuf clear() {
        this.parent.clear();
        return this;
    }

    @Override
    public PacketByteBuf discardReadBytes() {
        this.parent.discardReadBytes();
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
    public int getUnsignedShort(int index) {
        return this.parent.getUnsignedShort(index);
    }

    @Override
    public int getMedium(int index) {
        return this.parent.getMedium(index);
    }

    @Override
    public int getUnsignedMedium(int index) {
        return this.parent.getUnsignedMedium(index);
    }

    @Override
    public int getInt(int index) {
        return this.parent.getInt(index);
    }

    @Override
    public long getUnsignedInt(int index) {
        return this.parent.getUnsignedInt(index);
    }

    @Override
    public long getLong(int index) {
        return this.parent.getLong(index);
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
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.parent.getCharSequence(index, length, charset);
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
    public PacketByteBuf setMedium(int index, int value) {
        this.parent.setMedium(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setInt(int index, int value) {
        this.parent.setInt(index, value);
        return this;
    }

    @Override
    public PacketByteBuf setLong(int index, long value) {
        this.parent.setLong(index, value);
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
    public PacketByteBuf setZero(int index, int length) {
        this.parent.setZero(index, length);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.parent.setCharSequence(index, sequence, charset);
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
    public int readUnsignedShort() {
        return this.parent.readUnsignedShort();
    }

    @Override
    public int readMedium() {
        return this.parent.readMedium();
    }

    @Override
    public int readUnsignedMedium() {
        return this.parent.readUnsignedMedium();
    }

    @Override
    public int readInt() {
        return this.parent.readInt();
    }

    @Override
    public long readUnsignedInt() {
        return this.parent.readUnsignedInt();
    }

    @Override
    public long readLong() {
        return this.parent.readLong();
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
        return new PacketByteBufImpl(this.parent.readBytes(length));
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
    public CharSequence readCharSequence(int length, Charset charset) {
        return this.readCharSequence(length, charset);
    }

    @Override
    public String readString() {
        return this.readString(StandardCharsets.UTF_8);
    }

    @Override
    public String readString(Charset charset) {
        return (String) this.readCharSequence(this.readInt(), charset);
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
    public PacketByteBuf writeMedium(int value) {
        this.parent.writeMedium(value);
        return this;
    }

    @Override
    public PacketByteBuf writeInt(int value) {
        this.parent.writeInt(value);
        return this;
    }

    @Override
    public PacketByteBuf writeLong(long value) {
        this.parent.writeLong(value);
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
    public PacketByteBuf writeZero(int length) {
        this.parent.writeZero(length);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.parent.writeCharSequence(sequence, charset);
    }

    @Override
    public PacketByteBuf writeString(String value) {
         return this.writeString(value, StandardCharsets.UTF_8);
    }

    @Override
    public PacketByteBuf writeString(String value, Charset charset) {
        this.writeInt(value.length());
        this.writeCharSequence(value, charset);
        return this;
    }

    @Override
    public PacketByteBuf forEachByte(Consumer<Byte> process) {
        this.parent.forEachByte(new ForEachByteProcessor(pByte -> {process.accept(pByte); return false;}));
        return this;
    }

    @Override
    public PacketByteBuf forEachByteDesc(Consumer<Byte> process) {
        this.parent.forEachByteDesc(new ForEachByteProcessor(pByte -> {process.accept(pByte); return false;}));
        return this;
    }

    @Override
    public PacketByteBuf copy() {
        return new PacketByteBufImpl(this.parent.copy());
    }

    @Override
    public PacketByteBuf copy(int index, int length) {
        return new PacketByteBufImpl(this.parent.copy(index, length));
    }

    @Override
    public PacketByteBuf duplicate() {
        return new PacketByteBufImpl(this.parent.duplicate());
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
    public String toString(Charset charset) {
        return this.parent.toString(charset);
    }

    @Override
    public String toString(int index, int length, Charset charset) {
        return this.parent.toString(index, length, charset);
    }

    @Override
    public int compareTo(@NotNull PacketByteBuf buf) {
        ByteBuf bufParent = ((PacketByteBufImpl) buf).parent;
        return this.parent.compareTo(bufParent);
    }

    @Override
    public boolean isAccessible() {
        return this.parent.refCnt() != 0;
    }

    @Override
    public int refCnt() {
        return this.parent.refCnt();
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
    public ReferenceCounted retain() {
        return this.parent.retain();
    }

    @Override
    public ReferenceCounted retain(int increment) {
        return this.parent.retain(increment);
    }

    @Override
    public ReferenceCounted touch() {
        return this.parent.touch();
    }

    @Override
    public ReferenceCounted touch(Object hint) {
        return this.parent.touch();
    }

    @Override
    public ByteBuf asByteBuf() {
        return this.parent;
    }

    @Override
    public PacketByteBuf addRefCount() {
        this.retain();
        return this;
    }
}
