package fitba

import (
        "github.com/hazelcast/hazelcast-go-client/serialization"
)

const (
        TeamClassId = 1
        MyDataSerializableFactoryId = 1000
)

/* Team object
 */
type Team struct {
        league          string
        pos             int32
        name            string
        pld             int32
        w               int32
        l               int32
        d               int32
        gf              int32
        ga              int32
        gd              int32
        pts             int32
}

func (s *Team) FactoryID() int32 {
        return MyDataSerializableFactoryId
}

func (s *Team) ClassID() int32 {
        return TeamClassId
}

func (s *Team) WriteData(output serialization.DataOutput) error {
        output.WriteUTF(s.league)
        output.WriteInt32(s.pos)
        output.WriteUTF(s.name)
        output.WriteInt32(s.pld)
        output.WriteInt32(s.w)
        output.WriteInt32(s.l)
        output.WriteInt32(s.d)
        output.WriteInt32(s.gf)
        output.WriteInt32(s.ga)
        output.WriteInt32(s.gd)
        output.WriteInt32(s.pts)
        return nil
}

func (s *Team) ReadData(input serialization.DataInput) error {
        s.league         = input.ReadUTF()
	s.pos            = input.ReadInt32()
        s.name           = input.ReadUTF()
	s.pld            = input.ReadInt32()
	s.w              = input.ReadInt32()
	s.l              = input.ReadInt32()
	s.d              = input.ReadInt32()
	s.gf             = input.ReadInt32()
	s.ga             = input.ReadInt32()
	s.gd             = input.ReadInt32()
	s.pts            = input.ReadInt32()
        return nil
}

func (s *Team) SetLeague(league string) {
    s.league = league
}
func (s *Team) SetPos(pos int32) {
    s.pos = pos
}
func (s *Team) SetName(name string) {
    s.name = name
}
func (s *Team) SetPld(pld int32) {
    s.pld = pld
}
func (s *Team) SetW(w int32) {
    s.w = w
}
func (s *Team) SetL(l int32) {
    s.l = l
}
func (s *Team) SetD(d int32) {
    s.d = d
}
func (s *Team) SetGf(gf int32) {
    s.gf = gf
}
func (s *Team) SetGa(ga int32) {
    s.ga = ga
}
func (s *Team) SetGd(gd int32) {
    s.gd = gd
}
func (s *Team) SetPts(pts int32) {
    s.pts = pts
}

func (s *Team) League() string {
    return s.league
}
func (s *Team) Pos() int32 {
    return s.pos
}
func (s *Team) Name() string {
    return s.name
}
func (s *Team) Pld() int32 {
    return s.pld
}
func (s *Team) W() int32 {
    return s.l
}
func (s *Team) L() int32 {
    return s.l
}
func (s *Team) D() int32 {
    return s.d
}
func (s *Team) Gf() int32 {
    return s.gf
}
func (s *Team) Ga() int32 {
    return s.ga
}
func (s *Team) Gd() int32 {
    return s.gd
}
func (s *Team) Pts() int32 {
    return s.pts
}


/* Factory to build these structures
 */
type MyDataSerializableFactory struct {
}

func (*MyDataSerializableFactory) Create(classId int32) serialization.IdentifiedDataSerializable {
        if classId == TeamClassId {
                return &Team{}
        }
        return nil
}

