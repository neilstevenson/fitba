package neil.demo.fitba;

import com.hazelcast.nio.serialization.DataSerializableFactory;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyDataSerializableFactory implements DataSerializableFactory {

	@Override
	public IdentifiedDataSerializable create(int typeId) {
		log.trace("IdentifiedDataSerializable create({})", typeId);

		if (typeId == MyConstants.CLASS_ID_TEAM) {
			return new Team();
		}

		log.error("Unknown typeId: {}", typeId);
		return null;
	}

}