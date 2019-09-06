package neil.demo.fitba;

import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import com.hazelcast.core.HazelcastInstance;

@ShellComponent
@EnableCaching
public class Fibonacci {

        @Autowired
        private Fibonacci fibonacci;
        @Autowired
        private HazelcastInstance hazelcastInstance;
        
        @ShellMethod(key = "FIBONACCI", value = "Timed Fibonacci calculation")
        public void fibonacci(@ShellOption(value="input") long l) {
        	long beforeWithCaching = System.currentTimeMillis();
        	long resultWithCaching = this.wrappedFibonacci(l, true);
        	long elapsedWithCaching = System.currentTimeMillis() - beforeWithCaching;
        	
        	System.out.format("With    caching, result %,3d in %,3d ms%n", 
        			resultWithCaching, elapsedWithCaching);
        	
        	long beforeWithoutCaching = System.currentTimeMillis();
        	long resultWithoutCaching = this.wrappedFibonacci(l, false);
        	long elapsedWithoutCaching = System.currentTimeMillis() - beforeWithoutCaching;

        	System.out.format("Without caching, result %,3d in %,3d ms%n", 
        			resultWithoutCaching, elapsedWithoutCaching);

        	if (resultWithCaching != resultWithoutCaching) {
        		System.err.println("Fibonacci results don't match!");
        	}
        }
        
        public long wrappedFibonacci(long l, boolean useCache) {
        	if (useCache) {
        		return this.cachingFibonacci(l);
        	} else {
        		return this.nonCachingFibonacci(l);
        	}
        }
 
        public long nonCachingFibonacci(long input) {
            if (input <= 2) {
            	return 1;
            } else {
            	return this.fibonacci.nonCachingFibonacci(input - 1)
            			+ this.fibonacci.nonCachingFibonacci(input - 2);
            }
        }

        @Cacheable(value = MyConstants.FIBONACCI)
        public long cachingFibonacci(long input) {
            if (input <= 2) {
            	return 1;
            } else {
            	return this.fibonacci.cachingFibonacci(input - 1)
            			+ this.fibonacci.cachingFibonacci(input - 2);
            }
        }
        
        // Standard Java map methods
        @ShellMethod(key = "FIBONACCI_CACHE", value = "Print cached Fibonacci calculation")
        public void fibonacciCache() {
        	Map<Long,Long> fibonacciCache =
        			this.hazelcastInstance.getMap(MyConstants.FIBONACCI);
        			
        	new TreeSet<>(fibonacciCache.keySet())
        	.forEach(key -> {
        		System.out.printf("(K,V)==(%d,%d)%n", key, fibonacciCache.get(key));
        	});
        	
        	if (fibonacciCache.isEmpty()) {
        		System.out.println("No results");
        	}
        }

}