package com.diozero.util;

/*-
 * #%L
 * Organisation: diozero
 * Project:      Device I/O Zero - Core
 * Filename:     MmapIntBuffer.java  
 * 
 * This file is part of the diozero project. More information about this project
 * can be found at http://www.diozero.com/
 * %%
 * Copyright (C) 2016 - 2021 diozero
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class MmapIntBuffer implements AutoCloseable {
	private MmapByteBuffer mmapByteBuffer;
	private IntBuffer intBuffer;

	public MmapIntBuffer(String file, int offset, int pageSize, ByteOrder byteOrder) {
		mmapByteBuffer = MmapBufferNative.createMmapBuffer(file, offset, pageSize);
		intBuffer = mmapByteBuffer.getBuffer().order(byteOrder).asIntBuffer();
	}

	@Override
	public void close() {
		MmapBufferNative.closeMmapBuffer(mmapByteBuffer.getFd(), mmapByteBuffer.getAddress(),
				mmapByteBuffer.getLength());
		mmapByteBuffer = null;
		intBuffer = null;
	}

	@Deprecated()
	public IntBuffer getIntBuffer() {
		return intBuffer;
	}
	
	public int get(int index) {
		return intBuffer.get(index);
	}
	
	public int get(int index, int mask) {
		return intBuffer.get(index) & mask;
	}
	
	public int getShiftRight(int index, int shift, int mask) {
		return (intBuffer.get(index) >> shift) & mask;
	}
	
	public void put(int index, int i) {
		intBuffer.put(index, i);
	}
}
