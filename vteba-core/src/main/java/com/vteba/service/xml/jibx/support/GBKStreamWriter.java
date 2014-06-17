package com.vteba.service.xml.jibx.support;

import java.io.IOException;
import java.util.Arrays;

import org.jibx.runtime.IXMLWriter;
import org.jibx.runtime.impl.StreamWriterBase;

import com.vteba.utils.charstr.CharUtils;

/**
 * JiBX的GBK编码流StreamWriter
 * @author yinlei
 * @date 2013年9月21日下午10:08:11
 */
public class GBKStreamWriter extends StreamWriterBase {
	/** Conversion buffer for prefixes; */
    private byte[] m_converts;
	
	public GBKStreamWriter(StreamWriterBase base, String[] uris) {
		super(base, uris);
		try {
			defineNamespace(0, "");
			defineNamespace(1, "xml");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public GBKStreamWriter(String[] uris) {
		super("GBK", uris);
		try {
			defineNamespace(0, "");
			defineNamespace(1, "xml");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public IXMLWriter createChildWriter(String[] uris) throws IOException {
		flagContent();
		return new GBKStreamWriter(this, uris);
	}

	@Override
	public void writeTextContent(String text) throws IOException {
		flagTextContent();
        int length = text.length();
        makeSpace(length * 5);
        int fill = m_fillOffset;
        for (int i = 0; i < length; i++) {
            char chr = text.charAt(i);
            if (chr == '&') {
                fill = writeEntity(m_ampEntityBytes, fill);
            } else if (chr == '<') {
                fill = writeEntity(m_ltEntityBytes, fill);
            } else if (chr == '>' && i > 2 && text.charAt(i-1) == ']' &&
                text.charAt(i-2) == ']') {
                fill = writeEntity(m_gtEntityBytes, fill);
            } else if (chr < 0x20) {
                if (chr != 0x9 && chr != 0xA && chr != 0xD) {
                    throw new IOException("Illegal character code 0x" +
                        Integer.toHexString(chr) + " in content text");
                } else {
                    m_buffer[fill++] = (byte)chr;
                }
            } else {
                if (chr > 0xFF) {
                	if (chr >= 0x4e00 && chr <= 0x9fff) {//0x9fa5,0x9fbb
                		byte[] b = CharUtils.gbkBytes(chr);
                		m_buffer[fill++] = (byte)(b[0]);
                        m_buffer[fill++] = (byte)(b[1]);
                	} else {
						if (chr > 0xD7FF && (chr < 0xE000 || chr == 0xFFFE
										|| chr == 0xFFFF || chr > 0x10FFFF)) {
							throw new IOException("Illegal character code 0x"
									+ Integer.toHexString(chr)
									+ " in character data text");
						} else {
							m_fillOffset = fill;
							makeSpace(length - i + 8);
							fill = m_fillOffset;
							m_buffer[fill++] = (byte) '&';
							m_buffer[fill++] = (byte) '#';
							m_buffer[fill++] = (byte) 'x';
							for (int j = 12; j >= 0; j -= 4) {
								int nib = (chr >> j) & 0xF;
								if (nib < 10) {
									m_buffer[fill++] = (byte) ('0' + nib);
								} else {
									m_buffer[fill++] = (byte) ('A' + nib);
								}
							}
							m_buffer[fill++] = (byte) ';';
						}
                	}
                } else {
                    m_buffer[fill++] = (byte)chr;
                }
            }
        }
        m_fillOffset = fill;
	}

	@Override
	public void writeCData(String text) throws IOException {
		flagTextContent();
        int length = text.length();
        makeSpace(length + 12);
        int fill = m_fillOffset;
        fill = writeEntity(m_cdataStartBytes, fill);
        for (int i = 0; i < length; i++) {
            char chr = text.charAt(i);
            if (chr == '>' && i > 2 && text.charAt(i-1) == ']' &&
                text.charAt(i-2) == ']') {
                throw new IOException("Sequence \"]]>\" is not allowed " +
                    "within CDATA section text");
            } else if (chr < 0x20) {
                if (chr != 0x9 && chr != 0xA && chr != 0xD) {
                    throw new IOException("Illegal character code 0x" +
                        Integer.toHexString(chr) + " in content text");
                } else {
                    m_buffer[fill++] = (byte)chr;
                }
            } else {
                if (chr > 0xFF) {
                	if (chr >= 0x4e00 && chr <= 0x9fff) {//0x9fa5,0x9fbb
                		byte[] b = CharUtils.gbkBytes(chr);
                		m_buffer[fill++] = (byte)(b[0]);
                        m_buffer[fill++] = (byte)(b[1]);
                	} else {
                		throw new IOException("Character code 0x" +
                				Integer.toHexString(chr) +
                				" not allowed by GBK encoding in CDATA section text");
                	}
                } else {
                    m_buffer[fill++] = (byte)chr;
                }
            }
        }
        m_fillOffset = writeEntity(m_cdataEndBytes, fill);

	}

	@Override
	protected void writeMarkup(String text) throws IOException {
		int length = text.length();
        makeSpace(length * 2);
        for (int i = 0; i < length; i++) {
            char chr = text.charAt(i);
            if (chr > 0xFF) {
            	if (chr >= 0x4e00 && chr <= 0x9fff) {//0x9fa5,0x9fbb
            		byte[] b = CharUtils.gbkBytes(chr);
            		m_buffer[m_fillOffset++] = (byte)(b[0]);
                    m_buffer[m_fillOffset++] = (byte)(b[1]);
            	} else {
            		throw new IOException("Unable to write character code 0x" +
            				Integer.toHexString(chr) + " in encoding GBK");
            	}
            } else {
                m_buffer[m_fillOffset++] = (byte)chr;
            }
        }
	}

	@Override
	protected void writeMarkup(char chr) throws IOException {
		makeSpace(2);
		if (chr > 0xFF) {
        	if (chr >= 0x4e00 && chr <= 0x9fff) {//0x9fa5,0x9fbb
        		byte[] b = CharUtils.gbkBytes(chr);
        		m_buffer[m_fillOffset++] = (byte)(b[0]);
                m_buffer[m_fillOffset++] = (byte)(b[1]);
        	} else {
        		throw new IOException("Unable to write character code 0x" +
        				Integer.toHexString(chr) + " in encoding GBK");
        	}
        } else {
            m_buffer[m_fillOffset++] = (byte)chr;
        }

	}

	@Override
	protected void writeAttributeText(String text) throws IOException {
		int length = text.length();
        makeSpace(length * 6);
        int fill = m_fillOffset;
        for (int i = 0; i < length; i++) {
            char chr = text.charAt(i);
            if (chr == '"') {
                fill = writeEntity(m_quotEntityBytes, fill);
            } else if (chr == '&') {
                fill = writeEntity(m_ampEntityBytes, fill);
            } else if (chr == '<') {
                fill = writeEntity(m_ltEntityBytes, fill);
            } else if (chr == '>' && i > 2 && text.charAt(i-1) == ']' &&
                text.charAt(i-2) == ']') {
                m_buffer[fill++] = (byte)']';
                m_buffer[fill++] = (byte)']';
                fill = writeEntity(m_gtEntityBytes, fill);
            } else if (chr < 0x20) {
                if (chr != 0x9 && chr != 0xA && chr != 0xD) {
                    throw new IOException("Illegal character code 0x" +
                        Integer.toHexString(chr) + " in attribute value text by GBK encoding ");
                } else {
                    m_buffer[fill++] = (byte)chr;
                }
            } else {
                if (chr > 0xFF) {
                	if (chr >= 0x4e00 && chr <= 0x9fff) {//0x9fa5,0x9fbb
                		byte[] b = CharUtils.gbkBytes(chr);
                		m_buffer[fill++] = (byte)(b[0]);
                        m_buffer[fill++] = (byte)(b[1]);
                	} else {
						if (chr > 0xD7FF && (chr < 0xE000 || chr == 0xFFFE
										|| chr == 0xFFFF || chr > 0x10FFFF)) {
							throw new IOException("Illegal character code 0x"
									+ Integer.toHexString(chr)
									+ " in attribute value text by GBK encoding ");
						} else {
							m_fillOffset = fill;
							makeSpace(length - i + 8);
							fill = m_fillOffset;
							m_buffer[fill++] = (byte) '&';
							m_buffer[fill++] = (byte) '#';
							m_buffer[fill++] = (byte) 'x';
							for (int j = 12; j >= 0; j -= 4) {
								int nib = (chr >> j) & 0xF;
								if (nib < 10) {
									m_buffer[fill++] = (byte) ('0' + nib);
								} else {
									m_buffer[fill++] = (byte) ('A' + nib);
								}
							}
							m_buffer[fill++] = (byte) ';';
						}
                	}
                } else {
                    m_buffer[fill++] = (byte)chr;
                }
            }
        }
        m_fillOffset = fill;

	}

	@Override
	protected void defineNamespace(int index, String prefix) throws IOException {
		int limit = prefix.length() * 2;
        if (m_converts == null) {
            m_converts = new byte[limit];
        } else if (limit > m_converts.length) {
            m_converts = new byte[limit];
        }
        int fill = 0;
        for (int i = 0; i < prefix.length(); i++) {
            char chr = prefix.charAt(i);
            if (chr > 0xFF) {
            	if (chr >= 0x4e00 && chr <= 0x9fff) {//0x9fa5,0x9fbb
            		byte[] b = CharUtils.gbkBytes(chr);
            		m_buffer[fill++] = (byte)(b[0]);
                    m_buffer[fill++] = (byte)(b[1]);
            	} else {
            		throw new IOException("Illegal character code 0x"
							+ Integer.toHexString(chr)
							+ " in namespace value text by GBK encoding ");
            	}
            } else {
                m_converts[fill++] = (byte)chr;
            }
        }
        byte[] trim;
        if (fill > 0) {
            trim = new byte[fill+1];
            System.arraycopy(m_converts, 0, trim, 0, fill);
            trim[fill] = ':';
        } else {
            trim = new byte[0];
        }
        if (index < m_prefixBytes.length) {
            m_prefixBytes[index] = trim;
        } else if (m_extensionBytes != null) {
            index -= m_prefixBytes.length;
            for (int i = 0; i < m_extensionBytes.length; i++) {
                int length = m_extensionBytes[i].length;
                if (index < length) {
                    m_extensionBytes[i][index] = trim;
                } else {
                    index -= length;
                }
            }
        } else {
            throw new IllegalArgumentException("Index out of range");
        }

	}

	public static void main(String[] args) {
		String text = "����ѽ";
		byte[] bytes = text.getBytes();//[-80, -94, -54, -57]
		System.out.println(Arrays.toString(bytes));
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			System.out.println(Integer.toHexString(c));
			System.out.println(Integer.toBinaryString(c));//1001011000111111
			System.out.println((int)c);
			System.out.println((byte)(0x963f - 0x4e00));
			System.out.println();
//			
//			System.out.println(38463 % 128);
//			
//			System.out.println((byte)c);
//			//System.out.println((c & 0x3f));
//			System.out.println((byte)((c + 0xA0)) );
//			System.out.println((byte)(0x96 >> 1));
//			System.out.println((byte)((0xA0 + (c >> 6)) >> 2));
//			System.out.println((byte)(0xa0 + (c & 0x3F)));
//			
//			System.out.println(c >> 8);
			
			System.out.println((byte)(0xE0 + (c >> 12)));
			System.out.println((byte)(0x80 + ((c >> 6) & 0x3F)));
			System.out.println((byte)(0x80 + (c & 0x3F)));
			
//			System.out.println((byte)(0xA0 + (c )));
//			System.out.println((byte)(0x4e00 + ((c >> 6) & 0xFF)));
//			System.out.println((byte)(0x4e00 + (c & 0xFF)));
			
			System.out.println();
			System.out.println(Character.isSurrogate(c));
			
		}
		
		
	}
	
	protected int getGB18030(short[] outerIndex, String[] innerEncoderIndex,
			char ch) {
		int offset = outerIndex[((ch & 0xff00) >> 8)] << 8;
		return innerEncoderIndex[offset >> 12].charAt((offset & 0xfff) + (ch & 0xff));
	}
}
