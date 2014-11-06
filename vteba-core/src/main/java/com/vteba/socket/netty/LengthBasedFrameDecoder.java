package com.vteba.socket.netty;

import java.util.List;

import com.vteba.utils.annotation.ThreadSafe;
import com.vteba.utils.charstr.ByteUtils;
import com.vteba.utils.charstr.Char;
import com.vteba.utils.zip.ZipUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 基于头长度的netty decoder。netty原生的实现太复杂，且不能喝解压一起使用。
 * @author yinlei
 * @date 2014-2-1
 */
@ThreadSafe
public class LengthBasedFrameDecoder extends ByteToMessageDecoder {
	private final int headerLength;
	private final boolean uncompress;
	private ThreadLocal<Integer> frameLengthLocal = new ThreadLocal<Integer>();
	
	/**
	 * 构造一个实例。
	 * @param headerLength 头长度（只包含内容的长度）
	 * @param uncompress 是否解压数据
	 */
	public LengthBasedFrameDecoder(int headerLength, boolean uncompress) {
		super();
		this.headerLength = headerLength;
		this.uncompress = uncompress;
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() >= headerLength) {
			Integer contentLength = frameLengthLocal.get();
			if (contentLength == null) {
				byte[] header = new byte[headerLength];
				in.readBytes(header);
				int length = ByteUtils.toInt(header);
				frameLengthLocal.set(length);
			} else if (contentLength == in.readableBytes()) {
				byte[] dest = new byte[contentLength];
				in.readBytes(dest);
				String result = null;
				if (uncompress) {
					result = ZipUtils.unzlibs(dest);
				} else {
					result = new String(dest, Char.UTF8);
				}
				out.add(result);
				frameLengthLocal.remove();
			}
		}
	}

}
