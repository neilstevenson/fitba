package main

import (
        "fmt"
        "sync"
        "time"
        "github.com/hazelcast/hazelcast-go-client"
	"github.com/hazelcast/hazelcast-go-client/config/property"
	"github.com/hazelcast/hazelcast-go-client/core/logger"
        . "github.com/neilstevenson/fitba/go"
)

func main() {
           clientConfig := hazelcast.NewConfig()
           clientConfig.GroupConfig().SetName("fitba")
           clientConfig.NetworkConfig().AddAddress("127.0.0.1:5701")
           clientConfig.NetworkConfig().AddAddress("127.0.0.1:5702")

           clientConfig.SetProperty(property.LoggingLevel.Name(), logger.OffLevel)
           clientConfig.SetProperty("hazelcast.client.statistics.enabled", "true")

	   clientConfig.SerializationConfig().AddDataSerializableFactory(MyDataSerializableFactoryId, &MyDataSerializableFactory{})

           hazelcastClient, err := hazelcast.NewClientWithConfig(clientConfig)
           if err != nil {
               fmt.Println(err)
               return
           }

           bundesligaMap, _ := hazelcastClient.GetMap("Bundesliga")
		
           var wg *sync.WaitGroup = new(sync.WaitGroup)
           entryListener := &EntryListener{Wg: wg}
           registrationId, _ := bundesligaMap.AddEntryListener(entryListener, true)

           fmt.Println(registrationId)
	   fmt.Printf("======================================\n")   
    	   fmt.Printf("Chuck Norris doesn't sleep, he waits.\n")
	   fmt.Printf("======================================\n")   
	   fmt.Println()
		
           time.Sleep(30 * time.Minute)
                
           bundesligaMap.RemoveEntryListener(registrationId)

           fmt.Printf("======================================\n")  
           hazelcastClient.Shutdown()  
}
