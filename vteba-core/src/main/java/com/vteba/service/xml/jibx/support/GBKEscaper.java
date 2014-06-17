package com.vteba.service.xml.jibx.support;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.jibx.runtime.ICharacterEscaper;

/**
 * JiBX GBK转码器
 * @author yinlei
 * @date 2013年9月21日 下午10:00:35
 */
public class GBKEscaper implements ICharacterEscaper {
	private static GBKEscaper instance = new GBKEscaper();
	
	private GBKEscaper() {
		
	}
	
	public static GBKEscaper getInstance() {
		return instance;
	}
	
	
	@Override
	public void writeAttribute(String text, Writer writer) throws IOException {
		int mark = 0;
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (chr == '"') {
                writer.write(text, mark, i-mark);
                mark = i+1;
                writer.write("&quot;");
            } else if (chr == '&') {
                writer.write(text, mark, i-mark);
                mark = i+1;
                writer.write("&amp;");
            } else if (chr == '<') {
                writer.write(text, mark, i-mark);
                mark = i+1;
                writer.write("&lt;");
            } else if (chr == '>' && i > 2 && text.charAt(i-1) == ']' &&
                text.charAt(i-2) == ']') {
                writer.write(text, mark, i-mark-2);
                mark = i+1;
                writer.write("]]&gt;");
            } else if (chr < 0x20) {
                if (chr != 0x9 && chr != 0xA && chr != 0xD) {
                    throw new IOException("Illegal character code 0x" +
                        Integer.toHexString(chr) + " in attribute value text");
                }
            } else if (chr >= 0x80) {
                if (chr < 0xA0 || chr > 0xFF) {
                	if (!(chr >= 0x4e00 && chr <= 0x9fbb)) {
                		writer.write(text, mark, i-mark);
                		mark = i+1;
                		if (chr > 0xD7FF && (chr < 0xE000 || chr == 0xFFFE ||
                				chr == 0xFFF || chr > 0x10FFFF)) {
                			throw new IOException("Illegal character code 0x" +
                					Integer.toHexString(chr) +
                					" in attribute value text");
                		}
                		writer.write("&#x");
                		writer.write(Integer.toHexString(chr));
                		writer.write(';');
                	}
                }
            }
        }
        writer.write(text, mark, text.length()-mark);
	}

	@Override
	public void writeContent(String text, Writer writer) throws IOException {
		int mark = 0;
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (chr == '&') {
                writer.write(text, mark, i-mark);
                mark = i+1;
                writer.write("&amp;");
            } else if (chr == '<') {
                writer.write(text, mark, i-mark);
                mark = i+1;
                writer.write("&lt;");
            } else if (chr == '>' && i > 2 && text.charAt(i-1) == ']' &&
                text.charAt(i-2) == ']') {
                writer.write(text, mark, i-mark-2);
                mark = i+1;
                writer.write("]]&gt;");
            } else if (chr < 0x20) {
                if (chr != 0x9 && chr != 0xA && chr != 0xD) {
                    throw new IOException("Illegal character code 0x" +
                        Integer.toHexString(chr) + " in content text");
                }
            } else if (chr >= 0x80) {
                if (chr < 0xA0 || chr > 0xFF) {
                    if (!(chr >= 0x4e00 && chr <= 0x9fbb)) {
                    	writer.write(text, mark, i-mark);
                    	mark = i+1;
                    	if (chr > 0xD7FF && (chr < 0xE000 || chr == 0xFFFE ||
                    			chr == 0xFFF || chr > 0x10FFFF)) {
                    		throw new IOException("Illegal character code 0x" +
                    				Integer.toHexString(chr) + " in content text");
                    	}
                    	writer.write("&#x");
                    	writer.write(Integer.toHexString(chr));
                    	writer.write(';');
                    }
                }
            }
        }
        writer.write(text, mark, text.length()-mark);

	}

	@Override
	public void writeCData(String text, Writer writer) throws IOException {
		writer.write("<![CDATA[");
        for (int i = 0; i < text.length(); i++) {
            char chr = text.charAt(i);
            if (chr == '>' && i > 2 && text.charAt(i-1) == ']' &&
                text.charAt(i-2) == ']') {
                throw new IOException("Sequence \"]]>\" is not allowed " +
                    "within CDATA section text");
            } else if (chr < 0x20) {
                if (chr != 0x9 && chr != 0xA && chr != 0xD) {
                    throw new IOException("Illegal character code 0x" +
                        Integer.toHexString(chr) + " in CDATA section");
                }
            } else if (chr >= 0x80 && (chr < 0xA0 || chr > 0xFF)) {
            	if (chr < 0xA0 || chr > 0xFF) {
                    if (!(chr >= 0x4e00 && chr <= 0x9fbb)) {
                    	throw new IOException("Character code 0x" +
                    			Integer.toHexString(chr) +
                    			" not supported by encoding in CDATA section");
                    }
                }
            }
        }
        writer.write(text);
        writer.write("]]>");

	}
	
	/**
	 *  8140-FEFE�����ֽ��� 81-FE ֮�䣬β�ֽ��� 40-FE ֮��
	 *  GBK 0x81-0xFE��129-254��|0x40-0xFE��64-254��
	 *  if ((chr >= 0x4e00) && (chr <= 0x9fbb)) {//����}
	 *  ���ֵ�Unicode���뷶ΧΪ\u4E00-\u9FA5 \uF900-\uFA2D,����������Χ�ھͲ��Ǻ�����
	 * @param args
	 */
	public static void main(String[] args) {
		ICharacterEscaper gbk = getInstance();
		
		String text = "��w���և�������<�����������T";
		StringWriter writer = new StringWriter();
		try {
			gbk.writeContent(new String(text.getBytes(), "GBK"), writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(writer.toString());
		
		StringWriter cWriter = new StringWriter();
		try {
			gbk.writeCData(text, cWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(cWriter.toString());
		
		StringWriter aWriter = new StringWriter();
		try {
			gbk.writeAttribute(text, aWriter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(aWriter.toString());

	}
	
}
