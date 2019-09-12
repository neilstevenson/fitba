package neil.demo.fitba;

import java.math.BigDecimal;
import java.util.Map.Entry;

import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.map.AbstractEntryProcessor;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("serial")
@Slf4j
public class AccountMergeEntryProcessor extends AbstractEntryProcessor<String, HazelcastJsonValue> {
        
        private String incomingTransaction;
        
        AccountMergeEntryProcessor(String arg0) {
        	this.incomingTransaction = arg0;
        }

		public Object process(Entry<String, HazelcastJsonValue> entry) {
			if (entry.getValue()==null) {
				log.warn("No value for key '{}'", entry.getKey());
				return null;
			}

			try {
				JSONObject jsonObject = new JSONObject(entry.getValue().toString());
				BigDecimal oldBalance = new BigDecimal(jsonObject.getString("balance"));
				JSONObject incomingJsonObject = new JSONObject(this.incomingTransaction);

				BigDecimal transactionAmount;
				BigDecimal newBalance;
				boolean isDebit = incomingJsonObject.has("debit");
				
				if (isDebit) {
					transactionAmount = new BigDecimal(incomingJsonObject.getString("debit"));
					newBalance = oldBalance.subtract(transactionAmount);
				} else {
					transactionAmount = new BigDecimal(incomingJsonObject.getString("credit"));
					newBalance = oldBalance.add(transactionAmount);
				}
				jsonObject.put("balance", newBalance.toString());

				// Add basics of transaction to list, already know account
				JSONArray transactions = jsonObject.getJSONArray("transactions");
				String description = incomingJsonObject.getString("description");
				String timestamp = incomingJsonObject.getString("timestamp");

				String txn = "{ \"amount\" : \"" + transactionAmount + "\","
						+ " \"description\" : \"" + description + "\","
						+ " \"timestamp\" : \"" + timestamp + "\" }";
				
				transactions.put(new JSONObject(txn));
				jsonObject.put("transactions", transactions);

				// Save
				entry.setValue(new HazelcastJsonValue(jsonObject.toString()));

			} catch (Exception e) {
				e.printStackTrace();
			}

			// Caller doesn't need to know what was saved
			return null;
		}
}