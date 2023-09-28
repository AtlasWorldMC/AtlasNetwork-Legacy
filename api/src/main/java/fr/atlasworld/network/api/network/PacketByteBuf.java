package fr.atlasworld.network.api.network;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.function.Consumer;

/**
 * PacketByteBuf is a holder for your packet data; Based of Netty's ByteBuf with added functionalities
 */
public interface PacketByteBuf extends Comparable<PacketByteBuf> {

    /**
     * Returns the number of bytes (octets) this buffer can contain.
     */
    int capacity();

    /**
     * Adjusts the capacity of this buffer.  If the {@code newCapacity} is less than the current
     * capacity, the content of this buffer is truncated.  If the {@code newCapacity} is greater
     * than the current capacity, the buffer is appended with unspecified data whose length is
     * {@code (newCapacity - currentCapacity)}.
     *
     * @throws IllegalArgumentException if the {@code newCapacity} is greater than {@link #maxCapacity()}
     */
    PacketByteBuf capacity(int newCapacity);

    /**
     * Returns the maximum allowed capacity of this buffer. This value provides an upper
     * bound on {@link #capacity()}.
     */
    int maxCapacity();

    /**
     * Returns {@code true} if and only if this buffer is read-only.
     */
    boolean isReadOnly();

    /**
     * Returns a read-only version of this buffer.
     */
    PacketByteBuf asReadOnly();

    /**
     * Returns the {@code readerIndex} of this buffer.
     */
    int readerIndex();

    /**
     * Sets the {@code readerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code readerIndex} is
     *            less than {@code 0} or
     *            greater than {@code this.writerIndex}
     */
    PacketByteBuf readerIndex(int readerIndex);

    /**
     * Returns the {@code writerIndex} of this buffer.
     */
    int writerIndex();

    /**
     * Sets the {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code writerIndex} is
     *            less than {@code this.readerIndex} or
     *            greater than {@code this.capacity}
     */
    PacketByteBuf writerIndex(int writerIndex);

    /**
     * Returns the number of readable bytes which is equal to
     * {@code (this.writerIndex - this.readerIndex)}.
     */
    int readableBytes();

    /**
     * Returns the number of writable bytes which is equal to
     * {@code (this.capacity - this.writerIndex)}.
     */
    int writableBytes();

    /**
     * Returns the maximum possible number of writable bytes, which is equal to
     * {@code (this.maxCapacity - this.writerIndex)}.
     */
    int maxWritableBytes();

    /**
     * Returns {@code true}
     * if and only if {@code (this.writerIndex - this.readerIndex)} is greater
     * than {@code 0}.
     */
    boolean isReadable();

    /**
     * Returns {@code true} if and only if this buffer contains equal to or more than the specified number of elements.
     */
    boolean isReadable(int size);

    /**
     * Returns {@code true}
     * if and only if {@code (this.capacity - this.writerIndex)} is greater
     * than {@code 0}.
     */
    boolean isWritable();

    /**
     * Returns {@code true} if and only if this buffer has enough room to allow writing the specified number of
     * elements.
     */
    boolean isWritable(int size);

    /**
     * Sets the {@code readerIndex} and {@code writerIndex} of this buffer to
     * {@code 0}.
     */
    PacketByteBuf clear();

    /**
     * Discards the bytes between the 0th index and {@code readerIndex}.
     * It moves the bytes between {@code readerIndex} and {@code writerIndex}
     * to the 0th index, and sets {@code readerIndex} and {@code writerIndex}
     * to {@code 0}.
     * <p>
     * Please remember that discarding bytes may add cpu load, this will most likely cause memory copying
     */
    PacketByteBuf discardReadBytes();

    /**
     * Expands the buffer {@link #capacity()} to make sure the number of
     * {@linkplain #writableBytes() writable bytes} is equal to or greater than the
     * specified value.  If there are enough writable bytes in this buffer, this method
     * returns with no side effect.
     *
     * @param minWritableBytes
     *        the expected minimum number of writable bytes
     * @throws IndexOutOfBoundsException
     *         if {@link #writerIndex()} + {@code minWritableBytes} &gt; {@link #maxCapacity()}.
     * @see #capacity(int)
     */
    PacketByteBuf ensureWritable(int minWritableBytes);

    /**
     * Expands the buffer {@link #capacity()} to make sure the number of
     * {@linkplain #writableBytes() writable bytes} is equal to or greater than the
     * specified value. Unlike {@link #ensureWritable(int)}, this method returns a status code.
     *
     * @param minWritableBytes
     *        the expected minimum number of writable bytes
     * @param force
     *        When {@link #writerIndex()} + {@code minWritableBytes} &gt; {@link #maxCapacity()}:
     *        <ul>
     *        <li>{@code true} - the capacity of the buffer is expanded to {@link #maxCapacity()}</li>
     *        <li>{@code false} - the capacity of the buffer is unchanged</li>
     *        </ul>
     * @return {@code 0} if the buffer has enough writable bytes, and its capacity is unchanged.
     *         {@code 1} if the buffer does not have enough bytes, and its capacity is unchanged.
     *         {@code 2} if the buffer has enough writable bytes, and its capacity has been increased.
     *         {@code 3} if the buffer does not have enough bytes, but its capacity has been
     *                   increased to its maximum.
     */
    int ensureWritable(int minWritableBytes, boolean force);


    /**
     * Gets a boolean at the specified absolute (@code index) in this buffer.
     * This method does not modify the {@code readerIndex} or {@code writerIndex}
     * of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    boolean getBoolean(int index);

    /**
     * Gets a byte at the specified absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    byte getByte(int index);

    /**
     * Gets an unsigned byte at the specified absolute {@code index} in this
     * buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    short getUnsignedByte(int index);

    /**
     * Gets a 16-bit short integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    short getShort(int index);

    /**
     * Gets an unsigned 16-bit short integer at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    int getUnsignedShort(int index);

    /**
     * Gets a 24-bit medium integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    int getMedium(int index);

    /**
     * Gets an unsigned 24-bit medium integer at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    int getUnsignedMedium(int index);

    /**
     * Gets a 32-bit integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    int getInt(int index);

    /**
     * Gets an unsigned 32-bit integer at the specified absolute {@code index}
     * in this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    long getUnsignedInt(int index);

    /**
     * Gets a 64-bit long integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    long getLong(int index);

    /**
     * Gets a 2-byte UTF-16 character at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    char getChar(int index);

    /**
     * Gets a 32-bit floating point number at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    float getFloat(int index);

    /**
     * Gets a 64-bit floating point number at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    double getDouble(int index);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + dst.length} is greater than
     *            {@code this.capacity}
     */
    PacketByteBuf getBytes(int index, byte[] dst);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex}
     * of this buffer.
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code dstIndex + length} is greater than
     *            {@code dst.length}
     */
    PacketByteBuf getBytes(int index, byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index} until the destination's position
     * reaches its limit.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer while the destination's {@code position} will be increased.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + dst.remaining()} is greater than
     *            {@code this.capacity}
     */
    PacketByteBuf getBytes(int index, ByteBuffer dst);

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than
     *            {@code this.capacity}
     * @throws IOException
     *         if the specified stream threw an exception during I/O
     */
    PacketByteBuf getBytes(int index, OutputStream out, int length) throws IOException;

    /**
     * Gets a {@link CharSequence} with the given length at the given index.
     *
     * @param length the length to read
     * @param charset that should be used
     * @return the sequence
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    CharSequence getCharSequence(int index, int length, Charset charset);

    /**
     * Sets the specified boolean at the specified absolute {@code index} in this
     * buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    PacketByteBuf setBoolean(int index, boolean value);

    /**
     * Sets the specified byte at the specified absolute {@code index} in this
     * buffer.  The 24 high-order bits of the specified value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 1} is greater than {@code this.capacity}
     */
    PacketByteBuf setByte(int index, int value);

    /**
     * Sets the specified 16-bit short integer at the specified absolute
     * {@code index} in this buffer.  The 16 high-order bits of the specified
     * value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    PacketByteBuf setShort(int index, int value);

    /**
     * Sets the specified 24-bit medium integer at the specified absolute
     * {@code index} in this buffer.  Please note that the most significant
     * byte is ignored in the specified value.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 3} is greater than {@code this.capacity}
     */
    PacketByteBuf setMedium(int index, int value);

    /**
     * Sets the specified 32-bit integer at the specified absolute
     * {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    PacketByteBuf setInt(int index, int value);

    /**
     * Sets the specified 64-bit long integer at the specified absolute
     * {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    PacketByteBuf setLong(int index, long value);

    /**
     * Sets the specified 2-byte UTF-16 character at the specified absolute
     * {@code index} in this buffer.
     * The 16 high-order bits of the specified value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 2} is greater than {@code this.capacity}
     */
    PacketByteBuf setChar(int index, int value);

    /**
     * Sets the specified 32-bit floating-point number at the specified
     * absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 4} is greater than {@code this.capacity}
     */
    PacketByteBuf setFloat(int index, float value);

    /**
     * Sets the specified 64-bit floating-point number at the specified
     * absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         {@code index + 8} is greater than {@code this.capacity}
     */
    PacketByteBuf setDouble(int index, double value);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + src.length} is greater than
     *            {@code this.capacity}
     */
    PacketByteBuf setBytes(int index, byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0},
     *         if the specified {@code srcIndex} is less than {@code 0},
     *         if {@code index + length} is greater than
     *            {@code this.capacity}, or
     *         if {@code srcIndex + length} is greater than {@code src.length}
     */
    PacketByteBuf setBytes(int index, byte[] src, int srcIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the specified absolute {@code index} until the source buffer's position
     * reaches its limit.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + src.remaining()} is greater than
     *            {@code this.capacity}
     */
    PacketByteBuf setBytes(int index, ByteBuffer src);

    /**
     * Transfers the content of the specified source stream to this buffer
     * starting at the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel.
     *         {@code -1} if the specified {@link InputStream} reached EOF.
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     * @throws IOException
     *         if the specified stream threw an exception during I/O
     */
    int setBytes(int index, InputStream in, int length) throws IOException;

    /**
     * Fills this buffer with <tt>NUL (0x00)</tt> starting at the specified
     * absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @param length the number of <tt>NUL</tt>s to write to the buffer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code index} is less than {@code 0} or
     *         if {@code index + length} is greater than {@code this.capacity}
     */
    PacketByteBuf setZero(int index, int length);

    /**
     * Writes the specified {@link CharSequence} at the given {@code index}.
     * The {@code writerIndex} is not modified by this method.
     *
     * @param index on which the sequence should be written
     * @param sequence to write
     * @param charset that should be used.
     * @return the written number of bytes.
     * @throws IndexOutOfBoundsException
     *         if the sequence at the given index would be out of bounds of the buffer capacity
     */
    int setCharSequence(int index, CharSequence sequence, Charset charset);

    /**
     * Gets a boolean at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 1}
     */
    boolean readBoolean();

    /**
     * Gets a byte at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 1}
     */
    byte readByte();

    /**
     * Gets an unsigned byte at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 1}
     */
    short readUnsignedByte();

    /**
     * Gets a 16-bit short integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    short readShort();

    /**
     * Gets an unsigned 16-bit short integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    int readUnsignedShort();

    /**
     * Gets a 24-bit medium integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 3} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 3}
     */
    int readMedium();

    /**
     * Gets an unsigned 24-bit medium integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 3} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 3}
     */
    int readUnsignedMedium();

    /**
     * Gets a 32-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    int readInt();

    /**
     * Gets an unsigned 32-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    long readUnsignedInt();

    /**
     * Gets a 64-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 8}
     */
    long readLong();

    /**
     * Gets a 2-byte UTF-16 character at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 2}
     */
    char readChar();

    /**
     * Gets a 32-bit floating point number at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 4}
     */
    float readFloat();

    /**
     * Gets a 64-bit floating point number at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code this.readableBytes} is less than {@code 8}
     */
    double readDouble();

    /**
     * Transfers this buffer's data to a newly created buffer starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code length}).
     * The returned buffer's {@code readerIndex} and {@code writerIndex} are
     * {@code 0} and {@code length} respectively.
     *
     * @param length the number of bytes to transfer
     *
     * @return the newly created buffer which contains the transferred bytes
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    PacketByteBuf readBytes(int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code dst.length}).
     *
     * @throws IndexOutOfBoundsException
     *         if {@code dst.length} is greater than {@code this.readableBytes}
     */
    PacketByteBuf readBytes(byte[] dst);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} and increases the {@code readerIndex}
     * by the number of the transferred bytes (= {@code length}).
     *
     * @param dstIndex the first index of the destination
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code dstIndex} is less than {@code 0},
     *         if {@code length} is greater than {@code this.readableBytes}, or
     *         if {@code dstIndex + length} is greater than {@code dst.length}
     */
    PacketByteBuf readBytes(byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the current {@code readerIndex} until the destination's position
     * reaches its limit, and increases the {@code readerIndex} by the
     * number of the transferred bytes.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code dst.remaining()} is greater than
     *            {@code this.readableBytes}
     */
    PacketByteBuf readBytes(ByteBuffer dst);

    /**
     * Transfers this buffer's data to the specified stream starting at the
     * current {@code readerIndex}.
     *
     * @param length the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     * @throws IOException
     *         if the specified stream threw an exception during I/O
     */
    PacketByteBuf readBytes(OutputStream out, int length) throws IOException;

    /**
     * Gets a {@link CharSequence} with the given length at the current {@code readerIndex}
     * and increases the {@code readerIndex} by the given length.
     *
     * @param length the length to read
     * @param charset that should be used
     * @return the sequence
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    CharSequence readCharSequence(int length, Charset charset);

    /**
     * Increases the current {@code readerIndex} by the specified
     * {@code length} in this buffer.
     *
     * @throws IndexOutOfBoundsException
     *         if {@code length} is greater than {@code this.readableBytes}
     */
    PacketByteBuf skipBytes(int length);

    /**
     * Sets the specified boolean at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 1} in this buffer.
     * If {@code this.writableBytes} is less than {@code 1}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeBoolean(boolean value);

    /**
     * Sets the specified byte at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 1} in this buffer.
     * The 24 high-order bits of the specified value are ignored.
     * If {@code this.writableBytes} is less than {@code 1}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeByte(int value);

    /**
     * Sets the specified 16-bit short integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
     * in this buffer.  The 16 high-order bits of the specified value are ignored.
     * If {@code this.writableBytes} is less than {@code 2}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeShort(int value);

    /**
     * Sets the specified 24-bit medium integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 3}
     * in this buffer.
     * If {@code this.writableBytes} is less than {@code 3}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeMedium(int value);

    /**
     * Sets the specified 32-bit integer at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 4} in this buffer.
     * If {@code this.writableBytes} is less than {@code 4}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeInt(int value);

    /**
     * Sets the specified 64-bit long integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
     * in this buffer.
     * If {@code this.writableBytes} is less than {@code 8}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeLong(long value);

    /**
     * Sets the specified 2-byte UTF-16 character at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
     * in this buffer.  The 16 high-order bits of the specified value are ignored.
     * If {@code this.writableBytes} is less than {@code 2}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeChar(int value);

    /**
     * Sets the specified 32-bit floating point number at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 4}
     * in this buffer.
     * If {@code this.writableBytes} is less than {@code 4}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeFloat(float value);

    /**
     * Sets the specified 64-bit floating point number at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
     * in this buffer.
     * If {@code this.writableBytes} is less than {@code 8}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeDouble(double value);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code src.length}).
     * If {@code this.writableBytes} is less than {@code src.length}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     */
    PacketByteBuf writeBytes(byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code length}).
     * If {@code this.writableBytes} is less than {@code length}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     *
     * @throws IndexOutOfBoundsException
     *         if the specified {@code srcIndex} is less than {@code 0}, or
     *         if {@code srcIndex + length} is greater than {@code src.length}
     */
    PacketByteBuf writeBytes(byte[] src, int srcIndex, int length);

    /**
     * Transfers the specified source buffer's data to this buffer starting at
     * the current {@code writerIndex} until the source buffer's position
     * reaches its limit, and increases the {@code writerIndex} by the
     * number of the transferred bytes.
     * If {@code this.writableBytes} is less than {@code src.remaining()},
     * {@link #ensureWritable(int)} will be called in an attempt to expand
     * capacity to accommodate.
     */
    PacketByteBuf writeBytes(ByteBuffer src);

    /**
     * Transfers the content of the specified stream to this buffer
     * starting at the current {@code writerIndex} and increases the
     * {@code writerIndex} by the number of the transferred bytes.
     * If {@code this.writableBytes} is less than {@code length}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     *
     * @param length the number of bytes to transfer
     *
     * @return the actual number of bytes read in from the specified channel.
     *         {@code -1} if the specified {@link InputStream} reached EOF.
     *
     * @throws IOException if the specified stream threw an exception during I/O
     */
    int writeBytes(InputStream in, int length) throws IOException;

    /**
     * Fills this buffer with <tt>NUL (0x00)</tt> starting at the current
     * {@code writerIndex} and increases the {@code writerIndex} by the
     * specified {@code length}.
     * If {@code this.writableBytes} is less than {@code length}, {@link #ensureWritable(int)}
     * will be called in an attempt to expand capacity to accommodate.
     *
     * @param length the number of <tt>NUL</tt>s to write to the buffer
     */
    PacketByteBuf writeZero(int length);

    /**
     * Writes the specified {@link CharSequence} at the current {@code writerIndex} and increases
     * the {@code writerIndex} by the written bytes.
     * in this buffer.
     * If {@code this.writableBytes} is not large enough to write the whole sequence,
     * {@link #ensureWritable(int)} will be called in an attempt to expand capacity to accommodate.
     *
     * @param sequence to write
     * @param charset that should be used
     * @return the written number of bytes
     */
    int writeCharSequence(CharSequence sequence, Charset charset);

    /**
     * Iterates over the readable bytes of this buffer in ascending order.
     */
    PacketByteBuf forEachByte(Consumer<Byte> process);

    /**
     * Iterates over the readable bytes of this buffer in descending order.
     */
    PacketByteBuf forEachByteDesc(Consumer<Byte> process);

    /**
     * Returns a copy of this buffer's readable bytes.  Modifying the content
     * of the returned buffer or this buffer does not affect each other at all.
     * This method is identical to {@code buf.copy(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    PacketByteBuf copy();

    /**
     * Returns a copy of this buffer's sub-region.  Modifying the content of
     * the returned buffer or this buffer does not affect each other at all.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    PacketByteBuf copy(int index, int length);

    /**
     * Returns a buffer which shares the whole region of this buffer.
     * Modifying the content of the returned buffer or this buffer affects
     * each other's content while they maintain separate indexes and marks.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * The reader and writer marks will not be duplicated. The reference count will NOT be increased.
     * @return A buffer whose readable content is equivalent to the buffer returned by {@link #slice()}.
     * However, this buffer will share the capacity of the underlying buffer, and therefore allows access to all of the
     * underlying content if necessary.
     */
    PacketByteBuf duplicate();

    /**
     * Returns {@code true} if and only if this buffer has a backing byte array.
     * If this method returns true, you can safely call {@link #array()} and
     * {@link #arrayOffset()}.
     */
    boolean hasArray();

    /**
     * Returns the backing byte array of this buffer.
     *
     * @throws UnsupportedOperationException
     *         if there no accessible backing byte array
     */
    byte[] array();

    /**
     * Returns the offset of the first byte within the backing byte array of
     * this buffer.
     *
     * @throws UnsupportedOperationException
     *         if there no accessible backing byte array
     */
    int arrayOffset();

    /**
     * Decodes this buffer's readable bytes into a string with the UTF-8
     * character set name.  This method is identical to
     * {@code buf.toString(buf.readerIndex(), buf.readableBytes(), charsetName)}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    @Override
    String toString();

    /**
     * Decodes this buffer's readable bytes into a string with the specified
     * character set name.  This method is identical to
     * {@code buf.toString(buf.readerIndex(), buf.readableBytes(), charsetName)}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws UnsupportedCharsetException
     *         if the specified character set name is not supported by the
     *         current VM
     */
    String toString(Charset charset);

    /**
     * Decodes this buffer's sub-region into a string with the specified
     * character set.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     */
    String toString(int index, int length, Charset charset);

    /**
     * Returns a hash code which was calculated from the content of this
     * buffer.  If there's a byte array which is
     * equal to this array, both arrays should
     * return the same value.
     */
    @Override
    int hashCode();

    /**
     * Determines if the content of the specified buffer is identical to the
     * content of this array.  'Identical' here means:
     * <ul>
     * <li>the size of the contents of the two buffers are same and</li>
     * <li>every single byte of the content of the two buffers are same.</li>
     * </ul>
     * Please note that it does not compare {@link #readerIndex()} nor
     * {@link #writerIndex()}.  This method also returns {@code false} for
     * {@code null} and an object which is not an instance of
     * {@link PacketByteBuf} type.
     */
    @Override
    boolean equals(Object obj);

    /**
     * Compares the content of the specified buffer to the content of this
     * buffer. Comparison is performed in the same manner with the string
     * comparison functions of various languages such as {@code strcmp},
     * {@code memcmp} and {@link String#compareTo(String)}.
     */
    @Override
    int compareTo(@NotNull PacketByteBuf buf);

    /**
     * Checks if the ByteBuf has been released or not.
     * @return false if the ByteBuf has been released.
     */
    boolean isAccessible();

    /**
     * Returns the reference count of this object.  If {@code 0}, it means this object has been deallocated.
     */
    int refCnt();

    /**
     * Decreases the reference count by {@code 1} and deallocates this object if the reference count reaches at
     * {@code 0}.
     *
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated
     */
    boolean release();

    /**
     * Decreases the reference count by the specified {@code decrement} and deallocates this object if the reference
     * count reaches at {@code 0}.
     *
     * @return {@code true} if and only if the reference count became {@code 0} and this object has been deallocated
     */
    boolean release(int decrement);

    /**
     * Increases the reference count by {@code 1}.
     */
    PacketByteBuf addRefCount();
}
