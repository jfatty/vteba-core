package com.vteba.service.xml.xstream;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.vteba.service.xml.Field;

/**
 * xstream的xml field映射绑定扩展。
 * @author yinlei
 * @date 2013年12月1日 下午8:42:57
 */
public class FieldConverter implements Converter {

	@Override
	public boolean canConvert(@SuppressWarnings("rawtypes") Class type) {
		if (type == Field.class) {
			return true;
		}
		return false;
	}

	@Override
	public void marshal(Object source, HierarchicalStreamWriter writer,
			MarshallingContext context) {
		Field field = (Field) source;
		// writer.startNode("field");
		writer.addAttribute("name", field.getName());
		writer.setValue(field.getValue());
		// writer.endNode();
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		Field field = new Field();
		String name = reader.getAttribute("name");
		field.setName(name);
		String value = reader.getValue();
		field.setValue(value);
		return field;
	}

}
