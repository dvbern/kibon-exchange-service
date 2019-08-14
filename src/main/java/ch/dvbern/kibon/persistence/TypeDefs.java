package ch.dvbern.kibon.persistence;

import javax.persistence.MappedSuperclass;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import org.hibernate.annotations.TypeDef;

@SuppressWarnings("EmptyClass")
@TypeDef(name = "json", typeClass = JsonStringType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "jsonb-node", typeClass = JsonNodeBinaryType.class)
@MappedSuperclass
public class TypeDefs {
}
