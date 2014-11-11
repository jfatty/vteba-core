package com.vteba.socket.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

import com.vteba.utils.annotation.ThreadSafe;
import com.vteba.utils.charstr.ByteUtils;
import com.vteba.utils.charstr.Char;
import com.vteba.utils.zip.ZipUtils;

/**
 * 基于头长度的netty decoder。netty原生的实现长度有限制，且不能喝解压一起使用。
 * 如果不解压，那么返回UTF-8编码的字符串。
 * @author yinlei
 * @date 2014-2-1
 */
@ThreadSafe
public class LengthBasedFrameDecoder extends ByteToMessageDecoder {
	private final int headerLength;
	private final boolean headerHex;
	private final boolean uncompress;
	
	//private ThreadLocal<Integer> frameLengthLocal = new ThreadLocal<Integer>();
	private Integer frameLength = null;
	
	/**
	 * 构造一个实例。
	 * @param headerLength 头长度（只包含内容的长度）
	 * @param uncompress 是否解压数据
	 */
	public LengthBasedFrameDecoder(int headerLength, boolean uncompress, boolean headerHex) {
		super();
		this.headerLength = headerLength;
		this.uncompress = uncompress;
		this.headerHex = headerHex;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		int readable = in.readableBytes();
		if (readable >= headerLength) {
			//Integer frameLength = frameLengthLocal.get();
			if (frameLength == null) {
				byte[] header = new byte[headerLength];
				in.readBytes(header);
				int length = 0;
				if (headerHex) {
					length = ByteUtils.hexByte2Int(header);
				} else {
					length = ByteUtils.toInt(header);
				}
				//frameLengthLocal.set(length);
				frameLength = length;
			} else if (frameLength <= readable) {// 如果可读的数据大于内容长度，说明粘包了，只取内容长度的字节
				byte[] dest = new byte[frameLength];
				in.readBytes(dest);
				String result = null;
				if (uncompress) {
					result = ZipUtils.unzlibs(dest);
				} else {
					result = new String(dest, Char.UTF8);
				}
				out.add(result);
				//frameLengthLocal.remove();
				frameLength = null;
			}
		}
	}

}
