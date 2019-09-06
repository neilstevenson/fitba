package fitba

import (
        "fmt"
        "github.com/hazelcast/hazelcast-go-client/core"
        "sync"
)

type EntryListener struct {
        Wg *sync.WaitGroup
}

func (expireEntry *EntryListener) EntryExpired(event core.EntryEvent) {
        fmt.Println("KEY EXPIRED '", event.Key(), "'")
        fmt.Println("")
}
func (updateEntry *EntryListener) EntryUpdated(event core.EntryEvent) {
        fmt.Println("KEY UPDATED '", event.Key(), "'")
        fmt.Println("  VALUE WAS: '", event.OldValue(), "'")
        fmt.Println("  VALUE IS.: '", event.Value(), "'")
        fmt.Println("")
}
func (loadEntry *EntryListener) EntryLoaded(event core.EntryEvent) {
        fmt.Println("KEY LOADED '", event.Key(), "'")
        fmt.Println("  VALUE IS.: '", event.Value(), "'")
        fmt.Println("")
}
