package neil.demo.fitba;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.IMap;

@SuppressWarnings("serial")
public class SumDifferenceSquaredFn implements Callable<Double>, HazelcastInstanceAware, Serializable {

	private transient HazelcastInstance hazelcastInstance;
    private String league;
    private double average;
    
    @Override
    public Double call() throws Exception {
        IMap<String, Team> leagueMap = this.hazelcastInstance.getMap(this.league);
    	double total = 0;

    	for (String key : leagueMap.localKeySet()) {
            
    		double goalsFor = leagueMap.get(key).getGf();
        
    		double difference = goalsFor - this.average;
        
    		total += difference * difference;
    	}

    	return total;
    }

    @Override
	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
    	this.hazelcastInstance = hazelcastInstance;
    }
    
    public SumDifferenceSquaredFn() {}
    public SumDifferenceSquaredFn(String league, double average) {
    	this.league = league;
    	this.average = average;
    }

}