package entity.id_generator;

import java.io.Serializable;
import java.util.stream.Stream;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.query.spi.QueryImplementor;

public class LoThuocStringGeneratorId implements IdentifierGenerator {

		private String prefix = "Lo";

		@Override
		public Serializable generate(SharedSessionContractImplementor session, Object object) {
			// Select all id
			QueryImplementor<String> query = session.createQuery("SELECT lt.maLoThuoc FROM LoThuoc lt", String.class);
			try (Stream<String> stream = query.stream()) {
				Long max = stream.map(t -> t.replace(prefix, ""))
						.mapToLong(Long::parseLong)
						.max()
						.orElse(0L);
				return prefix + String.format("%08d", max + 1);
			}
		}
	}