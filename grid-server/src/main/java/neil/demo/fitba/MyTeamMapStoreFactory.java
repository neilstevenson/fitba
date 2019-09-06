package neil.demo.fitba;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.hazelcast.core.MapLoader;
import com.hazelcast.core.MapStoreFactory;

@SuppressWarnings("rawtypes")
@Component
public class MyTeamMapStoreFactory implements MapStoreFactory {

   @Autowired
   private ApplicationContext applicationContext;

   @Override
   public MapLoader newMapStore(String league, Properties properties) {
	   TeamRepository teamRepository = this.applicationContext.getBean(TeamRepository.class);
	   return new TeamMapStore(league, teamRepository);
   }

}
