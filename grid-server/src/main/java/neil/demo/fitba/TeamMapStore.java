package neil.demo.fitba;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.hazelcast.core.MapStore;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TeamMapStore implements MapStore<String, Team> {

	private final String league;
	private final TeamRepository teamRepository;
	
	public TeamMapStore(String arg0, TeamRepository arg1) {
		this.league = arg0;
		this.teamRepository = arg1;
	}

	@Override
	public Team load(String key) {
		log.info("'{}'::load('{}')", this.league, key);

		try {
			// Map-to-table is many-to-one, don't cross load leagues 
			Team team = this.teamRepository.findById(key).get();
			return this.league.equals(team.getLeague()) ? team : null;
		} catch (Exception exception) {
			log.warn("'" + this.league + "'::load('" + key + "')," + exception.getMessage());
			return null;
		}
	}

	@Override
	public Map<String, Team> loadAll(Collection<String> keys) {
		log.trace("'{}'::loadAll('{}')", this.league, keys);

		Map<String, Team> result = new HashMap<>();
		for (String key : keys) {
			Team team = null;
			try {
				team = this.load(key);
			} catch (Exception exception) {
				log.error("'" + this.league + "'::loadAll(" + keys + ")", exception);
			}

			if (team != null) {
				result.put(key, team);
			}
		}
		return result;
	}

	@Override
	public Iterable<String> loadAllKeys() {
		log.debug("'{}'::loadAllKeys()");

		try {
			return this.teamRepository.findLeagueTeams(this.league);
		} catch (Exception exception) {
			log.error("'" + this.league + "'::loadAllKeys()", exception);
			return Collections.emptyList();
		}
	}

	@Override
	public void store(String key, Team value) {
		log.info("'{}'::store('{}', '{}')",
				this.league, key, value);
		this.teamRepository.save(value);
	}

	@Override
	public void storeAll(Map<String, Team> map) {
		log.error("'{}'::storeAll('{}'), not yet implemeted",
				this.league, map.entrySet());
	}

	@Override
	public void delete(String key) {
		log.error("'{}'::delete('{}'), not yet implemeted",
				this.league, key);
	}

	@Override
	public void deleteAll(Collection<String> keys) {
		log.error("'{}'::deleteAll('{}'), not yet implemeted",
				this.league, keys);
	}

}
